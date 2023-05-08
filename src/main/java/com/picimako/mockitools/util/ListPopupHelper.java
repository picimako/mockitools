//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.util;

import java.util.List;
import java.util.function.Supplier;
import javax.swing.*;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiElement;
import com.intellij.ui.popup.list.ListPopupImpl;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.Nullable;

/**
 * Provides methods to work with list popups.
 */
public final class ListPopupHelper {

    public static <T extends PsiElement> void selectItemAndRun(String title, List<T> listItems,
                                                               Consumer<T> action,
                                                               Supplier<ListCellRenderer<?>> cellRenderer,
                                                               Editor editor, Project project) {
        var step = new BaseListPopupStep<>(title, listItems) {
            @Override
            public @Nullable PopupStep<?> onChosen(T selectedItem, boolean finalChoice) {
                action.consume(selectedItem);
                return null;
            }
        };
        new ListPopupImpl(project, step) {
            @Override
            protected ListCellRenderer<?> getListElementRenderer() {
                return cellRenderer.get();
            }
        }.showInBestPositionFor(editor);
    }

    /**
     * Shows a list popup with the provided list of actions.
     *
     * @param title     the title of the list popup. Can include & symbols for shortcut keys.
     *                  See {@link JBPopupFactory.ActionSelectionAid#MNEMONICS}.
     * @param listItems the list of actions to display
     * @param editor    the editor in which it is invoked from
     */
    public static void showActionsInListPopup(String title, List<? extends AnAction> listItems, Editor editor) {
        JBPopupFactory.getInstance().createActionGroupPopup(title,
            new DefaultActionGroup(listItems), getEditorDataContext(editor),
            JBPopupFactory.ActionSelectionAid.MNEMONICS, true
        ).showInBestPositionFor(editor);
    }

    /**
     * Temporarily replaces {@code EditorUtil#getEditorDataContext(Editor)} since it is introduced in 212.4746.92.
     */
    private static DataContext getEditorDataContext(Editor editor) {
        DataContext context = DataManager.getInstance().getDataContext(editor.getContentComponent());
        if (CommonDataKeys.PROJECT.getData(context) == editor.getProject()) return context;
        return dataId -> CommonDataKeys.PROJECT.is(dataId) ? editor.getProject() : context.getData(dataId);
    }
    
    private ListPopupHelper() {
        //Utility class
    }
}
