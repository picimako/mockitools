//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import com.picimako.mockitools.StubType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a stubbing approach.
 * <p>
 * See subclasses of {@link ConvertStubbingIntentionBase}.
 */
@Getter
@AllArgsConstructor
public final class StubbingDescriptor {

    /**
     * Text to display in {@link ConvertStubbingAction}s.
     */
    private final String actionText;
    private final String stubStarterClassFqn;
    private final String stubStarterClassName;
    private final String prefix;
    private final String stubTargetSpecifierMethodName;
    private final StubType stubType;

    /**
     * Returns whether this and the provided descriptors have the same stub type.
     */
    public boolean hasSameStubTypeAs(StubbingDescriptor other) {
        return stubType == other.stubType;
    }

    /**
     * Returns e.g. {@code Mockito.when}.
     */
    public String getBeginningOfStubbing(StubbingDescriptor from) {
        if (hasSameStubTypeAs(from) || from.getStubType() == StubType.STUBBING) return stubStarterClassName;
        return stubStarterClassName + "." + stubTargetSpecifierMethodName;
    }
}
