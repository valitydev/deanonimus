package dev.vality.deanonimus.kafka.handler.party.management.shop;

import dev.vality.damsel.domain.Suspension;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.deanonimus.db.exception.PartyNotFoundException;
import dev.vality.deanonimus.db.exception.ShopNotFoundException;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.domain.Shop;
import dev.vality.deanonimus.kafka.handler.party.management.PartyManagementHandler;
import dev.vality.deanonimus.service.OpenSearchService;
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
public class ShopSuspensionHandler implements PartyManagementHandler {

    private final OpenSearchService openSearchService;
    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "shop_suspension",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        Suspension suspension = change.getShopSuspension().getSuspension();
        String shopId = change.getShopSuspension().getShopId();
        String partyId = event.getSourceId();
        log.info("Start shop suspension handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);

        Party party = openSearchService.findPartyById(partyId);
        Shop shop = party.getShopById(shopId).orElseThrow(() -> new ShopNotFoundException(shopId));

        if (suspension.isSetActive()) {
            shop.setSuspension(dev.vality.deanonimus.domain.Suspension.active);
        } else if (suspension.isSetSuspended()) {
            shop.setSuspension(dev.vality.deanonimus.domain.Suspension.suspended);
        }

        openSearchService.updateParty(party);

        log.info("End shop suspension handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
