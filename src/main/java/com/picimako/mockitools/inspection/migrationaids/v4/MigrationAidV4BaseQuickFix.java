//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_MATCHERS;
import static com.picimako.mockitools.PsiMethodUtil.getQualifier;
import static com.siyeh.ig.psiutils.ImportUtils.addStaticImport;
import static com.siyeh.ig.psiutils.ImportUtils.isAlreadyStaticallyImported;

import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.inspection.migrationaids.v4.ArgumentMatchersCalledViaMatchersInspection.ReplaceMatchersWithArgumentMatchersQuickFix;

/**
 * Base class for Mockito v4 migration aid quick fixes.
 */
public abstract class MigrationAidV4BaseQuickFix extends InspectionGadgetsFix {

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return com.picimako.mockitools.resources.MockitoolsBundle.quickFixFamily("migration.aid.v4");
    }

    /**
     * Replaces org.mockito.Matchers qualifiers with org.mockito.ArgumentMatchers and static imports the matcher
     * that is provided in {@code matcherToImport}.
     */
    protected void replaceMatchersQualifierAndStaticImportMatcher(PsiMethodCallExpression parentCall, String matcherToImport, PsiElement context) {
        if (!replaceMatchersQualifier(parentCall) && !isAlreadyStaticallyImported(parentCall.getMethodExpression())) {
            staticImport(parentCall, ORG_MOCKITO_ARGUMENT_MATCHERS, matcherToImport, context);
        }
    }

    /**
     * Static imports {@code method} from the {@code clazz} class if it is not already imported.
     */
    protected void staticImport(PsiMethodCallExpression parentCall, String clazz, String method, PsiElement context) {
        if (!isAlreadyStaticallyImported(parentCall.getMethodExpression())) {
            addStaticImport(clazz, method, context);
        }
    }

    /**
     * Returns whether the argument method call has a qualifier or not.
     */
    protected boolean replaceMatchersQualifier(PsiMethodCallExpression parentCall) {
        if (parentCall.getMethodExpression().isQualified()) {
            new ReplaceMatchersWithArgumentMatchersQuickFix().replace(getQualifier(parentCall), parentCall.getProject());
            return true;
        }
        return false;
    }
}
