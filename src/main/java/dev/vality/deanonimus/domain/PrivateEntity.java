package dev.vality.deanonimus.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PrivateEntity {
    russian_private_entity("russian_private_entity");

    private final String value;
}
