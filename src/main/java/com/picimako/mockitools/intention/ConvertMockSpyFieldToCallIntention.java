//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.intellij.openapi.application.ReadAction.compute;
import static com.intellij.openapi.application.ReadAction.run;
import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.picimako.mockitools.MockitoQualifiedNames.ANSWER;
import static com.picimako.mockitools.MockitoQualifiedNames.DEFAULT_ANSWER;
import static com.picimako.mockitools.MockitoQualifiedNames.EXTRA_INTERFACES;
import static com.picimako.mockitools.MockitoQualifiedNames.MOCK;
import static com.picimako.mockitools.MockitoQualifiedNames.MOCK_MAKER;
import static com.picimako.mockitools.MockitoQualifiedNames.NAME;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_QUALITY_STRICTNESS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_SPY;
import static com.picimako.mockitools.MockitoQualifiedNames.SPY;
import static com.picimako.mockitools.MockitoQualifiedNames.STRICTNESS;
import static com.picimako.mockitools.MockitoolsPsiUtil.MOCKITO_WITH_SETTINGS;
import static com.picimako.mockitools.dsl.MockAnnotation.isAttributeEnabledOnMockAnnotation;
import static com.picimako.mockitools.intention.ConvertMockCallToFieldIntention.isDefaultAnswer;
import static com.picimako.mockitools.intention.MethodRearranger.reOrder;
import static com.picimako.mockitools.util.ListPopupHelper.selectItemAndRun;
import static java.util.stream.Collectors.joining;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiQualifiedReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiTypeElement;
import com.intellij.util.IncorrectOperationException;
import com.picimako.mockitools.MockitoQualifiedNames;
import com.picimako.mockitools.intention.MethodRearranger.ClassMethodCellRenderer;
import com.picimako.mockitools.resources.MockitoolsBundle;
import com.picimako.mockitools.util.PsiClassUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Converts {@code @Mock} and {@code @Spy} annotated fields to {@code Mockito.mock()} and {@code Mockito.spy()} local variable declarations, respectively.
 * <p>
 * Attributes of the {@code @Mock} annotation are also taken into account when constructing the Mockito.mock() calls.
 * <p>
 * The created variable declaration is always added as the first statement to the selected method, which is selected according to this logic:
 * <ul>
 *     <li>if there is only one method in the class, then that is the target method</li>
 *     <li>if there are multiple methods in the class, then users are able to choose which method to introduce the variable in</li>
 * </ul>
 * In case of converting @Mock fields, default attribute values are ignored and not added to the result Mockito.mock() call.
 * <p>
 * NOTE: inner classes are not taken into consideration. Converting the field is possible only within the same class.
 * <p>
 * NOTE 2: the intention is not available when the field is annotated with both {@code @Mock} and {@code @Spy}.
 *
 * @since 0.2.0
 */
@SuppressWarnings("UnstableApiUsage")
final class ConvertMockSpyFieldToCallIntention implements IntentionAction {

    /**
     * Defines the annotation attributes and the conditions when they are allowed to be added to the Mockito.mock() call.
     * <p>
     * These are used for {@code Mockito.mock(Class, String)} and {@code Mockito.mock(Class, Answer)}.
     */
    private static final Map<String, Predicate<PsiAnnotationMemberValue>> MOCK_OVERLOAD_ARGS = Map.of(
        "answer", value -> value instanceof PsiReferenceExpression memberValue && !isDefaultAnswer(memberValue),
        "name", value -> !isBlank(value)
    );
    private static final Set<String> BOOLEAN_ATTRIBUTES = Set.of("stubOnly", "serializable", "lenient", "withoutAnnotations");
    private static final ClassMethodCellRenderer METHOD_CELL_RENDERER = new ClassMethodCellRenderer();

    @IntentionName
    private String mockingCall;

