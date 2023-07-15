//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.picimako.mockitools.MockitoQualifiedNames.ANY;
import static com.picimako.mockitools.MockitoQualifiedNames.ANY_COLLECTION_OF;
import static com.picimako.mockitools.MockitoQualifiedNames.ANY_ITERABLE_OF;
import static com.picimako.mockitools.MockitoQualifiedNames.ANY_LIST_OF;
import static com.picimako.mockitools.MockitoQualifiedNames.ANY_OBJECT;
import static com.picimako.mockitools.MockitoQualifiedNames.ANY_SET_OF;
import static com.picimako.mockitools.MockitoQualifiedNames.ANY_VARARG;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_MATCHERS;
import static com.picimako.mockitools.util.PsiMethodUtil.getParentCall;
import static com.picimako.mockitools.util.PsiMethodUtil.getReferenceNameElement;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.MockitoQualifiedNames;
import com.picimako.mockitools.util.PsiMethodUtil;
import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports problems with calls to {@code anyX()} matchers which are deprecated since Mockito v2, and are removed in v4.
 * <p>
 * It also provides quick fixes
 * <ul>
 *     <li>to replace {@code anyObject()} and {@code anyVararg()} with {@code any()},</li>
 *     <li>to replace {@code anyCollectionOf()}, {@code anyIterableOf()}, {@code anyListOf()}, {@code anyMapOf()} and {@code anySetOf()}
 *     with their {@code anyX()} variants.</li>
 * </ul>
 *
 * @since 0.1.0
 */
public class UsageOfAnyMatchersInspection extends MigrationAidBase.V23ToV4BaseInspection {
    private static final CallMatcher ANY_OBJECT_OR_ANY_VARARG = CallMatcher.staticCall(ORG_MOCKITO_ARGUMENT_MATCHERS, ANY_OBJECT, ANY_VARARG);
    private static final CallMatcher ANY_COLLECTION = CallMatcher.anyOf(
        CallMatcher.staticCall(ORG_MOCKITO_ARGUMENT_MATCHERS, ANY_COLLECTION_OF, ANY_ITERABLE_OF, ANY_LIST_OF, ANY_SET_OF).parameterCount(1),
        CallMatcher.staticCall(ORG_MOCKITO_ARGUMENT_MATCHERS, MockitoQualifiedNames.ANY_MAP_OF).parameterCount(2)
    );

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (ANY_OBJECT_OR_ANY_VARARG.matches(expression)) {
            holder.registerProblem(
                getReferenceNameElement(expression), //referenceName null value is already checked by the CallMatcher
                MockitoolsBundle.message("inspection.migration.aid.v4.use.any", getMethodName(expression)),
                new ReplaceAnyObjectOrAnyVarargWithAnyQuickFix());
            return;
        }
        if (ANY_COLLECTION.matches(expression)) {
            holder.registerProblem(
                getReferenceNameElement(expression), //referenceName null value is already checked by the CallMatcher
                MockitoolsBundle.message("inspection.migration.aid.v4.use.any.collection.type", getMethodName(expression)),
                new ReplaceAnyXOfWithAnyXQuickFix(getMethodName(expression)));
        }
    }

    private static final class ReplaceAnyObjectOrAnyVarargWithAnyQuickFix extends MigrationAidV4BaseQuickFix {
        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.message("quick.fix.migration.aid.v4.replace.with", ANY);
        }

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) {
            var parentCall = getParentCall(descriptor.getPsiElement());
            if (parentCall != null) {
                PsiElement elementAfterReplace = descriptor.getPsiElement().replace(PsiElementFactory.getInstance(project).createIdentifier(ANY));
                replaceMatchersQualifierAndStaticImportMatcher(parentCall, ANY, elementAfterReplace);
            }
        }
    }

    private static final class ReplaceAnyXOfWithAnyXQuickFix extends MigrationAidV4BaseQuickFix {
        private static final Pattern ANY_X_OF_PATTERN = Pattern.compile("(?<anyX>any(Collection|Iterable|List|Map|Set))Of");
        private final String anyX;

        public ReplaceAnyXOfWithAnyXQuickFix(String referenceName) {
            this.anyX = getAnyX(referenceName);
        }

        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.message("quick.fix.migration.aid.v4.replace.with", anyX);
        }

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) {
            var parentCall = getParentCall(descriptor.getPsiElement());
            if (parentCall != null) {
                PsiElement elementAfterReplace = descriptor.getPsiElement().replace(PsiElementFactory.getInstance(project).createIdentifier(anyX));
                PsiMethodUtil.deleteArguments(parentCall); //delete all matcher call arguments, since any<collectionType>() methods don't have arguments
                replaceMatchersQualifierAndStaticImportMatcher(parentCall, anyX, elementAfterReplace);
            }
        }

        private String getAnyX(String referenceName) {
            Matcher matcher = ANY_X_OF_PATTERN.matcher(referenceName);
            matcher.matches(); //no need to check if it matches. It already matches because of the initial CallMatcher.
            return matcher.group("anyX");
        }
    }
}
