package dev.vality.deanonimus.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public enum Suspension {
    active("active"),

    suspended("suspended");

    private final String value;
}
