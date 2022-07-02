//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getNextSiblingOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.Ranges.isWithinSelection;
import static java.util.stream.Collectors.joining;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Helps to retrieve information from the selection in a {@link PsiFile} and {@link Editor}.
 */
public final class FromSelectionDataRetriever {

    public static int selectionLengthIn(SelectionModel model) {
        return model.getSelectionEnd() - model.getSelectionStart();
    }

    /**
     * Collects all {@link PsiExpressionStatement}s from the selection in the argument {@code editor}.
     */
    @NotNull
    public static List<PsiExpressionStatement> collectStatementsInSelection(Editor editor, PsiFile file) {
        var statements = new SmartList<PsiExpressionStatement>();
        var statement = findFirstSelectedStatement(editor, file);
        while (isWithinSelection(statement, editor)) {
            statements.add(statement);
            statement = getNextSiblingOfType(statement, PsiExpressionStatement.class);
        }
        return statements;
    }

    /**
     * Returns the first selected {@link PsiExpressionStatement} from the argument {@code file}.
     * <p>
     * It checks if the selection start is at whitespaces preceding a call chain, to improve user experience,
     * since the first expression doesn't have to be selected precisely.
     */
    @Nullable
    private static PsiExpressionStatement findFirstSelectedStatement(Editor editor, PsiFile file) {
        var elementAtStart = file.findElementAt(editor.getSelectionModel().getSelectionStart());
        if (elementAtStart instanceof PsiIdentifier)
            return getParentOfType(elementAtStart, PsiExpressionStatement.class);
        else if (elementAtStart instanceof PsiWhiteSpace)
            return getNextSiblingOfType(elementAtStart, PsiExpressionStatement.class);
        return null;
    }

    /**
     * Collects all the mock objects from the provided list of selected statements.
     * <p>
     * This is necessary to enumerate all used mock objects in the {@code Mockito.inOrder([mock objects])} call.
     */
    @NotNull
    public static String collectMockObjects(List<PsiExpressionStatement> statementsInSelection) {
        return statementsInSelection.stream()
            .map(FromSelectionDataRetriever::getVerificationCall)
            .map(verificationCall -> getFirstArgument(verificationCall).getText())
            .distinct()
            .collect(joining(", "));
    }

    @Nullable
    private static PsiMethodCallExpression getVerificationCall(PsiExpressionStatement statement) {
        return getParentOfType(/*identifier*/findChildOfType(statement, PsiIdentifier.class), PsiMethodCallExpression.class);
    }

    private FromSelectionDataRetriever() {
        //Utility class
    }
}
