package dev.vality.deanonimus.kafka.handler.wallet;

import dev.vality.deanonimus.service.OpenSearchService;
import dev.vality.fistful.wallet.TimestampedChange;
import dev.vality.geck.filter.Filter;
import dev.vality.geck.filter.PathConditionFilter;
import dev.vality.geck.filter.condition.IsNullCondition;
import dev.vality.geck.filter.rule.PathConditionRule;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletAccountCreatedHandler implements WalletHandler {

    private final OpenSearchService openSearchService;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.account.created", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @SneakyThrows
    public void handle(TimestampedChange timestampedChange, MachineEvent event, Integer changeId) {
        var change = timestampedChange.getChange();
        var account = change.getAccount().getCreated();
        var sequenceId = event.getEventId();
        var walletId = event.getSourceId();
        log.info("Start wallet account created handling, sequenceId={}, walletId={}",
                sequenceId, walletId);
        var identity = openSearchService.findIdentityById(account.getIdentity());
        if (identity == null) {
            TimeUnit.SECONDS.sleep(5);
            throw new RuntimeException("Waiting write identity by consumer");
        }
        var wallet = openSearchService.findWalletById(walletId);
        wallet.setIdentityId(account.getIdentity());
        wallet.setPartyId(identity.getPartyId());
        openSearchService.updateWallet(wallet);
        log.info("Wallet account has been changed, sequenceId={}, walletId={}", sequenceId, walletId);
    }
}
