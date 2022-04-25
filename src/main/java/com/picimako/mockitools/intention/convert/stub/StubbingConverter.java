//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.google.common.collect.Iterables.getLast;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.getReferenceNameElement;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.List;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.search.ProjectScope;
import com.siyeh.ig.psiutils.ImportUtils;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.StubType;

/**
 * Converts stubbing call chains between the different approaches.
 * <p>
 * {@code Mockito.lenient()} is not supported at the moment.
 */
public final class StubbingConverter {

    private final Project project;
    private final Editor editor;
    private final PsiFile file;
    private final PsiDocumentManager documentManager;

    public StubbingConverter(Project project, Editor editor, PsiFile file) {
        this.project = project;
        this.editor = editor;
        this.file = file;
        documentManager = PsiDocumentManager.getInstance(project);
    }

    /**
     * Converts the call chain started by the argument {@code firstCallInChain} to the target stubbing approach.
     *
     * @param firstCallInChain the first call in the call chain on which the conversion is invoked
     * @param from             the stubbing approach to convert from
     * @param to               the target stubbing approach to convert to
     */
    public void convert(PsiMethodCallExpression firstCallInChain, StubbingDescriptor from, StubbingDescriptor to) {
        //To make sure that converting a chain to itself doesn't happen
        if (from.hasSameStubTypeAs(to) && from.getStubTargetSpecifierMethodName().equals(to.getStubTargetSpecifierMethodName())) return;

        var calls = collectCallsInChainFromFirst(firstCallInChain, true);

        if (from.hasSameStubTypeAs(to)) convertSameType(calls, from, to);
        else if (to.getStubType() == StubType.STUBBER) convertToStubber(calls, from, to);
        else if (to.getStubType() == StubType.STUBBING) convertToStubbing(calls, from, to);
    }

    //Stub type specific conversions

    /**
     * Does the conversion when both the from and to approaches have the same stub type,
     * so that either both are {@link StubType#STUBBING} or both are {@link StubType#STUBBER}.
     * <p>
     * For example:
     * <pre>
     * BDDMockito.given(mock.doSomething()).will*();
     * //may become
     * Mockito.when(mock.doSomething()).then*();
     * </pre>
     */
    private void convertSameType(List<PsiMethodCallExpression> calls, StubbingDescriptor from, StubbingDescriptor to) {
        WriteCommandAction.runWriteCommandAction(project,
            () -> doBaseConversion(from, to, calls, endOffsetOf(calls.get(0).getMethodExpression().getQualifierExpression()), to.getBeginningOfStubbing(from)));
    }

    /**
     * Does the conversion from a {@link StubType#STUBBING} to a {@link StubType#STUBBER} approach.
     * <p>
     * For example:
     * <pre>
     * Mockito.when(mock.doSomething()).then*();
     * //may become
     * Mockito.do*().when(mock).doSomething();
     * </pre>
     */
    private void convertToStubber(List<PsiMethodCallExpression> calls, StubbingDescriptor from, StubbingDescriptor to) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            //These have to be saved before the base conversion, so their values are kept properly
            var stubbedCall = ((PsiMethodCallExpression) getFirstArgument(calls.get(0))); //mock.doSomething()
            String stubbedCallQualifier = stubbedCall.getMethodExpression().getQualifierExpression().getText(); //"mock"
            String stubbedCallText = stubbedCall.getText(); //"mock.doSomething()"

            //At this point the example chain becomes 'Mockito.do*();'
            doBaseConversion(from, to, calls, endOffsetOf(calls.get(0)), to.getBeginningOfStubbing(from));

