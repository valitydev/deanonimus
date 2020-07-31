package com.rbkmoney.deanonimus.domain;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class Contractor {
    private String id;
    private String partyId;
    private ContractorType type;
    private String identificationalLevel;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
    private String registeredUserEmail;
    private LegalEntity legalEntity;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
    private String russianLegalEntityRegisteredName;
    private String russianLegalEntityRegisteredNumber;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
    private String russianLegalEntityInn;
    private String russianLegalEntityActualAddress;
    private String russianLegalEntityPostAddress;
    private String russianLegalEntityRepresentativePosition;
    private String russianLegalEntityRepresentativeFullName;
    private String russianLegalEntityRepresentativeDocument;
    private String russianLegalEntityRussianBankAccount;
    private String russianLegalEntityRussianBankName;
    private String russianLegalEntityRussianBankPostAccount;
    private String russianLegalEntityRussianBankBik;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
    private String internationalLegalEntityLegalName;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
    private String internationalLegalEntityTradingName;
    private String internationalLegalEntityRegisteredAddress;
    private String internationalLegalEntityActualAddress;
    private String internationalLegalEntityRegisteredNumber;
    private PrivateEntity privateEntity;
    private String russianPrivateEntityFirstName;
    private String russianPrivateEntitySecondName;
    private String russianPrivateEntityMiddleName;
    private String russianPrivateEntityPhoneNumber;
    private String russianPrivateEntityEmail;
}
