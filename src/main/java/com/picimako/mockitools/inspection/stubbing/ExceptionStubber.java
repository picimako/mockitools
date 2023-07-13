//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.stubbing;

import static com.siyeh.ig.callMatcher.CallMatcher.anyOf;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;

import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Descriptor for holding *Throw() call related information.
 */
public final class ExceptionStubber {
    private static final CallMatcher[] CALL_MATCHER_EMPTY = new CallMatcher[0];
    private static final String CLASS_THROWABLE = "java.lang.Class<? extends java.lang.Throwable>";
    private static final String CLASS_THROWABLES = CLASS_THROWABLE + "...";
    private static final String JAVA_LANG_THROWABLES = "java.lang.Throwable...";

    /**
     * The method name that accepts the mock object or the call on a mock object. Usually {@code given} or {@code when}.
     */
    private final CallMatcher matcher;
    public final CallMatcher classMatcher;
    public final CallMatcher throwablesMatcher;

    public ExceptionStubber(String methodName, String instanceClassName, @Nullable String staticClassName) {
        this.classMatcher = createClassMatcher(methodName, instanceClassName, staticClassName);
        this.throwablesMatcher = createThrowablesMatcher(methodName, instanceClassName, staticClassName);
        this.matcher = anyOf(classMatcher, throwablesMatcher);
    }

    public boolean isApplicableTo(PsiMethodCallExpression call) {
        return matcher.matches(call);
    }

    /**
     * Creates a {@link CallMatcher} for the provided method name in the provided classes for the method signatures with
     * {@code Class} and {@code Class, Class...} parameter lists.
     * <p>
     * If {@code staticClassName} is specified as well (so there are both static and instance calls available for the method name),
     * then call matchers for that static method are created too.
     */
    private CallMatcher createClassMatcher(String methodName, String instanceClassName, @Nullable String staticClassName) {
        var classMatchers = new ArrayList<CallMatcher>(staticClassName != null ? 4 : 2);
        classMatchers.add(instanceCall(instanceClassName, methodName).parameterTypes(CLASS_THROWABLE));
        classMatchers.add(instanceCall(instanceClassName, methodName).parameterTypes(CLASS_THROWABLE, CLASS_THROWABLES));
        if (staticClassName != null) {
            classMatchers.add(staticCall(staticClassName, methodName).parameterTypes(CLASS_THROWABLE));
            classMatchers.add(staticCall(staticClassName, methodName).parameterTypes(CLASS_THROWABLE, CLASS_THROWABLES));
        }
        return anyOf(classMatchers.toArray(CALL_MATCHER_EMPTY));
    }

    /**
     * Similar to {@link #createClassMatcher(String, String, String)}, but creates the matcher for the {@code java.lang.Throwable...}
     * specific parametization.
     */
    private CallMatcher createThrowablesMatcher(String methodName, String instanceClassName, @Nullable String staticClassName) {
        var throwablesMatchers = new ArrayList<CallMatcher>(2);
        throwablesMatchers.add(instanceCall(instanceClassName, methodName).parameterTypes(JAVA_LANG_THROWABLES));
        if (staticClassName != null) {
            throwablesMatchers.add(staticCall(staticClassName, methodName).parameterTypes(JAVA_LANG_THROWABLES));
        }
        return anyOf(throwablesMatchers.toArray(CALL_MATCHER_EMPTY));
    }
}
