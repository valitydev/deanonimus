package dev.vality.deanonimus.kafka.handler.party.management.shop;

import dev.vality.damsel.domain.Blocking;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.deanonimus.db.PartyRepository;
import dev.vality.deanonimus.db.exception.PartyNotFoundException;
import dev.vality.deanonimus.db.exception.ShopNotFoundException;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.domain.Blocking;
import dev.vality.deanonimus.domain.Shop;
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
public class ShopBlockingHandler implements PartyManagementHandler {

    private final PartyRepository partyRepository;
    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "shop_blocking",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        Blocking blocking = change.getShopBlocking().getBlocking();
        String shopId = change.getShopBlocking().getShopId();
        String partyId = event.getSourceId();
        log.info("Start shop blocking handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);

        Party party = partyRepository.findById(partyId).orElseThrow(() -> new PartyNotFoundException(partyId));

        initBlockingFields(blocking, party.getShopById(shopId).orElseThrow(() -> new ShopNotFoundException(shopId)));

        partyRepository.save(party);

        log.info("End shop blocking handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);
    }

    private void initBlockingFields(Blocking blocking, Shop shopSource) {
        if (blocking.isSetUnblocked()) {
            shopSource.setBlocking(Blocking.unblocked);
        } else if (blocking.isSetBlocked()) {
            shopSource.setBlocking(Blocking.blocked);
        }
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
