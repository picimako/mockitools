//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.mocking;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.picimako.mockitools.MockitoMockMatchers.MOCK_WITH_SETTINGS;
import static com.picimako.mockitools.util.PsiMethodUtil.*;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

/**
 * This inspection reports when the mocked type and the type of the spied instance don't match
 * in a {@code mock(Type.class, withSettings().spiedInstance(...));}-type mock creation.
 * <p>
 * Limitations: since validation for all possible cases would need the runtime type of both the mocked type and the
 * spied instance, the inspection checks the mock creation only when the mock type is a {@link PsiClassObjectAccessExpression}
 * e.g. {@code <type>.class}, and when the spied instance is a 'new' expression e.g. {@code new SomeObject<>()}.
 * <p>
 * Based on Mockito's behaviour, in case of mocking/spying type with generic types, only the raw type is taken into account when determining the mismatch.
 */
public class MockSpiedInstanceTypeMismatchInspection extends MockitoolsBaseInspection {

    private static final String SPIED_INSTANCE = "spiedInstance";

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        //mock(<type>, withSettings()...)
        if (MOCK_WITH_SETTINGS.matches(expression) && hasTwoArguments(expression)) {
            var withSettings = get2ndArgument(expression);
            var calls = collectCallsInChainFromLast(withSettings);

            //Skips the 'withSettings()' call because it doesn't have to be checked.
            for (int i = calls.size() - 2; i >= 0; i--) {
                var settingsCall = calls.get(i);
                if (SPIED_INSTANCE.equals(getMethodName(settingsCall))
                    && hasArgument(settingsCall) //e.g. spiedInstance(someObject)
                    && getFirstArgument(settingsCall) instanceof PsiNewExpression spiedInstance) { //e.g. someObject
                    var mockTypeArg = getFirstArgument(expression);
                    if (mockTypeArg instanceof PsiClassObjectAccessExpression mockTypeAsClassAccess
                        && !Objects.equals(getRawType(mockTypeAsClassAccess.getOperand().getType()), getRawType(spiedInstance.getType()))) {
                        holder.registerProblem(mockTypeArg, MockitoolsBundle.message("inspection.mock.type.spied.instance.type.mismatch"));
                    }
                    break; //Found the 'spiedInstance()' method, further inspection of the call chain is not needed.
                }
            }
        }
    }

    @Nullable
    private PsiType getRawType(PsiType type) {
        return type instanceof PsiClassReferenceType refType ? refType.rawType() : null;
    }
}
