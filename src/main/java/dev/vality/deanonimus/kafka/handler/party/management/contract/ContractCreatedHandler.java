package dev.vality.deanonimus.kafka.handler.party.management.contract;

import dev.vality.damsel.payment_processing.ClaimEffect;
import dev.vality.damsel.payment_processing.ContractEffectUnit;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.deanonimus.db.PartyRepository;
import dev.vality.deanonimus.db.exception.PartyNotFoundException;
import dev.vality.deanonimus.domain.Contract;
import dev.vality.deanonimus.domain.ContractStatus;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.kafka.handler.party.management.AbstractClaimChangedHandler;
import dev.vality.deanonimus.util.ContractUtil;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractCreatedHandler extends AbstractClaimChangedHandler {

    private final PartyRepository partyRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (ClaimEffect claimEffect : claimEffects) {
            if (claimEffect.isSetContractEffect()
                    && claimEffect.getContractEffect().getEffect().isSetCreated()) {
                handleEvent(event, changeId, sequenceId, claimEffect);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect e) {
        ContractEffectUnit contractEffectUnit = e.getContractEffect();
        String contractId = contractEffectUnit.getContractId();
        String partyId = event.getSourceId();
        log.info("Start contract created handling, sequenceId={}, partyId={}, contractId={}, changeId={}",
                sequenceId, partyId, contractId, changeId);
        Contract contract = new Contract();
        contract.setId(contractId);
        dev.vality.damsel.domain.Contract contractCreated = contractEffectUnit.getEffect().getCreated();
        contract.setPartyId(partyId);
        if (contractCreated.isSetPaymentInstitution()) {
            contract.setPaymentInstitutionId(contractCreated.getPaymentInstitution().getId());
        }
        if (contractCreated.isSetValidSince()) {
            contract.setValidSince(TypeUtil.stringToLocalDateTime(contractCreated.getValidSince()));
        }
        if (contractCreated.isSetValidUntil()) {
            contract.setValidUntil(TypeUtil.stringToLocalDateTime(contractCreated.getValidUntil()));
        }
        contract.setStatus(TBaseUtil.unionFieldToEnum(contractCreated.getStatus(), ContractStatus.class));
        contract.setTermsId(contractCreated.getTerms().getId());
        if (contractCreated.isSetLegalAgreement()) {
            ContractUtil.fillContractLegalAgreementFields(contract, contractCreated.getLegalAgreement());
        }
        if (contractCreated.isSetReportPreferences()
                && contractCreated.getReportPreferences().isSetServiceAcceptanceActPreferences()) {
            ContractUtil.fillReportPreferences(contract,
                    contractCreated.getReportPreferences().getServiceAcceptanceActPreferences());
        }
        String contractorId = initContractorId(contractCreated);
        contract.setContractorId(contractorId);
        Party party = partyRepository.findById(partyId).orElseThrow(() -> new PartyNotFoundException(partyId));
        party.addContract(contract);
        partyRepository.save(party);
        log.info("End contract created handling, sequenceId={}, partyId={}, contractId={}, changeId={}",
                sequenceId, partyId, contractId, changeId);
    }

    private String initContractorId(dev.vality.damsel.domain.Contract contractCreated) {
        String contractorId = "";
        if (contractCreated.isSetContractorId()) {
            contractorId = contractCreated.getContractorId();
        } else if (contractCreated.isSetContractor()) {
            contractorId = UUID.randomUUID().toString();
        }
        return contractorId;
    }

}
