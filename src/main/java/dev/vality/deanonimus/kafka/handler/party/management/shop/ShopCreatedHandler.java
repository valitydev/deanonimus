package dev.vality.deanonimus.kafka.handler.party.management.shop;


import dev.vality.damsel.payment_processing.ClaimEffect;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.damsel.payment_processing.ShopEffectUnit;
import dev.vality.deanonimus.domain.Blocking;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.domain.Shop;
import dev.vality.deanonimus.domain.Suspension;
import dev.vality.deanonimus.kafka.handler.party.management.AbstractClaimChangedHandler;
import dev.vality.deanonimus.service.OpenSearchService;
import dev.vality.deanonimus.util.ShopUtil;
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
public class ShopCreatedHandler extends AbstractClaimChangedHandler {

    private final OpenSearchService openSearchService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (ClaimEffect claimEffect : claimEffects) {
            if (claimEffect.isSetShopEffect() && claimEffect.getShopEffect().getEffect().isSetCreated()) {
                handleEvent(event, changeId, claimEffect);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, ClaimEffect e) {
        long sequenceId = event.getEventId();
        ShopEffectUnit shopEffect = e.getShopEffect();
        dev.vality.damsel.domain.Shop shopCreated = shopEffect.getEffect().getCreated();
        String shopId = shopEffect.getShopId();
        String partyId = event.getSourceId();
        log.info("Start shop created handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);

        Party party = openSearchService.findPartyById(partyId);

        Shop shop = fillShopInfo(shopCreated, shopId);

        party.addShop(shop);

        openSearchService.updateParty(party);

        log.info("End shop created handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);
    }

    private Shop fillShopInfo(dev.vality.damsel.domain.Shop shopCreated, String shopId) {
        Shop shop = new Shop();
        shop.setId(shopId);

        if (shopCreated.getBlocking().isSetUnblocked()) {
            shop.setBlocking(Blocking.unblocked);
        } else if (shopCreated.getBlocking().isSetBlocked()) {
            shop.setBlocking(Blocking.blocked);
        }

        if (shopCreated.getSuspension().isSetActive()) {
            shop.setSuspension(Suspension.active);
        } else if (shopCreated.getSuspension().isSetSuspended()) {
            shop.setSuspension(Suspension.suspended);
        }
        shop.setDetailsName(shopCreated.getDetails().getName());
        shop.setDetailsDescription(shopCreated.getDetails().getDescription());
        if (shopCreated.getLocation().isSetUrl()) {
            shop.setLocationUrl(shopCreated.getLocation().getUrl());
        } else {
            throw new IllegalArgumentException("Illegal shop location " + shopCreated.getLocation());
        }
        shop.setCategoryId(shopCreated.getCategory().getId());
        if (shopCreated.isSetAccount()) {
            ShopUtil.fillShopAccount(shop, shopCreated.getAccount());
        }
        shop.setContractId(shopCreated.getContractId());
        return shop;
    }
}
