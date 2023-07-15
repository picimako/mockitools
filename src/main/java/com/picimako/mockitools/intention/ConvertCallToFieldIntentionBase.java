//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.picimako.mockitools.util.ClassObjectAccessUtil.resolveOperandType;
import static com.picimako.mockitools.util.ListPopupHelper.selectItemAndRun;
import static com.picimako.mockitools.util.PsiClassUtil.getParentClasses;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.util.Ranges.endOffsetOf;
import static java.util.stream.Collectors.joining;

import com.google.common.base.CaseFormat;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.ide.util.PsiClassListCellRenderer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiType;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenamer;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenamer;
import com.intellij.util.Consumer;
import com.intellij.util.IncorrectOperationException;
import com.picimako.mockitools.resources.MockitoolsBundle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base class for intention converting mocking calls to fields annotated with various Mockito annotations.
 *
 * @see ConvertMockCallToFieldIntention
 * @see ConvertSpyCallToFieldIntention
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class ConvertCallToFieldIntentionBase implements IntentionAction {

    protected static final Supplier<String> NO_INITIALIZER = null;
    private final String mockitoCallName;
    private final String annotationName;

    //Intention name

    @Override
    public @IntentionName @NotNull String getText() {
        return MockitoolsBundle.message("intention.convert.x.call.to.field", annotationName);
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return MockitoolsBundle.message("intention.convert.x.call.to.y.field.family", mockitoCallName, annotationName);
    }

    //Conversion

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        final var element = file.findElementAt(editor.getCaretModel().getOffset());
        var ctx = new ConversionContext((PsiMethodCallExpression) element.getParent().getParent(), editor, project);

        if (ctx.mockTypeOrObject instanceof PsiClassObjectAccessExpression) {
            selectTargetClassAndIntroduceField(getParentClasses(ctx.mockTypeOrObject),
                project,
                editor,
                targetClass -> introduceFieldForClassObjectAccess(ctx.targetClass(targetClass)));
        }
        //This branch is valid only in case of ConvertSpyCallToFieldIntention
        else if (ctx.mockTypeOrObject instanceof PsiNewExpression) {
            selectTargetClassAndIntroduceField(getParentClasses(ctx.mockTypeOrObject),
                project,
                editor,
                targetClass -> introduceFieldForNewExpression(ctx.targetClass(targetClass)));
        }
    }

    /**
     * Introduces the field taking into account that the Mockito.spy() or Mockito.mock() call's argument is a 'new' expression, so that the field type, name
     * and initializer are constructed properly.
     * <p>
     * Applicable only to {@link ConvertSpyCallToFieldIntention}.
     */
    protected void introduceFieldForNewExpression(ConversionContext ctx) {
        //No-op by default to prevent it being used in ConvertMockCallToFieldIntention
    }

    /**
     * Introduces the field taking into account that the Mockito.spy() or Mockito.mock() call's argument is a class object access expression, so that the field type and name
     * are constructed properly.
     */
    protected void introduceFieldForClassObjectAccess(ConversionContext ctx) {
        introduceField(ctx,
            () -> Pair.create(
                resolveOperandType(ctx.mockTypeOrObject).getName(),
                toLowerCamel(resolveOperandType(ctx.mockTypeOrObject).getName())),
            NO_INITIALIZER);
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

    protected void introduceField(ConversionContext ctx, Supplier<Pair<String, String>> typeAndFieldName, Supplier<String> initializer) {
        if (ctx.spyOrMockCall.getParent() instanceof PsiLocalVariable variable) {
            var field = createField(variable.getType().getCanonicalText(), getLocalVariableName(variable), initializer, ctx);
            doIntroduceFieldForLocalVariable(ctx, field, variable);
        } else {
            var typeAndField = typeAndFieldName.get();
            var field = createField(typeAndField.first, typeAndField.second, initializer, ctx);
            doIntroduceFieldForStandaloneMethodCall(ctx, field);
        }
    }

    protected abstract PsiElement createField(String fieldType, String fieldName, Supplier<String> initializer, ConversionContext context);

    /**
     * Does the actual conversion when the Mockito.spy() or Mockito.mock() call is part of a local variable declaration.
     * <p>
     * First it adds the field to the target class, then deletes the whole variable declaration because the field is created with the same name as the variable
     * had.
     * <p>
     * Field rename is triggered only when there is already a field existing with the to be introduced field's name.
     */
    private void doIntroduceFieldForLocalVariable(ConversionContext ctx, PsiElement fieldToAdd, PsiLocalVariable variableDeclaration) {
        boolean hasFieldWithSameNameAlready = Arrays.stream(ctx.targetClass.getFields()).anyMatch(f -> ((PsiField) fieldToAdd).getName().equals(f.getName()));
        if (hasFieldWithSameNameAlready && !ApplicationManager.getApplication().isUnitTestMode()) {
            //Moving the caret to the identifier is needed, so that rename doesn't run into an underlying NPE
            ctx.editor.getCaretModel().moveToOffset(variableDeclaration.getNameIdentifier().getTextOffset());
            new VariableInplaceRenamer(variableDeclaration, ctx.editor, ctx.project) {
                @Override
                protected void moveOffsetAfter(boolean success) {
                    super.moveOffsetAfter(success);
                    if (!success) return;

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
            }.performInplaceRename();
        } else {
            WriteCommandAction.runWriteCommandAction(ctx.project, () -> {
                PsiDocumentManager.getInstance(ctx.project).commitAllDocuments();
                ctx.targetClass.add(fieldToAdd);
                ctx.spyOrMockCall.getParent().delete(); //remove local variable declaration
            });
        }
    }

    /**
     * Does the actual conversion when the Mockito.spy() Mockito.mock() call is as an individual method call but embedded in e.g. as a method call argument.
     */
    private void doIntroduceFieldForStandaloneMethodCall(ConversionContext ctx, PsiElement field) {
        var manager = PsiDocumentManager.getInstance(ctx.project);
        manager.commitAllDocuments();
        var addedField = new Ref<PsiField>();
        WriteCommandAction.runWriteCommandAction(ctx.project, () -> {
            addedField.set((PsiField) ctx.targetClass.add(field));
            manager.commitDocument(ctx.getDocument());
        });
        WriteCommandAction.runWriteCommandAction(ctx.project, () -> {
            //replace standalone spy() call with the name of the new field.
            ctx.getDocument().replaceString(ctx.spyOrMockCall.getTextOffset(), endOffsetOf(ctx.spyOrMockCall), addedField.get().getName());
            manager.commitDocument(ctx.getDocument());
        });
        //This is separated, because running this in the previous WriteCommandAction the added field's position and formatting is not proper.
        if (!addedField.isNull() && !ApplicationManager.getApplication().isUnitTestMode()) {
            //Moving the caret to the identifier is needed, so that rename doesn't run into an underlying NPE
            ctx.editor.getCaretModel().moveToOffset(addedField.get().getNameIdentifier().getTextOffset());
            new MemberInplaceRenamer(addedField.get(), null, ctx.editor).performInplaceRename();
        }
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
    protected Pair<String, String> constructFieldTypeAndName(PsiJavaCodeReferenceElement classReference) {
        String fieldName = classReference.getTypeParameters().length == 0
            ? classReference.getReferenceName()
            : classReference.getReferenceName() + "<" + Arrays.stream(classReference.getTypeParameters()).map(PsiType::getCanonicalText).collect(joining(", ")) + ">";
        return Pair.create(fieldName, toLowerCamel(classReference.getReferenceName()));
    }

    private String toLowerCamel(String text) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, text);
    }

    private String getLocalVariableName(PsiElement localVar) {
        return ((PsiLocalVariable) localVar).getName();
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    protected static final class ConversionContext {
        final PsiMethodCallExpression spyOrMockCall;
        final PsiExpression mockTypeOrObject;
        final Editor editor;
        final Project project;
        @Accessors(fluent = true)
        @Setter
        PsiClass targetClass;

        ConversionContext(PsiMethodCallExpression spyOrMockCall, Editor editor, Project project) {
            this.spyOrMockCall = spyOrMockCall;
            this.mockTypeOrObject = getFirstArgument(spyOrMockCall);
            this.editor = editor;
            this.project = project;
        }

        Document getDocument() {
            return editor.getDocument();
        }
    }
}
