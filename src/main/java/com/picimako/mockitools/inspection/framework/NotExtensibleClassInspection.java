//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.framework;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_NOT_EXTENSIBLE;

import java.util.Arrays;
import java.util.Optional;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiTypeParameter;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports any types of classes or interfaces that extend a class or at least one interface annotated as Mockito's {@code NotExtensible} annotation.
 * <p>
 * This inspection is meant for Mockito framework integrators, not regular end-users, this is disabled by default in the plugin.xml.
 *
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#framework_integrations_api">Advanced public API for framework integrations (Since 2.10.+)</a>
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/NotExtensible.html">@NotExtensible javadoc</a>
 * @since 0.1.0
 */
final class NotExtensibleClassInspection extends LocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new JavaElementVisitor() {
            @Override
            public void visitClass(PsiClass aClass) {
                if (aClass.isAnnotationType()
                    || aClass.isEnum()
                    || aClass.isRecord()
                    || aClass instanceof PsiTypeParameter
                    || aClass instanceof PsiAnonymousClass
                    || aClass.getExtendsList() == null) {
                    return;
                }
                if (aClass.isInterface()) {
                    if (Arrays.stream(aClass.getExtendsListTypes())
                        .anyMatch(superInterface -> Optional.ofNullable(superInterface.resolve()).map(i -> i.hasAnnotation(ORG_MOCKITO_NOT_EXTENSIBLE)).orElse(false))) {
                        //at this point the name identifier should not be null
                        holder.registerProblem(aClass.getNameIdentifier(), MockitoolsBundle.message("inspection.interface.extends.not.extensible"));
                    }
                } else if (aClass.getSuperClass() != null && aClass.getSuperClass().hasAnnotation(ORG_MOCKITO_NOT_EXTENSIBLE)) {
                    //at this point the name identifier should not be null
                    holder.registerProblem(aClass.getNameIdentifier(), MockitoolsBundle.message("inspection.class.extends.not.extensible"));
                }
            }
        };
    }
}
