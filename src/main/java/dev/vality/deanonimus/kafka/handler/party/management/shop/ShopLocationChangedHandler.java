package dev.vality.deanonimus.kafka.handler.party.management.shop;

import dev.vality.damsel.domain.ShopLocation;
import dev.vality.damsel.payment_processing.ClaimEffect;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.damsel.payment_processing.ShopEffectUnit;
import dev.vality.deanonimus.db.exception.PartyNotFoundException;
import dev.vality.deanonimus.db.exception.ShopNotFoundException;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.domain.Shop;
import dev.vality.deanonimus.kafka.handler.party.management.AbstractClaimChangedHandler;
import dev.vality.deanonimus.service.OpenSearchService;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopLocationChangedHandler extends AbstractClaimChangedHandler {

    private final OpenSearchService openSearchService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (ClaimEffect claimEffect : claimEffects) {
            if (claimEffect.isSetShopEffect() && claimEffect.getShopEffect().getEffect().isSetLocationChanged()) {
                handleEvent(event, changeId, sequenceId, claimEffect);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect claimEffect) {
        ShopEffectUnit shopEffect = claimEffect.getShopEffect();
        ShopLocation locationChanged = shopEffect.getEffect().getLocationChanged();
        String shopId = shopEffect.getShopId();
        String partyId = event.getSourceId();
        log.info("Start shop locationChanged handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);
        Party party = openSearchService.findPartyById(partyId);

        Shop shop = party.getShopById(shopId).orElseThrow(() -> new ShopNotFoundException(shopId));

        if (locationChanged.isSetUrl()) {
            shop.setLocationUrl(locationChanged.getUrl());
        } else {
            throw new IllegalArgumentException("Illegal shop location " + locationChanged);
        }

        openSearchService.updateParty(party);

        log.info("End shop locationChanged handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);
    }
}
