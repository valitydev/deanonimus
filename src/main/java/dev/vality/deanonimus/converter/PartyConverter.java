package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.Party;
import dev.vality.deanonimus.util.EnumUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyConverter implements Converter<dev.vality.deanonimus.domain.Party, Party> {

    private final ContractorConverter contractorConverter;
    private final ContractConverter contractConverter;
    private final ShopListConverter shopConverter;

    @Override
    public Party convert(dev.vality.deanonimus.domain.Party domainParty) {
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
