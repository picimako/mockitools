//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.picimako.mockitools.MockitoQualifiedNames.GIVEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoGiven;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoDoX;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoWhen;
import static com.picimako.mockitools.intention.convert.stub.CallChainEndsWith.ENDS_WITH_WHEN;

import java.util.Set;

import com.intellij.psi.PsiMethodCallExpression;

import com.picimako.mockitools.StubType;

/**
 * Converts stubbing call chains to the {@code BDDMockito.will*().given()} approach.
 * <p>
 * The intention is available on static {@code will*()} methods of {@code org.mockito.BDDMockito} when
 * <ul>
 *     <li>the result would not be the same as from what approach the user wants to convert, and</li>
 *     <li>the from approach is {@code Mockito.when().then*()}, or</li>
 *     <li>the from approach is {@code BDDMockito.given().will*()}, or</li>
 *     <li>the from approach is {@code Mockito.do*().when()}, and the chain ends with {@code .when(mock).doSomething();}</li>
 * </ul>
 * 
 * @since 0.4.0
 */
public class ConvertStubbingToBDDMockitoWillIntention extends ConvertStubbingIntentionBase {

    private static final Set<String> ITSELF_METHOD_NAMES = Set.of("willReturn", "willThrow", "willAnswer", "willCallRealMethod", "will", "willDoNothing");
    public static final StubbingDescriptor TARGET_DESCRIPTOR = new StubbingDescriptor(ORG_MOCKITO_BDDMOCKITO, "BDDMockito", "will", GIVEN, StubType.STUBBER);

    public ConvertStubbingToBDDMockitoWillIntention() {
        super("BDDMockito.will*()");
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
        if (isMockitoWhen(methodCall) || isBDDMockitoGiven(methodCall)) return true;
        if (isMockitoDoX(methodCall)) return isCallChainMatch(methodCall, ENDS_WITH_WHEN);
        return false;
    }
}