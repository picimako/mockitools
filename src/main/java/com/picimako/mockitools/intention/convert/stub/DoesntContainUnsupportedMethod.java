//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.List;

import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.CallChainAnalyzer;

/**
 * Analyzes a call chain whether it contains a call to the method with a predefined name.
 * <p>
 * It is used to filter out call chains for stubbing approach conversions with no matching alternative in the target approach.
 */
public final class DoesntContainUnsupportedMethod implements CallChainAnalyzer {
    public static final DoesntContainUnsupportedMethod DOESNT_CONTAIN_THEN = new DoesntContainUnsupportedMethod("then");
    public static final DoesntContainUnsupportedMethod DOESNT_CONTAIN_WILL = new DoesntContainUnsupportedMethod("will");
    public static final DoesntContainUnsupportedMethod DOESNT_CONTAIN_DO_NOTHING = new DoesntContainUnsupportedMethod("doNothing");
    public static final DoesntContainUnsupportedMethod DOESNT_CONTAIN_WILL_DO_NOTHING = new DoesntContainUnsupportedMethod("willDoNothing");

    private final String unsupportedMethodName;

    public DoesntContainUnsupportedMethod(String unsupportedMethodName) {
        this.unsupportedMethodName = unsupportedMethodName;
    }

    @Override
    public boolean analyze(List<PsiMethodCallExpression> calls) {
        return calls.stream().noneMatch(call -> unsupportedMethodName.equals(getMethodName(call)));
    }
}
