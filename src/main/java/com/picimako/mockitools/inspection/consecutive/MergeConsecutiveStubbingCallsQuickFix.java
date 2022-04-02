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
import com.intellij.util.SmartList;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Quick fix that merges consecutive {@code *Throw()} or {@code *Return()} calls, respectively.
 * Only a single section of consecutive calls is merged if there are multiple.
 */
public class MergeConsecutiveStubbingCallsQuickFix extends InspectionGadgetsFix {
    private final ConsecutiveCallRegistrarDescriptor registrar;
    private final TypeConversionMethod argumentTypeConverter;

    public MergeConsecutiveStubbingCallsQuickFix(ConsecutiveCallRegistrarDescriptor registrar, TypeConversionMethod argumentTypeConverter) {
        this.registrar = registrar;
        this.argumentTypeConverter = argumentTypeConverter;
    }

    @Override
    public @IntentionName @NotNull String getName() {
        switch (argumentTypeConverter) {
            case NO_CONVERSION:
            case TO_THROWABLES_SIMPLE:
                return MockitoolsBundle.quickFix("merge.with.previous.consecutive.calls", registrar.consecutiveMethodName);
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
        StringBuilder sb = new StringBuilder(registrar.mockitoClass);
        var consecutiveCalls = new SmartList<PsiMethodCallExpression>();

        for (int i = 0; i < registrar.wholeChainPointers.size(); i++) {
            PsiMethodCallExpression call = registrar.wholeChainPointers.get(i).getElement();

            String methodName = getMethodName(call);
            //The indeces check makes it possible to merge only a single section of consecutive calls.
            //Also, if it's a call that we are looking for the consecutiveness of, save it for later processing.
            if (registrar.consecutiveCallIndeces.contains(i) && registrar.consecutiveMethodName.equals(methodName)) {
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

        var wholeCallChain = getLast(registrar.wholeChainPointers).getElement();
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
        sb.append(".").append(registrar.consecutiveMethodName).append("(")
            .append(consecutiveCalls.stream()
                .flatMap(c -> Arrays.stream(c.getArgumentList().getExpressions()))
                .map(argumentTypeConverter::convert)
                .collect(joining(", ")))
            .append(")");
    }
}
