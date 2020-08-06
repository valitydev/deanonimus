package com.rbkmoney.deanonimus.kafka.handler.party_management.party;

import com.rbkmoney.damsel.domain.Suspension;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.deanonimus.db.PartyRepository;
import com.rbkmoney.deanonimus.db.exception.PartyNotFoundException;
import com.rbkmoney.deanonimus.domain.Party;
import com.rbkmoney.deanonimus.kafka.handler.party_management.PartyManagementHandler;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
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
            party.setSuspension(com.rbkmoney.deanonimus.domain.Suspension.active);
        } else if (partySuspension.isSetSuspended()) {
            party.setSuspension(com.rbkmoney.deanonimus.domain.Suspension.suspended);
        }

        partyRepository.save(party);
        log.info("End party suspension handling, eventId={}, partyId={}, changeId={}", sequenceId, partyId, changeId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
