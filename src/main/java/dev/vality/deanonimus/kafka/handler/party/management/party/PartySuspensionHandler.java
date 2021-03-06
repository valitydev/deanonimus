package dev.vality.deanonimus.kafka.handler.party.management.party;

import dev.vality.damsel.domain.Suspension;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.deanonimus.db.PartyRepository;
import dev.vality.deanonimus.db.exception.PartyNotFoundException;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.kafka.handler.party.management.PartyManagementHandler;
import dev.vality.geck.filter.Filter;
import dev.vality.geck.filter.PathConditionFilter;
import dev.vality.geck.filter.condition.IsNullCondition;
import dev.vality.geck.filter.rule.PathConditionRule;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartySuspensionHandler implements PartyManagementHandler {

    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "party_suspension",
            new IsNullCondition().not()));

    private final PartyRepository partyRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        Suspension partySuspension = change.getPartySuspension();
        String partyId = event.getSourceId();
        log.info("Start party suspension handling, eventId={}, partyId={}, changeId={}", sequenceId, partyId, changeId);
        Party party = partyRepository.findById(partyId).orElseThrow(() -> new PartyNotFoundException(partyId));

        if (partySuspension.isSetActive()) {
            party.setSuspension(dev.vality.deanonimus.domain.Suspension.active);
        } else if (partySuspension.isSetSuspended()) {
            party.setSuspension(dev.vality.deanonimus.domain.Suspension.suspended);
        }

        partyRepository.save(party);
        log.info("End party suspension handling, eventId={}, partyId={}, changeId={}", sequenceId, partyId, changeId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
