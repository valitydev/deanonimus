package dev.vality.deanonimus.kafka.handler.party.management.party;

import dev.vality.damsel.domain.Blocking;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.deanonimus.db.PartyRepository;
import dev.vality.deanonimus.db.exception.PartyNotFoundException;
import dev.vality.deanonimus.domain.Party;
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
public class PartyBlockingHandler implements PartyManagementHandler {

    private final PartyRepository partyRepository;
    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "party_blocking",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        Blocking partyBlocking = change.getPartyBlocking();
        String partyId = event.getSourceId();
        log.info("Start party blocking handling, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId,
                changeId);
        Party partySource = partyRepository.findById(partyId).orElseThrow(() -> new PartyNotFoundException(partyId));

        if (partyBlocking.isSetUnblocked()) {
            partySource.setBlocking(dev.vality.deanonimus.domain.Blocking.unblocked);
        } else if (partyBlocking.isSetBlocked()) {
            partySource.setBlocking(dev.vality.deanonimus.domain.Blocking.blocked);
        }

        partyRepository.save(partySource);
        log.info("End party blocking handling, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId, changeId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
