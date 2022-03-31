//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.PsiMethodUtil.findCallDownwardsInChain;
import static com.picimako.mockitools.PsiMethodUtil.findCallUpwardsInChain;
import static com.picimako.mockitools.PsiMethodUtil.isMethodCall;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;

import java.util.ArrayList;
import java.util.Optional;

import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.Nullable;

/**
 * Descriptor for holding *Throw() call related information.
 */
public final class ThrowStubDescriptor {
    private static final CallMatcher[] CALL_MATCHER_EMPTY = new CallMatcher[0];
    private static final String CLASS_THROWABLE = "java.lang.Class<? extends java.lang.Throwable>";
    private static final String CLASS_THROWABLES = CLASS_THROWABLE + "...";
    private static final String JAVA_LANG_THROWABLES = "java.lang.Throwable...";

    /**
     * The method name that accepts the mock object or the call on a mock object. Usually {@code given} or {@code when}.
     */
    private final String stubberCallName;
    public final StubType stubType;
    public final CallMatcher matcher;
    public CallMatcher classMatcher;
    public CallMatcher throwablesMatcher;

    public ThrowStubDescriptor(String stubberCallName, StubType stubType, String methodName, String instanceName, @Nullable String staticName) {
        this.stubberCallName = stubberCallName;
        this.stubType = stubType;
        this.matcher = createMatcher(methodName, instanceName, staticName);
    }

    boolean isValidStubbingArgument(PsiExpression stub) {
        return stubType == StubType.STUBBING ? isMethodCall(stub) : stub instanceof PsiReferenceExpression;
    }

    Optional<PsiMethodCallExpression> findStubbingCallInChain(PsiMethodCallExpression expression) {
        return stubType == StubType.STUBBING
            ? findCallUpwardsInChain(expression, stubberCallName)
            : findCallDownwardsInChain(expression, stubberCallName);
    }

    /**
     * Creates {@link CallMatcher}s for the provided method name in the provided classes for the method signatures with
     * {@code Class} and {@code Class, Class...} parameter lists.
     * <p>
     * If {@code staticClassName} is specified as well (so there are both static and instance calls available for the method name),
     * then call matchers for that static method are created too.
     * <p>
     * It behaves similarly to the Throwables based parameterization.
     */
    private CallMatcher createMatcher(String methodName, String instanceName, @Nullable String staticName) {
        var matchers = new ArrayList<CallMatcher>(4);
        var classMatchers = new ArrayList<CallMatcher>(2);
        var throwablesMatchers = new ArrayList<CallMatcher>(2);
        
        classMatchers.add(instanceCall(instanceName, methodName).parameterTypes(CLASS_THROWABLE));
        classMatchers.add(instanceCall(instanceName, methodName).parameterTypes(CLASS_THROWABLE, CLASS_THROWABLES));
        throwablesMatchers.add(instanceCall(instanceName, methodName).parameterTypes(JAVA_LANG_THROWABLES));

        if (staticName != null) {
            classMatchers.add(staticCall(staticName, methodName).parameterTypes(CLASS_THROWABLE));
            classMatchers.add(staticCall(staticName, methodName).parameterTypes(CLASS_THROWABLE, CLASS_THROWABLES));
            throwablesMatchers.add(staticCall(staticName, methodName).parameterTypes(JAVA_LANG_THROWABLES));
        }
        matchers.addAll(classMatchers);
        matchers.addAll(throwablesMatchers);

        classMatcher = CallMatcher.anyOf(classMatchers.toArray(CALL_MATCHER_EMPTY));
        throwablesMatcher = CallMatcher.anyOf(throwablesMatchers.toArray(CALL_MATCHER_EMPTY));
        return CallMatcher.anyOf(matchers.toArray(CALL_MATCHER_EMPTY));
    }

    /**
     * Naming is according the Mockito naming conventions.
     */
    public enum StubType {
        /**
         * When the stubbing of the action is called later in a stubbing call chain than the specification of the mock object.
         * <p>
         * E.g. {@code Mockito.when(mockObject.doesSomething()).thenThrow(SomeException.class)}
         */
        STUBBING,
        /**
         * When the stubbing of the action is called earlier in a stubbing call chain than the specification of the mock object.
         * <p>
         * E.g. {@code Mockito.doThrow(SomeException.class).when(mockObject.doesSomething())}
         */
        STUBBER
    }
}
