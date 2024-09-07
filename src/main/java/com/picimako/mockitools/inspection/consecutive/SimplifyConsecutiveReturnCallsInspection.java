//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.picimako.mockitools.MockitoQualifiedNames.DO_RETURN;
import static com.picimako.mockitools.MockitoQualifiedNames.DO_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.GIVEN;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN_RETURN;
import static com.picimako.mockitools.MockitoQualifiedNames.WHEN;
import static com.picimako.mockitools.MockitoQualifiedNames.WILL_RETURN;
import static com.picimako.mockitools.MockitoQualifiedNames.WILL_THROW;
import static com.picimako.mockitools.inspection.consecutive.ConsecutiveCallAnalysisDescriptor.MethodType.INSTANCE;

import com.intellij.codeInspection.ProblemsHolder;
import com.picimako.mockitools.inspection.consecutive.ConsecutiveCallAnalysisDescriptor.Builder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Reports multiple consecutive calls to {@code *Return()} methods, so that they may be merged into a single call.
 * <p>
 * {@code org.mockito.Mockito}, {@code org.mockito.BDDMockito} and {@code org.mockito.MockedStatic }based stubbings
 * are supported, including calls to {@code doReturn()}, {@code thenReturn()} and {@code willReturn()}.
 * <p>
 * If there are multiple sections of consecutive calls within the same call chain, they are reported separately for better notification,
 * and can be merged separately.
 *
 * @since 0.3.0
 */
final class SimplifyConsecutiveReturnCallsInspection extends SimplifyConsecutiveCallsInspectionBase {
    private static final List<ConsecutiveCallAnalysisDescriptor> RETURN_DESCRIPTORS = List.of(
        Builder.forMockito(DO_RETURN)
            .inCallChainsBeginningWith(DO_RETURN, DO_THROW, "doNothing", "doAnswer", "doCallRealMethod").build(),
        Builder.forBDDMockito(WILL_RETURN)
            .inCallChainsBeginningWith(GIVEN, WILL_RETURN, WILL_THROW, "will", "willDoNothing", "willAnswer", "willCallRealMethod").build(),
        Builder.forMockito(THEN_RETURN)
            .indexToStartInspectionAt(1)
            .inCallChainsBeginningWith(WHEN).build(),
        Builder.forMockedStatic(THEN_RETURN)
            .indexToStartInspectionAt(1)
            .inCallChainsBeginningWith(INSTANCE, WHEN).build()
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
