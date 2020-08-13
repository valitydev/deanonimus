package com.rbkmoney.deanonimus.converter;

import com.rbkmoney.damsel.deanonimus.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ContractConverter {

    public Map<String, Contract> convert(List<com.rbkmoney.deanonimus.domain.Contract> contracts) {
        if (CollectionUtils.isEmpty(contracts)) return Collections.emptyMap();
        return contracts.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<String, Contract> convertToEntity(com.rbkmoney.deanonimus.domain.Contract contract) {
        return new AbstractMap.SimpleEntry<>(contract.getId(), convertContract(contract));
    }

    private Contract convertContract(com.rbkmoney.deanonimus.domain.Contract contractDomain) {
        Contract contract = new Contract(
                contractDomain.getId(),
                convertStatus(contractDomain.getStatus()),
                contractDomain.getTermsId()
        );
        if (contractDomain.getLegalAgreementId() != null) {
            contract.setLegalAgreement(new LegalAgreement(
                    contractDomain.getLegalAgreementId()
            ));
        }
        contract.setContractorId(contractDomain.getContractorId());
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

    private ContractStatus convertStatus(com.rbkmoney.deanonimus.domain.ContractStatus status) {
        switch (status) {
            case active:
                return ContractStatus.active(new ContractActive());
            case expired:
                return ContractStatus.expired(new ContractExpired());
            case terminated:
                return ContractStatus.terminated(new ContractTerminated());
            default:
                throw new RuntimeException("No such contractStatus");
        }
    }

}
