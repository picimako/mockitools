//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.WHEN;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoGiven;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoWillX;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoWhen;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.intention.convert.stub.CallChainEndsWith.ENDS_WITH_GIVEN;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_THEN;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_WILL;

import java.util.Set;

import com.intellij.psi.PsiMethodCallExpression;

import com.picimako.mockitools.StubType;

/**
 * Converts stubbing call chains to the {@code Mockito.do*().when()} approach.
 * <p>
 * The intention is available on static {@code do*()} methods of {@code org.mockito.Mockito} when
 * <ul>
 *     <li>the result would not be the same as from what approach the user wants to convert, and</li>
 *     <li>the from approach is {@code Mockito.when().then*()} and there is no {@code then()} call in the chain, or</li>
 *     <li>the from approach is {@code BDDMockito.given().will*()} and there is no {@code will()} call in the chain, or</li>
 *     <li>the from approach is {@code BDDMockito.will*().given()} and there is no {@code will()} call in the chain,
 *     and the chain ends with {@code .given(mock).doSomething();}</li>
 * </ul>
 * <p>
 * The reason for excluding chains containing {@code then()} or {@code will()} calls is that they don't have a matching method in the
 * {@code Mockito.do*().when()} approach.
 * <p>
 * Conversion from {@code BDDMockito.given().will*()} and {@code BDDMockito.will*().given()} approaches is possible only when
 * {@link com.picimako.mockitools.inspection.EnforceConventionInspection} is disabled, or it doesn't enforce {@code org.mockito.BDDMockito}.
 *
 * @since 0.4.0
 */
public class ConvertStubbingToMockitoDoIntention extends ConvertStubbingIntentionBase {

    private static final Set<String> ITSELF_METHOD_NAMES = Set.of("doReturn", "doThrow", "doAnswer", "doCallRealMethod", "doNothing");
    public static final StubbingDescriptor TARGET_DESCRIPTOR = new StubbingDescriptor(ORG_MOCKITO_MOCKITO, "Mockito", "do", WHEN, StubType.STUBBER);

    public ConvertStubbingToMockitoDoIntention() {
        super("Mockito.do*()");
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
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        if (isMockitoWhen(methodCall)) return isCallChainMatch(methodCall, DOESNT_CONTAIN_THEN);
        if (isBDDMockitoGiven(methodCall)) return !isBDDMockitoEnforced(methodCall) && isCallChainMatch(methodCall, DOESNT_CONTAIN_WILL);
        if (isBDDMockitoWillX(methodCall)) return !isBDDMockitoEnforced(methodCall) && isCallChainMatch(methodCall, DOESNT_CONTAIN_WILL, ENDS_WITH_GIVEN);
        return false;
    }
}
