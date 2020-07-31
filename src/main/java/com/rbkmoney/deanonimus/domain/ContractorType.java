package com.rbkmoney.deanonimus.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContractorType {

    registered_user("registered_user"),

    legal_entity("legal_entity"),

    private_entity("private_entity");

    private final String value;

}
