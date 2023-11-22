package dev.vality.deanonimus.kafka.handler.party.management.party;

import dev.vality.damsel.domain.Blocking;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.kafka.handler.party.management.PartyManagementHandler;
import dev.vality.deanonimus.service.OpenSearchService;
import dev.vality.geck.filter.Filter;
import dev.vality.geck.filter.PathConditionFilter;
import dev.vality.geck.filter.condition.IsNullCondition;
import dev.vality.geck.filter.rule.PathConditionRule;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartyBlockingHandler implements PartyManagementHandler {

    private final OpenSearchService openSearchService;

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
        Party partySource = openSearchService.findPartyById(partyId);

        if (partyBlocking.isSetUnblocked()) {
            partySource.setBlocking(dev.vality.deanonimus.domain.Blocking.unblocked);
        } else if (partyBlocking.isSetBlocked()) {
            partySource.setBlocking(dev.vality.deanonimus.domain.Blocking.blocked);
        }

        openSearchService.updateParty(partySource);
        log.info("End party blocking handling, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId, changeId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
