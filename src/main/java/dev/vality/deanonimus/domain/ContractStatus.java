package dev.vality.deanonimus.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContractStatus {

    active("active"),

    terminated("terminated"),

    expired("expired");

    private final String value;
}