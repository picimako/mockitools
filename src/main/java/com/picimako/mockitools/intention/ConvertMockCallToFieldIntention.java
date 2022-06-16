//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.google.common.collect.Iterables.getLast;
import static com.picimako.mockitools.MockitoQualifiedNames.ANSWER;
import static com.picimako.mockitools.MockitoQualifiedNames.DEFAULT_ANSWER;
import static com.picimako.mockitools.MockitoQualifiedNames.EXTRA_INTERFACES;
import static com.picimako.mockitools.MockitoQualifiedNames.LENIENT;
import static com.picimako.mockitools.MockitoQualifiedNames.NAME;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ANSWER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ANSWERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK_SERIALIZABLE_MODE;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK_SETTINGS;
import static com.picimako.mockitools.MockitoQualifiedNames.SERIALIZABLE;
import static com.picimako.mockitools.MockitoQualifiedNames.STUB_ONLY;
import static com.picimako.mockitools.MockitoolsPsiUtil.MOCKITO_MOCK;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockableTypeInAnyWay;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromLast;
import static com.picimako.mockitools.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.PsiMethodUtil.getArguments;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.hasOneArgument;
import static com.picimako.mockitools.PsiMethodUtil.hasTwoArguments;
import static com.picimako.mockitools.PsiMethodUtil.isIdentifierOfMethodCall;
import static com.picimako.mockitools.inspection.ClassObjectAccessUtil.getOperandType;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;
import static java.util.stream.Collectors.joining;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.picimako.mockitools.MockitoQualifiedNames;
import com.picimako.mockitools.PsiClassUtil;
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
public class ConvertMockCallToFieldIntention extends ConvertCallToFieldIntentionBase {
    private static final String JAVA_LANG_CLASS = "java.lang.Class<T>";
    private static final CallMatcher MOCK = MOCKITO_MOCK.parameterTypes(JAVA_LANG_CLASS);
    private static final CallMatcher MOCK_WITH_NAME = MOCKITO_MOCK.parameterTypes(JAVA_LANG_CLASS, CommonClassNames.JAVA_LANG_STRING);
    private static final CallMatcher MOCK_WITH_ANSWER = MOCKITO_MOCK.parameterTypes(JAVA_LANG_CLASS, ORG_MOCKITO_ANSWER);
    private static final CallMatcher MOCK_WITH_SETTINGS = MOCKITO_MOCK.parameterTypes(JAVA_LANG_CLASS, ORG_MOCKITO_MOCK_SETTINGS);
    private static final CallMatcher MOCKITO_WITH_SETTINGS = staticCall(ORG_MOCKITO_MOCKITO, "withSettings");

    private static final CallMatcher MOCK_SETTINGS_SERIALIZABLE_WITH_MODE = instanceCall(ORG_MOCKITO_MOCK_SETTINGS, SERIALIZABLE).parameterTypes(ORG_MOCKITO_MOCK_SERIALIZABLE_MODE);
    private static final Set<String> SUPPORTED_MOCK_SETTINGS_METHODS = Set.of(DEFAULT_ANSWER, STUB_ONLY, NAME, EXTRA_INTERFACES, LENIENT, "strictness");

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
        if (!file.getFileType().equals(JavaFileType.INSTANCE)) return false;

