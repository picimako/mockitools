//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.google.common.collect.Iterables.getLast;
import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getQualifier;
import static com.picimako.mockitools.util.PsiMethodUtil.getReferenceNameElement;
import static com.picimako.mockitools.util.Ranges.endOffsetOf;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.List;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.StubType;
import com.picimako.mockitools.StubbingApproach;
import com.picimako.mockitools.util.PsiClassUtil;
import lombok.RequiredArgsConstructor;

/**
 * Converts stubbing call chains between the different approaches.
 * <p>
 * {@code Mockito.lenient()} is not supported at the moment.
 */
@RequiredArgsConstructor
public final class StubbingConverter {

    private final Project project;
    private final Document document;
    private final PsiFile file;

    /**
     * Converts the call chain started by the argument {@code firstCallInChain} to the target stubbing approach.
     *
     * @param firstCallInChain the first call in the call chain on which the conversion is invoked
     * @param from             the stubbing approach to convert from
     * @param to               the target stubbing approach to convert to
     */
    public void convert(PsiMethodCallExpression firstCallInChain, StubbingApproach from, StubbingApproach to) {
        //To make sure that converting a chain to itself doesn't happen
        if (from.hasSameStubTypeAs(to) && from.methodCallStubber.equals(to.methodCallStubber))
            return;

        runWriteCommandAction(project, () -> {
            var calls = collectCallsInChainFromFirst(firstCallInChain, true);

            if (from.hasSameStubTypeAs(to)) convertSameType(calls, from, to);
            else if (to.getStubType() == StubType.STUBBER) convertToStubber(calls, from, to);
            else if (to.getStubType() == StubType.STUBBING) convertToStubbing(calls, from, to);
        });
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
    private void convertSameType(List<PsiMethodCallExpression> calls, StubbingApproach from, StubbingApproach to) {
        doBaseConversion(from, to, calls, endOffsetOf(getQualifier(calls.getFirst())), to.getBeginningOfStubbing(from));
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
    private void convertToStubber(List<PsiMethodCallExpression> calls, StubbingApproach from, StubbingApproach to) {
        //These have to be saved before the base conversion, so their values are kept properly
        var stubbedCall = ((PsiMethodCallExpression) getFirstArgument(calls.getFirst())); //mock.doSomething()
        var stubbedCallQualifier = getQualifier(stubbedCall).getText(); //"mock"
        var stubbedCallText = stubbedCall.getText(); //"mock.doSomething()"

        //At this point the example chain becomes 'Mockito.do*();'
        doBaseConversion(from, to, calls, endOffsetOf(calls.getFirst()), to.getBeginningOfStubbing(from));

        //Adds the '.when(mock).doSomething()' part at the end of the call chain, so the example becomes: 'Mockito.do*().when(mock).doSomething();'
        document.insertString(
            endOffsetOf(getLast(calls)),
            "." + to.methodCallStubber + "(" + stubbedCallQualifier + ")" + stubbedCallText.replace(stubbedCallQualifier, ""));
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
    private void convertToStubbing(List<PsiMethodCallExpression> calls, StubbingApproach from, StubbingApproach to) {
        var stubbingMethod = calls.get(calls.size() - 2); //when(mock)
        int endOffset = endOffsetOf(getQualifier(calls.getFirst())); //end offset of Mockito

        //BDDMockito.given + ( + mock + .doSomething() + )
        String replacement = to.getBeginningOfStubbing(from) + "(" + getFirstArgument(stubbingMethod).getText() + getLast(calls).getText().replace(stubbingMethod.getText(), "") + ")";

        //At this point the example chain becomes 'BDDMockito.given(mock.doSomething()).will*().when(mock).doSomething();'
        doBaseConversion(from, to, calls, endOffset, replacement);

        //Removes the '.when(mock).doSomething()' part at the end, and the example becomes: 'BDDMockito.given(mock.doSomething()).will*();'
        document.deleteString(endOffsetOf(calls.get(calls.size() - 3)), endOffsetOf(getLast(calls)));
    }

    //Low-level conversion logic

    private void doBaseConversion(StubbingApproach from, StubbingApproach to, List<PsiMethodCallExpression> calls, int endOffset, String replacement) {
        replaceBeginningOfChain(calls, endOffset, replacement);
        PsiClassUtil.importClassAndCommit(to.getStubStarterClassFqn(), project, file, document);
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
        replaceString(calls.getFirst().getTextOffset(), endOffset, replacement);
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
    private void convertMethodNames(List<PsiMethodCallExpression> calls, StubbingApproach from, StubbingApproach to) {
        int startIndex = to.hasSameStubTypeAs(from) || from.getStubType() == StubType.STUBBER ? 0 : 1;
        //Don't rename methods of calls after 'given(mock)' and 'when(mock)' because they are not part of the Mockito framework
        int endIndex = from.getStubType() == StubType.STUBBING ? calls.size() : calls.size() - 1;
        for (int i = startIndex; i < endIndex; i++) {
            String currentMethodName = getMethodName(calls.get(i));
            //If a method is a stubbing target (given() or when()) then simply replace the method name,
            // otherwise replace the prefix in the method name
            String newMethodName = currentMethodName.equals(from.methodCallStubber)
                ? to.methodCallStubber
                : currentMethodName.replace(from.getStubPrefix(), to.getStubPrefix());

            TextRange textRange = getReferenceNameElement(calls.get(i)).getTextRange();

            replaceString(textRange.getStartOffset(), textRange.getEndOffset(), newMethodName);
        }
    }

    private void replaceString(int startOffset, int endOffset, String replacement) {
        document.replaceString(startOffset, endOffset, replacement);
        PsiDocumentManager.getInstance(project).commitDocument(document);
    }
}
