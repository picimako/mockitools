//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Utility for working with module dependencies.
 *
 * @see <a href="https://plugins.jetbrains.com/docs/intellij/module.html#how-do-i-get-dependencies-and-classpath-of-a-module">How do I get dependencies and classpath of a module?</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ModuleDependencyHelper {
    private static final String MOCKITO_CORE_2_X = "mockito-core-2.";
    private static final String MOCKITO_CORE_3_X = "mockito-core-3.";

    /**
     * Returns whether mockito-core-3.x is available in the module where the argument file is located.
     */
    static boolean isMockitoCore3xAvailableInModuleOf(@NotNull PsiFile file, Project project) {
        return isMockitoXAvailableInModuleOf(file, project, lib -> lib.getName().startsWith(MOCKITO_CORE_3_X));
    }

    /**
     * Returns whether mockito-core-2.x or mockito-core-3.x is available in the module where the argument file is located.
     */
    static boolean isMockitoCore2xOr3xAvailableInModuleOf(@NotNull PsiFile file, Project project) {
        return isMockitoXAvailableInModuleOf(file, project, lib -> lib.getName().startsWith(MOCKITO_CORE_2_X) || lib.getName().startsWith(MOCKITO_CORE_3_X));
    }

    private static boolean isMockitoXAvailableInModuleOf(@NotNull PsiFile file, Project project, Predicate<VirtualFile> isMatchingMockitoLib) {
        var module = ModuleUtilCore.findModuleForFile(file.getVirtualFile(), project);
        return module != null && Arrays.stream(OrderEnumerator.orderEntries(module)
                .librariesOnly()
                .compileOnly()
                .classes()
                .usingCache()
                .getRoots())
            .anyMatch(isMatchingMockitoLib);
    }
}
