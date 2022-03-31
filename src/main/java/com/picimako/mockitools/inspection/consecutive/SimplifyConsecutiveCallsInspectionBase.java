//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.List;
import java.util.function.Predicate;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.inspection.ConsecutiveCallDescriptor;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;

/**
 * Base class for reporting simplifiable consecutive calls.
 *
 * @param <T> the type of call descriptor this inspection uses
 */
public abstract class SimplifyConsecutiveCallsInspectionBase<T extends ConsecutiveCallDescriptor> extends MockitoolsBaseInspection {

    /**
     * Goes through a stubbing call chain, and
     * <ul>
     *     <li>if it encounters a method that we are looking for the consecutiveness of (e.g. {@code thenReturn()}, or {@code willThrow()), saves its index,</li>
     *     <li>if it encounters a different method (e.g. {@code given()}), or there is no more call in the chain,
     *     but there were multiple consecutive calls before, it registers the last method in the consecutive chain.
     *     This separate registration is to provide better notification for users, and in the future, to be able to merge different consecutive calls separately.</li>
     * </ul>
     */
    protected void checkCallChainAndRegister(T descriptor, PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        var callsInWholeChain = collectCallsInChainFromFirst(expression, true);
        var consecutiveCallIndeces = new SmartList<Integer>();

        for (int i = descriptor.indexToStartInspectionAt; i < callsInWholeChain.size(); i++) {
            PsiMethodCallExpression call = callsInWholeChain.get(i);

            if (descriptor.consecutiveMethodName.equals(getMethodName(call)) && extraCondition().test(call)) {
                consecutiveCallIndeces.add(i);
            } else {
                if (consecutiveCallIndeces.size() > 1) {
                    register(callsInWholeChain, consecutiveCallIndeces, descriptor, holder);
                }
                consecutiveCallIndeces.clear();
            }
        }

        //When one or more matching calls are at the end of the call chain, they have to be added too.
        if (consecutiveCallIndeces.size() > 1) {
            register(callsInWholeChain, consecutiveCallIndeces, descriptor, holder);
        }
    }

    /**
     * Additional condition on the method call.
     */
    protected Predicate<PsiMethodCallExpression> extraCondition() {
        return call -> true;
    }

    protected abstract void register(List<PsiMethodCallExpression> callsInWholeChain, List<Integer> consecutiveCallIndeces, T descriptor, @NotNull ProblemsHolder holder);

}
