//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.picimako.mockitools;

import java.util.Arrays;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for working with module dependencies.
 *
 * @see <a href="https://plugins.jetbrains.com/docs/intellij/module.html#how-do-i-get-dependencies-and-classpath-of-a-module">How do I get dependencies and classpath of a module?</a>
 */
public final class ModuleDependencyHelper {

    private static final String MOCKITO_CORE_2_X = "mockito-core-2.";
    private static final String MOCKITO_CORE_3_X = "mockito-core-3.";

    /**
     * Returns whether mockito-core-3.x is available in the module where the argument file is located.
     */
    public static boolean isMockitoCore3xAvailableInModuleOf(@NotNull PsiFile file, Project project) {
        Module module = ModuleUtilCore.findModuleForFile(file.getVirtualFile(), project);
        return module != null && Arrays.stream(OrderEnumerator.orderEntries(module)
                .librariesOnly()
                .compileOnly()
                .classes()
                .usingCache()
                .getRoots())
            .anyMatch(lib -> lib.getName().startsWith(MOCKITO_CORE_3_X));
    }

    /**
     * Returns whether mockito-core-2.x or mockito-core-3.x is available in the module where the argument file is located.
     */
    public static boolean isMockitoCore2xOr3xAvailableInModuleOf(@NotNull PsiFile file, Project project) {
        Module module = ModuleUtilCore.findModuleForFile(file.getVirtualFile(), project);
        return module != null && Arrays.stream(OrderEnumerator.orderEntries(module)
                .librariesOnly()
                .compileOnly()
                .classes()
                .usingCache() //to improve performance, since this check is executed at the beginning of migration aid inspections
                .getRoots())
            .anyMatch(lib -> lib.getName().startsWith(MOCKITO_CORE_2_X) || lib.getName().startsWith(MOCKITO_CORE_3_X));
    }

    private ModuleDependencyHelper() {
        //Utility class
    }
}
