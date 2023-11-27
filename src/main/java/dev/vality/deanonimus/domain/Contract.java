package dev.vality.deanonimus.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    private String id;
    private String contractorId;
    private String partyId;
    private Integer paymentInstitutionId;
    private LocalDateTime validSince;
    private LocalDateTime validUntil;
    private ContractStatus status;
    private Integer termsId;
    private String legalAgreementId;
    private String reportActSignerFullName;
}
