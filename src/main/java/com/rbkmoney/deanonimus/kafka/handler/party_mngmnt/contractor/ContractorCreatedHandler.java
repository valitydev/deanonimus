package com.rbkmoney.deanonimus.kafka.handler.party_mngmnt.contractor;

import com.rbkmoney.damsel.domain.PartyContractor;
import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.ContractorEffectUnit;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.deanonimus.db.PartyRepository;
import com.rbkmoney.deanonimus.db.exception.PartyNotFoundException;
import com.rbkmoney.deanonimus.domain.Party;
import com.rbkmoney.deanonimus.kafka.handler.party_mngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.deanonimus.util.ContractorUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Slf4j
@Component
@Order(HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ContractorCreatedHandler extends AbstractClaimChangedHandler {

    private final PartyRepository partyRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long eventId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (ClaimEffect claimEffect : claimEffects) {
            if (claimEffect.isSetContractorEffect() && claimEffect.getContractorEffect().getEffect().isSetCreated()) {
                handleEvent(event, eventId, claimEffect);
            }
        }
    }

    private void handleEvent(MachineEvent event, long eventId, ClaimEffect claimEffect) {
        ContractorEffectUnit contractorEffect = claimEffect.getContractorEffect();
        PartyContractor partyContractor = contractorEffect.getEffect().getCreated();
        com.rbkmoney.damsel.domain.Contractor contractorCreated = partyContractor.getContractor();
        String contractorId = contractorEffect.getId();
        String partyId = event.getSourceId();
        log.info("Start contractor created handling, eventId={}, partyId={}, contractorId={}", eventId, partyId, contractorId);
        Party party = partyRepository.findById(partyId).orElseThrow(PartyNotFoundException::new);
        com.rbkmoney.deanonimus.domain.Contractor contractor = ContractorUtil.convertContractor(partyId, contractorCreated, contractorId);
        contractor.setIdentificationalLevel(partyContractor.getStatus().name());

        party.addContractor(contractor);

        partyRepository.save(party);
        log.info("End contractor created handling, eventId={}, partyId={}, contractorId={}", eventId, partyId, contractorId);
    }


}
