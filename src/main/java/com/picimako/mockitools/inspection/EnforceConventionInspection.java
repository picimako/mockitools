//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE;
import static com.picimako.mockitools.MockitoolsPsiUtil.INORDER_VERIFY;
import static com.picimako.mockitools.PsiMethodUtil.getReferenceNameElement;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.profile.codeInspection.InspectionProfileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.ui.JBUI;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;
import com.picimako.mockitools.resources.MockitoolsBundle;
import com.siyeh.ig.callMatcher.CallMatcher;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
 * <p>
 * {@code MockedStatic} specific InOrder.verify() methods are excluded from the enforcement.
 *
 * @since 0.4.0
 */
public class EnforceConventionInspection extends MockitoolsBaseInspection {
    public static final String SHORT_NAME = "EnforceConvention";
    //MockedStatic specific verify() methods are excluded, since BDDMockito has no way to verify MockedStatic
    public static final CallMatcher IN_ORDER_VERIFY_NON_MOCKED_STATIC = CallMatcher.anyOf(
        INORDER_VERIFY.parameterTypes("T"),
        INORDER_VERIFY.parameterTypes("T", ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE));
    private static final CallMatcher MOCKITO_MATCHER = CallMatcher.anyOf(
        staticCall(ORG_MOCKITO_MOCKITO,
            "when", "doReturn", "doThrow", "doAnswer", "doCallRealMethod", "doNothing", //stubbing
            "verify", "verifyNoMoreInteractions", "verifyNoInteractions", //verification
            "verifyZeroInteractions" //to support Mockito 3.x
        ), IN_ORDER_VERIFY_NON_MOCKED_STATIC);
    private static final CallMatcher BDDMOCKITO_MATCHER = staticCall(ORG_MOCKITO_BDDMOCKITO,
        "given", "will", "willReturn", "willThrow", "willAnswer", "willCallRealMethod", "willDoNothing", //stubbing
        "then" //verification
    );

    @SuppressWarnings("PublicField")
    public Convention conventionToEnforce = Convention.MOCKITO;

    @Nullable
    @Override
    public JComponent createOptionsPanel() {
        final var panel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 5, true, false));
        panel.add(new JLabel(MockitoolsBundle.inspectionOption("enforce.stubbing.and.verification.label")));

        var group = new ButtonGroup();
        for (var convention : Convention.values()) {
            var radioButton = new JRadioButton(convention.getClassFqn(), convention == conventionToEnforce);
            radioButton.setBorder(JBUI.Borders.emptyLeft(20));
            radioButton.addActionListener(e -> conventionToEnforce = convention);
            panel.add(radioButton);
            group.add(radioButton);
        }

        return panel;
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
            MockitoolsBundle.inspection("stubbing.and.verification.must.be.performed.via.x", conventionToEnforce.getClassFqn()));
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

    @Getter
    @RequiredArgsConstructor
    public enum Convention {
        MOCKITO(ORG_MOCKITO_MOCKITO + " / " + ORG_MOCKITO_INORDER),
        BDD_MOCKITO(ORG_MOCKITO_BDDMOCKITO);

        private final String classFqn;
    }
}
