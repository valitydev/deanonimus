package com.rbkmoney.deanonimus.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    @Field(type = FieldType.Keyword)
    private String id;
    private String contractorId;
    private String partyId;
    private Integer paymentInstitutionId;
    private LocalDateTime validSince;
    private LocalDateTime validUntil;
    private ContractStatus status;
    private Integer termsId;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
    private String legalAgreementId;
    private Integer reportActScheduleId;
    private String reportActSignerPosition;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
    private String reportActSignerFullName;
    private RepresentativeDocument reportActSignerDocument;
    private String reportActSignerDocPowerOfAttorneyLegalAgreementId;
    private LocalDateTime reportActSignerDocPowerOfAttorneyValidUntil;
}
