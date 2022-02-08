package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ContractConverter {

    public Map<String, Contract> convert(List<dev.vality.deanonimus.domain.Contract> contracts) {
        return Optional.ofNullable(contracts).orElse(Collections.emptyList())
                .stream()
                .map(this::convertToEntity)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));
    }

    private Map.Entry<String, Contract> convertToEntity(dev.vality.deanonimus.domain.Contract contract) {
        return Map.entry(contract.getId(), convertContract(contract));
    }

    private Contract convertContract(dev.vality.deanonimus.domain.Contract contractDomain) {
        Contract contract = new Contract(
                contractDomain.getId(),
                convertStatus(contractDomain.getStatus()),
                contractDomain.getTermsId()
        );
        contract.setContractorId(contractDomain.getContractorId());

        if (contractDomain.getLegalAgreementId() != null) {
            contract.setLegalAgreement(new LegalAgreement(
                    contractDomain.getLegalAgreementId()
            ));
        }
        if (contractDomain.getPaymentInstitutionId() != null) {
            contract.setPaymentInstitutionId(contractDomain.getPaymentInstitutionId());
        }
        if (contractDomain.getValidSince() != null) {
            contract.setValidSince(TypeUtil.temporalToString(contractDomain.getValidSince()));
        }
        if (contractDomain.getValidUntil() != null) {
            contract.setValidUntil(TypeUtil.temporalToString(contractDomain.getValidUntil()));
        }
        return contract;
    }

    private ContractStatus convertStatus(dev.vality.deanonimus.domain.ContractStatus status) {
        return switch (status) {
            case active -> ContractStatus.active(new ContractActive());
            case expired -> ContractStatus.expired(new ContractExpired());
            case terminated -> ContractStatus.terminated(new ContractTerminated());
            default -> throw new IllegalArgumentException("No such contractStatus: " + status);
        };
    }

}
