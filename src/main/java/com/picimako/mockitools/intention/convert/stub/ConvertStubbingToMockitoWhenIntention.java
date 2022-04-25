//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN;
import static com.picimako.mockitools.MockitoQualifiedNames.WHEN;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoGiven;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoWillX;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoDoX;
import static com.picimako.mockitools.intention.convert.stub.CallChainEndsWith.ENDS_WITH_GIVEN;
import static com.picimako.mockitools.intention.convert.stub.CallChainEndsWith.ENDS_WITH_WHEN;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_DO_NOTHING;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_WILL_DO_NOTHING;

import java.util.Set;

import com.intellij.psi.PsiMethodCallExpression;

import com.picimako.mockitools.StubType;

/**
 * Converts stubbing call chains to the {@code Mockito.when().then*()} approach.
 * <p>
 * The intention is available on {@code Mockito.when()} when
 * <ul>
 *     <li>the result would not be the same as from what approach the user wants to convert, and</li>
 *     <li>the from approach is {@code Mockito.do*().when()} and there is no {@code doNothing()} call in the chain,
 *  *     and the chain ends with {@code .when(mock).doSomething();}, or</li>
 *     <li>the from approach is {@code BDDMockito.given().will*()}, or</li>
 *     <li>the from approach is {@code BDDMockito.will*().given()} and there is no {@code willDoNothing()} call in the chain,
 *     and the chain ends with {@code .given(mock).doSomething();}</li>
 * </ul>
 * <p>
 * The reason for excluding chains containing {@code doNothing()} or {@code willDoNothing()} calls is that they don't have a matching method in the
 * {@code Mockito.when().then*()} approach.
 *
 * @since 0.4.0
 */
public class ConvertStubbingToMockitoWhenIntention extends ConvertStubbingIntentionBase {

    private static final Set<String> ITSELF_METHOD_NAMES = Set.of(WHEN);
    public static final StubbingDescriptor TARGET_DESCRIPTOR = new StubbingDescriptor(ORG_MOCKITO_MOCKITO, "Mockito", THEN, WHEN, StubType.STUBBING);

    public ConvertStubbingToMockitoWhenIntention() {
        super("Mockito.when()");
    }

    @Override
    protected StubbingDescriptor targetDescriptor() {
        return TARGET_DESCRIPTOR;
    }

    @Override
    protected Set<String> itselfMethodNames() {
        return ITSELF_METHOD_NAMES;
    }

    @Override
    protected boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        if (isMockitoDoX(methodCall)) return isCallChainMatch(methodCall, DOESNT_CONTAIN_DO_NOTHING, ENDS_WITH_WHEN);
        if (isBDDMockitoGiven(methodCall)) return true;
        if (isBDDMockitoWillX(methodCall)) return isCallChainMatch(methodCall, DOESNT_CONTAIN_WILL_DO_NOTHING, ENDS_WITH_GIVEN);
        return false;
    }
}
