//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.google.common.collect.Iterables.getLast;
import static com.picimako.mockitools.ExpressionCreationHelper.createExpressionFromText;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.SmartList;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.inspection.ConsecutiveCallDescriptor;
import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Quick fix that merges consecutive {@code *Throw()} or {@code *Return()} calls, respectively.
 * If there are multiple separate consecutive calls, they are merged respectively.
 */
public class MergeConsecutiveStubbingCallsQuickFix extends InspectionGadgetsFix {
    private final ConsecutiveCallDescriptor callDescriptor;
    private final List<SmartPsiElementPointer<PsiMethodCallExpression>> callsInWholeChain;
    private final TypeConversionMethod argumentTypeConverter;

    public MergeConsecutiveStubbingCallsQuickFix(ConsecutiveCallDescriptor descriptor,
                                                 List<SmartPsiElementPointer<PsiMethodCallExpression>> callsInWholeChain,
                                                 TypeConversionMethod argumentTypeConverter) {
        this.callDescriptor = descriptor;
        this.callsInWholeChain = callsInWholeChain;
        this.argumentTypeConverter = argumentTypeConverter;
    }

    @Override
    public @IntentionName @NotNull String getName() {
        switch (argumentTypeConverter) {
            case NO_CONVERSION:
            case TO_THROWABLES_SIMPLE:
                return MockitoolsBundle.quickFix("merge.with.previous.consecutive.calls", callDescriptor.consecutiveMethodName);
            default:
                return MockitoolsBundle.quickFix("merge.with.previous.consecutive.calls.and.convert.params", argumentTypeConverter.message);
        }
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
    private void mergeCallsAndAppend(List<PsiMethodCallExpression> consecutiveCalls, StringBuilder sb) {
        sb.append(".").append(callDescriptor.consecutiveMethodName).append("(")
            .append(consecutiveCalls.stream().flatMap(c -> Arrays.stream(c.getArgumentList().getExpressions())).map(argumentTypeConverter::convert).collect(joining(", ")))
            .append(")");
    }
}
