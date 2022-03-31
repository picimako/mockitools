//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.psi.PsiMethodCallExpression;

/**
 * An extension of {@link ConsecutiveCallDescriptor} that stores an additional {@link ThrowStubDescriptor} and convenience methods ot work with it.
 */
public class ConsecutiveThrowCallDescriptor extends ConsecutiveCallDescriptor {

    private final ThrowStubDescriptor throwDescriptor;

    public ConsecutiveThrowCallDescriptor(String mockitoClass, String consecutiveMethodName, int indexToStartInspectionAt, ThrowStubDescriptor throwDescriptor,
                                          String... chainStarterMethodNames) {
        super(mockitoClass, consecutiveMethodName, indexToStartInspectionAt, chainStarterMethodNames);
        this.throwDescriptor = throwDescriptor;
    }

    public boolean isCallToClasses(PsiMethodCallExpression call) {
        return throwDescriptor.classMatcher.matches(call);
    }
    
    public boolean isCallToThrowables(PsiMethodCallExpression call) {
        return throwDescriptor.throwablesMatcher.matches(call);
    }
}
