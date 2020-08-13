package com.rbkmoney.deanonimus.converter;

import com.rbkmoney.damsel.deanonimus.*;
import com.rbkmoney.deanonimus.util.EnumUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ShopConverter {

    public Map<String, Shop> convert(List<com.rbkmoney.deanonimus.domain.Shop> shops) {
        if (CollectionUtils.isEmpty(shops)) return Collections.emptyMap();
        return shops.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<String, Shop> convertToEntity(com.rbkmoney.deanonimus.domain.Shop shopDomain) {
        return new AbstractMap.SimpleEntry<>(shopDomain.getId(), convertShop(shopDomain));
    }

    private Shop convertShop(com.rbkmoney.deanonimus.domain.Shop shopDomain) {
        Shop shop = new Shop();
        shop.setId(shopDomain.getId());
        setAccount(shopDomain, shop);
        shop.setBlocking(EnumUtils.convertBlocking(shopDomain.getBlocking()));
        shop.setSuspension(EnumUtils.convertSuspension(shopDomain.getSuspension()));
        shop.setCategory(new CategoryRef(shopDomain.getCategoryId()));
        shop.setContractId(shopDomain.getContractId());
        shop.setDetails(new ShopDetails()
                .setName(shopDomain.getDetailsName())
                .setDescription(shopDomain.getDetailsDescription())
        );

        ShopLocation shopLocation = new ShopLocation();
        shopLocation.setUrl(shopDomain.getLocationUrl());
        shop.setLocation(shopLocation);

        shop.setPayoutToolId(shopDomain.getPayoutToolId());
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
