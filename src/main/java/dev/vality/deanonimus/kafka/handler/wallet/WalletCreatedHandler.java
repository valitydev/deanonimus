package dev.vality.deanonimus.kafka.handler.wallet;

import dev.vality.deanonimus.domain.wallet.Wallet;
import dev.vality.deanonimus.service.OpenSearchService;
import dev.vality.fistful.wallet.TimestampedChange;
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
public class WalletCreatedHandler implements WalletHandler {

    private final OpenSearchService openSearchService;

    @Getter
    private final Filter filter =
            new PathConditionFilter(new PathConditionRule("change.created", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event, Integer changeId) {
        var change = timestampedChange.getChange();
        var sequenceId = event.getEventId();
        var walletId = event.getSourceId();
        log.info("Start wallet created handling, sequenceId={}, walletId={}", sequenceId, walletId);
        var wallet = Wallet.builder()
                .id(walletId)
                .name(change.getCreated().getName())
                .externalId(change.getCreated().getExternalId())
                .build();
        openSearchService.createWallet(wallet);
        log.info("Wallet created has been saved, sequenceId={}, walletId={}", sequenceId, walletId);
    }
}
