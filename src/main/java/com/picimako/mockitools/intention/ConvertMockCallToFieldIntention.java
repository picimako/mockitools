//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.google.common.collect.Iterables.getLast;
import static com.intellij.openapi.application.ReadAction.compute;
import static com.intellij.openapi.application.ReadAction.run;
import static com.picimako.mockitools.MockableTypesUtil.isMockableTypeInAnyWay;
import static com.picimako.mockitools.MockitoMockMatchers.MOCK_WITH_ANSWER;
import static com.picimako.mockitools.MockitoMockMatchers.MOCK_WITH_NAME;
import static com.picimako.mockitools.MockitoMockMatchers.MOCK_WITH_SETTINGS;
import static com.picimako.mockitools.MockitoQualifiedNames.ANSWER;
import static com.picimako.mockitools.MockitoQualifiedNames.DEFAULT_ANSWER;
import static com.picimako.mockitools.MockitoQualifiedNames.EXTRA_INTERFACES;
import static com.picimako.mockitools.MockitoQualifiedNames.LENIENT;
import static com.picimako.mockitools.MockitoQualifiedNames.MOCK_MAKER;
import static com.picimako.mockitools.MockitoQualifiedNames.NAME;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK_SERIALIZABLE_MODE;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK_SETTINGS;
import static com.picimako.mockitools.MockitoQualifiedNames.SERIALIZABLE;
import static com.picimako.mockitools.MockitoQualifiedNames.STRICTNESS;
import static com.picimako.mockitools.MockitoQualifiedNames.STUB_ONLY;
import static com.picimako.mockitools.MockitoQualifiedNames.WITHOUT_ANNOTATIONS;
import static com.picimako.mockitools.util.ClassObjectAccessUtil.getOperandType;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromLast;
import static com.picimako.mockitools.util.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getArguments;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getMethodCallAtCaretOrEmpty;
import static com.picimako.mockitools.util.PsiMethodUtil.hasOneArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.hasTwoArguments;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;
import static java.util.stream.Collectors.joining;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.picimako.mockitools.MockitoMockMatchers;
import com.picimako.mockitools.MockitoQualifiedNames;
import com.picimako.mockitools.util.PsiClassUtil;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Converts {@code Mockito.mock()} calls to {@code @Mock} annotated fields.
 * <p>
 * <strong>Some examples:</strong>
 * <pre>
 * mock(Clazz.class) -> @Mock Clazz clazz;
 * mock(Clazz.class, "some name") -> @Mock(name = "some name") Clazz clazz;
 * mock(Clazz.class, Answers.RETURNS_SMART_NULLS) -> @Mock(answer = Answers.RETURNS_SMART_NULLS) Clazz clazz;
 * mock(Clazz.class, Mockito.withSettings().lenient()) -> @Mock(lenient = true) Clazz clazz;
 * mock(Clazz.class, Mockito.withSettings().strictness(Strictness.WARN)) -> @Mock(strictness = Mock.Strictness.WARN) Clazz clazz;
 *
 * Clazz localVar = mock(Clazz.class) -> @Mock Clazz localVar;
 * Clazz&lt;typeargs> localVar = mock(Clazz.class) -> @Mock Clazz&lt;typeargs> localVar;
 * </pre>
 * <p>
 * <strong>Naming</strong>
 * <ul>
 *     <li>if the {@code Mockito.mock()} call is part of a local variable declaration, then by default will use the variable's name,
 *     but if there is already a field with the same name in the target class, a rename refactor is invoked first.</li>
 *     <li>if the call is not part of a local variable declaration, a rename refactor is invoked first, where the default field name provided is the
 *     mock type's name in lowercase format.</li>
 * </ul>
 * <strong>Target class selection</strong>
 * <p>
 * If there is more than one parent class of the selected mock() call, a list is shown from which the class where the field will be introduced, can be selected.
 * <p>
 * If within a {@code Mockito.withSettings()} call chain there are multiple of the same configurations called, it is the last one from each specific configuration
 * that will be included in the @Mock annotation attributes.
 *
 * @see ConvertSpyCallToFieldIntention
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mock.html">@Mock annotation javadoc</a>
 * @since 0.4.0
 */
final class ConvertMockCallToFieldIntention extends ConvertCallToFieldIntentionBase {
    private static final CallMatcher MOCKITO_WITH_SETTINGS = staticCall(ORG_MOCKITO_MOCKITO, "withSettings");

    private static final CallMatcher MOCK_SETTINGS_SERIALIZABLE_WITH_MODE = instanceCall(ORG_MOCKITO_MOCK_SETTINGS, SERIALIZABLE).parameterTypes(ORG_MOCKITO_MOCK_SERIALIZABLE_MODE);
    /**
     * {@code MockSettings.spiedInstance()} could be a special case for converting into a {@code @Spy} field,
     * but {@code @Mock} doesn't support such configuration, and there is also the actual object instance that is passed in,
     * because of which the field could not be created.
     */
    private static final Set<String> SUPPORTED_MOCK_SETTINGS_METHODS =
        Set.of(DEFAULT_ANSWER, STUB_ONLY, NAME, EXTRA_INTERFACES, LENIENT, STRICTNESS, MOCK_MAKER, WITHOUT_ANNOTATIONS);

