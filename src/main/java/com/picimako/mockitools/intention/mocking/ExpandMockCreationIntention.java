//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.mocking;

import static com.intellij.openapi.application.ReadAction.compute;
import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.picimako.mockitools.MockitoMockMatchers.MOCKITO_SPY_T;
import static com.picimako.mockitools.MockitoMockMatchers.MOCK_WITH_ANSWER;
import static com.picimako.mockitools.MockitoMockMatchers.MOCK_WITH_NAME;
import static com.picimako.mockitools.util.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getMethodCallAtCaretOrEmpty;
import static com.picimako.mockitools.util.PsiMethodUtil.hasArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.hasTwoArguments;

import java.util.function.Supplier;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.util.IncorrectOperationException;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This intention action converts certain mock/spy creation calls to use concrete {@code MockSettings}
 * configuration, and aims to simplify the process of converting mock creation logic when
 * further mock settings need to be added.
 *
 * @see com.picimako.mockitools.inspection.mocking.SimplifyMockCreationInspection
 * @since 0.11.0
 */
final class ExpandMockCreationIntention implements IntentionAction {

    //Intention name

    @Override
    public @IntentionName @NotNull String getText() {
        return MockitoolsBundle.message("intention.expand.mock.creation");
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return MockitoolsBundle.message("intention.expand.mock.creation.family");
    }

    //Availability

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return getMethodCallAtCaretOrEmpty(file, editor)
            .map(call -> compute(() -> {
                if (MOCKITO_SPY_T.matches(call)) return hasArgument(call);
                if (MOCK_WITH_NAME.matches(call) || MOCK_WITH_ANSWER.matches(call))
                    return hasTwoArguments(call);
                return false;
            })).orElse(false);
    }

    //Conversion

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        getMethodCallAtCaretOrEmpty(file, editor).ifPresent(call -> {
            //Mockito.spy(spiedInstance) -> Mockito.mock(<raw type of spiedInstance>, withSettings().spiedInstance(spiedInstance))
            if (compute(() -> MOCKITO_SPY_T.matches(call))) {
                expand(call, project, () -> {
                    var spiedInstance = getFirstArgument(call);
                    return String.format(
                        "org.mockito.Mockito.mock(%s.class, org.mockito.Mockito.withSettings().spiedInstance(%s))",
                        getRawType(spiedInstance.getType()).getCanonicalText(), spiedInstance.getText());
                });
            }
            //Mockito.mock(<type>.class, <name>) -> Mockito.mock(<type>.class, withSettings().name(<name>))
            else if (compute(() -> MOCK_WITH_NAME.matches(call))) {
                expand(call, project, () -> String.format(
                    "org.mockito.Mockito.mock(%s, org.mockito.Mockito.withSettings().name(%s))",
                    getFirstArgument(call).getText(), get2ndArgument(call).getText()));
            }
            //Mockito.mock(<type>.class, <answer>) -> Mockito.mock(<type>.class, withSettings().defaultAnswer(<answer>))
            else if (compute(() -> MOCK_WITH_ANSWER.matches(call))) {
                expand(call, project, () -> String.format(
                    "org.mockito.Mockito.mock(%s, org.mockito.Mockito.withSettings().defaultAnswer(%s))",
                    getFirstArgument(call).getText(), get2ndArgument(call).getText()));
            }
        });
    }

    @Nullable
    private PsiType getRawType(PsiType type) {
        return type instanceof PsiClassReferenceType refType ? refType.rawType() : null;
    }

    private void expand(PsiMethodCallExpression mockitoSpyOrMock, Project project, Supplier<String> replacementText) {
        runWriteCommandAction(project, () -> {
            var expandedReplacementCall = JavaCodeStyleManager.getInstance(project)
                .shortenClassReferences(JavaPsiFacade.getElementFactory(project)
                    .createExpressionFromText(replacementText.get(), mockitoSpyOrMock));
            mockitoSpyOrMock.replace(expandedReplacementCall);
        });
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
