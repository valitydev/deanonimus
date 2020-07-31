package com.rbkmoney.deanonimus.kafka.handler.party_mngmnt.shop;

import com.rbkmoney.damsel.domain.ShopLocation;
import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.ShopEffectUnit;
import com.rbkmoney.deanonimus.db.PartyRepository;
import com.rbkmoney.deanonimus.db.exception.PartyNotFoundException;
import com.rbkmoney.deanonimus.domain.Party;
import com.rbkmoney.deanonimus.domain.Shop;
import com.rbkmoney.deanonimus.kafka.handler.party_mngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
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

    private final PartyRepository partyRepository;

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
        Party party = partyRepository.findById(partyId).orElseThrow(PartyNotFoundException::new);

        Shop shop = party.getShops().get(0);

        if (locationChanged.isSetUrl()) {
            shop.setLocationUrl(locationChanged.getUrl());
        } else {
            throw new IllegalArgumentException("Illegal shop location " + locationChanged);
        }

        partyRepository.save(party);

        log.info("End shop locationChanged handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);
    }
}
