//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import java.util.List;
import java.util.Optional;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.introduceField.ElementToWorkOn;
import com.intellij.util.SmartList;
import com.siyeh.ig.psiutils.ImportUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for handling PsiClasses.
 */
public final class PsiClassUtil {

    /**
     * Returns the list of parent classes for the argument expression.
     * <p>
     * Based on {@link com.intellij.refactoring.introduceField.BaseExpressionToFieldHandler}.
     */
    public static List<PsiClass> getParentClasses(@NotNull PsiExpression expression) {
        var parentClasses = new SmartList<PsiClass>();
        PsiElement parent = Optional.ofNullable(expression.getUserData(ElementToWorkOn.PARENT)).orElseGet(expression::getParent);
        while (parent != null) {
            if (parent instanceof PsiClass) {
                parentClasses.add((PsiClass) parent);
            }
            parent = PsiTreeUtil.getParentOfType(parent, PsiClass.class);
        }
        return parentClasses;
    }

    public static void importClass(String fqn, PsiElement context) {
        PsiClass collectionClass = JavaPsiFacade.getInstance(context.getProject()).findClass(fqn, ProjectScope.getAllScope(context.getProject()));
        ImportUtils.addImportIfNeeded(collectionClass, context);
    }

    private PsiClassUtil() {
        //Utility class
    }
}
