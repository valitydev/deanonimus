package dev.vality.deanonimus.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Blocking {
    unblocked("unblocked"),

    blocked("blocked");

    private final String value;
}
