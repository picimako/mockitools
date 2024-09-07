//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.intellij.openapi.application.ReadAction.compute;
import static com.picimako.mockitools.intention.convert.FromSelectionDataRetriever.collectStatementsInSelection;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.util.PsiMethodUtil.getMethodCallAtCaret;

import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.picimako.mockitools.CallChainAnalyzer;
import com.picimako.mockitools.intention.convert.ConversionIntentionBase;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Base class for intention actions that convert call chains between the different verification approaches.
 *
 * @see ConvertFromMockitoWhenIntention
 * @see ConvertFromMockitoDoIntention
 * @see ConvertFromBDDMockitoGivenIntention
 * @see ConvertFromBDDMockitoWillIntention
 * @since 0.6.0
 */
public abstract class ConvertStubbingIntentionBase extends ConversionIntentionBase {
    private final String qualifierType;

    protected ConvertStubbingIntentionBase(String sourceApproachName, String qualifierType) {
        super(sourceApproachName, 16);
        this.qualifierType = qualifierType;
    }

    //Intention names

    @Override
    public @IntentionName @NotNull String getText() {
        return MockitoolsBundle.message("intention.convert.stubbing.to");
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return MockitoolsBundle.message("intention.convert.stubbing.x.to.family", sourceApproachName);
    }

    /**
     * Returns whether the argument qualifier expression has the correct type. The qualifier may be a local variable
     * (in case of e.g. InOrder), or a class (in case of Mockito or BDDMockito).
     */
    @Override
    protected boolean isQualifierHaveCorrectType(PsiExpression qualifier) {
        return compute(() -> qualifier instanceof PsiReferenceExpression qualifierAsRef
                             && qualifierAsRef.resolve() instanceof PsiClass qualifierClass
                             && qualifierType.equals(qualifierClass.getQualifiedName()));
    }

    protected boolean doAllCallChainsMatch(CallChainAnalyzer analyzer, boolean isBulkMode, Editor editor, PsiFile file) {
        if (isBulkMode)
            return collectStatementsInSelection(editor, file).stream()
                .map(ConvertStubbingAction::getFirstCallInChain)
                .allMatch(firstCall -> isCallChainMatch(firstCall, analyzer));
        else return isCallChainMatch(getMethodCallAtCaret(file, editor), analyzer);
    }

    /**
     * Returns whether the call chain started by the provided method call matches all specified analyzers.
     */
    private boolean isCallChainMatch(PsiMethodCallExpression firstCallInChain, CallChainAnalyzer... analyzers) {
        if (analyzers == null || analyzers.length == 0) return true;
        var calls = collectCallsInChainFromFirst(firstCallInChain);
        return Arrays.stream(analyzers).allMatch(analyzer -> analyzer.analyze(calls));
    }

}
