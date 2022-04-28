//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.picimako.mockitools.MockitoQualifiedNames.DO_RETURN;
import static com.picimako.mockitools.MockitoQualifiedNames.DO_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.GIVEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN_RETURN;
import static com.picimako.mockitools.MockitoQualifiedNames.WHEN;
import static com.picimako.mockitools.MockitoQualifiedNames.WILL_RETURN;
import static com.picimako.mockitools.MockitoQualifiedNames.WILL_THROW;

import java.util.List;

import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Reports multiple consecutive calls to {@code *Return()} methods, so that they may be merged into a single call.
 * <p>
 * Both {@code org.mockito.Mockito} and {@code org.mockito.BDDMockito} based stubbing chains are supported,
 * including calls to {@code doReturn()}, {@code thenReturn()} and {@code willReturn()}.
 * <p>
 * If there are multiple sections of consecutive calls within the same call chain, they are reported separately for better notification,
 * and can be merged separately.
 *
 * @since 0.3.0
 */
public class SimplifyConsecutiveReturnCallsInspection extends SimplifyConsecutiveCallsInspectionBase {
    private static final List<ConsecutiveCallAnalysisDescriptor> RETURN_DESCRIPTORS = List.of(
        new ConsecutiveCallAnalysisDescriptor.Builder(ORG_MOCKITO_MOCKITO)
            .consecutiveMethodName(DO_RETURN)
            .chainStarterMethodNames(DO_RETURN, DO_THROW, "doNothing", "doAnswer", "doCallRealMethod").build(),
        new ConsecutiveCallAnalysisDescriptor.Builder(ORG_MOCKITO_BDDMOCKITO)
            .consecutiveMethodName(WILL_RETURN)
            .chainStarterMethodNames(GIVEN, WILL_RETURN, WILL_THROW, "will", "willDoNothing", "willAnswer", "willCallRealMethod").build(),
        new ConsecutiveCallAnalysisDescriptor.Builder(ORG_MOCKITO_MOCKITO)
            .consecutiveMethodName(THEN_RETURN)
            .indexToStartInspectionAt(1)
            .chainStarterMethodNames(WHEN).build()
    );

    @Override
    protected List<ConsecutiveCallAnalysisDescriptor> analysisDescriptors() {
        return RETURN_DESCRIPTORS;
    }

    @Override
    protected void register(ConsecutiveCallRegistrarContext registrar, @NotNull ProblemsHolder holder) {
        doRegister(registrar, holder, new MergeConsecutiveStubbingCallsQuickFix(new ConsecutiveCallQuickFixContext(registrar), TypeConversionMethod.NO_CONVERSION));
    }
}