        final PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        if (isIdentifierOfMethodCall(element)) {
            var methodCall = (PsiMethodCallExpression) element.getParent().getParent();
            if (!MockitoQualifiedNames.MOCK.equals(getMethodName(methodCall))) return false;

            var mockTypeArg = getFirstArgument(methodCall);
            if (!(mockTypeArg instanceof PsiClassObjectAccessExpression) || !isMockableTypeInAnyWay(getOperandType(mockTypeArg)))
                return false;
            if (MOCK.matches(methodCall)) return hasOneArgument(methodCall);
            if (MOCK_WITH_NAME.matches(methodCall) || MOCK_WITH_ANSWER.matches(methodCall))
                return hasTwoArguments(methodCall);
            if (MOCK_WITH_SETTINGS.matches(methodCall))
                return hasTwoArguments(methodCall) && isSettingsSupportedByMockAnnotation(get2ndArgument(methodCall));
        }
        return false;
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
            if (MOCKITO_WITH_SETTINGS.matches(getLast(settingsCalls))) {
                return settingsCalls.stream()
                    .limit(settingsCalls.size() - 1L) //Skip analyzing the Mockito.withSettings() call as it has already been checked
                    .allMatch(call -> {
                        String methodName = getMethodName(call);
                        return SUPPORTED_MOCK_SETTINGS_METHODS.contains(methodName) || (SERIALIZABLE.equals(methodName) && !MOCK_SETTINGS_SERIALIZABLE_WITH_MODE.matches(call));
                    });
            }
        }
        return false;
    }

    //Conversion

    @Override
    protected PsiElement createField(String fieldType, String fieldName, Supplier<String> initializer, ConversionContext ctx) {
        String mockFieldText = "@" + MockitoQualifiedNames.ORG_MOCKITO_MOCK + " " + fieldType + " " + fieldName + ";";
        PsiField mockField = (PsiField) JavaCodeStyleManager.getInstance(ctx.project)
            .shortenClassReferences(JavaPsiFacade.getElementFactory(ctx.project).createFieldFromText(mockFieldText, ctx.targetClass));
        if (hasTwoArguments(ctx.spyOrMockCall)) {
            setMockAnnotationAttributes(mockField, ctx);
        }
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
        var mockAnnotation = field.getAnnotation(ORG_MOCKITO_MOCK);
        var configArg = get2ndArgument(ctx.spyOrMockCall);
        var configurer = new MockSettingsBasedAnnotationConfigurer(ctx.project, mockAnnotation);

        if (MOCK_WITH_NAME.matches(ctx.spyOrMockCall)) configurer.configureName(configArg.getText());
        else if (MOCK_WITH_ANSWER.matches(ctx.spyOrMockCall)) configurer.configureAnswer(configArg);
        else if (MOCK_WITH_SETTINGS.matches(ctx.spyOrMockCall))
            configureFromMockSettings(configurer, (PsiMethodCallExpression) configArg);
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
            if (STUB_ONLY.equals(methodName) || LENIENT.equals(methodName) || SERIALIZABLE.equals(methodName))
                configurer.configureBooleanAttribute(methodName);
            else if (NAME.equals(methodName)) configurer.configureName(call);
            else if (DEFAULT_ANSWER.equals(methodName)) configurer.configureAnswerFromCall(call);
            else if (EXTRA_INTERFACES.equals(methodName)) configurer.configureExtraInterfaces(call);
            else if ("strictness".equals(methodName)) configurer.configureStrictness(call);
        }
    }

    /**
     * Returns whether the argument Answer expression is a reference to {@code org.mockito.Answers.RETURNS_DEFAULTS}.
     */
    public static boolean isDefaultAnswer(PsiExpression answer) {
        return answer instanceof PsiReferenceExpression
            && isEnumConstant(((PsiReferenceExpression) answer).resolve(), ORG_MOCKITO_ANSWERS, "RETURNS_DEFAULTS");
    }

    private static boolean isEnumConstant(PsiElement constant, String enumClassName, String enumConstantName) {
        return constant instanceof PsiEnumConstant
            && enumClassName.equals(((PsiEnumConstant) constant).getContainingClass().getQualifiedName())
            && enumConstantName.equals(((PsiEnumConstant) constant).getName());
    }

    /**
     * Helper class to set the attributes of the @Mock annotation being created.
     */
    private static final class MockSettingsBasedAnnotationConfigurer {
        private final Project project;
        private final PsiAnnotation mockAnnotation;

        private MockSettingsBasedAnnotationConfigurer(Project project, PsiAnnotation mockAnnotation) {
            this.project = project;
            this.mockAnnotation = mockAnnotation;
        }

        private void configureBooleanAttribute(String methodName) {
            mockAnnotation.setDeclaredAttributeValue(methodName, attributeValue("true"));
        }

        private void configureName(PsiMethodCallExpression call) {
            configureName(getFirstArgument(call).getText());
        }

        private void configureName(String name) {
            mockAnnotation.setDeclaredAttributeValue(NAME, attributeValue(name));
        }

        private void configureAnswerFromCall(PsiMethodCallExpression call) {
            configureAnswer(getFirstArgument(call));
        }

        private void configureAnswer(PsiExpression answer) {
            if (!isDefaultAnswer(answer)) {
                mockAnnotation.setDeclaredAttributeValue(ANSWER, attributeValue(answer.getText()));
            }
        }

        private void configureExtraInterfaces(PsiMethodCallExpression call) {
            var arguments = getArguments(call);
            if (arguments.length == 1) {
                mockAnnotation.setDeclaredAttributeValue(EXTRA_INTERFACES, attributeValue(arguments[0].getText()));
            } else {
                String interfaces = Arrays.stream(arguments).map(PsiElement::getText).collect(joining(","));
                mockAnnotation.setDeclaredAttributeValue(EXTRA_INTERFACES, attributeValue("{" + interfaces + "}"));
            }
        }

        private void configureStrictness(PsiMethodCallExpression call) {
            var strictness = getFirstArgument(call);
            //null value passed into MockSettings.strictness() is not handled since it is invalid anyway.
            if (strictness instanceof PsiReferenceExpression) {
                PsiElement resolved = ((PsiReferenceExpression) strictness).resolve();
                if (resolved instanceof PsiEnumConstant) {
                    PsiEnumConstant constant = (PsiEnumConstant) resolved;
                    String strictnessName = constant.getName();
                    PsiClassUtil.importClass("org.mockito.Mock.Strictness", mockAnnotation);
                    mockAnnotation.setDeclaredAttributeValue("strictness", attributeValue("Mock.Strictness." + strictnessName));
                }
            }
        }

        @NotNull
        private PsiExpression attributeValue(String text) {
            return JavaPsiFacade.getElementFactory(project).createExpressionFromText(text, mockAnnotation);
        }
    }
}
