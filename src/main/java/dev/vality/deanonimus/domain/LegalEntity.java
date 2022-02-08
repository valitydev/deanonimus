package dev.vality.deanonimus.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LegalEntity {
    russian_legal_entity("russian_legal_entity"),

    international_legal_entity("international_legal_entity");

    private final String value;
}
