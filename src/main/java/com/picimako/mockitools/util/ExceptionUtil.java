//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.util;

import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.InheritanceUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Utility for exceptions.
 */
public final class ExceptionUtil {

    /**
     * Returns whether the exception class referenced by the argument expression is a checked exception.
     */
    public static boolean isCheckedException(PsiExpression expression) {
        return expression instanceof PsiClassObjectAccessExpression
            ? isCheckedException(ClassObjectAccessUtil.getOperandType(expression))
            : isCheckedException(expression.getType());
    }

    /**
     * Returns whether the argument type is a checked exception.
     */
    private static boolean isCheckedException(@Nullable PsiType type) {
        return !InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_LANG_RUNTIME_EXCEPTION)
            && !InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_LANG_ERROR);
    }

    private ExceptionUtil() {
        //Utility class
    }
}
