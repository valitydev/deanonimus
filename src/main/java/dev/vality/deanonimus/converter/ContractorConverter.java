package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.*;
import dev.vality.deanonimus.domain.Contractor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.vality.damsel.deanonimus.Contractor.*;

@Component
public class ContractorConverter {

    public Map<String, PartyContractor> convert(List<Contractor> contractors) {
        return Optional.ofNullable(contractors).orElse(Collections.emptyList())
                .stream()
                .map(this::convertToEntity)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));
    }

    private Map.Entry<String, PartyContractor> convertToEntity(Contractor contractor) {
        return Map.entry(
                contractor.getId(),
                new PartyContractor(
                        contractor.getId(),
                        convertContractor(contractor)));
    }

    private dev.vality.damsel.deanonimus.Contractor convertContractor(Contractor contractor) {
        return switch (contractor.getType()) {
            case legal_entity -> legal_entity(convertLegalEntity(contractor));
            case private_entity -> private_entity(new PrivateEntity());
            case registered_user -> registered_user(new RegisteredUser(contractor.getRegisteredUserEmail()));
        };
    }

    private LegalEntity convertLegalEntity(Contractor contractor) {
        return switch (contractor.getLegalEntity()) {
            case international_legal_entity -> buildInternationalLegalEntity(contractor);
            case russian_legal_entity -> buildRussianLegalEntity(contractor);
        };
    }

    @NotNull
    private LegalEntity buildInternationalLegalEntity(Contractor contractor) {
        var internationalLegalEntity = new InternationalLegalEntity()
                .setLegalName(contractor.getInternationalLegalEntityLegalName())
                .setTradingName(contractor.getInternationalLegalEntityTradingName())
                .setRegisteredAddress(contractor.getInternationalLegalEntityRegisteredAddress())
                .setActualAddress(contractor.getInternationalLegalEntityActualAddress())
                .setRegisteredNumber(contractor.getInternationalLegalEntityRegisteredNumber());
        return LegalEntity.international_legal_entity(internationalLegalEntity);
    }

    @NotNull
    private LegalEntity buildRussianLegalEntity(Contractor contractor) {
        var russianLegalEntity = new RussianLegalEntity()
                .setRegisteredName(contractor.getRussianLegalEntityRegisteredName())
                .setRegisteredNumber(contractor.getRussianLegalEntityRegisteredNumber())
                .setInn(contractor.getRussianLegalEntityInn())
                .setActualAddress(contractor.getRussianLegalEntityActualAddress())
                .setPostAddress(contractor.getRussianLegalEntityPostAddress())
                .setRussianBankAccount(new RussianBankAccount(
                        contractor.getRussianLegalEntityRussianBankAccount(),
                        contractor.getRussianLegalEntityRussianBankName(),
                        contractor.getRussianLegalEntityRussianBankPostAccount(),
                        contractor.getRussianLegalEntityRussianBankBik()));
        return LegalEntity.russian_legal_entity(russianLegalEntity);
    }
}
