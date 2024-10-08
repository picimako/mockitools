//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.intellij.openapi.application.ReadAction.compute;
import static com.picimako.mockitools.MockableTypesUtil.isMockableTypeInAnyWay;
import static com.picimako.mockitools.MockitoQualifiedNames.SPY;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoSpy;
import static com.picimako.mockitools.util.ClassObjectAccessUtil.getOperandType;
import static com.picimako.mockitools.util.PsiMethodUtil.getMethodCallAtCaretOrEmpty;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.picimako.mockitools.MockitoQualifiedNames;
import com.picimako.mockitools.util.PsiMethodUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Converts {@code Mockito.spy()} calls to {@code @Spy} annotated fields.
 * <p>
 * The intention is available
 * <ul>
 *      <li>on Mockito.spy() calls,</li>
 *      <li>when the argument of the call is either a new expression, or a class object access expression,
 *      and it is not an array creation, or when the type is generic inferred and there is no call argument,</li>
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
 * Clazz localVar = spy(new Clazz()) -> @Spy Clazz localVar;
 * Clazz localVar = spy(new Clazz(&lt;arguments>)) -> @Spy Clazz localVar = new Clazz(&lt;arguments>);
 * Clazz&lt;typeargs> localVar = spy(new Clazz&lt;typeargs>()) -> @Spy Clazz&lt;typeargs> localVar;
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
 * NOTE: The format {@code spy(<reference to already created object>)} where the object passed in to the spy() call is created outside
 * the call is not yet supported.
 *
 * @see ConvertMockCallToFieldIntention
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Spy.html">@Spy annotation javadoc</a>
 * @since 0.2.0
 */
final class ConvertSpyCallToFieldIntention extends ConvertCallToFieldIntentionBase {

    public ConvertSpyCallToFieldIntention() {
        super(SPY, "@Spy");
    }

    //Availability

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        var methodCallAtCaret = getMethodCallAtCaretOrEmpty(file, editor);
        if (methodCallAtCaret.isPresent() && isMockitoSpy(methodCallAtCaret.get())) {
            return methodCallAtCaret
                .filter(PsiMethodUtil::hasOneArgument)
                .map(PsiMethodUtil::getFirstArgument)
                .map(spiedTypeOrInstance -> spiedTypeOrInstance instanceof PsiNewExpression newSpiedInstance
                                            //e.g. Mockito.spy(new ObjectToSpy())
                                            ? compute(() -> !newSpiedInstance.isArrayCreation()) && isMockableTypeInAnyWay(spiedTypeOrInstance.getType())
                                            //e.g. Mockito.spy(ObjectToSpy.class)
                                            : isMockableTypeInAnyWay(getOperandType(spiedTypeOrInstance)))
                //Generic inferred Mockito.spy()
                .orElseGet(() -> {
                    var parentVariable = PsiTreeUtil.getParentOfType(methodCallAtCaret.get(), PsiLocalVariable.class);
                    return parentVariable != null
                           //SomeObject someVariable = ...;
                           && !parentVariable.getTypeElement().isInferredType()
                           && isMockableTypeInAnyWay(compute(parentVariable::getType));
                });
        }

        return false;
    }

    //Conversion

    /**
     * Introduces the field taking into account that the Mockito.spy() call's argument is a new expression, so that the field type and name are constructed properly.
     */
    @Override
    protected void introduceFieldForNewExpression(ConversionContext ctx) {
        var newExpression = (PsiNewExpression) ctx.mockTypeOrObject;
        introduceField(ctx, () -> constructFieldTypeAndName(compute(newExpression::getClassReference)), getInitializer(newExpression));
    }

    /**
     * Returns the initializer of the to be created @Spy field. If the called constructor is the default one, no initializer is necessary,
     * otherwise the whole constructor call will be copied.
     */
    private Supplier<String> getInitializer(PsiNewExpression newExpression) {
        return isDefaultConstructor(newExpression) ? NO_INITIALIZER : newExpression::getText;
    }

    /**
     * Returns whether the argument expression is a default constructor.
     * <p>
     * It is either an explicitly defined one, or an auto-generated one when the instantiated type has no non-default constructor.
     */
    private static boolean isDefaultConstructor(@NotNull PsiNewExpression newExpression) {
        PsiMethod constructor = compute(newExpression::resolveConstructor); //in case of auto-generated default constructor it returns null
        //new Clazz() + explicit Clazz() -> true
        //new Clazz() + Clazz(String) -> false
        //new Clazz("") + Clazz(String) -> false
        //new Clazz("") + Clazz(int) -> false
        //new Clazz("") + Clazz() + Clazz(int) -> false
        if (constructor != null) {
            return compute(() -> constructor.getParameterList().isEmpty()
                                 && (constructor.hasModifierProperty(PsiModifier.PUBLIC) || constructor.hasModifierProperty(PsiModifier.PACKAGE_LOCAL)));
        }
        //new Clazz() + auto-gen Clazz() -> true
        //new Clazz() + Clazz(String) + Clazz(int) -> false
        //new Clazz("") + Clazz() + Clazz(int) + Clazz(double) -> false
        return Optional.ofNullable(compute(newExpression::getClassReference))
            .map(ref -> compute(ref::resolve))
            .map(PsiClass.class::cast)
            .map(PsiClass::getConstructors)
            .stream()
            .flatMap(Arrays::stream)
            .noneMatch(constr -> !compute(() -> constr.getParameterList().isEmpty()));
    }

    /**
     * Creates the @Spy field that will be introduced in the selected class.
     * <p>
     * An initializer is also added if necessary.
     *
     * @param initializer if no initializer is needed, pass in {@link #NO_INITIALIZER}
     */
    @Override
    protected PsiElement createField(String fieldType, String fieldName, Supplier<String> initializer, ConversionContext ctx) {
        StringBuilder sb = new StringBuilder("@" + MockitoQualifiedNames.ORG_MOCKITO_SPY + " " + fieldType + " " + fieldName);
        if (initializer != NO_INITIALIZER) {
            sb.append(" = ").append(compute(initializer::get));
        }
        return compute(() -> JavaCodeStyleManager.getInstance(ctx.project)
            .shortenClassReferences(JavaPsiFacade.getElementFactory(ctx.project).createFieldFromText(sb.append(";").toString(), ctx.targetClass)));
    }
}
