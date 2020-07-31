package com.rbkmoney.deanonimus.kafka.handler.party_mngmnt.contractor;

import com.rbkmoney.damsel.domain.ContractorIdentificationLevel;
import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.ContractorEffectUnit;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.deanonimus.db.PartyRepository;
import com.rbkmoney.deanonimus.db.exception.ContractorNotFoundException;
import com.rbkmoney.deanonimus.db.exception.PartyNotFoundException;
import com.rbkmoney.deanonimus.domain.Contractor;
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
public class ContractorIdentificationalLevelChangedHandler extends AbstractClaimChangedHandler {

    private final PartyRepository partyRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (ClaimEffect claimEffect : claimEffects) {
            if (claimEffect.isSetContractorEffect() && claimEffect.getContractorEffect().getEffect().isSetIdentificationLevelChanged()) {
                handleEvent(event, sequenceId, claimEffect);
            }
        }
    }

    private void handleEvent(MachineEvent event, long sequenceId, ClaimEffect claimEffect) {
        ContractorEffectUnit contractorEffect = claimEffect.getContractorEffect();
        ContractorIdentificationLevel identificationLevelChanged = contractorEffect.getEffect().getIdentificationLevelChanged();
        String contractorId = contractorEffect.getId();
        String partyId = event.getSourceId();
        log.info("Start identificational level changed handling, sequenceId={}, partyId={}, contractorId={}", sequenceId, partyId, contractorId);

        Party party = partyRepository.findById(partyId).orElseThrow(PartyNotFoundException::new);
        Contractor contractor = party.getContractorById(contractorId).orElseThrow(ContractorNotFoundException::new);

        contractor.setIdentificationalLevel(identificationLevelChanged.name());

        partyRepository.save(party);

        log.info("End identificational level changed handling, sequenceId={}, partyId={}, contractorId={}", sequenceId, partyId, contractorId);
    }
}
