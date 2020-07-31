package com.rbkmoney.deanonimus.domain;

import lombok.Data;

@Data
public class Wallet {
    private String id;
    private String walletName;
    private String identityId;
    private String partyId;
    private String currencyCode;
}
