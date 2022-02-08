package dev.vality.deanonimus.util;


import dev.vality.damsel.domain.LegalAgreement;
import dev.vality.damsel.domain.ServiceAcceptanceActPreferences;
import dev.vality.deanonimus.domain.Contract;
import dev.vality.deanonimus.domain.RepresentativeDocument;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractUtil {

    public static void fillReportPreferences(Contract contract,
                                             ServiceAcceptanceActPreferences serviceAcceptanceActPreferences) {
        contract.setReportActSignerFullName(serviceAcceptanceActPreferences.getSigner().getFullName());
        var document = serviceAcceptanceActPreferences.getSigner().getDocument();
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
