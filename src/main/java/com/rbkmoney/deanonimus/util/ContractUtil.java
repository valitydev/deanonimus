package com.rbkmoney.deanonimus.util;


import com.rbkmoney.damsel.domain.LegalAgreement;
import com.rbkmoney.damsel.domain.ServiceAcceptanceActPreferences;
import com.rbkmoney.deanonimus.domain.Contract;
import com.rbkmoney.deanonimus.domain.RepresentativeDocument;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractUtil {

    public static void fillReportPreferences(Contract contract,
                                             ServiceAcceptanceActPreferences serviceAcceptanceActPreferences) {
        contract.setReportActSignerFullName(serviceAcceptanceActPreferences.getSigner().getFullName());
        final com.rbkmoney.damsel.domain.RepresentativeDocument document =
                serviceAcceptanceActPreferences.getSigner().getDocument();
        RepresentativeDocument reportActSignerDocument =
                TypeUtil.toEnumField(document.getSetField().getFieldName(), RepresentativeDocument.class);
        if (reportActSignerDocument == null) {
            throw new IllegalArgumentException("Illegal representative document: " + document);
        }
    }

    public static void fillContractLegalAgreementFields(Contract contract, LegalAgreement legalAgreement) {
        contract.setLegalAgreementId(legalAgreement.getLegalAgreementId());
    }

    public static void setNullReportPreferences(Contract contract) {
        contract.setReportActSignerFullName(null);
    }

}
