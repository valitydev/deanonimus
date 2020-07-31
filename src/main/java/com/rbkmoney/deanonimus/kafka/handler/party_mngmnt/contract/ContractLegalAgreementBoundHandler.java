package com.rbkmoney.deanonimus.kafka.handler.party_mngmnt.contract;

import com.rbkmoney.damsel.domain.LegalAgreement;
import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.ContractEffectUnit;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.deanonimus.db.PartyRepository;
import com.rbkmoney.deanonimus.db.exception.ContractNotFoundException;
import com.rbkmoney.deanonimus.db.exception.PartyNotFoundException;
import com.rbkmoney.deanonimus.domain.Contract;
import com.rbkmoney.deanonimus.domain.Party;
import com.rbkmoney.deanonimus.kafka.handler.party_mngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractLegalAgreementBoundHandler extends AbstractClaimChangedHandler {

    private final PartyRepository partyRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (int i = 0; i < claimEffects.size(); i++) {
            ClaimEffect claimEffect = claimEffects.get(i);
            if (claimEffect.isSetContractEffect() && claimEffect.getContractEffect().getEffect().isSetLegalAgreementBound()) {
                handleEvent(event, changeId, sequenceId, claimEffects.get(i));
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect claimEffect) {
        ContractEffectUnit contractEffectUnit = claimEffect.getContractEffect();
        LegalAgreement legalAgreement = contractEffectUnit.getEffect().getLegalAgreementBound();
        String contractId = contractEffectUnit.getContractId();
        String partyId = event.getSourceId();
        log.info("Start contract legal agreement bound handling, sequenceId={}, partyId={}, contractId={}, changeId={}",
                sequenceId, partyId, contractId, changeId);

        Party party = partyRepository.findById(partyId).orElseThrow(PartyNotFoundException::new);

        Contract contract = party.getContractById(contractId).orElseThrow(ContractNotFoundException::new);
        contract.setLegalAgreementId(legalAgreement.getLegalAgreementId());

        partyRepository.save(party);

        log.info("End contract legal agreement bound handling, sequenceId={}, partyId={}, contractId={}, changeId={}",
                sequenceId, partyId, contractId, changeId);
    }
}
