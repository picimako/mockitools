//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.getReferenceNameElement;
import static com.picimako.mockitools.UnitTestPsiUtil.isInTestSourceContent;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.List;
import java.util.function.Predicate;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.SmartList;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Base class for reporting simplifiable consecutive calls.
 */
public abstract class SimplifyConsecutiveCallsInspectionBase extends MockitoolsBaseInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return isInTestSourceContent(session.getFile()) ? methodCallVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
    }

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        analysisDescriptors().stream()
            .filter(descriptor -> descriptor.matches(expression))
            .findFirst()
            .ifPresent(analyzer -> checkCallChainAndRegister(analyzer, expression, holder));
    }

    /**
     * Goes through a stubbing call chain, and
     * <ul>
     *     <li>if it encounters a method that we are looking for the consecutiveness of (e.g. {@code thenReturn()}, or {@code willThrow()), saves its index,</li>
     *     <li>if it encounters a different method (e.g. {@code given()}), or there is no more call in the chain,
     *     but there were multiple consecutive calls before, it registers the last method in the consecutive chain.
     *     This separate registration is to provide better notification for users, and in the future, to be able to merge different consecutive calls separately.</li>
     * </ul>
     */
    protected void checkCallChainAndRegister(ConsecutiveCallAnalysisDescriptor analyzer, PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        var callsInWholeChain = collectCallsInChainFromFirst(expression, true);
        var consecutiveCallIndeces = new SmartList<Integer>();

        for (int i = analyzer.indexToStartInspectionAt; i < callsInWholeChain.size(); i++) {
            PsiMethodCallExpression call = callsInWholeChain.get(i);

            if (analyzer.consecutiveMethodName.equals(getMethodName(call)) && extraCondition().test(call)) {
                consecutiveCallIndeces.add(i);
            } else {
                registerMultiple(callsInWholeChain, consecutiveCallIndeces, analyzer, holder);
                consecutiveCallIndeces.clear();
            }
        }

        //When one or more matching calls are at the end of the call chain, they have to be added too.
        registerMultiple(callsInWholeChain, consecutiveCallIndeces, analyzer, holder);
    }

    private void registerMultiple(List<PsiMethodCallExpression> callsInWholeChain, List<Integer> consecutiveCallIndeces,
                                  ConsecutiveCallAnalysisDescriptor analyzer, @NotNull ProblemsHolder holder) {
        if (consecutiveCallIndeces.size() > 1) {
            register(new ConsecutiveCallRegistrarDescriptor(analyzer, callsInWholeChain, consecutiveCallIndeces), holder);
        }
    }

    protected void doRegister(ConsecutiveCallRegistrarDescriptor registrar, @NotNull ProblemsHolder holder, InspectionGadgetsFix... quickFixes) {
        holder.registerProblem(getReferenceNameElement(registrar.getLastConsecutiveCall()),
            MockitoolsBundle.inspection("can.merge.with.previous.consecutive.calls", registrar.consecutiveMethodName), quickFixes);
    }

    /**
     * Additional condition on the method call.
     */
    protected Predicate<PsiMethodCallExpression> extraCondition() {
        return call -> true;
    }

    protected abstract void register(ConsecutiveCallRegistrarDescriptor context, @NotNull ProblemsHolder holder);

    protected abstract List<ConsecutiveCallAnalysisDescriptor> analysisDescriptors();

}
