//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.google.common.collect.Iterables.getLast;
import static com.picimako.mockitools.ExpressionCreationHelper.createExpressionFromText;
import static com.picimako.mockitools.MockitoQualifiedNames.DO_RETURN;
import static com.picimako.mockitools.MockitoQualifiedNames.DO_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.GIVEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN_RETURN;
import static com.picimako.mockitools.MockitoQualifiedNames.WHEN;
import static com.picimako.mockitools.MockitoQualifiedNames.WILL_RETURN;
import static com.picimako.mockitools.MockitoQualifiedNames.WILL_THROW;
import static com.picimako.mockitools.PointersUtil.toPointers;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.getReferenceNameElement;
import static com.picimako.mockitools.UnitTestPsiUtil.isInTestSourceContent;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.SmartList;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports multiple consecutive calls to {@code *Return()} methods, so that they may be merge into a single call.
 * <p>
 * Both {@code org.mockito.Mockito} and {@code org.mockito.BDDMockito} based stubbing chains are supported,
 * including calls to {@code doReturn()}, {@code thenReturn()} and {@code willReturn()}.
 * <p>
 * If there are multiple sections of consecutive calls within the same call chain, they are reported separately for better notification,
 * but upon invoking the quick fix, all sections are merged respectively.
 *
 * @since 0.3.0
 */
