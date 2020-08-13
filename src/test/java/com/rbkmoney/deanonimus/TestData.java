package com.rbkmoney.deanonimus;

import com.rbkmoney.damsel.domain.Blocked;
import com.rbkmoney.damsel.domain.Blocking;
import com.rbkmoney.damsel.domain.PartyContactInfo;
import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyCreated;
import com.rbkmoney.deanonimus.domain.ContractStatus;
import com.rbkmoney.deanonimus.domain.ContractorType;
import com.rbkmoney.deanonimus.domain.LegalEntity;
import com.rbkmoney.deanonimus.domain.Suspension;
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

    private static final MockTBaseProcessor mockTBaseProcessor = new MockTBaseProcessor(MockMode.ALL, 15, 1);

    static {
        mockTBaseProcessor.addFieldHandler(timeFields.getKey(), timeFields.getValue());
    }

    public static final String SOURCE_ID_ONE = "source";
    public static final String EMAIL_ONE = "email@mail.ru";
    public static final LocalDateTime TIME_ONE = LocalDateTime.now();

    public static SinkEvent createSinkEvent(String sourceId, PartyChange partyChange) {

        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(createMessage(sourceId).setData(wrapEventPayload(List.of(partyChange))));
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

    public static com.rbkmoney.deanonimus.domain.Shop shop(String id, String url) {
        return com.rbkmoney.deanonimus.domain.Shop.builder()
                .id(id)
                .locationUrl(url)
                .blocking(com.rbkmoney.deanonimus.domain.Blocking.unblocked)
                .suspension(Suspension.active)
                .categoryId(1)
                .contractId("1")
                .detailsName("name")
                .build();
    }

    public static com.rbkmoney.deanonimus.domain.Contract contract(String id,
                                                                   Integer termsId,
                                                                   String legalAgreementId,
                                                                   String reportActSignerFullName) {
        return com.rbkmoney.deanonimus.domain.Contract.builder()
                .id(id)
                .status(ContractStatus.active)
                .termsId(termsId)
                .legalAgreementId(legalAgreementId)
                .reportActSignerFullName(reportActSignerFullName)
                .build();
    }

    public static com.rbkmoney.deanonimus.domain.Contractor contractor(String id,
                                                                       String registeredUserEmail,
                                                                       String russianLegalEntityRegisteredName,
                                                                       String russianLegalEntityRegisteredInn,
                                                                       String russianLegalEntityRussianBankAccount,
                                                                       String internationalLegalEntityLegalName,
                                                                       String internationalLegalEntityTradingName) {
        return com.rbkmoney.deanonimus.domain.Contractor.builder()
                .id(id)
                .type(getContractorType(registeredUserEmail, russianLegalEntityRegisteredInn, internationalLegalEntityLegalName))
                .legalEntity(getLegalEntity(russianLegalEntityRegisteredInn, internationalLegalEntityLegalName))
                .registeredUserEmail(registeredUserEmail)
                .russianLegalEntityRegisteredName(russianLegalEntityRegisteredName)
                .russianLegalEntityRussianBankAccount(russianLegalEntityRussianBankAccount)
                .russianLegalEntityInn(russianLegalEntityRegisteredInn)
                .internationalLegalEntityLegalName(internationalLegalEntityLegalName)
                .internationalLegalEntityTradingName(internationalLegalEntityTradingName)
                .build();
    }

    private static ContractorType getContractorType(String registeredUserEmail,
                                                    String russianLegalEntityRegisteredInn,
                                                    String internationalLegalEntityLegalName) {
        if (registeredUserEmail != null) {
            return ContractorType.registered_user;
        }
        if (russianLegalEntityRegisteredInn != null || internationalLegalEntityLegalName != null) {
            return ContractorType.legal_entity;
        }
        return null;
    }

    private static LegalEntity getLegalEntity(String russianLegalEntityRegisteredInn, String internationalLegalEntityLegalName) {
        if (russianLegalEntityRegisteredInn != null) {
            return LegalEntity.russian_legal_entity;
        }
        if (internationalLegalEntityLegalName != null) {
            return LegalEntity.international_legal_entity;
        }
        return null;
    }

    public static com.rbkmoney.deanonimus.domain.Party party(String id, String email) {
        return com.rbkmoney.deanonimus.domain.Party.builder()
                .id(id)
                .email(email)
                .blocking(com.rbkmoney.deanonimus.domain.Blocking.unblocked)
                .suspension(Suspension.active)
                .build();
    }
}
