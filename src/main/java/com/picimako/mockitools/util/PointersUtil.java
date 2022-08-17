/*
 * Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package com.picimako.mockitools.util;

import static java.util.stream.Collectors.toList;

import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Utility for {@link SmartPsiElementPointer}s.
 */
public final class PointersUtil {

    public static <T extends PsiElement> List<SmartPsiElementPointer<T>> toPointers(List<T> calls) {
        return calls.stream().map(PointersUtil::toPointer).collect(toList());
    }

    @NotNull
    public static <T extends PsiElement> SmartPsiElementPointer<T> toPointer(T element) {
        return SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element, element.getContainingFile());
    }

    private PointersUtil() {
        //Utility class
    }
}
