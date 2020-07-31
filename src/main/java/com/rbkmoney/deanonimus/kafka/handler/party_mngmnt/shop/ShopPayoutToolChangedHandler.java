package com.rbkmoney.deanonimus.kafka.handler.party_mngmnt.shop;

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
public class ShopPayoutToolChangedHandler extends AbstractClaimChangedHandler {

    private final PartyRepository partyRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (ClaimEffect claimEffect : claimEffects) {
            if (claimEffect.isSetShopEffect() && claimEffect.getShopEffect().getEffect().isSetPayoutToolChanged()) {
                handleEvent(event, changeId, sequenceId, claimEffect);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect e) {
        ShopEffectUnit shopEffect = e.getShopEffect();
        String payoutToolChanged = shopEffect.getEffect().getPayoutToolChanged();
        String shopId = shopEffect.getShopId();
        String partyId = event.getSourceId();
        log.info("Start shop payoutToolChanged handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);

        Party party = partyRepository.findById(partyId).orElseThrow(PartyNotFoundException::new);
        Shop shop = party.getShops().get(0);

        shop.setPayoutToolId(payoutToolChanged);

        partyRepository.save(party);

        log.info("End shop payoutToolChanged handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);
    }
}
