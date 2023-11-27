package dev.vality.deanonimus.kafka.handler.party.management.shop;

import dev.vality.damsel.payment_processing.ClaimEffect;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.damsel.payment_processing.ShopEffectUnit;
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
public class ShopCategoryChangedHandler extends AbstractClaimChangedHandler {

    private final OpenSearchService openSearchService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (ClaimEffect claimEffect : claimEffects) {
            if (claimEffect.isSetShopEffect() && claimEffect.getShopEffect().getEffect().isSetCategoryChanged()) {
                handleEvent(event, changeId, sequenceId, claimEffect);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect e) {
        ShopEffectUnit shopEffect = e.getShopEffect();
        int categoryId = shopEffect.getEffect().getCategoryChanged().getId();
        String shopId = shopEffect.getShopId();
        String partyId = event.getSourceId();
        log.info("Start shop categoryId changed handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);

        Party party = openSearchService.findPartyById(partyId);
        Shop shop = party.getShopById(shopId).orElseThrow(() -> new ShopNotFoundException(shopId));

        shop.setCategoryId(categoryId);

        openSearchService.updateParty(party);

        log.info("End shop categoryId changed handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);
    }
}
