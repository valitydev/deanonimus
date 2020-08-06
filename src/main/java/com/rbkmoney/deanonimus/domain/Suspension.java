package com.rbkmoney.deanonimus.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Suspension {
    active("active"),

    suspended("suspended");

    private final String value;
}
