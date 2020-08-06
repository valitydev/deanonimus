package com.rbkmoney.deanonimus.kafka.handler.party_management.shop;

import com.rbkmoney.damsel.domain.Suspension;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.deanonimus.db.PartyRepository;
import com.rbkmoney.deanonimus.db.exception.PartyNotFoundException;
import com.rbkmoney.deanonimus.db.exception.ShopNotFoundException;
import com.rbkmoney.deanonimus.domain.Party;
import com.rbkmoney.deanonimus.domain.Shop;
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
public class ShopSuspensionHandler implements PartyManagementHandler {

    private final PartyRepository partyRepository;
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

        Party party = partyRepository.findById(partyId).orElseThrow(() -> new PartyNotFoundException(partyId));
        Shop shop = party.getShopById(shopId).orElseThrow(() -> new ShopNotFoundException(shopId));

        if (suspension.isSetActive()) {
            shop.setSuspension(com.rbkmoney.deanonimus.domain.Suspension.active);
        } else if (suspension.isSetSuspended()) {
            shop.setSuspension(com.rbkmoney.deanonimus.domain.Suspension.suspended);
        }

        partyRepository.save(party);

        log.info("End shop suspension handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
