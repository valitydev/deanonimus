package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.Shop;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShopListConverter {

    private final Converter<dev.vality.deanonimus.domain.Shop, Shop> converter;

    public Map<String, Shop> convert(List<dev.vality.deanonimus.domain.Shop> shops) {
        return Optional.ofNullable(shops).orElse(Collections.emptyList())
                .stream()
                .map(this::convertToEntity)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));
    }

    private Map.Entry<String, Shop> convertToEntity(dev.vality.deanonimus.domain.Shop shopDomain) {
        return Map.entry(shopDomain.getId(), Objects.requireNonNull(converter.convert(shopDomain)));
    }
}
