//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.picimako.mockitools;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a third-party library with its Maven coordinate.
 */
@Getter
@RequiredArgsConstructor
public enum ThirdPartyLibrary {
    MOCKITO_V3("org.mockito:mockito-core:3.12.4"),
    MOCKITO_V4("org.mockito:mockito-core:4.11.0"),
    MOCKITO_V5("org.mockito:mockito-core:5.12.0"),
    JUNIT_4("junit:junit:4.13.2");

    private final String mavenCoordinate;
}
