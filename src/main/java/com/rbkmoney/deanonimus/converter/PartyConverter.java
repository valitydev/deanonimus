package com.rbkmoney.deanonimus.converter;

import com.rbkmoney.damsel.deanonimus.Party;
import com.rbkmoney.deanonimus.util.EnumUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyConverter {

    private final ContractorConverter contractorConverter;
    private final ContractConverter contractConverter;
    private final ShopConverter shopConverter;

    public Party convert(com.rbkmoney.deanonimus.domain.Party domainParty) {
        return new Party(
                domainParty.getId(),
                domainParty.getEmail(),
                EnumUtils.convertBlocking(domainParty.getBlocking()),
                EnumUtils.convertSuspension(domainParty.getSuspension()),
                contractorConverter.convert(domainParty.getContractors()),
                contractConverter.convert(domainParty.getContracts()),
                shopConverter.convert(domainParty.getShops())
        );
    }
}
