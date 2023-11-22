package dev.vality.deanonimus.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contractor {
    private String id;
    private String partyId;
    private ContractorType type;
    private String registeredUserEmail;
    private LegalEntity legalEntity;
    private String russianLegalEntityRegisteredName;
    private String russianLegalEntityRegisteredNumber;
    private String russianLegalEntityInn;
    private String russianLegalEntityActualAddress;
    private String russianLegalEntityPostAddress;
    private String russianLegalEntityRussianBankAccount;
    private String russianLegalEntityRussianBankName;
    private String russianLegalEntityRussianBankPostAccount;
    private String russianLegalEntityRussianBankBik;
    private String internationalLegalEntityLegalName;
    private String internationalLegalEntityTradingName;
    private String internationalLegalEntityRegisteredAddress;
    private String internationalLegalEntityActualAddress;
    private String internationalLegalEntityRegisteredNumber;
}
