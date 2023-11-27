package dev.vality.deanonimus;

import dev.vality.damsel.domain.*;
import dev.vality.damsel.payment_processing.*;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.geck.serializer.kit.mock.MockMode;
import dev.vality.geck.serializer.kit.mock.MockTBaseProcessor;
import dev.vality.geck.serializer.kit.tbase.TBaseHandler;
import dev.vality.machinegun.eventsink.MachineEvent;
import dev.vality.machinegun.eventsink.SinkEvent;
import dev.vality.machinegun.msgpack.Value;
import dev.vality.sink.common.serialization.impl.PartyEventDataSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PartyFlowGenerator {

    private static final String PARTY_EMAIL = "testPartyEmail";
    private static final String SOURCE_NS = "source_ns";
    private static final String PARTY_BLOCK_REASON = "testPartyBlockReason";
    private static final Long PARTY_REVISION_ID = 12345L;
    private static final Long CLAIM_ID = 524523L;
    private static final Integer REVISION_ID = 431531;
    private static final String WALLET_ID = "345435435";
    private static final String CONTRACTOR_ID = "563462";
    private static final String INN = "213123123123";

    public static List<SinkEvent> generatePartyContractorFlow(String partyId) throws IOException {
        List<SinkEvent> sinkEvents = new ArrayList<>();
        Long sequenceId = 0L;
        sinkEvents.add(buildSinkEvent(buildMessagePartyCreated(sequenceId++, partyId)));
        sinkEvents.add(buildSinkEvent(buildMessagePartyBlocking(sequenceId++, partyId)));
        sinkEvents.add(buildSinkEvent(buildMessagePartySuspension(sequenceId++, partyId)));
        sinkEvents.add(buildSinkEvent(buildMessagePartyRevisionChanged(sequenceId++, partyId)));
        sinkEvents.add(buildSinkEvent(
                buildContractorCreated(sequenceId++, buildRussianLegalPartyContractor(partyId), partyId)));
        sinkEvents.add(buildSinkEvent(buildContractorIdentificationLevelChanged(sequenceId, partyId)));

        return sinkEvents;
    }

    public static List<SinkEvent> generateWalletFlow(String partyId) throws IOException {
        List<SinkEvent> sinkEvents = new ArrayList<>();
        long sequenceId = 0L;
        sinkEvents.add(buildSinkEvent(buildMessagePartyCreated(sequenceId++, partyId)));
        sinkEvents.add(buildSinkEvent(buildMessageWalletCreated(sequenceId, partyId, WALLET_ID)));
        return sinkEvents;
    }

    public static MachineEvent buildMessagePartyCreated(Long sequenceId, String partyId) {
        PartyChange partyChange = buildPartyCreatedPartyChange(partyId);
        return buildMachineEvent(partyId, sequenceId, partyChange);
    }

    public static PartyChange buildPartyCreatedPartyChange(String partyId) {
        PartyCreated partyCreated = buildPartyCreated(partyId);
        PartyChange partyChange = new PartyChange();
        partyChange.setPartyCreated(partyCreated);
        return partyChange;
    }

    public static MachineEvent buildMessagePartyBlocking(Long sequenceId, String partyId) {
        PartyChange partyChange = buildPartyBlockingPartyChange();
        return buildMachineEvent(partyId, sequenceId, partyChange);
    }

    public static PartyChange buildPartyBlockingPartyChange() {
        Blocking blocking = buildPartyBlocking();
        PartyChange partyChange = new PartyChange();
        partyChange.setPartyBlocking(blocking);
        return partyChange;
    }

    public static MachineEvent buildMessagePartySuspension(Long sequenceId, String partyId) {
        PartyChange partyChange = buildPartySuspensionPartyChange();
        return buildMachineEvent(partyId, sequenceId, partyChange);
    }

    public static PartyChange buildPartySuspensionPartyChange() {
        Suspension suspension = buildPartySuspension();
        PartyChange partyChange = new PartyChange();
        partyChange.setPartySuspension(suspension);
        return partyChange;
    }

    public static MachineEvent buildMessagePartyRevisionChanged(Long sequenceId, String partyId) {
        PartyChange partyChange = buildPartyRevisionChangedPartyChange();
        return buildMachineEvent(partyId, sequenceId, partyChange);
    }

    public static PartyChange buildPartyRevisionChangedPartyChange() {
        PartyRevisionChanged partyRevisionChanged = buildPartyRevisionChanged();
        PartyChange partyChange = new PartyChange();
        partyChange.setRevisionChanged(partyRevisionChanged);
        return partyChange;
    }

    public static MachineEvent buildMessageWalletCreated(Long sequenceId, String partyId, String walletId)
            throws IOException {
        PartyChange partyChange = buildWalletCreatedPartyChange(walletId);
        return buildMachineEvent(partyId, sequenceId, partyChange);
    }

    public static PartyChange buildWalletCreatedPartyChange(String walletd) throws IOException {
        WalletEffectUnit walletEffectUnit = new WalletEffectUnit();
        walletEffectUnit.setId(walletd);
        WalletEffect shopEffect = new WalletEffect();
        shopEffect.setCreated(buildWalletCreated());
        walletEffectUnit.setEffect(shopEffect);
        ClaimEffect claimEffect = new ClaimEffect();
        claimEffect.setWalletEffect(walletEffectUnit);
        Claim claim = buildClaimCreated(claimEffect);
        PartyChange partyChange = new PartyChange();
        partyChange.setClaimCreated(claim);
        return partyChange;
    }

    public static MachineEvent buildContractorCreated(
            Long sequenceId,
            PartyContractor partyContractor,
            String partyId) {
        PartyChange partyChange = buildContractorCreatedPartyChange(partyContractor);
        return buildMachineEvent(partyId, sequenceId, partyChange);
    }

    public static PartyChange buildContractorCreatedPartyChange(PartyContractor partyContractor) {
        ContractorEffectUnit contractorEffectUnit = new ContractorEffectUnit();
        contractorEffectUnit.setId(CONTRACTOR_ID);
        ContractorEffect contractorEffect = new ContractorEffect();
        contractorEffect.setCreated(partyContractor);
        contractorEffectUnit.setEffect(contractorEffect);
        ClaimEffect claimEffect = new ClaimEffect();
        claimEffect.setContractorEffect(contractorEffectUnit);
        Claim claim = buildClaimCreated(claimEffect);
        PartyChange partyChange = new PartyChange();
        partyChange.setClaimCreated(claim);
        return partyChange;
    }

    public static MachineEvent buildContractorIdentificationLevelChanged(Long sequenceId, String partyId) {
        PartyChange partyChange = buildContractorIdentificationLevelChangedPartyChange();
        return buildMachineEvent(partyId, sequenceId, partyChange);
    }

    public static PartyChange buildContractorIdentificationLevelChangedPartyChange() {
        ContractorEffectUnit contractorEffectUnit = new ContractorEffectUnit();
        contractorEffectUnit.setId(CONTRACTOR_ID);
        ContractorEffect contractorEffect = new ContractorEffect();
        contractorEffect.setIdentificationLevelChanged(ContractorIdentificationLevel.partial);
        contractorEffectUnit.setEffect(contractorEffect);
        ClaimEffect claimEffect = new ClaimEffect();
        claimEffect.setContractorEffect(contractorEffectUnit);
        Claim claim = buildClaimCreated(claimEffect);
        PartyChange partyChange = new PartyChange();
        partyChange.setClaimCreated(claim);
        return partyChange;
    }

    public static PartyContractor buildRussianLegalPartyContractor(String partyId) throws IOException {
        PartyContractor partyContractor = new PartyContractor();
        partyContractor.setId(partyId);
        partyContractor.setStatus(ContractorIdentificationLevel.none);
        LegalEntity legalEntity = new LegalEntity();
        RussianLegalEntity russianLegalEntity = new RussianLegalEntity();
        russianLegalEntity = new MockTBaseProcessor(MockMode.ALL)
                .process(russianLegalEntity, new TBaseHandler<>(RussianLegalEntity.class));
        russianLegalEntity.setInn(INN);
        legalEntity.setRussianLegalEntity(russianLegalEntity);
        Contractor contractor = new Contractor();
        contractor.setLegalEntity(legalEntity);
        partyContractor.setContractor(contractor);
        partyContractor.setIdentityDocuments(Collections.emptyList());
        return partyContractor;
    }

    public static Claim buildClaimCreated(ClaimEffect claimEffect) {
        ClaimAccepted claimAccepted = new ClaimAccepted();
        claimAccepted.setEffects(Collections.singletonList(claimEffect));
        ClaimStatus claimStatus = ClaimStatus.accepted(claimAccepted);
        return new Claim(CLAIM_ID, claimStatus, REVISION_ID,
                TypeUtil.temporalToString(LocalDateTime.now()));
    }

    public static Wallet buildWalletCreated() throws IOException {
        Wallet wallet = new Wallet();
        wallet = new MockTBaseProcessor(MockMode.ALL).process(wallet, new TBaseHandler<>(Wallet.class));
        wallet.setCreatedAt(TypeUtil.temporalToString(LocalDateTime.now()));
        wallet.setSuspension(buildPartySuspension());
        return wallet;
    }

    public static PartyRevisionChanged buildPartyRevisionChanged() {
        return new PartyRevisionChanged(TypeUtil.temporalToString(LocalDateTime.now()), PARTY_REVISION_ID);
    }

    public static PartyCreated buildPartyCreated(String partyId) {
        return new PartyCreated(partyId, new PartyContactInfo(PARTY_EMAIL),
                TypeUtil.temporalToString(LocalDateTime.now()));
    }

    public static Suspension buildPartySuspension() {
        Suspension suspension = new Suspension();
        suspension.setActive(new Active(TypeUtil.temporalToString(LocalDateTime.now())));

        return suspension;
    }

    public static Blocking buildPartyBlocking() {
        Blocking blocking = new Blocking();
        blocking.setBlocked(new Blocked(PARTY_BLOCK_REASON, TypeUtil.temporalToString(LocalDateTime.now())));
        return blocking;
    }

    public static MachineEvent buildMachineEvent(String sourceId, Long sequenceId, PartyChange... partyChange) {
        MachineEvent message = new MachineEvent();
        message.setCreatedAt(TypeUtil.temporalToString(Instant.now()));
        message.setEventId(sequenceId);
        message.setSourceNs(SOURCE_NS);
        message.setSourceId(sourceId);
        PartyEventDataSerializer partyEventDataSerializer = new PartyEventDataSerializer();
        Value data = new Value();
        ArrayList<PartyChange> partyChanges = new ArrayList<>(Arrays.asList(partyChange));
        data.setBin(partyEventDataSerializer.serialize(new PartyEventData(partyChanges)));
        message.setData(data);
        return message;
    }

    static SinkEvent buildSinkEvent(MachineEvent machineEvent) {
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(machineEvent);
        return sinkEvent;
    }
}
