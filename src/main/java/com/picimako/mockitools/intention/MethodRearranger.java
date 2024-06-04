//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.intellij.openapi.application.ReadAction.compute;

import com.google.common.collect.ImmutableList;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.MethodCellRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.util.PsiFormatUtil;
import com.intellij.psi.util.PsiFormatUtilBase;
import com.intellij.util.SmartList;

import javax.swing.*;
import java.util.List;
import java.util.Set;

/**
 * Reorders {@link PsiMethod}s for better visual presentation in list popups.
 *
 * @see ConvertMockSpyFieldToCallIntention
 * @since 0.7.0
 */
public final class MethodRearranger {

    private static final Set<String> BEFORE_ANNOTATIONS = Set.of(
        //JUnit 4
        "org.junit.Before", "org.junit.BeforeClass",
        //JUnit 5
        "org.junit.jupiter.api.BeforeAll", "org.junit.jupiter.api.BeforeEach",
        //TestNG
        "org.testng.annotations.BeforeSuite", "org.testng.annotations.BeforeTest",
        "org.testng.annotations.BeforeGroups", "org.testng.annotations.BeforeClass",
        "org.testng.annotations.BeforeMethod"
    );
    private static final Set<String> TEST_ANNOTATIONS = Set.of("org.junit.Test", "org.junit.jupiter.api.Test", "org.testng.annotations.Test");

    /**
     * Returns a reordered variant of the argument methods array.
     * <p>
     * Before hooks come first (see {@link #BEFORE_ANNOTATIONS}), then test methods (see {@link #TEST_ANNOTATIONS}),
     * then the rest of the methods.
     * <p>
     * After hooks are not distinguished because it not likely that mock creation will happen in an after hook.
     *
     * @param methods the methods to reorder
     */
    public static List<PsiMethod> reOrder(PsiMethod[] methods) {
        var beforeHooks = new SmartList<PsiMethod>();
        var testMethods = new SmartList<PsiMethod>();
        var restOfMethods = new SmartList<PsiMethod>();

        for (var method : methods) {
            if (BEFORE_ANNOTATIONS.stream().anyMatch(annotation -> compute(() -> method.hasAnnotation(annotation)))) beforeHooks.add(method);
            else if (TEST_ANNOTATIONS.stream().anyMatch(annotation -> compute(() -> method.hasAnnotation(annotation)))) testMethods.add(method);
            else restOfMethods.add(method);
        }

        return new ImmutableList.Builder<PsiMethod>().addAll(beforeHooks).addAll(testMethods).addAll(restOfMethods).build();
    }

    private MethodRearranger() {
        //Utility
    }

    /**
     * Custom cell renderer for {@link ConvertMockSpyFieldToCallIntention} that displays only the method signature
     * without the container class to minimize noise in the target method selection list popup.
     * <p>
     * Hooks and test methods are displayed with dedicated icons to better distinguish them visually.
     */
    static final class ClassMethodCellRenderer extends MethodCellRenderer {
        ClassMethodCellRenderer() {
            super(true);
        }

        @Override
        public String getContainerText(PsiMethod element, String name) {
            //No container text is displayed
            return null;
        }

        @Override
        public String getElementText(PsiMethod element) {
            //The element text is always method name and the parameter list, e.g. 'testMethod(String)'
            return PsiFormatUtil.formatMethod(element, PsiSubstitutor.EMPTY, PsiFormatUtilBase.SHOW_NAME | PsiFormatUtilBase.SHOW_PARAMETERS, PsiFormatUtilBase.SHOW_TYPE);
        }

        @Override
        protected Icon getIcon(PsiElement element) {
            if (BEFORE_ANNOTATIONS.stream().anyMatch(ann -> ((PsiMethod) element).hasAnnotation(ann)))
                return AllIcons.Gutter.ExtAnnotation;
            else if (TEST_ANNOTATIONS.stream().anyMatch(ann -> ((PsiMethod) element).hasAnnotation(ann)))
                return AllIcons.Actions.Execute;
            return super.getIcon(element);
        }
    }
}
