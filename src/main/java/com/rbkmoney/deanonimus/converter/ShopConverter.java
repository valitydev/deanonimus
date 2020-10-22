package com.rbkmoney.deanonimus.converter;

import com.rbkmoney.damsel.deanonimus.*;
import com.rbkmoney.deanonimus.util.EnumUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ShopConverter {

    public Map<String, Shop> convert(List<com.rbkmoney.deanonimus.domain.Shop> shops) {
        return Optional.ofNullable(shops).orElse(Collections.emptyList())
                .stream()
                .map(this::convertToEntity)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));
    }

    private Map.Entry<String, Shop> convertToEntity(com.rbkmoney.deanonimus.domain.Shop shopDomain) {
        return Map.entry(shopDomain.getId(), convertShop(shopDomain));
    }

    private Shop convertShop(com.rbkmoney.deanonimus.domain.Shop shopDomain) {
        Shop shop = new Shop()
                .setId(shopDomain.getId())
        .setBlocking(EnumUtils.convertBlocking(shopDomain.getBlocking()))
        .setSuspension(EnumUtils.convertSuspension(shopDomain.getSuspension()))
        .setCategory(new CategoryRef(shopDomain.getCategoryId()))
        .setContractId(shopDomain.getContractId())
        .setDetails(new ShopDetails()
                .setName(shopDomain.getDetailsName())
                .setDescription(shopDomain.getDetailsDescription())
        )
        .setPayoutToolId(shopDomain.getPayoutToolId());

        setAccount(shopDomain, shop);

        ShopLocation shopLocation = new ShopLocation();
        shopLocation.setUrl(shopDomain.getLocationUrl());
        shop.setLocation(shopLocation);

        if (shopDomain.getPayoutScheduleId() != null) {
            shop.setPayoutSchedule(new BusinessScheduleRef(shopDomain.getPayoutScheduleId()));
        }
        return shop;
    }

    private void setAccount(com.rbkmoney.deanonimus.domain.Shop shopDomain, Shop shop) {
        if (shopDomain.getAccountCurrencyCode() != null) {
            shop.setAccount(new ShopAccount(
                    new CurrencyRef(shopDomain.getAccountCurrencyCode()),
                    shopDomain.getAccountSettlement(),
                    shopDomain.getAccountGuarantee(),
                    shopDomain.getAccountPayout()
            ));
        }
    }

}
