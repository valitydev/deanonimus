package com.rbkmoney.deanonimus;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.mock.FieldHandler;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import lombok.SneakyThrows;
import org.apache.thrift.TBase;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestData {

    private static final Map.Entry<FieldHandler, String[]> timeFields = Map.entry(
            structHandler -> structHandler.value(Instant.now().toString()),
            new String[]{"created_at", "at", "due"}
    );

    private static MockTBaseProcessor mockTBaseProcessor = new MockTBaseProcessor(MockMode.ALL, 15, 1);

    static {
        mockTBaseProcessor.addFieldHandler(timeFields.getKey(), timeFields.getValue());
    }

    public static final String SOURCE_ID_ONE = "source1";
    public static final String EMAIL_ONE = "email1@mail.ru";
    private static String SHOP_ID_ONE = "shop";
    private static String CONTRACT_ID_ONE = "contract";
    private static String PAYOUT_TOOL_ID_ONE = "payoutTool";
    public static final LocalDateTime TIME_ONE = LocalDateTime.now();

    public static SinkEvent createSinkEvent(String sourceId, PartyChange partyChange) {

        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(createMessage(sourceId).setData(wrapEventPayload(List.of(partyChange))));
        return sinkEvent;
    }

    public static SinkEvent createSinkEventWithManyChangesInPayload(String sourceId, List<PartyChange> partyChanges) {

        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(createMessage(sourceId).setData(wrapEventPayload(partyChanges)));
        return sinkEvent;
    }

    public static List<SinkEvent> createSinkEvents(String sourceId, List<PartyChange> partyChanges) {
        List<SinkEvent> sinkEvents = new ArrayList<>();
        for (PartyChange partyChange : partyChanges) {
            SinkEvent sinkEvent = new SinkEvent();
            sinkEvent.setEvent(createMessage(sourceId).setData(wrapEventPayload(List.of(partyChange))));
            sinkEvents.add(sinkEvent);
        }
        return sinkEvents;
    }

    @NotNull
    private static Value wrapEventPayload(List<PartyChange> partyChanges) {
        return Value.bin(toByteArray(EventPayload.party_changes(partyChanges)));
    }

    private static MachineEvent createMessage(String sourceId) {
        MachineEvent message = new MachineEvent();
        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setEventId(1L);
        message.setSourceNs("party");
        message.setSourceId(sourceId);
        return message;
    }

    @SneakyThrows
    public static <T extends TBase> T fillTBaseObject(T tBase, Class<T> type) {
        return mockTBaseProcessor.process(tBase, new TBaseHandler<>(type));
    }

    @SneakyThrows
    public static byte[] toByteArray(TBase tBase) {
        return new TSerializer(new TBinaryProtocol.Factory()).serialize(tBase);
    }

    public static PartyChange partyCreated() {
        PartyCreated partyCreated = new PartyCreated(
                SOURCE_ID_ONE,
                new PartyContactInfo(EMAIL_ONE),
                TypeUtil.temporalToString(TIME_ONE));
        fillTBaseObject(partyCreated, PartyCreated.class);

        return PartyChange.party_created(partyCreated);
    }

    public static PartyChange partyBlocked() {
        return PartyChange.party_blocking(Blocking.blocked(
                new Blocked("reason", TypeUtil.temporalToString(LocalDateTime.now())))
        );
    }

    public static PartyChange shopCreated() {
        return fillTBaseObject(PartyChange.claim_created(
                new Claim(1L,
                        ClaimStatus.accepted(new ClaimAccepted().setEffects(
                                List.of(ClaimEffect.shop_effect(new ShopEffectUnit(
                                                SHOP_ID_ONE,
                                                ShopEffect.created(new Shop(
                                                        SHOP_ID_ONE,
                                                        TypeUtil.temporalToString(TIME_ONE),
                                                        Blocking.unblocked(new Unblocked("Потому что", TypeUtil.temporalToString(TIME_ONE))),
                                                        Suspension.active(new Active(TypeUtil.temporalToString(TIME_ONE))),
                                                        new ShopDetails("myshop"),
                                                        ShopLocation.url("http://localhost.ru"),
                                                        new CategoryRef(1),
                                                        CONTRACT_ID_ONE
                                                ))
                                                )
                                        )
                                )
                        )),
                        List.of(),
                        1,
                        TypeUtil.temporalToString(TIME_ONE)
                )), PartyChange.class);

    }
}
