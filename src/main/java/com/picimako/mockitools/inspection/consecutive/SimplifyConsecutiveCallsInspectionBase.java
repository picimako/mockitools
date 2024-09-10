//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.util.PsiMethodUtil.getReferenceNameElement;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.SmartList;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Base class for reporting simplifiable consecutive calls.
 * <p>
 * Flow:
 * {@link #checkMethodCallExpression} ->
 * {@link #checkCallChainAndRegister} ->
 * {@link #registerMultiple} ->
 * {@link #register} ->
 * {@link #doRegister}
 */
abstract class SimplifyConsecutiveCallsInspectionBase extends MockitoolsBaseInspection {

    //Analysis workflow

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        analyzers().stream()
            .filter(analyzer -> analyzer.canAnalyze(expression))
            .findFirst()
            .ifPresent(analyzer -> checkCallChainAndRegister(analyzer, expression, holder));
    }

    /**
     * Goes through a stubbing call chain, and
     * <ul>
     *     <li>if it encounters a method that we are looking for the consecutiveness of (e.g. {@code thenReturn()}, or {@code willThrow()}), saves its index,</li>
     *     <li>if it encounters a different method (e.g. {@code given()}), or there is no more call in the chain,
     *     but there were multiple consecutive calls before, it registers the last method in the consecutive chain.
     *     This separate registration is to provide better notification for users, and in the future, to be able to merge different consecutive calls separately.</li>
     * </ul>
     */
    protected void checkCallChainAndRegister(ConsecutiveCallAnalyzer analyzer, PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        var callsInWholeChain = collectCallsInChainFromFirst(expression, true);
        var consecutiveCallIndeces = new SmartList<Integer>();

        for (int i = analyzer.skipAnalysisOfFirstCall ? 1 : 0; i < callsInWholeChain.size(); i++) {
            var call = callsInWholeChain.get(i);

            if (analyzer.consecutiveMethodName.equals(getMethodName(call)) && extraCondition().test(call)) {
                consecutiveCallIndeces.add(i);
            } else {
                registerMultiple(analyzer, callsInWholeChain, consecutiveCallIndeces, holder);
                consecutiveCallIndeces.clear();
            }
        }

        //When one or more matching calls are at the end of the call chain, they have to be added too.
        registerMultiple(analyzer, callsInWholeChain, consecutiveCallIndeces, holder);
    }

    /**
     * Additional condition on the method call.
     */
    protected Predicate<PsiMethodCallExpression> extraCondition() {
        return call -> true;
    }

    protected abstract List<ConsecutiveCallAnalyzer> analyzers();

    //Registration workflow

    private void registerMultiple(ConsecutiveCallAnalyzer analyzer, List<PsiMethodCallExpression> callsInWholeChain,
                                  List<Integer> consecutiveCallIndeces, @NotNull ProblemsHolder holder) {
        if (consecutiveCallIndeces.size() > 1) {
            register(new ConsecutiveCallRegistrar(analyzer, callsInWholeChain, consecutiveCallIndeces), holder);
        }
    }

    protected abstract void register(ConsecutiveCallRegistrar context, @NotNull ProblemsHolder holder);

    protected void doRegister(ConsecutiveCallRegistrar registrar, @NotNull ProblemsHolder holder, TypeConversionMethod... typeConversionMethods) {
        holder.registerProblem(getReferenceNameElement(registrar.getLastConsecutiveCall()),
            MockitoolsBundle.message("inspection.can.merge.with.previous.consecutive.calls", registrar.consecutiveMethodName),
            Arrays.stream(typeConversionMethods).map(method -> new MergeConsecutiveStubbingCallsQuickFix(registrar, method)).toArray(LocalQuickFix[]::new));
    }
}