    public ConvertMockCallToFieldIntention() {
        super(MockitoQualifiedNames.MOCK, "@Mock");
    }

    //Availability

    /**
     * The intention is available
     * <ul>
     *      <li>on {@code Mockito.mock(Class)}, {@code Mockito.mock(Class, String)}, {@code Mockito.mock(Class, Answer)}
     *      and {@code Mockito.mock(Class, MockSettings)} calls,</li>
     *      <li>when the type that is being mocked can actually be mocked (by Mockito's rules)/allowed to be mocked (by the @DoNotMock annotation),</li>
     *      <li>and in case of the {@code MockSettings} specific overload, if the @Mock annotation supports all configuration specified.</li>
     * </ul>
     */
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return getMethodCallAtCaretOrEmpty(file, editor)
            .filter(call -> MockitoQualifiedNames.MOCK.equals(getMethodName(call)))
            .map(call -> compute(() -> {
                var mockTypeArg = getFirstArgument(call);
                //Mockito.mock(<type>, ...)
                if (mockTypeArg != null) {
                    if (isMockableTypeInAnyWay(getOperandType(mockTypeArg))) {
                        if (MockitoMockMatchers.MOCK.matches(call)) return hasOneArgument(call);
                        if (MOCK_WITH_NAME.matches(call) || MOCK_WITH_ANSWER.matches(call))
                            return hasTwoArguments(call);
                        if (MOCK_WITH_SETTINGS.matches(call))
                            return hasTwoArguments(call) && isSettingsSupportedByMockAnnotation(get2ndArgument(call));
                    }
                }
                //Generic inferred Mockito.mock()
                else {
                    var parentVariable = PsiTreeUtil.getParentOfType(call, PsiLocalVariable.class);
                    return parentVariable != null
                           //SomeObject someVariable = ...;
                           && !parentVariable.getTypeElement().isInferredType()
                           && isMockableTypeInAnyWay(parentVariable.getType());
                }
                return false;
            }))
            .orElse(false);
    }

    /**
     * The annotation supports the specified {@code MockSettings} if
     * <ul>
     *     <li>it starts with the {@code Mockito.withSettings()} call,</li>
     *     <li>it doesn't have a call other than to {@code lenient()}, {@code stubOnly()}, {@code defaultAnswer()}, {@code name()}, {@code extraInterfaces()}
     *     or {@code serializable()} but not its overloaded variant {@code serializable(SerializableMode)}.</li>
     * </ul>
     */
    private boolean isSettingsSupportedByMockAnnotation(PsiExpression settingsArg) {
        if (settingsArg instanceof PsiMethodCallExpression) {
            var settingsCalls = collectCallsInChainFromLast(settingsArg);
            return MOCKITO_WITH_SETTINGS.matches(getLast(settingsCalls))
                   && settingsCalls.stream()
                       .limit(settingsCalls.size() - 1L) //Skip analyzing the Mockito.withSettings() call as it has already been checked
                       .allMatch(call -> {
                           String methodName = getMethodName(call);
                           return SUPPORTED_MOCK_SETTINGS_METHODS.contains(methodName)
                                  || (SERIALIZABLE.equals(methodName) && !MOCK_SETTINGS_SERIALIZABLE_WITH_MODE.matches(call));
                       });
        }
        return false;
    }

    //Conversion

    @Override
    protected PsiElement createField(String fieldType, String fieldName, Supplier<String> initializer, ConversionContext ctx) {
        String mockFieldText = "@" + MockitoQualifiedNames.ORG_MOCKITO_MOCK + " " + fieldType + " " + fieldName + ";";
        PsiField mockField = (PsiField) compute(() -> JavaCodeStyleManager.getInstance(ctx.project)
            .shortenClassReferences(JavaPsiFacade.getElementFactory(ctx.project).createFieldFromText(mockFieldText, ctx.targetClass)));

        if (hasTwoArguments(ctx.spyOrMockCall)) setMockAnnotationAttributes(mockField, ctx);
        return mockField;
    }

    /**
     * Configures the @Mock annotation based on {@code Mockito.mock()} calls that have the second String, Answer or MockSettings argument specified.
     * <p>
     * In case of answers (whether via {@code Mockito.mock(Class, Answer)} or {@code Mockito.mock(Class, MockSettings)}),
     * if the specified Answer is {@code org.mockito.Answers.RETURNS_DEFAULTS}, then the @Mock annotation won't specify explicitly this value:
     * <pre>
     * Mockito.mock(Type.class, Answers.RETURNS_DEFAULTS) -> @Mock
     * Mockito.mock(Type.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_DEFAULTS)) -> @Mock
     * </pre>
     */
    private void setMockAnnotationAttributes(PsiField field, ConversionContext ctx) {
        run(() -> {
            var configArg = get2ndArgument(ctx.spyOrMockCall);
            var configurer = new MockSettingsBasedAnnotationConfigurer(ctx.project, field.getAnnotation(ORG_MOCKITO_MOCK));

            if (MOCK_WITH_NAME.matches(ctx.spyOrMockCall)) configurer.configureName(configArg.getText());
            else if (MOCK_WITH_ANSWER.matches(ctx.spyOrMockCall)) configurer.configureAnswer(configArg);
            else if (MOCK_WITH_SETTINGS.matches(ctx.spyOrMockCall))
                configureFromMockSettings(configurer, (PsiMethodCallExpression) configArg);
        });
    }

    /**
     * Configures the @Mock annotation based on a {@code Mockito.mock(Class, MockSettings)} call.
     */
    private void configureFromMockSettings(MockSettingsBasedAnnotationConfigurer configurer, PsiMethodCallExpression config) {
        var calls = collectCallsInChainFromLast(config);
        //Skips the withSettings() call because it doesn't have to be converted.
        //Goes through the collected calls in reverse order, to convert the calls in their original order.
        for (int i = calls.size() - 2; i >= 0; i--) {
            var call = calls.get(i);
            String methodName = getMethodName(call);
            if (STUB_ONLY.equals(methodName)
                || LENIENT.equals(methodName)
                || SERIALIZABLE.equals(methodName)
                || WITHOUT_ANNOTATIONS.equals(methodName))
                configurer.configureBooleanAttribute(methodName);
            else if (NAME.equals(methodName)) configurer.configureName(call);
            else if (DEFAULT_ANSWER.equals(methodName)) configurer.configureAnswerFromCall(call);
            else if (EXTRA_INTERFACES.equals(methodName)) configurer.configureExtraInterfaces(call);
            else if (STRICTNESS.equals(methodName)) configurer.configureStrictness(call);
            else if (MOCK_MAKER.equals(methodName)) configurer.configureMockMaker(call);
        }
    }

    /**
     * Returns whether the argument Answer expression is a reference to {@code org.mockito.Answers.RETURNS_DEFAULTS}.
     */
    public static boolean isDefaultAnswer(PsiExpression answer) {
        return answer instanceof PsiReferenceExpression answerExpr && isAnswersReturnDefaults(compute(answerExpr::resolve));
    }

    private static boolean isAnswersReturnDefaults(PsiElement element) {
        return element instanceof PsiEnumConstant constant
               && MockitoQualifiedNames.ORG_MOCKITO_ANSWERS.equals(constant.getContainingClass().getQualifiedName())
               && "RETURNS_DEFAULTS".equals(constant.getName());
    }

    /**
     * Helper class to set the attributes of the @Mock annotation being created.
     */
    private record MockSettingsBasedAnnotationConfigurer(Project project, PsiAnnotation mockAnnotation) {

        private void configureBooleanAttribute(String attributeName) {
            setDeclaredAttributeValue(attributeName, attributeValue("true"));
        }

        private void configureName(PsiMethodCallExpression call) {
            configureName(getFirstArgument(call).getText());
        }

        private void configureName(String name) {
            setDeclaredAttributeValue(NAME, attributeValue(name));
        }

        private void configureAnswerFromCall(PsiMethodCallExpression call) {
            configureAnswer(getFirstArgument(call));
        }

        private void configureAnswer(PsiExpression answer) {
            if (!isDefaultAnswer(answer)) {
                setDeclaredAttributeValue(ANSWER, attributeValue(answer.getText()));
            }
        }

        private void configureExtraInterfaces(PsiMethodCallExpression call) {
            var arguments = getArguments(call);
            if (arguments.length == 1) {
                setDeclaredAttributeValue(EXTRA_INTERFACES, attributeValue(arguments[0].getText()));
            } else {
                String interfaces = Arrays.stream(arguments).map(arg -> compute(arg::getText)).collect(joining(","));
                setDeclaredAttributeValue(EXTRA_INTERFACES, attributeValue("{" + interfaces + "}"));
            }
        }

        private void configureStrictness(PsiMethodCallExpression call) {
            var strictnessArg = getFirstArgument(call);
            //null value passed into MockSettings.strictness() is not handled since it is invalid anyway.
            if (strictnessArg instanceof PsiReferenceExpression strictnessExpr
                && compute(strictnessExpr::resolve) instanceof PsiEnumConstant strictness) {
                PsiClassUtil.importClass("org.mockito.Mock.Strictness", mockAnnotation);
                setDeclaredAttributeValue(STRICTNESS, attributeValue("Mock.Strictness." + strictness.getName()));
            }
        }

        public void configureMockMaker(PsiMethodCallExpression call) {
            setDeclaredAttributeValue(MOCK_MAKER, attributeValue(getFirstArgument(call).getText()));
        }

        @NotNull
        private PsiExpression attributeValue(String text) {
            return compute(() -> JavaPsiFacade.getElementFactory(project).createExpressionFromText(text, mockAnnotation));
        }

        private void setDeclaredAttributeValue(String attributeName, PsiExpression value) {
            run(() -> mockAnnotation.setDeclaredAttributeValue(attributeName, value));
        }
    }
}
