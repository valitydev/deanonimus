package dev.vality.deanonimus.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contractor {
    @Field(type = FieldType.Keyword)
    private String id;
    private String partyId;
    private ContractorType type;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
    private String registeredUserEmail;
    private LegalEntity legalEntity;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
    private String russianLegalEntityRegisteredName;
    private String russianLegalEntityRegisteredNumber;
    @Field(type = FieldType.Keyword)
    private String russianLegalEntityInn;
    private String russianLegalEntityActualAddress;
    private String russianLegalEntityPostAddress;
    @Field(type = FieldType.Keyword)
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
}
