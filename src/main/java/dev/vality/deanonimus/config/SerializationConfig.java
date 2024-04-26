package dev.vality.deanonimus.config;

import dev.vality.damsel.payment_processing.PartyEventData;
import dev.vality.fistful.wallet.TimestampedChange;
import dev.vality.sink.common.parser.impl.MachineEventParser;
import dev.vality.sink.common.parser.impl.PartyEventDataMachineEventParser;
import dev.vality.sink.common.serialization.BinaryDeserializer;
import dev.vality.sink.common.serialization.impl.AbstractThriftBinaryDeserializer;
import dev.vality.sink.common.serialization.impl.PartyEventDataDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializationConfig {

    @Bean
    public BinaryDeserializer<PartyEventData> partyEventDataBinaryDeserializer() {
        return new PartyEventDataDeserializer();
    }

    @Bean
    public MachineEventParser<PartyEventData> partyEventDataMachineEventParser(
            BinaryDeserializer<PartyEventData> partyEventDataBinaryDeserializer) {
        return new PartyEventDataMachineEventParser(partyEventDataBinaryDeserializer);
    }

    @Bean
    public BinaryDeserializer<TimestampedChange> walletChangeDeserializer() {
        return new AbstractThriftBinaryDeserializer<>() {

            @Override
            public TimestampedChange deserialize(byte[] bin) {
                return deserialize(bin, new TimestampedChange());
            }
        };
    }

    @Bean
    public MachineEventParser<TimestampedChange> walletChangeMachineEventParser(
            BinaryDeserializer<TimestampedChange> walletChangeDeserializer) {
        return new MachineEventParser<>(walletChangeDeserializer);
    }

    @Bean
    public BinaryDeserializer<dev.vality.fistful.identity.TimestampedChange> identityChangeDeserializer() {
        return new AbstractThriftBinaryDeserializer<>() {

            @Override
            public dev.vality.fistful.identity.TimestampedChange deserialize(byte[] bin) {
                return deserialize(bin, new dev.vality.fistful.identity.TimestampedChange());
            }
        };
    }

    @Bean
    public MachineEventParser<dev.vality.fistful.identity.TimestampedChange> identityChangeMachineEventParser(
            BinaryDeserializer<dev.vality.fistful.identity.TimestampedChange> identityChangeDeserializer) {
        return new MachineEventParser<>(identityChangeDeserializer);
    }
}
