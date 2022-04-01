//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.google.common.collect.Iterables.getLast;
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

import java.util.List;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiMethodCallExpression;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.inspection.ConsecutiveCallDescriptor;

/**
 * Reports multiple consecutive calls to {@code *Return()} methods, so that they may be merged into a single call.
 * <p>
 * Both {@code org.mockito.Mockito} and {@code org.mockito.BDDMockito} based stubbing chains are supported,
 * including calls to {@code doReturn()}, {@code thenReturn()} and {@code willReturn()}.
 * <p>
 * If there are multiple sections of consecutive calls within the same call chain, they are reported separately for better notification,
 * but upon invoking the quick fix, all sections are merged respectively.
 *
 * @since 0.3.0
 */
public class SimplifyConsecutiveReturnCallsInspection extends SimplifyConsecutiveCallsInspectionBase {
    private static final List<ConsecutiveCallDescriptor> RETURN_DESCRIPTORS = List.of(
        new ConsecutiveCallDescriptor.Builder(ORG_MOCKITO_MOCKITO)
            .consecutiveMethodName(DO_RETURN)
            .chainStarterMethodNames(DO_RETURN, DO_THROW, "doNothing", "doAnswer", "doCallRealMethod").build(),
        new ConsecutiveCallDescriptor.Builder(ORG_MOCKITO_BDDMOCKITO)
            .consecutiveMethodName(WILL_RETURN)
            .chainStarterMethodNames(GIVEN, WILL_RETURN, WILL_THROW, "will", "willDoNothing", "willAnswer", "willCallRealMethod").build(),
        new ConsecutiveCallDescriptor.Builder(ORG_MOCKITO_MOCKITO)
            .consecutiveMethodName(THEN_RETURN)
            .indexToStartInspectionAt(1)
            .chainStarterMethodNames(WHEN).build()
    );

    @Override
    protected List<ConsecutiveCallDescriptor> callDescriptors() {
        return RETURN_DESCRIPTORS;
    }

    @Override
    protected void register(List<PsiMethodCallExpression> callsInWholeChain, List<Integer> consecutiveCallIndeces,
                            ConsecutiveCallDescriptor descriptor, @NotNull ProblemsHolder holder) {
        doRegister(callsInWholeChain.get(getLast(consecutiveCallIndeces)), descriptor, holder,
            new MergeConsecutiveStubbingCallsQuickFix(descriptor, toPointers(callsInWholeChain), TypeConversionMethod.NO_CONVERSION));
    }
}
