//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.intellij.psi.search.GlobalSearchScope.moduleWithLibrariesScope;
import static com.picimako.mockitools.MockitoQualifiedNames.ARGUMENT_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MATCHERS;
import static com.siyeh.ig.psiutils.ImportUtils.nameCanBeImported;

import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.siyeh.ig.psiutils.ImportUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Provides common replacements for expression qualifiers.
 */
final class QualifierReplacements {

    /**
     * Replaces {@code org.mockito.Matchers} qualifiers with {@code org.mockito.ArgumentMatchers}.
     *
     * @param qualifier the qualifier expression to replace. 'org.mockito.Matchers' or 'Matchers'
     */
    static void replaceMatchersWithArgumentMatchers(PsiExpression qualifier, @NotNull Project project) {
        //if the matcher is referenced with fully qualified name: replace the fqn appropriately
        //This also respects that the class is referenced by its fqn rather via import by keeping the fqn of ArgumentMatchers.
        if (qualifier.textMatches(ORG_MOCKITO_MATCHERS)) {
            replaceQualifierWith(ORG_MOCKITO_ARGUMENT_MATCHERS, qualifier);
        }
        //if the matcher is referenced as Matchers.<matcher>:
        //  then replace the classname (the qualifier) with ArgumentMatchers,
        //  add import statement for org.mockito.ArgumentMatchers if necessary
        else if (qualifier.textMatches(MATCHERS)) {
            PsiElement elementAfterReplace = replaceQualifierWith(ARGUMENT_MATCHERS, qualifier);
            if (nameCanBeImported(ORG_MOCKITO_ARGUMENT_MATCHERS, elementAfterReplace)) {
                //Module scope is used because different modules may use different versions of Mockito
                Optional.ofNullable(ModuleUtilCore.findModuleForFile(elementAfterReplace.getContainingFile().getVirtualFile(), project))
                    .map(module -> JavaPsiFacade.getInstance(project).findClass(ORG_MOCKITO_ARGUMENT_MATCHERS, moduleWithLibrariesScope(module)))
                    .ifPresent(argumentMatchers -> ImportUtils.addImportIfNeeded(argumentMatchers, elementAfterReplace));
            }
        }
    }

    private static PsiElement replaceQualifierWith(String replacement, PsiExpression qualifier) {
        return qualifier.replace(PsiElementFactory.getInstance(qualifier.getProject()).createExpressionFromText(replacement, qualifier));
    }
}
