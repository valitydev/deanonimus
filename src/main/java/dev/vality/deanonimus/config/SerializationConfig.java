package dev.vality.deanonimus.config;

import dev.vality.damsel.payment_processing.PartyEventData;
import dev.vality.sink.common.parser.impl.MachineEventParser;
import dev.vality.sink.common.parser.impl.PartyEventDataMachineEventParser;
import dev.vality.sink.common.serialization.BinaryDeserializer;
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
}
