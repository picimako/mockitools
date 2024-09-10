//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.picimako.mockitools.completion;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.psi.util.PsiTreeUtil.findChildrenOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;

import java.util.regex.Pattern;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiResolveHelper;
import com.intellij.util.ProcessingContext;
import com.picimako.mockitools.MockitoolsPsiUtil;
import com.picimako.mockitools.dsl.MockObject;
import org.jetbrains.annotations.NotNull;

/**
 * Provides code completion items for Mockito methods that accept mock objects as arguments.
 * <p>
 * These methods are matched by {@link MockitoolsPsiUtil#MOCK_OBJECT_PARAMETER_HOLDER}.
 * <p>
 * Completion collects all {@code @Mock}, {@code @Spy} and {@code InjectMocks} fields in the current class,
 * as well as all local variables in the current methods that are initialized by {@code Mockito.mock()} or {@code Mockito.spy()}.
 *
 * @since 0.12.0
 */
final class MockCompletionContributor extends CompletionContributor {

    private static final Pattern MOCKITO_VERIFY_PATTERN = Pattern.compile("Mockito\\.(verifyNoInteractions|verifyNoMoreInteractions|verifyZeroInteractions)");
    private static final Pattern VERIFY_PATTERN = Pattern.compile("verifyNoInteractions|verifyNoMoreInteractions|verifyZeroInteractions");

    private static final PsiElementPattern.Capture<PsiElement> MOCK_OBJECT_PARAMETER_HOLDER_PATTERN =
        psiElement(JavaTokenType.IDENTIFIER)
            .withSuperParent(3, psiElement(PsiMethodCallExpression.class)
                .with(new PatternCondition<>("Mockito mock object parameter holders") {
                    @Override
                    public boolean accepts(@NotNull PsiMethodCallExpression element, ProcessingContext context) {
                        if (MockitoolsPsiUtil.MOCK_OBJECT_PARAMETER_HOLDER.matches(element))
                            return true;
                        else {

                            String canonicalText = element.getMethodExpression().getCanonicalText();
                            //E.g. 'Mockito.verifyNoInteractions'
                            return MOCKITO_VERIFY_PATTERN.matcher(canonicalText).matches()
                                //E.g. 'verifyNoInteractions' when static-imported
                                || (VERIFY_PATTERN.matcher(canonicalText).matches() && isMethodDeclaredInMockitoClass(element));
                        }
                    }
                }));

    /**
     * Using {@link PsiResolveHelper} because neither {@link com.siyeh.ig.psiutils.ImportUtils#isAlreadyStaticallyImported(PsiJavaCodeReferenceElement)}
     * nor {@link com.intellij.psi.impl.source.codeStyle.ImportHelper#isAlreadyImported(PsiJavaFile, String)} worked for determining if the method is already imported.
     */
    private static boolean isMethodDeclaredInMockitoClass(@NotNull PsiMethodCallExpression methodCall) {
        var referencedMethodCandidates = PsiResolveHelper.getInstance(methodCall.getProject()).getReferencedMethodCandidates(methodCall, false, true);
        if (referencedMethodCandidates.length == 1) {
            var containingClass = ((PsiMethod) referencedMethodCandidates[0].getElement()).getContainingClass();
            return containingClass != null && ORG_MOCKITO_MOCKITO.equals(containingClass.getQualifiedName());
        }
        return false;
    }

    private static final CompletionProvider<CompletionParameters> COMPLETION_PROVIDER = new CompletionProvider<>() {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
            //Look up and add fields annotated with either @Mock, @Spy or @InjectMocks in the current class
            result.addAllElements(findChildrenOfType(parameters.getOriginalFile(), PsiField.class).stream()
                .filter(MockObject::isAnyKindOfMock)
                .map(field ->
                    LookupElementBuilder.create(field)
                        .withIcon(AllIcons.Nodes.Field)
                        .withTypeText(MockObject.getMockAnnotationName(field))
                        .withCaseSensitivity(false))
                .toList());

            //Look up and add local variables in the current method initialized with 'Mockito.mock()' or 'Mockito.spy()'.
            //Fields/variables initialized with mock() or spy() somewhere else are not looked up at the moment.
            var parentMethod = getParentOfType(parameters.getOriginalPosition(), PsiMethod.class);
            result.addAllElements(findChildrenOfType(parentMethod, PsiLocalVariable.class).stream()
                .filter(MockObject::isAnyKindOfMock)
                .map(localVar ->
                    LookupElementBuilder.create(localVar)
                        .withIcon(AllIcons.Nodes.Variable)
                        .withTypeText(MockObject.getMockInitializerAsString(localVar))
                        .withCaseSensitivity(false))
                .toList());

            result.stopHere();
        }
    };

    public MockCompletionContributor() {
        extend(CompletionType.BASIC, MOCK_OBJECT_PARAMETER_HOLDER_PATTERN, COMPLETION_PROVIDER);
    }
}
