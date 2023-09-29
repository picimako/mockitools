//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Either standard {@code Mockito} and {@code InOrder} based stubbing and verification,
 * or BDD-style via {@code BDDMockito}.
 */
@Getter
@RequiredArgsConstructor
public enum Convention {
    MOCKITO(ORG_MOCKITO_MOCKITO + " / " + ORG_MOCKITO_INORDER),
    BDD_MOCKITO(ORG_MOCKITO_BDDMOCKITO);

    private final String classFqn;
}
