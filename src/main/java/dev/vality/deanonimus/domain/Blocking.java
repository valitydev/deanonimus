package dev.vality.deanonimus.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Blocking {
    unblocked("unblocked"),

    blocked("blocked");

    private final String value;
}
