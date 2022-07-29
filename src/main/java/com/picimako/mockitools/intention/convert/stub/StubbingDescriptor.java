//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import com.picimako.mockitools.StubType;

/**
 * Represents a stubbing approach.
 * <p>
 * See subclasses of {@link ConvertStubbingIntentionBase}.
 */
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

    public StubbingDescriptor(String actionText, String stubStarter, String stubStarterClassName, String prefix, String stubTargetSpecifierMethodName, StubType stubType) {
        this.actionText = actionText;
        this.stubStarterClassFqn = stubStarter;
        this.stubStarterClassName = stubStarterClassName;
        this.prefix = prefix;
        this.stubTargetSpecifierMethodName = stubTargetSpecifierMethodName;
        this.stubType = stubType;
    }

    public String getActionText() {
        return actionText;
    }

    public String getStubStarterClassFqn() {
        return stubStarterClassFqn;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getStubTargetSpecifierMethodName() {
        return stubTargetSpecifierMethodName;
    }

    public StubType getStubType() {
        return stubType;
    }

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
