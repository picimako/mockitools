//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import java.util.List;
import java.util.function.Supplier;
import javax.swing.*;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
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
    
    private ListPopupHelper() {
        //Utility class
    }
}
