package dev.vality.deanonimus.util;

import dev.vality.damsel.domain.ShopAccount;
import dev.vality.deanonimus.domain.Shop;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopUtil {

    public static void fillShopAccount(Shop shop, ShopAccount shopAccount) {
        shop.setAccountCurrencyCode(shopAccount.getCurrency().getSymbolicCode());
        shop.setAccountGuarantee(shopAccount.getGuarantee());
        shop.setAccountSettlement(shopAccount.getSettlement());
        shop.setAccountPayout(shopAccount.getPayout());
    }
}
