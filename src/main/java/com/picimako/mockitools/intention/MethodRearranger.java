//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.intellij.openapi.application.ReadAction.compute;

import com.google.common.collect.ImmutableList;
import com.intellij.psi.PsiMethod;
import com.intellij.util.SmartList;

import java.util.List;
import java.util.Set;

/**
 * Reorders {@link PsiMethod}s for better visual presentation in list popups.
 *
 * @see ConvertMockSpyFieldToCallIntention
 * @since 0.7.0
 */
public final class MethodRearranger {

    public static final Set<String> BEFORE_ANNOTATIONS = Set.of(
        //JUnit 4
        "org.junit.Before", "org.junit.BeforeClass",
        //JUnit 5
        "org.junit.jupiter.api.BeforeAll", "org.junit.jupiter.api.BeforeEach",
        //TestNG
        "org.testng.annotations.BeforeSuite", "org.testng.annotations.BeforeTest",
        "org.testng.annotations.BeforeGroups", "org.testng.annotations.BeforeClass",
        "org.testng.annotations.BeforeMethod"
    );
    public static final Set<String> TEST_ANNOTATIONS = Set.of("org.junit.Test", "org.junit.jupiter.api.Test", "org.testng.annotations.Test");

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
}
