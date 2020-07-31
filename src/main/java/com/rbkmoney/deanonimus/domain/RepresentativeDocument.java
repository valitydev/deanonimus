package com.rbkmoney.deanonimus.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RepresentativeDocument {
    articles_of_association("articles_of_association"),

    power_of_attorney("power_of_attorney"),

    expired("expired");

    private final String value;
}
