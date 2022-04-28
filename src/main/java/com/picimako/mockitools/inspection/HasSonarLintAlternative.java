//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

/**
 * Marks inspection classes that has a validation in SonarLint too.
 */
public @interface HasSonarLintAlternative {
    /**
     * The URL of the SonarLint rule documentation. 
     */
    String value();
}
