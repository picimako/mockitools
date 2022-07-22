//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.FromSelectionDataRetriever.collectStatementsInSelection;
import static com.picimako.mockitools.MockitoQualifiedNames.GIVEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN;
import static com.picimako.mockitools.MockitoQualifiedNames.WHEN;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;

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
import com.picimako.mockitools.StubType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.List;
import java.util.Optional;

/**
 * Action to convert between the different stubbing approaches.
 * <p>
 * It takes two {@link StubbingDescriptor}s as the source and target approaches, and converts a single call chain,
 * or multiple ones.
 *
 * @see StubbingConverter
 * @since 0.6.0
 */
public class ConvertStubbingAction extends AnAction {
    public static final StubbingDescriptor MOCKITO_WHEN =
        new StubbingDescriptor("Mockito.when()", ORG_MOCKITO_MOCKITO, "Mockito", THEN, WHEN, StubType.STUBBING);
    public static final StubbingDescriptor MOCKITO_DO =
        new StubbingDescriptor("Mockito.do*()", ORG_MOCKITO_MOCKITO, "Mockito", "do", WHEN, StubType.STUBBER);

    public static final StubbingDescriptor BDDMOCKITO_GIVEN =
        new StubbingDescriptor("BDDMockito.given()", ORG_MOCKITO_BDDMOCKITO, "BDDMockito", "will", GIVEN, StubType.STUBBING);
    public static final StubbingDescriptor BDDMOCKITO_WILL =
        new StubbingDescriptor("BDDMockito.will*()", ORG_MOCKITO_BDDMOCKITO, "BDDMockito", "will", GIVEN, StubType.STUBBER);

    private StubbingConverter converter;
    private final StubbingDescriptor from;

    private final StubbingDescriptor to;
    /**
     * Whether the conversion is caret- (single call chain) or selection-based (one or more call chains).
     */
    private final boolean isBulkMode;

    public ConvertStubbingAction(StubbingDescriptor from, StubbingDescriptor to, boolean isBulkMode) {
        super(to.getActionText());
        this.from = from;
        this.to = to;
        this.isBulkMode = isBulkMode;
    }

    @TestOnly
    public StubbingDescriptor getTo() {
        return to;
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