public class SimplifyConsecutiveStubbingCallsInspection extends MockitoolsBaseInspection {
    private static final List<ConsecutiveCallDescriptor> RETURN_DESCRIPTORS = List.of(
        new ConsecutiveCallDescriptor(ORG_MOCKITO_MOCKITO, DO_RETURN, 0,
            DO_RETURN, DO_THROW, "doNothing", "doAnswer", "doCallRealMethod"),
        new ConsecutiveCallDescriptor(ORG_MOCKITO_BDDMOCKITO, WILL_RETURN, 0,
            GIVEN, WILL_RETURN, WILL_THROW, "will", "willDoNothing", "willAnswer", "willCallRealMethod"),
        new ConsecutiveCallDescriptor(ORG_MOCKITO_MOCKITO, THEN_RETURN, 1, WHEN)
    );

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return isInTestSourceContent(session.getFile()) ? methodCallVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
    }

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        RETURN_DESCRIPTORS.stream()
            .filter(descriptor -> descriptor.matches(expression))
            .findFirst()
            .ifPresent(descriptor -> checkCallChainAndRegister(descriptor, expression, holder));
    }

    /**
     * Goes through a stubbing call chain, and
     * <ul>
     *     <li>if it encounters a method that we are looking for the consecutiveness of (e.g. {@code thenReturn()}), saves its index,</li>
     *     <li>if it encounters a different method (e.g. {@code given()}), or there is no more call in the chain,
     *     but there were multiple consecutive calls before, it registers the last method in the consecutive chain.
     *     This separate registration is to provide better notification for users, and in the future, to be able to merge different consecutive calls separately.</li>
     * </ul>
     */
    private void checkCallChainAndRegister(ConsecutiveCallDescriptor descriptor, PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        var callsInWholeChain = collectCallsInChainFromFirst(expression, true);
        var consecutiveCallIndeces = new SmartList<Integer>();

        for (int i = descriptor.indexToStartInspectionAt; i < callsInWholeChain.size(); i++) {
            PsiMethodCallExpression call = callsInWholeChain.get(i);

            if (descriptor.consecutiveMethodName.equals(getMethodName(call))) {
                consecutiveCallIndeces.add(i);
            } else {
                if (consecutiveCallIndeces.size() > 1) {
                    register(callsInWholeChain, callsInWholeChain.get(getLast(consecutiveCallIndeces)), descriptor, holder);
                }
                consecutiveCallIndeces.clear();
            }
        }

        //When one or more matching calls are at the end of the call chain, they have to be added too.
        if (consecutiveCallIndeces.size() > 1) {
            register(callsInWholeChain, callsInWholeChain.get(getLast(consecutiveCallIndeces)), descriptor, holder);
        }
    }

    private void register(List<PsiMethodCallExpression> callsInWholeChain, PsiMethodCallExpression lastConsecutiveCall,
                          ConsecutiveCallDescriptor descriptor, @NotNull ProblemsHolder holder) {
        holder.registerProblem(getReferenceNameElement(lastConsecutiveCall),
            MockitoolsBundle.inspection("can.merge.with.previous.consecutive.calls", descriptor.consecutiveMethodName),
            new MergeConsecutiveReturnCallsQuickFix(descriptor, toPointers(callsInWholeChain)));
    }

    /**
     * Quick fix that merges consecutive {@code *Return()} calls. If there are multiple separate consecutive calls, they are merged respectively.
     */
    private static final class MergeConsecutiveReturnCallsQuickFix extends InspectionGadgetsFix {
        private final ConsecutiveCallDescriptor callDescriptor;
        private final List<SmartPsiElementPointer<PsiMethodCallExpression>> callsInWholeChain;

        public MergeConsecutiveReturnCallsQuickFix(ConsecutiveCallDescriptor descriptor, List<SmartPsiElementPointer<PsiMethodCallExpression>> callsInWholeChain) {
            this.callDescriptor = descriptor;
            this.callsInWholeChain = callsInWholeChain;
        }

        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.quickFix("merge.with.previous.consecutive.calls", callDescriptor.consecutiveMethodName);
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return MockitoolsBundle.quickFixFamily("simplify.consecutive.stubbing.calls");
        }

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) {
            StringBuilder sb = new StringBuilder(callDescriptor.mockitoClass);
            var consecutiveCalls = new SmartList<PsiMethodCallExpression>();

            for (var callPointer : callsInWholeChain) {
                PsiMethodCallExpression call = callPointer.getElement();

                String methodName = getMethodName(call);
                //If it's a call that we are looking for the consecutiveness of, save it for later processing.
                if (callDescriptor.consecutiveMethodName.equals(methodName)) {
                    consecutiveCalls.add(call);
                    continue;
                }
                if (consecutiveCalls.size() == 1) {
                    //If it's a different call and there were no consecutive calls, just add that call.
                    sb.append(".").append(getMethodName(consecutiveCalls.get(0))).append(consecutiveCalls.get(0).getArgumentList().getText());
                } else if (consecutiveCalls.size() > 1) {
                    //...there were at least two consecutive calls, merge their arguments into one call.
                    mergeCallsAndAppend(consecutiveCalls, sb);
                }
                //Add the current call
                sb.append(".").append(methodName).append(call.getArgumentList().getText());
                consecutiveCalls.clear();
            }

            //When one or more matching calls are at the end of the call chain, they have to be added too.
            if (!consecutiveCalls.isEmpty()) {
                mergeCallsAndAppend(consecutiveCalls, sb);
            }

            var wholeCallChain = getLast(callsInWholeChain).getElement();
            wholeCallChain.replace(createExpressionFromText(sb.toString(), wholeCallChain, project));
        }

        /**
         * For examples the following of consecutive calls
         * <pre>
         * .thenReturn(arg1).thenReturn(arg2).thenReturn(arg2)
         * </pre>
         * become
         * <pre>
         * .thenReturn(arg1,arg2,arg3)
         * </pre>
         */
        private StringBuilder mergeCallsAndAppend(List<PsiMethodCallExpression> consecutiveCalls, StringBuilder sb) {
            return sb.append(".").append(callDescriptor.consecutiveMethodName).append("(")
                .append(consecutiveCalls.stream().flatMap(c -> Arrays.stream(c.getArgumentList().getExpressions())).map(PsiElement::getText).collect(joining(", ")))
                .append(")");
        }
    }

    private static final class ConsecutiveCallDescriptor {
        /**
         * Used in the quick fix as the beginning of the expression that is built for replacement.
         * <p>
         * Usually either {@code org.mockito.Mockito} or {@code org.mockito.BDDMockito}.
         */
        private final String mockitoClass;
        /**
         * The first call in a stubbing call chain from where the calls are collected.
         * <p>
         * E.g. {@code Mockito.when()}, {@code BDDMockito.willReturn()}, etc.
         */
        private final List<String> chainStarterMethodNames;
        /**
         * The call matcher built for {@link #chainStarterMethodNames}.
         */
        private final CallMatcher chainStarterMethodMatcher;
        /**
         * The method name whose consecutiveness the inspection looks for.
         * <p>
         * E.g. {@code doReturn}, {@code thenReturn}, etc.
         */
        private final String consecutiveMethodName;
        /**
         * The index to start inspecting the call chain from, because in case of e.g. {@code Mockito.when()} it is certain that
         * {@code when()} will never match e.g. {@code thenReturn()}, so we can skip that comparison and start at the next call.
         */
        public final int indexToStartInspectionAt;

        private ConsecutiveCallDescriptor(String mockitoClass, String consecutiveMethodName, int indexToStartInspectionAt, String... chainStarterMethodNames) {
            this.mockitoClass = mockitoClass;
            this.chainStarterMethodNames = Arrays.asList(chainStarterMethodNames);
            this.consecutiveMethodName = consecutiveMethodName;
            this.indexToStartInspectionAt = indexToStartInspectionAt;
            this.chainStarterMethodMatcher = CallMatcher.staticCall(mockitoClass, chainStarterMethodNames);
        }

        boolean matches(PsiMethodCallExpression expression) {
            return chainStarterMethodNames.contains(getMethodName(expression)) && chainStarterMethodMatcher.matches(expression);
        }
    }
}
