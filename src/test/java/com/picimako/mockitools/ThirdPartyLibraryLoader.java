//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import java.io.File;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Loads third-party libraries for unit testing.
 */
public final class ThirdPartyLibraryLoader {

    private static final String THIRD_PARTY_LIB_DIRECTORY = "lib";

    public static void loadMockito3(@NotNull Disposable projectDisposable, @NotNull Module module) {
        loadLibrary(projectDisposable, module, "Mockito 3 Library", "mockito-core-3.11.2.jar");
    }

    public static void loadMockito4Latest(@NotNull Disposable projectDisposable, @NotNull Module module) {
        loadLibrary(projectDisposable, module, "Mockito 4 Library", "mockito-core-4.6.1.jar");
    }

    public static void loadJUnit4(@NotNull Disposable projectDisposable, @NotNull Module module) {
        loadLibrary(projectDisposable, module, "JUnit 4 Library", "junit-4.13.2.jar");
    }

    /**
     * Loads the library with the given filename from the [PROJECT_ROOT]/lib folder.
     *
     * @param projectDisposable project disposable from test fixture
     * @param module            the module of the current test fixture
     * @param libraryName       the name of the library
     * @param libraryJarName    the filename to load
     */
    public static void loadLibrary(@NotNull Disposable projectDisposable, @NotNull Module module, String libraryName, String libraryJarName) {
        String libPath = PathUtil.toSystemIndependentName(new File(THIRD_PARTY_LIB_DIRECTORY).getAbsolutePath());
        VfsRootAccess.allowRootAccess(projectDisposable, libPath);
        PsiTestUtil.addLibrary(projectDisposable, module, libraryName, libPath, libraryJarName);
    }

    private ThirdPartyLibraryLoader() {
        //Utility class
    }
}
