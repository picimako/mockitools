//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

/**
 * A no-op action, in case there is no actual option available within an ActionGroup.
 */
public class NoActionAvailableAction extends AnAction {
    public static final NoActionAvailableAction INSTANCE = new NoActionAvailableAction();

    public NoActionAvailableAction() {
        super(MockitoolsBundle.message("action.no.available.action"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //No-op
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(false);
    }
}
