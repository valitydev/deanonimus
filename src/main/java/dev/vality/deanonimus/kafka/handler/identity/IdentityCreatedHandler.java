package dev.vality.deanonimus.kafka.handler.identity;

import dev.vality.deanonimus.domain.wallet.Identity;
import dev.vality.deanonimus.service.OpenSearchService;
import dev.vality.fistful.identity.TimestampedChange;
import dev.vality.geck.filter.Filter;
import dev.vality.geck.filter.PathConditionFilter;
import dev.vality.geck.filter.condition.IsNullCondition;
import dev.vality.geck.filter.rule.PathConditionRule;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityCreatedHandler implements IdentityHandler {

    private final OpenSearchService openSearchService;

    @Getter
    private final Filter filter =
            new PathConditionFilter(new PathConditionRule("change.created", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event, Integer changeId) {
        var change = timestampedChange.getChange();
        var sequenceId = event.getEventId();
        var identityId = event.getSourceId();
        log.info("Start identity created handling, sequenceId={}, identityId={}", sequenceId, identityId);
        var identity = Identity.builder()
                .id(identityId)
                .name(change.getCreated().getName())
                .partyId(change.getCreated().getParty())
                .build();
        openSearchService.createIdentity(identity);
        log.info("Identity created has been saved, sequenceId={}, identityId={}", sequenceId, identityId);
    }
}
