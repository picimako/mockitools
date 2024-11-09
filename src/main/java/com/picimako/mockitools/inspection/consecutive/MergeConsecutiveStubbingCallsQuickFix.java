//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.picimako.mockitools.resources.MockitoolsBundle.message;
import static com.picimako.mockitools.util.Ranges.endOffsetOf;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.siyeh.ig.InspectionGadgetsFix;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Quick fix that merges consecutive {@code *Throw()} or {@code *Return()} calls, respectively.
 * Only a single section of consecutive calls is merged if there are multiple.
 */
@RequiredArgsConstructor
public class MergeConsecutiveStubbingCallsQuickFix extends InspectionGadgetsFix {
    private final ConsecutiveCallRegistrar registrar;
    private final TypeConversionMethod argumentTypeConverter;

    @Override
    public @IntentionName @NotNull String getName() {
        return switch (argumentTypeConverter) {
            case NO_CONVERSION, TO_THROWABLES_SIMPLE ->
                message("quick.fix.merge.with.previous.consecutive.calls", registrar.consecutiveMethodName);
            default ->
                message("quick.fix.merge.with.previous.consecutive.calls.and.convert.params", argumentTypeConverter.message);
        };
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return message("quick.fix.family.simplify.consecutive.stubbing.calls");
    }

    @Override
    protected void doFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        var firstConsecutiveCall = registrar.getFirstConsecutiveCall();
        var viewProvider = firstConsecutiveCall.getManager().findViewProvider(registrar.getContainingFile());
        if (viewProvider == null) return;
        var document = viewProvider.getDocument();
        if (document == null) return;
        var documentManager = PsiDocumentManager.getInstance(project);

        //Convert the first consecutive call's arguments to the target type
        for (var expression : firstConsecutiveCall.getArgumentList().getExpressions()) {
            expression.replace(argumentTypeConverter.convert(expression));
        }
        //Merge arguments by adding them to the first consecutive call
        registrar.consecutiveCallIndeces.stream()
            .skip(1)
            .map(registrar::getElement)
            .flatMap(methodCall -> Arrays.stream(methodCall.getArgumentList().getExpressions()))
            .map(argumentTypeConverter::convert)
            .forEach(argument -> {
                firstConsecutiveCall.getArgumentList().add(argument);
                documentManager.commitDocument(document);
            });

        documentManager.doPostponedOperationsAndUnblockDocument(document);

        //Remove the consecutive calls except the first one
        for (int i = 1; i < registrar.consecutiveCallIndeces.size(); i++) {
            Integer index = registrar.consecutiveCallIndeces.get(i);
            document.deleteString(
                endOffsetOf(registrar.getElement(index - 1)),
                endOffsetOf(registrar.getElement(index)));
            documentManager.commitDocument(document);
        }
    }
}
