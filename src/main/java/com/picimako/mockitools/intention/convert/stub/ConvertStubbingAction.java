//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.intention.convert.FromSelectionDataRetriever.collectStatementsInSelection;
import static com.picimako.mockitools.util.PsiMethodUtil.getMethodCallAtCaret;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.StubbingApproach;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.List;
import java.util.Optional;

/**
 * Action to convert between the different stubbing approaches.
 * <p>
 * It takes two {@link StubbingApproach}es as the source and target approaches, and converts a single call chain,
 * or multiple ones.
 *
 * @see StubbingConverter
 * @since 0.6.0
 */
public class ConvertStubbingAction extends AnAction {
    private final StubbingApproach from;
    @Getter
    @TestOnly
    private final StubbingApproach to;
    /**
     * Whether the conversion is caret- (single call chain) or selection-based (one or more call chains).
     */
    private final boolean isBulkMode;
    private StubbingConverter converter;

    public ConvertStubbingAction(StubbingApproach from, StubbingApproach to, boolean isBulkMode) {
        super(to.presentableText);
        this.from = from;
        this.to = to;
        this.isBulkMode = isBulkMode;
    }

    //Perform action

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (file == null) return;
        Project project = e.getProject();

        converter = new StubbingConverter(project, editor.getDocument(), file);

        runWriteCommandAction(project, () -> {
            if (isBulkMode) performActionInBulk(collectStatementsInSelection(editor, file), project, editor);
            else performAction(getMethodCallAtCaret(file, editor));
        });
    }

    /**
     * Performs this action for a single call chain, when the action is invoked under a single caret.
     */
    private void performAction(PsiMethodCallExpression firstCallInChain) {
        converter.convert(firstCallInChain, from, to);
    }

    /**
     * Performs this action for one or more call chains under selection.
     */
    private void performActionInBulk(List<PsiExpressionStatement> statementsInSelection, Project project, Editor editor) {
        var documentManager = PsiDocumentManager.getInstance(project);
        for (var statement : statementsInSelection) {
            var firstCall = getFirstCallInChain(statement);
            if (firstCall != null) {
                performAction(firstCall);
                documentManager.commitDocument(editor.getDocument());
            }
        }
    }

    @Nullable
    public static PsiMethodCallExpression getFirstCallInChain(PsiExpressionStatement statement) {
        return Optional.of(statement)
            .map(stmt -> findChildOfType(stmt, PsiIdentifier.class))
            .map(identifier -> getParentOfType(identifier, PsiMethodCallExpression.class))
            .orElse(null);
    }
}
