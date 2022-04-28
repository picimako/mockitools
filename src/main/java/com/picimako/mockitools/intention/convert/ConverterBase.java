//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.ProjectScope;
import com.siyeh.ig.psiutils.ImportUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for converting between different stubbing, verification and other approaches.
 */
public abstract class ConverterBase {

    protected final Project project;
    protected final Document document;
    protected final PsiFile file;
    protected final PsiDocumentManager documentManager;

    protected ConverterBase(Project project, Document document, PsiFile file) {
        this.project = project;
        this.document = document;
        this.file = file;
        documentManager = PsiDocumentManager.getInstance(project);
    }

    /**
     * Imports the class the stubbing call chain starts with: either {@code org.mockito.Mockito} or {@code org.mockito.BDDMockito}.
     */
    protected final void importClass(String fqn) {
        PsiClass mockitoClass = JavaPsiFacade.getInstance(project).findClass(fqn, ProjectScope.getLibrariesScope(project));
        if (mockitoClass != null) {
            performAndCommitDocument(() -> ImportUtils.addImportIfNeeded(mockitoClass, file));
            documentManager.doPostponedOperationsAndUnblockDocument(document);
        }
    }

    protected final void performAndCommitDocument(Runnable runnable) {
        runnable.run();
        documentManager.commitDocument(document);
    }

    protected final int endOffsetOf(@NotNull PsiElement element) {
        return element.getTextRange().getEndOffset();
    }
}
