//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.picimako.mockitools.MockitoQualifiedNames.DO_RETURN;
import static com.picimako.mockitools.MockitoQualifiedNames.DO_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.GIVEN;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.WHEN;
import static com.picimako.mockitools.MockitoQualifiedNames.WILL_RETURN;
import static com.picimako.mockitools.MockitoQualifiedNames.WILL_THROW;
import static com.picimako.mockitools.StubbingApproach.BDDMOCKITO_GIVEN;
import static com.picimako.mockitools.StubbingApproach.BDDMOCKITO_WILL_X;
import static com.picimako.mockitools.StubbingApproach.MOCKITO_DO_X;
import static com.picimako.mockitools.StubbingApproach.MOCKITO_WHEN;
import static com.picimako.mockitools.util.PsiMethodUtil.containsCallToNonDefaultConstructor;
import static com.picimako.mockitools.util.PsiMethodUtil.getArguments;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.inspection.consecutive.ConsecutiveCallAnalyzer.Analyzer;
import com.picimako.mockitools.util.PsiMethodUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * Reports multiple consecutive calls to {@code *Throw()} methods, so that they may be merged into a single call.
 * <p>
 * {@code org.mockito.Mockito}, {@code org.mockito.BDDMockito} and {@code org.mockito.MockedStatic} based stubbings
 * are supported, including calls to {@code doThrow()}, {@code thenThrow()} and {@code willThrow()}.
 * <p>
 * If there are multiple sections of consecutive calls within the same call chain, they are reported separately for better notification,
 * and can be merged separately.
 * <p>
 * Every parameter combination of {@code *Throw()} calls can be merged but
 * <ul>
 *     <li>when there are both PsiClassObjectAccessExpression and PsiNewExpressions (as default constructor calls), users can choose which type to convert
 *     the expressions to,</li>
 *     <li>when there is a PsiNewExpression call to a parameterized constructor, the arguments are automatically converted to Throwables (as in PsiNewExpressions)
 *     to keep the constructor arguments.</li>
 * </ul>
 *
 * @since 0.4.0
 */
final class SimplifyConsecutiveThrowCallsInspection extends SimplifyConsecutiveCallsInspectionBase {
    private static final List<ConsecutiveCallAnalyzer> THROW_CALL_DESCRIPTORS = List.of(
        Analyzer.forMockito(DO_THROW)
            .exceptionStubbingVia(MOCKITO_DO_X)
            .inCallChainsBeginningWithStatic(DO_RETURN, DO_THROW, "doNothing", "doAnswer", "doCallRealMethod").build(),
        Analyzer.forBDDMockito(WILL_THROW)
            .exceptionStubbingVia(BDDMOCKITO_WILL_X)
            .inCallChainsBeginningWithStatic(WILL_THROW).build(),
        Analyzer.forBDDMockito(WILL_THROW)
            .exceptionStubbingVia(BDDMOCKITO_GIVEN)
            .inCallChainsBeginningWithStatic(GIVEN, WILL_RETURN, "will", "willDoNothing", "willAnswer", "willCallRealMethod").build(),
        Analyzer.forMockito(THEN_THROW)
            .exceptionStubbingVia(MOCKITO_WHEN)
            .skippingAnalysisOfFirstCall()
            .inCallChainsBeginningWithStatic(WHEN).build(),
        Analyzer.forMockedStatic(THEN_THROW)
            .exceptionStubbingVia(MOCKITO_WHEN)
            .skippingAnalysisOfFirstCall()
            .inCallChainsBeginningWithInstance(WHEN).build()
    );

    @Override
    protected List<ConsecutiveCallAnalyzer> analyzers() {
        return THROW_CALL_DESCRIPTORS;
    }

    @Override
    protected Predicate<PsiMethodCallExpression> extraCondition() {
        return PsiMethodUtil::hasArgument;
    }

    /**
     * Based on the combination of parameter types passed into the *Throw() calls, it registers the problem with one or more proper quick fixes.
     * <p>
     * If the argument types are the same, then no type conversion is needed, while if they are mixed, then users will have multiple options,
     * so they can choose the target type.
     */
    @Override
    protected void register(ConsecutiveCallRegistrar registrar, @NotNull ProblemsHolder holder) {
        switch (determineParamCombination(registrar)) {
            case CLASSES, THROWABLES ->
                doRegister(registrar, holder, TypeConversionMethod.NO_CONVERSION);
            case MIXED_WITH_THROWABLES_PREFERRED ->
                doRegister(registrar, holder, TypeConversionMethod.TO_THROWABLES_SIMPLE);
            case MIXED -> doRegister(registrar, holder, TypeConversionMethod.TO_CLASSES, TypeConversionMethod.TO_THROWABLES);
        }
    }

    /**
     * Determines the combination of parameter types specified in the *Throw() calls.
     */
    private ThrowStubParameterCombination determineParamCombination(ConsecutiveCallRegistrar registrar) {
        boolean hasClasses = false;
        boolean hasThrowables = false;
        boolean isThereNonDefaultNewExpressionArg = false;
        for (Integer index : registrar.consecutiveCallIndeces) {
            var call = registrar.callsInWholeChain.get(index);
            //hasClasses is checked before to avoid unnecessary calls to CallMatcher.matches()
            if (!hasClasses && registrar.isCallToClasses(call)) {
                hasClasses = true;
                continue;
            }
            if (registrar.isCallToThrowables(call)) {
                hasThrowables = true;
                if (!isThereNonDefaultNewExpressionArg) {
                    isThereNonDefaultNewExpressionArg = containsCallToNonDefaultConstructor(getArguments(call));
                }
            }
        }

        if (hasClasses && !hasThrowables) return ThrowStubParameterCombination.CLASSES;
        if (!hasClasses && hasThrowables) return ThrowStubParameterCombination.THROWABLES;
        //!hasClasses && !hasThrowables is not an option because this method gets executed only when there are consecutive calls to register
        return isThereNonDefaultNewExpressionArg ? ThrowStubParameterCombination.MIXED_WITH_THROWABLES_PREFERRED : ThrowStubParameterCombination.MIXED;
    }

    /**
     * Defines the combination of argument types the consecutive *Throw() calls have.
     */
    private enum ThrowStubParameterCombination {
        /**
         * {@code Class} + {@code Class}
         * <p>
         * {@code Class} + {@code (Class, Class...)}
         * <p>
         * {@code (Class, Class...)} + {@code (Class, Class...)}
         */
        CLASSES,
        /**
         * {@code Throwable...} + {@code Throwable...}
         */
        THROWABLES,
        /**
         * {@code Class} + {@code Throwable...}
         * <p>
         * {@code (Class, Class...)} + {@code Throwable...}
         */
        MIXED,
        /**
         * Same as {@link #MIXED} but when there is at least one PsiNewExpression that is called with a non-default constructor,
         * the arguments must be converted to Throwables (as in PsiNewExpressions) to keep the constructor arguments.
         */
        MIXED_WITH_THROWABLES_PREFERRED,
    }
}
