package com.rbkmoney.deanonimus.domain;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
public class Contract {
    @Field(type = FieldType.Keyword)
    private String id;
    private String contractorId;
    private String partyId;
    private Integer paymentInstitutionId;
//    @Field(type = FieldType.Date, store = false, format = DateFormat.date_hour_minute_second_millis, index = false)
    private LocalDateTime validSince;
//    @Field(type = FieldType.Date, store = false, format = DateFormat.date_hour_minute_second_millis, index = false)
    private LocalDateTime validUntil;
    private ContractStatus status;
    @Field(type = FieldType.Integer)
    private Integer termsId;
    private String legalAgreementId;
    private Integer reportActScheduleId;
    private String reportActSignerPosition;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
    private String reportActSignerFullName;
    private RepresentativeDocument reportActSignerDocument;
    private String reportActSignerDocPowerOfAttorneyLegalAgreementId;
//    @Field(type = FieldType.Date, store = false, format = DateFormat.date_hour_minute_second_millis, index = false)
    private LocalDateTime reportActSignerDocPowerOfAttorneyValidUntil;
}
