//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import java.util.Arrays;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Quick fix that merges consecutive {@code *Throw()} or {@code *Return()} calls, respectively.
 * Only a single section of consecutive calls is merged if there are multiple.
 */
public class MergeConsecutiveStubbingCallsQuickFix extends InspectionGadgetsFix {
    private final ConsecutiveCallQuickFixContext context;
    private final TypeConversionMethod argumentTypeConverter;

    public MergeConsecutiveStubbingCallsQuickFix(ConsecutiveCallQuickFixContext context, TypeConversionMethod argumentTypeConverter) {
        this.context = context;
        this.argumentTypeConverter = argumentTypeConverter;
    }

    @Override
    public @IntentionName @NotNull String getName() {
        return switch (argumentTypeConverter) {
            case NO_CONVERSION, TO_THROWABLES_SIMPLE ->
                MockitoolsBundle.quickFix("merge.with.previous.consecutive.calls", context.consecutiveMethodName);
            default ->
                MockitoolsBundle.quickFix("merge.with.previous.consecutive.calls.and.convert.params", argumentTypeConverter.message);
        };
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return MockitoolsBundle.quickFixFamily("simplify.consecutive.stubbing.calls");
    }

    @Override
    protected void doFix(Project project, ProblemDescriptor descriptor) {
        var firstConsecutiveCall = context.getFirstConsecutiveCall();
        var viewProvider = firstConsecutiveCall.getManager().findViewProvider(context.getContainingFile());
        if (viewProvider == null) return;
        Document document = viewProvider.getDocument();
        if (document == null) return;
        var documentManager = PsiDocumentManager.getInstance(project);

        //Convert the first consecutive call's arguments to the target type
        for (var expression : firstConsecutiveCall.getArgumentList().getExpressions()) {
            expression.replace(argumentTypeConverter.convert(expression));
        }
        //Merge arguments by adding them to the first consecutive call
        context.consecutiveCallIndeces.stream()
            .skip(1)
            .map(context::getElement)
            .flatMap(methodCall -> Arrays.stream(methodCall.getArgumentList().getExpressions()))
            .map(argumentTypeConverter::convert)
            .forEach(argument -> {
                firstConsecutiveCall.getArgumentList().add(argument);
                documentManager.commitDocument(document);
            });

        documentManager.doPostponedOperationsAndUnblockDocument(document);
        
        //Remove the consecutive calls except the first one
        for (int i = 1; i < context.consecutiveCallIndeces.size(); i++) {
            Integer index = context.consecutiveCallIndeces.get(i);
            document.deleteString(
                context.getElement(index - 1).getTextRange().getEndOffset(),
                context.getElement(index).getTextRange().getEndOffset());
            documentManager.commitDocument(document);
        }
    }
}
