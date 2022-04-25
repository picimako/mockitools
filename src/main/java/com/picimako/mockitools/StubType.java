//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

/**
 * Represents the type of stubbing. Naming is according the Mockito naming conventions.
 */
public enum StubType {
    /**
     * When the stubbing of the action is called later in a stubbing call chain than the specification of the mock object.
     * <p>
     * E.g. {@code Mockito.when(mockObject.doesSomething()).thenThrow(SomeException.class)}
     */
    STUBBING,
    /**
     * When the stubbing of the action is called earlier in a stubbing call chain than the specification of the mock object.
     * <p>
     * E.g. {@code Mockito.doThrow(SomeException.class).when(mockObject.doesSomething())}
     */
    STUBBER
}
