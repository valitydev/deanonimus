package dev.vality.deanonimus.kafka.handler.party.management.party;

import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.damsel.payment_processing.PartyCreated;
import dev.vality.deanonimus.db.PartyRepository;
import dev.vality.deanonimus.domain.Blocking;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.domain.Suspension;
import dev.vality.deanonimus.kafka.handler.party.management.PartyManagementHandler;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartyCreatedHandler implements PartyManagementHandler {

    private final PartyRepository partyRepository;
    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "party_created",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        PartyCreated partyCreated = change.getPartyCreated();
        String partyId = partyCreated.getId();
        log.info("Start party created handling, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId, changeId);
        Party party = new Party();
        party.setId(partyId);
        party.setEmail(partyCreated.getContactInfo().getEmail());
        party.setBlocking(Blocking.unblocked);
        party.setSuspension(Suspension.active);

        partyRepository.save(party);
        log.info("Party has been saved, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId, changeId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
