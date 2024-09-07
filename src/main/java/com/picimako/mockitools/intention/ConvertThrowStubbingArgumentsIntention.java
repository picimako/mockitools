//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.picimako.mockitools.util.PsiMethodUtil.containsCallToNonDefaultConstructor;
import static com.picimako.mockitools.util.PsiMethodUtil.getArguments;
import static com.picimako.mockitools.util.PsiMethodUtil.getMethodCallAtCaret;
import static com.picimako.mockitools.util.PsiMethodUtil.getMethodCallAtCaretOrEmpty;
import static com.picimako.mockitools.inspection.consecutive.TypeConversionMethod.TO_CLASSES;
import static com.picimako.mockitools.inspection.consecutive.TypeConversionMethod.TO_THROWABLES;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import com.intellij.util.IncorrectOperationException;
import com.picimako.mockitools.StubbingApproach;
import com.picimako.mockitools.inspection.stubbing.ExceptionStubber;
import com.picimako.mockitools.inspection.consecutive.TypeConversionMethod;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Converts arguments of {@code *Throw()} stubbing calls from {@link PsiClassObjectAccessExpression}s to {@link PsiNewExpression}s
 * and vice versa.
 * <p>
 * The intention is available only when either all arguments are {@link PsiClassObjectAccessExpression}s or all are {@link PsiNewExpression}s,
 * and in case of the latter one there is no call to a non-default constructor.
 * <p>
 * All stubbing approaches are supported:
 * <ul>
 *     <li>Mockito.when().thenThrow()</li>
 *     <li>BDDMockito.given().willThrow()</li>
 *     <li>Mockito.doThrow().when()</li>
 *     <li>Mockito.do*().doThrow().when()</li>
 *     <li>BDDMockito.willl*).willThrow().given()</li>
 *     <li>BDDMockito.willThrow().given()</li>
 * </ul>
 *
 * @since 0.4.0
 */
final class ConvertThrowStubbingArgumentsIntention implements IntentionAction {
    private String message;

    @Override
    public @IntentionName @NotNull String getText() {
        return message != null
            ? MockitoolsBundle.message("intention.convert.throw.arguments.to.x", message)
            : MockitoolsBundle.message("intention.convert.throw.arguments.generic");
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return MockitoolsBundle.message("intention.convert.throw.arguments.to.x.family");
    }

    //Availability

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return getMethodCallAtCaretOrEmpty(file, editor)
            .map(call -> StubbingApproach.findExceptionStubberApplicableTo(call)
                .map(stubber -> isArgumentListConvertible(call, stubber))
                .findFirst()
                .orElse(false))
            .orElse(false);
    }

    private boolean isArgumentListConvertible(PsiMethodCallExpression call, ExceptionStubber stubber) {
        if (ReadAction.compute(() -> stubber.classMatcher.matches(call))) {
            if (areAllClassObjectAccessExpressions(getArguments(call))) {
                message = TO_THROWABLES.message;
                return true;
            }
        } else if (ReadAction.compute(() -> stubber.throwablesMatcher.matches(call))) {
            var arguments = getArguments(call);
            if (areAllNewExpressions(arguments) && !containsCallToNonDefaultConstructor(arguments)) {
                message = TO_CLASSES.message;
                return true;
            }
        }
        return false;
    }

    //Conversion

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            var arguments = getArguments(getMethodCallAtCaret(file, editor));
            if (areAllClassObjectAccessExpressions(arguments)) convert(arguments, TO_THROWABLES);
            else if (areAllNewExpressions(arguments)) convert(arguments, TO_CLASSES);
        });
    }

    private void convert(PsiExpression[] arguments, TypeConversionMethod conversionMethod) {
        for (var argument : arguments) {
            argument.replace(conversionMethod.convert(argument));
        }
    }

    private boolean areAllNewExpressions(PsiExpression[] expressions) {
        return Arrays.stream(expressions).allMatch(PsiNewExpression.class::isInstance);
    }

    private boolean areAllClassObjectAccessExpressions(PsiExpression[] expressions) {
        return Arrays.stream(expressions).allMatch(PsiClassObjectAccessExpression.class::isInstance);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
