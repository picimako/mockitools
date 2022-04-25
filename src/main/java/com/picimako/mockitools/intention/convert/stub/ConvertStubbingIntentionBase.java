//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoGiven;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoWillX;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoDoX;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoWhen;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.isIdentifierOfMethodCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.Arrays;
import java.util.Set;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Base class for intention actions that convert call chains between the different stubbing approaches.
 *
 * @see ConvertStubbingToMockitoWhenIntention
 * @see ConvertStubbingToMockitoDoIntention
 * @see ConvertStubbingToBDDMockitoGivenIntention
 * @see ConvertStubbingToBDDMockitoWillIntention
 * @since 0.4.0
 */
public abstract class ConvertStubbingIntentionBase implements IntentionAction {

    private final String targetApproachName;

    protected ConvertStubbingIntentionBase(String targetApproachName) {
        this.targetApproachName = targetApproachName;
    }

    //Intention names

    @Override
    public @IntentionName @NotNull String getText() {
        return MockitoolsBundle.message("intention.convert.stubbing.to.x", targetApproachName);
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return MockitoolsBundle.message("intention.convert.stubbing.to.x", targetApproachName);
    }

    /**
     * Returns the stubbing descriptor for the target approach.
     */
    protected abstract StubbingDescriptor targetDescriptor();

    //Availability

    /**
     * Returns whether the result of this intention would be the same as after invoking it on the provided method call.
     * <p>
     * This check is performed by a simple method name check, whether it is one of the names that constitutes as the same approach.
     */
    protected boolean isConversionToItself(PsiMethodCallExpression call) {
        return itselfMethodNames().contains(getMethodName(call));
    }

    /**
     * Returns the method names that identify this stubbing approach.
     */
    protected abstract Set<String> itselfMethodNames();

    /**
     * Returns whether the call chain started by the provided method call matches all specified analyzers.
     */
    protected boolean isCallChainMatch(PsiMethodCallExpression firstCallInChain, CallChainAnalyzer... analyzers) {
        if (analyzers == null || analyzers.length == 0) return true;
        var calls = collectCallsInChainFromFirst(firstCallInChain);
        return Arrays.stream(analyzers).allMatch(analyzer -> analyzer.analyze(calls));
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!file.getFileType().equals(JavaFileType.INSTANCE)) return false;

        final PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        if (isIdentifierOfMethodCall(element)) {
            var methodCall = (PsiMethodCallExpression) element.getParent().getParent();

            return !isConversionToItself(methodCall) && isAvailableFor(methodCall);
        }
        return false;
    }

    /**
     * Returns if this intention should be available for the provided method call.
     */
    protected abstract boolean isAvailableFor(PsiMethodCallExpression methodCall);

    //Conversion

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        final PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        var methodCall = (PsiMethodCallExpression) element.getParent().getParent();

        StubbingConverter converter = new StubbingConverter(project, editor, file);

        if (isMockitoWhen(methodCall)) converter.convert(methodCall, ConvertStubbingToMockitoWhenIntention.TARGET_DESCRIPTOR, targetDescriptor());
        if (isMockitoDoX(methodCall)) converter.convert(methodCall, ConvertStubbingToMockitoDoIntention.TARGET_DESCRIPTOR, targetDescriptor());
        if (isBDDMockitoGiven(methodCall)) converter.convert(methodCall, ConvertStubbingToBDDMockitoGivenIntention.TARGET_DESCRIPTOR, targetDescriptor());
        if (isBDDMockitoWillX(methodCall)) converter.convert(methodCall, ConvertStubbingToBDDMockitoWillIntention.TARGET_DESCRIPTOR, targetDescriptor());
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
