package com.rbkmoney.deanonimus.converter;

import com.rbkmoney.damsel.deanonimus.*;
import com.rbkmoney.deanonimus.domain.Contractor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ContractorConverter {

    public Map<String, PartyContractor> convert(List<Contractor> contractors) {
        return Optional.ofNullable(contractors).orElse(Collections.emptyList())
                .stream()
                .map(this::convertToEntity)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<String, PartyContractor> convertToEntity(Contractor contractor) {
        return Map.entry(contractor.getId(), new PartyContractor(
                contractor.getId(),
                convertContractor(contractor)
        ));
    }

    private com.rbkmoney.damsel.deanonimus.Contractor convertContractor(Contractor contractor) {
        switch (contractor.getType()) {
            case legal_entity:
                return com.rbkmoney.damsel.deanonimus.Contractor.legal_entity(convertLegalEntity(contractor));
            case private_entity:
                return com.rbkmoney.damsel.deanonimus.Contractor.private_entity(new PrivateEntity());
            case registered_user:
                return com.rbkmoney.damsel.deanonimus.Contractor.registered_user(new RegisteredUser(contractor.getRegisteredUserEmail()));
            default:
                throw new IllegalArgumentException("No such contractorType: " + contractor.getType());
        }
    }

    private LegalEntity convertLegalEntity(Contractor contractor) {
        switch (contractor.getLegalEntity()) {
            case international_legal_entity:
                InternationalLegalEntity internationalLegalEntity = new InternationalLegalEntity()
                        .setLegalName(contractor.getInternationalLegalEntityLegalName())
                        .setTradingName(contractor.getInternationalLegalEntityTradingName())
                        .setRegisteredAddress(contractor.getInternationalLegalEntityRegisteredAddress())
                        .setActualAddress(contractor.getInternationalLegalEntityActualAddress())
                        .setRegisteredNumber(contractor.getInternationalLegalEntityRegisteredNumber());
                return LegalEntity.international_legal_entity(internationalLegalEntity);
            case russian_legal_entity:
                RussianLegalEntity russianLegalEntity = new RussianLegalEntity()
                        .setRegisteredName(contractor.getRussianLegalEntityRegisteredName())
                        .setRegisteredNumber(contractor.getRussianLegalEntityRegisteredNumber())
                        .setInn(contractor.getRussianLegalEntityInn())
                        .setActualAddress(contractor.getRussianLegalEntityActualAddress())
                        .setPostAddress(contractor.getRussianLegalEntityPostAddress())
                        .setRussianBankAccount(new RussianBankAccount(
                                contractor.getRussianLegalEntityRussianBankAccount(),
                                contractor.getRussianLegalEntityRussianBankName(),
                                contractor.getRussianLegalEntityRussianBankPostAccount(),
                                contractor.getRussianLegalEntityRussianBankBik()
                        ));
                return LegalEntity.russian_legal_entity(russianLegalEntity);
            default:
                throw new IllegalArgumentException("No such legalEntity " + contractor.getLegalEntity());
        }
    }
}
