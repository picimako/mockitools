//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.picimako.mockitools.ListPopupHelper.selectItemAndRun;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockableTypeInAnyWay;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoSpy;
import static com.picimako.mockitools.PsiClassUtil.getParentClasses;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.hasOneArgument;
import static com.picimako.mockitools.PsiMethodUtil.isIdentifierOfMethodCall;
import static com.picimako.mockitools.inspection.ClassObjectAccessUtil.getOperandType;
import static com.picimako.mockitools.inspection.ClassObjectAccessUtil.resolveOperandType;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.CaseFormat;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.util.PsiClassListCellRenderer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenamer;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenamer;
import com.intellij.util.Consumer;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.MockitoQualifiedNames;
import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Converts {@code Mockito.spy()} calls to {@code @Spy} annotated fields.
 * <p>
 * The intention is available
 * <ul>
 *      <li>on Mockito.spy() calls,</li>
 *      <li>when the argument of the call is either a new expression, or a class object access expression,</li>
 *      <li>the argument is not an array creation,</li>
 *      <li>the type that is being mocked can actually be mocked (by Mockito's rules)/allowed to be mocked (by the @DoNotMock annotation)</li>
 * </ul>
 * <p>
 * <strong>Examples:</strong>
 * <pre>
 * spy(Clazz.class) -> @Spy Clazz clazz;
 * spy(new Clazz()) -> @Spy Clazz clazz;
 * spy(new Clazz(&lt;arguments>)) -> @Spy Clazz clazz = new Clazz(&lt;arguments>);
 * spy(new Clazz&lt;typeargs>()) -> @Spy Clazz&lt;typeargs> clazz;
 * Clazz localVar = spy(Clazz.class) -> @Spy Clazz localVar;
 * Clazz localVar = spy(new Clazz()) -> @Spy Clazz lovalVar;
 * Clazz localVar = spy(new Clazz(&lt;arguments>)) -> @Spy Clazz lovalVar = new Clazz(&lt;arguments>);
 * Clazz&lt;typeargs> localVar = spy(new Clazz&lt;typeargs>()) -> @Spy Clazz&lt;typeargs> lovalVar;
 * </pre>
 * <p>
 * <strong>Naming</strong>
 * <ul>
 *     <li>if the {@code Mockito.spy()} call is part of a local variable declaration, then by default will use the variable's name,
 *     but if there is already a field with the same name in the target class, a rename refactor is invoked first.</li>
 *     <li>if the call is not part of a local variable declaration, a rename refactor is invoked first, where the default field name provided is the
 *     mock type's name in lowercase format.</li>
 * </ul>
 * <strong>Target class selection</strong>
 * <p>
 * If there is more than one parent class of the selected spy() call, a list is shown from which the class where the field will be introduced, can be selected. 
 * <p>
 * NOTE: The format {@code spy(&lt;reference to already created object>)} where the object passed in to the spy() call is created outside of
 * the call is not yet supported.
 *
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Spy.html">@Spy annotation javadoc</a>
 * @since 0.2.0
 */
public class ConvertSpyCallToFieldIntention implements IntentionAction {
    private static final Supplier<String> NO_INITIALIZER = null;

    @Override
    public @IntentionName @NotNull String getText() {
        return MockitoolsBundle.message("intention.convert.spy.call.to.field");
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return MockitoolsBundle.message("intention.convert.spy.call.to.field.family");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!file.getFileType().equals(JavaFileType.INSTANCE)) {
            return false;
        }
        final PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        if (isIdentifierOfMethodCall(element)) {
            var methodCall = (PsiMethodCallExpression) element.getParent().getParent();
            if (isMockitoSpy(methodCall) && hasOneArgument(methodCall)) {
                var firstArg = getFirstArgument(methodCall);
                return firstArg instanceof PsiNewExpression
                    ? !((PsiNewExpression) firstArg).isArrayCreation() && isMockableTypeInAnyWay(firstArg.getType())
                    : firstArg instanceof PsiClassObjectAccessExpression && isMockableTypeInAnyWay(getOperandType(firstArg));
            }
        }
        return false;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        final PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        var ctx = new ConversionContext((PsiMethodCallExpression) element.getParent().getParent(), editor, project);
        List<PsiClass> parentClasses = getParentClasses(ctx.firstArg);

        if (ctx.firstArg instanceof PsiClassObjectAccessExpression) {
            selectTargetClassAndIntroduceField(parentClasses, project, editor, targetClass -> introduceFieldForClassObjectAccess(ctx.withTargetClass(targetClass)));
        } else if (ctx.firstArg instanceof PsiNewExpression) {
            selectTargetClassAndIntroduceField(parentClasses, project, editor, targetClass -> introduceFieldForNewExpression(ctx.withTargetClass(targetClass)));
        }
    }

    /**
     * If there is only one parent class, introduces the field there, otherwise lets the user choose the target parent class, and creates the field in that one.
     */
    private void selectTargetClassAndIntroduceField(List<PsiClass> parentClasses, Project project, Editor editor, Consumer<PsiClass> introduceField) {
        if (parentClasses.size() == 1) {
            introduceField.consume(parentClasses.get(0));
        } else {
            selectItemAndRun(MockitoolsBundle.message("intention.select.target.class"), parentClasses, introduceField, PsiClassListCellRenderer::new, editor, project);
        }
    }

    /**
     * Introduces the field taking into account that the Mockito.spy() call's argument is a class object access expression, so that the field type and name
     * are constructed properly.
     */
    private void introduceFieldForClassObjectAccess(ConversionContext ctx) {
        introduceField(ctx, () -> Pair.create(resolveOperandType(ctx.firstArg).getName(), toLowerCamel(resolveOperandType(ctx.firstArg).getName())), NO_INITIALIZER);
    }

    /**
     * Introduces the field taking into account that the Mockito.spy() call's argument is a new expression, so that the field type and name are constructed properly.
     */
    private void introduceFieldForNewExpression(ConversionContext ctx) {
        var newExpression = (PsiNewExpression) ctx.firstArg;
        introduceField(ctx, () -> constructFieldTypeAndName(newExpression.getClassReference()), getInitializer(newExpression));
    }

    /**
     * Constructs the field type and name pair for the class reference referenced in case of a new expression.
     * <p>
     * For example:
     * <pre>
     * new MockableObject(); -> Pair("MockableObject", "mockableObject")
     * new MockableObject&lt;String, Integer>(); -> Pair("MockableObject&lt;java.lang.String, java.lang.Integer>", "mockableObject")
     * </pre>
     */
    private Pair<String, String> constructFieldTypeAndName(PsiJavaCodeReferenceElement classReference) {
        String fieldName = classReference.getTypeParameters().length == 0
            ? classReference.getReferenceName()
            : classReference.getReferenceName() + "<" + Arrays.stream(classReference.getTypeParameters()).map(PsiType::getCanonicalText).collect(joining(", ")) + ">";
        return Pair.create(fieldName, toLowerCamel(classReference.getReferenceName()));
    }

    /**
     * Returns the initializer of the to be created @Spy field. If the called constructor is the default one, no initializer is necessary,
     * otherwise the whole constructor call will be copied.
     */
    private Supplier<String> getInitializer(PsiNewExpression newExpression) {
        return isDefaultConstructor(newExpression) ? NO_INITIALIZER : newExpression::getText;
    }

    private void introduceField(ConversionContext ctx, Supplier<Pair<String, String>> typeAndFieldName, Supplier<String> initializer) {
        if (ctx.spyCall.getParent() instanceof PsiLocalVariable) {
            var variable = (PsiLocalVariable) ctx.spyCall.getParent();
            PsiElement field = createField(variable.getType().getCanonicalText(), getLocalVariableName(ctx.spyCall.getParent()), initializer, ctx.targetClass, ctx.project);
            doIntroduceFieldForLocalVariable(ctx, field, variable);
        } else {
            var typeAndField = typeAndFieldName.get();
            PsiElement field = createField(typeAndField.first, typeAndField.second, initializer, ctx.targetClass, ctx.project);
            doIntroduceFieldForStandaloneMethodCall(ctx, field);
        }
    }

    private String toLowerCamel(String text) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, text);
    }

    private String getLocalVariableName(PsiElement localVar) {
        return ((PsiLocalVariable) localVar).getName();
    }

    /**
     * Does the actual conversion when the Mockito.spy() call is part of a local variable declaration.
     * <p>
     * First it adds the field to the target class, then deletes the whole variable declaration because the field is created with the same name as the variable
     * had.
     * <p>
     * Field rename is triggered only when there is already a field existing with the to be introduced field's name.
     */
    private void doIntroduceFieldForLocalVariable(ConversionContext ctx, PsiElement fieldToAdd, PsiLocalVariable variableDeclaration) {
        boolean hasFieldWithSameNameAlready = Arrays.stream(ctx.targetClass.getFields()).anyMatch(f -> ((PsiField) fieldToAdd).getName().equals(f.getName()));
        if (!ApplicationManager.getApplication().isUnitTestMode() && hasFieldWithSameNameAlready) {
            //Moving the caret to the identifier is needed, so that the rename doesn't run in an underlying NPE
            ctx.editor.getCaretModel().moveToOffset(variableDeclaration.getNameIdentifier().getTextOffset());
            new VariableInplaceRenamer(variableDeclaration, ctx.editor, ctx.project) {
                @Override
                protected void moveOffsetAfter(boolean success) {
                    super.moveOffsetAfter(success);
                    if (success) {
                        Optional.ofNullable(getVariable())
                            .filter(renamedVariable -> Arrays.stream(ctx.targetClass.getFields()).noneMatch(f -> renamedVariable.getName().equals(f.getName())))
                            .ifPresentOrElse(renamedVariable ->
                                    WriteCommandAction.runWriteCommandAction(ctx.project, () -> {
                                        PsiDocumentManager.getInstance(ctx.project).commitAllDocuments();
                                        //set the name of the field to be introduced, so that it is created with the name specified by the user
                                        if (renamedVariable.getName() != null) {
                                            ((PsiField) fieldToAdd).setName(renamedVariable.getName());
                                        }
                                        ctx.targetClass.add(fieldToAdd);
                                        renamedVariable.delete(); //remove local variable declaration
                                    }),
                                () -> Messages.showErrorDialog(
                                    MockitoolsBundle.message("intention.convert.x.call.to.field.conversion.cannot.happen.message", "@Spy", getVariable().getName()),
                                    MockitoolsBundle.message("intention.convert.x.call.to.field.conversion.cannot.happen.title")));
                    }
                }
            }.performInplaceRename();
        } else {
            WriteCommandAction.runWriteCommandAction(ctx.project, () -> {
                PsiDocumentManager.getInstance(ctx.project).commitAllDocuments();
                ctx.targetClass.add(fieldToAdd);
                ctx.spyCall.getParent().delete(); //remove local variable declaration
            });
        }
    }

    /**
     * Does the actual conversion when the Mockito.spy() call is as an individual method call but embedded in e.g. as a method call argument.
     */
    private void doIntroduceFieldForStandaloneMethodCall(ConversionContext ctx, PsiElement field) {
        var manager = PsiDocumentManager.getInstance(ctx.project);
        manager.commitAllDocuments();
        var addedField = new Ref<PsiField>();
        WriteCommandAction.runWriteCommandAction(ctx.project, () -> {
            addedField.set((PsiField) ctx.targetClass.add(field));
            manager.commitDocument(ctx.editor.getDocument());
        });
        WriteCommandAction.runWriteCommandAction(ctx.project, () -> {
            //replace standalone spy() call with the name of the new field
            ctx.editor.getDocument().replaceString(ctx.spyCall.getTextOffset(), ctx.spyCall.getTextRange().getEndOffset(), addedField.get().getName());
            manager.commitDocument(ctx.editor.getDocument());
        });
        //This is separated, because running this in the previous WriteCommandAction the added field's position and formatting is not proper.
        if (!ApplicationManager.getApplication().isUnitTestMode() && !addedField.isNull()) {
            //Moving the caret to the identifier is needed, so that the rename doesn't run in an underlying NPE
            ctx.editor.getCaretModel().moveToOffset(addedField.get().getNameIdentifier().getTextOffset());
            new MemberInplaceRenamer(addedField.get(), null, ctx.editor).performInplaceRename();
        }
    }

    /**
     * Returns whether the argument expression is a default constructor.
     * <p>
     * It is either an explicitly defined one, or an auto-generated one when there the instantiated type has no non-default constructor.
     */
    private static boolean isDefaultConstructor(@NotNull PsiNewExpression newExpression) {
        PsiMethod constructor = newExpression.resolveConstructor(); //in case of auto-generated default constructor it returns null
        //new Clazz() + explicit Clazz() -> true
        //new Clazz() + Clazz(String) -> false
        //new Clazz("") + Clazz(String) -> false
        //new Clazz("") + Clazz(int) -> false
        //new Clazz("") + Clazz() + Clazz(int) -> false
        if (constructor != null) {
            return constructor.getParameterList().isEmpty() && (constructor.hasModifierProperty(PsiModifier.PUBLIC) || constructor.hasModifierProperty(PsiModifier.PACKAGE_LOCAL));
        }
        //new Clazz() + auto-gen Clazz() -> true
        //new Clazz() + Clazz(String) + Clazz(int) -> false
        //new Clazz("") + Clazz() + Clazz(int) + Clazz(double) -> false
        return Optional.ofNullable(newExpression.getClassReference())
            .map(PsiReference::resolve)
            .map(PsiClass.class::cast)
            .map(PsiClass::getConstructors)
            .stream()
            .flatMap(Arrays::stream)
            .noneMatch(constr -> !constr.getParameterList().isEmpty());
    }

    /**
     * Creates the @Spy field that will be introduced in the selected class.
     * <p>
     * An initializer is also added if necessary.
     *
     * @param initializer if no initializer is needed, pass in {@link #NO_INITIALIZER}
     */
    private PsiElement createField(String fieldType, String fieldName, Supplier<String> initializer, PsiElement context, Project project) {
        StringBuilder sb = new StringBuilder("@" + MockitoQualifiedNames.ORG_MOCKITO_SPY + " " + fieldType + " " + fieldName);
        if (initializer != NO_INITIALIZER) {
            sb.append(" = ").append(initializer.get());
        }
        return JavaCodeStyleManager.getInstance(project)
            .shortenClassReferences(JavaPsiFacade.getElementFactory(project).createFieldFromText(sb.append(";").toString(), context));
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    private static final class ConversionContext {
        private final PsiMethodCallExpression spyCall;
        private final PsiExpression firstArg;
        private final Editor editor;
        private final Project project;
        private PsiClass targetClass;

        private ConversionContext(PsiMethodCallExpression spyCall, Editor editor, Project project) {
            this.spyCall = spyCall;
            this.firstArg = getFirstArgument(spyCall);
            this.editor = editor;
            this.project = project;
        }

        public ConversionContext withTargetClass(PsiClass targetClass) {
            this.targetClass = targetClass;
            return this;
        }
    }
}
