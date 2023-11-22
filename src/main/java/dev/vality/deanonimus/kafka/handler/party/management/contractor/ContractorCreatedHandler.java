package dev.vality.deanonimus.kafka.handler.party.management.contractor;

import dev.vality.damsel.domain.PartyContractor;
import dev.vality.damsel.payment_processing.ClaimEffect;
import dev.vality.damsel.payment_processing.ContractorEffectUnit;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.kafka.handler.party.management.AbstractClaimChangedHandler;
import dev.vality.deanonimus.service.OpenSearchService;
import dev.vality.deanonimus.util.ContractorUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractorCreatedHandler extends AbstractClaimChangedHandler {

    private final OpenSearchService openSearchService;

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
        dev.vality.damsel.domain.Contractor contractorCreated = partyContractor.getContractor();
        String contractorId = contractorEffect.getId();
        String partyId = event.getSourceId();
        log.info("Start contractor created handling, eventId={}, partyId={}, contractorId={}", eventId, partyId,
                contractorId);
        Party party = openSearchService.findPartyById(partyId);
        dev.vality.deanonimus.domain.Contractor contractor =
                ContractorUtil.convertContractor(partyId, contractorCreated, contractorId);

        party.addContractor(contractor);

        openSearchService.updateParty(party);
        log.info("End contractor created handling, eventId={}, partyId={}, contractorId={}", eventId, partyId,
                contractorId);
    }


}
