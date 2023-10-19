package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.*;
import dev.vality.deanonimus.util.EnumUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopDomainToShopApiConverter implements Converter<dev.vality.deanonimus.domain.Shop, Shop> {

    @Override
    public Shop convert(dev.vality.deanonimus.domain.Shop shopDomain) {
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

    private void setAccount(dev.vality.deanonimus.domain.Shop shopDomain, dev.vality.damsel.deanonimus.Shop shop) {
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
