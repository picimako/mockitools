//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoolsPsiUtil.INORDER_VERIFY;
import static com.picimako.mockitools.PsiMethodUtil.getReferenceNameElement;
import static com.picimako.mockitools.UnitTestPsiUtil.isInTestSourceContent;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.profile.codeInspection.InspectionProfileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.ui.JBUI;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;
import com.picimako.mockitools.resources.MockitoolsBundle;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;

/**
 * Helps to enforce project conventions for using Mockito.
 * <p>
 * It reports calls to static stubbing and verification methods of {@code org.mockito.Mockito / org.mockito.InOrder}
 * if {@code org.mockito.BDDMockito} based stubbing and verification must be used, and vice versa.
 * <p>
 * Whether to enforce one or the other can be configured on the inspection's options panel.
 * <p>
 * The inspection is not enabled by default since a project might not want to enforce these conventions.
 * <p>
 * There are no dedicated quick fixes since separate intention actions are available to convert between these approaches.
 * See subclasses of {@link com.picimako.mockitools.intention.convert.stub.ConvertStubbingIntentionBase}
 * and {@link ConvertVerificationIntentionBase}.
 *
 * @since 0.4.0
 */
public class EnforceConventionInspection extends MockitoolsBaseInspection {
    public static final String SHORT_NAME = "EnforceConvention";
    public static final CallMatcher IN_ORDER_VERIFY = CallMatcher.anyOf(
        INORDER_VERIFY.parameterCount(1),
        INORDER_VERIFY.parameterCount(2));
    private static final CallMatcher MOCKITO_MATCHER = CallMatcher.anyOf(
        CallMatcher.staticCall(ORG_MOCKITO_MOCKITO,
            "when", "doReturn", "doThrow", "doAnswer", "doCallRealMethod", "doNothing", //stubbing
            "verify", "verifyNoMoreInteractions", "verifyNoInteractions", //verification
            "verifyZeroInteractions" //to support Mockito 3.x
        ), IN_ORDER_VERIFY);
    private static final CallMatcher BDDMOCKITO_MATCHER = CallMatcher.staticCall(ORG_MOCKITO_BDDMOCKITO,
        "given", "will", "willReturn", "willThrow", "willAnswer", "willCallRealMethod", "willDoNothing", //stubbing
        "then" //verification
    );

    @SuppressWarnings("PublicField")
    public Convention conventionToEnforce = Convention.MOCKITO;

    @Nullable
    @Override
    public JComponent createOptionsPanel() {
        final JPanel panel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 5, true, false));
        panel.add(new JLabel(MockitoolsBundle.inspectionOption("enforce.stubbing.and.verification.label")));

        var group = new ButtonGroup();
        for (var convention : Convention.values()) {
            var radioButton = new JRadioButton(convention.getMessage(), convention == conventionToEnforce);
            radioButton.setBorder(JBUI.Borders.emptyLeft(20));
            radioButton.addActionListener(e -> conventionToEnforce = convention);
            panel.add(radioButton);
            group.add(radioButton);
        }

        return panel;
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return isInTestSourceContent(session.getFile()) ? methodCallVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
    }

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (conventionToEnforce == Convention.MOCKITO) {
            if (BDDMOCKITO_MATCHER.matches(expression)) register(expression, holder);
        } else if (conventionToEnforce == Convention.BDD_MOCKITO && MOCKITO_MATCHER.matches(expression))
            register(expression, holder);
    }

    private void register(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        holder.registerProblem(Optional.ofNullable(getReferenceNameElement(expression)).orElse(expression),
            MockitoolsBundle.inspection("stubbing.and.verification.must.be.performed.via.x", conventionToEnforce.getMessage()));
    }

    // Static helpers

    /**
     * Returns whether {@link EnforceConventionInspection} is enabled in the inspection profile currently active in the current project,
     * and {@link Convention#MOCKITO} is being enforced.
     *
     * @param methodCall the method call the intention availability is being checked
     */
    public static boolean isMockitoEnforced(PsiElement methodCall) {
        return isEnforced(methodCall, Convention.MOCKITO);
    }

    /**
     * Returns whether {@link EnforceConventionInspection} is enabled in the inspection profile currently active in the current project,
     * and {@link Convention#BDD_MOCKITO} is being enforced.
     *
     * @param methodCall the method call the intention availability is being checked
     */
    public static boolean isBDDMockitoEnforced(PsiElement methodCall) {
        return isEnforced(methodCall, Convention.BDD_MOCKITO);
    }

    private static boolean isEnforced(PsiElement methodCall, Convention convention) {
        var profile = InspectionProfileManager.getInstance(methodCall.getProject()).getCurrentProfile();
        if (profile.isToolEnabled(HighlightDisplayKey.find(SHORT_NAME))) {
            var enforceConvention = (EnforceConventionInspection) profile.getUnwrappedTool(SHORT_NAME, methodCall);
            return enforceConvention != null && enforceConvention.conventionToEnforce == convention;
        }
        return false;
    }

    //Convention type

    public enum Convention {
        MOCKITO(ORG_MOCKITO_MOCKITO + " / " + ORG_MOCKITO_INORDER),
        BDD_MOCKITO(ORG_MOCKITO_BDDMOCKITO);

        private final String classFqn;

        Convention(String classFqn) {
            this.classFqn = classFqn;
        }

        public @Nls String getMessage() {
            return classFqn;
        }
    }
}