            //Adds the '.when(mock).doSomething()' part at the end of the call chain, so the example becomes: 'Mockito.do*().when(mock).doSomething();'
            editor.getDocument().insertString(
                endOffsetOf(getLast(calls)),
                "." + to.getStubTargetSpecifierMethodName() + "(" + stubbedCallQualifier + ")" + stubbedCallText.replace(stubbedCallQualifier, ""));
        });
    }

    /**
     * Does the conversion from a {@link StubType#STUBBER} to a {@link StubType#STUBBING} approach.
     * <p>
     * For example:
     * <pre>
     * Mockito.do*().when(mock).doSomething();
     * //may become
     * BDDMockito.given(mock.doSomething()).will*();
     * </pre>
     */
    private void convertToStubbing(List<PsiMethodCallExpression> calls, StubbingDescriptor from, StubbingDescriptor to) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            var stubbingMethod = calls.get(calls.size() - 2); //when(mock)
            int endOffset = endOffsetOf(calls.get(0).getMethodExpression().getQualifierExpression()); //end offset of Mockito

            //BDDMockito.given + ( + mock + .doSomething() + )
            String replacement = to.getBeginningOfStubbing(from) + "(" + getFirstArgument(stubbingMethod).getText() + getLast(calls).getText().replace(stubbingMethod.getText(), "") + ")";

            //At this point the example chain becomes 'BDDMockito.given(mock.doSomething()).will*().when(mock).doSomething();'
            doBaseConversion(from, to, calls, endOffset, replacement);

            //Removes the '.when(mock).doSomething()' part at the end, and the example becomes: 'BDDMockito.given(mock.doSomething()).will*();'
            editor.getDocument().deleteString(endOffsetOf(calls.get(calls.size() - 3)), endOffsetOf(getLast(calls)));
        });
    }

    //Low-level conversion logic

    private void doBaseConversion(StubbingDescriptor from, StubbingDescriptor to, List<PsiMethodCallExpression> calls, int endOffset, String replacement) {
        replaceBeginningOfChain(calls, endOffset, replacement);
        importMockitoClass(to);
        convertMethodNames(calls, from, to);
    }

    /**
     * Replaces the beginning of the text of the call chain, until the given end offset, with the provided replacement text.
     * <p>
     * For example:
     * <pre>
     * //The following text:
     * Mockito.do*().when(mock).doSomething();
     * //could become:
     * BDDMockito.given(mock.doSomething).do*().when(mock).doSomething();
     * </pre>
     *
     * @param calls       the call chain
     * @param endOffset   the end offset to replace the text from the start offset of the chain
     * @param replacement the replacement text
     */
    private void replaceBeginningOfChain(List<PsiMethodCallExpression> calls, int endOffset, String replacement) {
        editor.getDocument().replaceString(calls.get(0).getTextOffset(), endOffset, replacement);
        documentManager.commitDocument(editor.getDocument());
    }

    /**
     * Converts method names to the target approach. Based on the nature of the method call, either the whole method name
     * is replaced (e.g. {@code given()} -> {@code when()}), or just the prefix is replaced (e.g. {@code doReturn()} -> {@code willReturn}).
     * <p>
     * Methods in different stubbing approaches have different prefixes:
     * <ul>
     *     <li>{@code Mockito.when()} is followed by {@code then*()} methods,</li>
     *     <li>{@code Mockito.do*()} chains have {@code do*()} methods,</li>
     *     <li>{@code BDDMockito.given()} and {@code BDDMockito.will*()} chains are followed by {@code will*()} methods.</li>
     * </ul>
     *
     * @param calls the call chain
     * @param from  the stubbing approach to convert from
     * @param to    the target stubbing approach to convert to
     */
    private void convertMethodNames(List<PsiMethodCallExpression> calls, StubbingDescriptor from, StubbingDescriptor to) {
        int startIndex = to.hasSameStubTypeAs(from) || from.getStubType() == StubType.STUBBER ? 0 : 1;
        //Don't rename methods of calls after 'given(mock)' and 'when(mock)' because they are not part of the Mockito framework
        int endIndex = from.getStubType() == StubType.STUBBING ? calls.size() : calls.size() - 1;
        for (int i = startIndex; i < endIndex; i++) {
            String currentMethodName = getMethodName(calls.get(i));
            //If a method is a stubbing target (given() or when()) then simply replace the method name,
            // otherwise replace the prefix in the method name
            String newMethodName = currentMethodName.equals(from.getStubTargetSpecifierMethodName())
                ? to.getStubTargetSpecifierMethodName()
                : currentMethodName.replace(from.getPrefix(), to.getPrefix());

            TextRange textRange = getReferenceNameElement(calls.get(i)).getTextRange();

            editor.getDocument().replaceString(textRange.getStartOffset(), textRange.getEndOffset(), newMethodName);
            documentManager.commitDocument(editor.getDocument());
        }
    }

    /**
     * Imports the class the stubbing call chain starts with: either {@code org.mockito.Mockito} or {@code org.mockito.BDDMockito}.
     */
    private void importMockitoClass(StubbingDescriptor to) {
        PsiClass mockitoClass = JavaPsiFacade.getInstance(project).findClass(to.getStubStarterClassFqn(), ProjectScope.getLibrariesScope(project));
        if (mockitoClass != null) {
            ImportUtils.addImportIfNeeded(mockitoClass, file);
            documentManager.commitDocument(editor.getDocument());
            documentManager.doPostponedOperationsAndUnblockDocument(editor.getDocument());
        }
    }

    //Support methods

    private static int endOffsetOf(@NotNull PsiElement element) {
        return element.getTextRange().getEndOffset();
    }
}