    @Override
    public @IntentionName @NotNull String getText() {
        return mockingCall != null
               ? MockitoolsBundle.message("intention.convert.mocking.field.to.call", mockingCall)
               : MockitoolsBundle.message("intention.convert.mocking.field.to.call.generic");
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return MockitoolsBundle.message("intention.convert.mocking.field.to.call.family");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return compute(() -> {
            final var element = file.findElementAt(editor.getCaretModel().getOffset());
            //If the caret is at a field identifier and the parent class has at least one method
            if (isIdentifierOfField(element)) {
                PsiField field = (PsiField) element.getParent();
                if (field.getContainingClass().getMethods().length == 0) return false;

                boolean hasMock;
                if ((hasMock = field.hasAnnotation(ORG_MOCKITO_MOCK)) && field.hasAnnotation(ORG_MOCKITO_SPY)) {
                    return false;
                }
                if (hasMock) {
                    mockingCall = MockitoQualifiedNames.MOCK;
                    return true;
                }
                if (field.hasAnnotation(ORG_MOCKITO_SPY)) {
                    mockingCall = MockitoQualifiedNames.SPY;
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        final var element = file.findElementAt(compute(() -> editor.getCaretModel().getOffset()));
        var field = (PsiField) compute(element::getParent);
        var methodsInClass = compute(() -> ((PsiClass) field.getParent()).getMethods());
        if (methodsInClass.length > 1) {
            selectItemAndRun(MockitoolsBundle.message("intention.convert.mocking.field.to.call.select.method"),
                reOrder(methodsInClass), selectedMethod -> introduceMockitoMockingCall(field, selectedMethod, file), () -> METHOD_CELL_RENDERER, editor, project);
        } else if (methodsInClass.length == 1) {
            introduceMockitoMockingCall(field, methodsInClass[0], file);
        }
    }

    /**
     * Adds the new variable declaration as the first statement of method body.
     */
    private void introduceMockitoMockingCall(PsiField fieldToConvert, PsiMethod targetMethod, PsiFile file) {
        final var mockitoMockingCall = new Ref<StringBuilder>();

        //This if-else is safe since isAvailable() returns true only in case of @Mock and @Spy annotations
        if (compute(() -> fieldToConvert.hasAnnotation(ORG_MOCKITO_MOCK))) {
            final var mockAnnotation = new Ref<PsiAnnotation>();
            final var isMockSettingsOverride = new Ref<Boolean>();

            run(() -> {
                //org.mockito.Mockito.mock(<fieldtype>.class
                mockitoMockingCall.set(new StringBuilder("Mockito").append(".").append(MOCK).append("("));
                appendType(fieldToConvert, mockitoMockingCall.get());

                mockAnnotation.set(fieldToConvert.getAnnotation(ORG_MOCKITO_MOCK));
                //Assemble Mockito.mock() call based on the @Mock annotation attributes
                isMockSettingsOverride.set(true);
                //Handle .mock(Class, Answer) and .mock(Class, String)
                if (mockAnnotation.get().getAttributes().size() == 1) {
                    for (var entry : MOCK_OVERLOAD_ARGS.entrySet()) {
                        var attributeValue = valueOf(mockAnnotation.get().findAttribute(entry.getKey()))
                            .filter(value -> entry.getValue().test(value));
                        if (attributeValue.isPresent()) {
                            mockitoMockingCall.get().append(", ").append(attributeValue.get().getText());
                            isMockSettingsOverride.set(false);
                            break;
                        }
                    }
                }
            });


            //if there is no attribute specified, no action is needed
            //If there is at least one attribute specified, but it is not the answer and name specific overrides of Mockito.mock(),
            // then build the MockSettings for Mockito.mock(Class, MockSettings)
            if (compute(() -> !mockAnnotation.get().getAttributes().isEmpty()) && isMockSettingsOverride.get()) {
                StringBuilder mockSettings = new StringBuilder("Mockito").append(".withSettings()");
                run(() -> {
                    //Handle boolean attributes
                    BOOLEAN_ATTRIBUTES.forEach(attributeName -> {
                        if (isAttributeEnabledOnMockAnnotation(mockAnnotation.get(), attributeName))
                            appendSetting(mockSettings, attributeName, "");
                    });

                    //Handle attributes for whose MockSettings counterpart the value of the attribute must be passed in.
                    //E.g. withSettings().name("some name") or withSettings().answer(anAnswer)
                    valueOf(mockAnnotation.get().findAttribute(NAME)).ifPresent(value -> {
                        if (MOCK_OVERLOAD_ARGS.get(NAME).test(value))
                            appendSetting(mockSettings, NAME, value.getText());
                    });
                    valueOf(mockAnnotation.get().findAttribute(ANSWER)).ifPresent(value -> {
                        if (MOCK_OVERLOAD_ARGS.get(ANSWER).test(value))
                            appendSetting(mockSettings, DEFAULT_ANSWER, value.getText());
                    });

                    //Handle extraInterfaces attribute.
                    //This needs special care because the attribute value may be an individual value or an array initializer.
                    valueOf(mockAnnotation.get().findAttribute(EXTRA_INTERFACES)).ifPresent(extraInterfacesValue -> {
                        if (extraInterfacesValue instanceof PsiArrayInitializerMemberValue attributeValue) {
                            //In case of empty extraInterfaces - @Mock(extraInterfaces = {}) - don't add the .extraInterfaces() call to the mock settings
                            var initializers = attributeValue.getInitializers();
                            if (initializers.length > 0) {
                                String interfaces = Arrays.stream(initializers).map(PsiElement::getText).collect(joining(","));
                                appendSetting(mockSettings, EXTRA_INTERFACES, interfaces);
                            }
                        } else {
                            appendSetting(mockSettings, EXTRA_INTERFACES, extraInterfacesValue.getText());
                        }
                    });
                });

                compute(() -> valueOf(mockAnnotation.get().findAttribute(STRICTNESS))
                    .filter(PsiReferenceExpression.class::isInstance)
                    .map(PsiReferenceExpression.class::cast)
                    .map(PsiReference::resolve)
                    .filter(PsiEnumConstant.class::isInstance)
                    .map(PsiEnumConstant.class::cast)
                    .map(PsiField::getName))
                    .ifPresent(strictnessName -> {
                        //Mock.Strictness.TEST_LEVEL_DEFAULT has no matching enum constant in Strictness, thus we'll ignore it.
                        if (!"TEST_LEVEL_DEFAULT".equals(strictnessName)) {
                            runWriteCommandAction(file.getProject(),
                                () -> PsiClassUtil.importClass(ORG_MOCKITO_QUALITY_STRICTNESS, mockAnnotation.get()));
                            appendSetting(mockSettings, STRICTNESS, "Strictness." + strictnessName);
                        }
                    });

                run(() -> valueOf(mockAnnotation.get().findAttribute(MOCK_MAKER)).ifPresent(value -> appendSetting(mockSettings, MOCK_MAKER, value.getText())));

                mockitoMockingCall.get().append(", ").append(mockSettings); //Adds the second parameter to Mockito.mock(Class, MockSettings)
            }
        } else { //@Spy -> Mockito.spy()
            run(() -> {
                mockitoMockingCall.set(new StringBuilder("Mockito").append(".").append(SPY).append("("));
                if (fieldToConvert.hasInitializer()) {
                    mockitoMockingCall.get().append(fieldToConvert.getInitializer().getText());
                } else {
                    appendType(fieldToConvert, mockitoMockingCall.get());
                }
            });
        }

        mockitoMockingCall.get().append(")");

        runWriteCommandAction(file.getProject(), () -> {
            var elementFactory = JavaPsiFacade.getElementFactory(file.getProject());
            PsiClassUtil.importClass(ORG_MOCKITO_MOCKITO, file);
            var mockitoMockingInitializer = elementFactory.createExpressionFromText(mockitoMockingCall.get().toString(), file);

            //Post-process variable initializer: if the second argument is a call to Mockito.withSettings() then it can be omitted.
            //E.g. mock(Type.class, Mockito.withSettings()) -> mock(Type.class)
            var finalMockingInitializer = (PsiMethodCallExpression) mockitoMockingInitializer;
            if (finalMockingInitializer.getArgumentList().getExpressionCount() > 1 && MOCKITO_WITH_SETTINGS.matches(finalMockingInitializer.getArgumentList().getExpressions()[1])) {
                finalMockingInitializer.getArgumentList().getExpressions()[1].delete();
            }

            //Introduces the variable declaration in the selected method
            //Deletes the field
            PsiDocumentManager.getInstance(file.getProject()).commitAllDocuments();
            var mockitoMockingVariableDeclaration = elementFactory.createVariableDeclarationStatement(fieldToConvert.getName(), fieldToConvert.getType(), finalMockingInitializer);
            PsiCodeBlock methodBody = targetMethod.getBody();
            if (methodBody != null) {
                if (methodBody.getFirstBodyElement() != null)
                    methodBody.addBefore(mockitoMockingVariableDeclaration, methodBody.getFirstBodyElement());
                else
                    methodBody.add(mockitoMockingVariableDeclaration);
            }
            fieldToConvert.delete();
        });
    }

    private static boolean isBlank(PsiAnnotationMemberValue value) {
        return value.getText().replace("\"", "").isBlank();
    }

    private boolean isIdentifierOfField(PsiElement element) {
        return element instanceof PsiIdentifier && compute(element::getParent) instanceof PsiField;
    }

    private Optional<PsiAnnotationMemberValue> valueOf(JvmAnnotationAttribute attribute) {
        return compute(() -> attribute instanceof PsiNameValuePair attributeNameValue && attributeNameValue.getValue() != null
                             ? Optional.ofNullable(attributeNameValue.getValue())
                             : Optional.empty());
    }

    private void appendSetting(StringBuilder sb, String methodName, String argument) {
        sb.append(".").append(methodName).append("(").append(argument).append(")");
    }

    /**
     * This is to make sure that the mock()/spy() call's argument (when a PsiClassObjectAccessExpression), doesn't include the generic type arguments.
     */
    private void appendType(PsiField fieldToConvert, StringBuilder mockitoMockingCall) {
        run(() -> {
            Optional.ofNullable(fieldToConvert.getTypeElement())
                .map(PsiTypeElement::getInnermostComponentReferenceElement)
                .map(PsiQualifiedReference::getReferenceName)
                .ifPresentOrElse(mockitoMockingCall::append, () -> mockitoMockingCall.append(fieldToConvert.getType().getCanonicalText()));
            mockitoMockingCall.append(".class");
        });
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
