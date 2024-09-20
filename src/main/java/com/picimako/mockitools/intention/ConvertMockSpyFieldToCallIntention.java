//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.intellij.openapi.application.ReadAction.compute;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_SPY;
import static com.picimako.mockitools.intention.ConvertMockSpyFieldToCallAction.introduceMockitoMockingCall;
import static com.picimako.mockitools.intention.MethodRearranger.reOrder;
import static com.picimako.mockitools.util.ListPopupHelper.showActionsInListPopup;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.util.IncorrectOperationException;
import com.picimako.mockitools.MockitoQualifiedNames;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

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
final class ConvertMockSpyFieldToCallIntention implements IntentionAction {

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
            showActionsInListPopup(
                MockitoolsBundle.message("intention.convert.mocking.field.to.call.select.method"),
                reOrder(methodsInClass).stream().map(method -> new ConvertMockSpyFieldToCallAction(field, method)).toList(),
                editor);
        } else if (methodsInClass.length == 1) {
            introduceMockitoMockingCall(field, methodsInClass[0], file);
        }
    }

    private boolean isIdentifierOfField(PsiElement element) {
        return element instanceof PsiIdentifier && compute(element::getParent) instanceof PsiField;
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
