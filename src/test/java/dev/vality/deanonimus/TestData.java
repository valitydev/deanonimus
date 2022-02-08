package dev.vality.deanonimus;

import dev.vality.deanonimus.domain.ContractStatus;
import dev.vality.deanonimus.domain.*;
import com.rbkmoney.geck.serializer.kit.mock.FieldHandler;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;

import java.time.Instant;
import java.util.Map;

public abstract class TestData {

    private static final Map.Entry<FieldHandler, String[]> timeFields = Map.entry(
            structHandler -> structHandler.value(Instant.now().toString()),
            new String[] {"created_at", "at", "due"}
    );

    private static final MockTBaseProcessor mockTBaseProcessor = new MockTBaseProcessor(MockMode.ALL, 15, 1);

    static {
        mockTBaseProcessor.addFieldHandler(timeFields.getKey(), timeFields.getValue());
    }

    public static final String SOURCE_ID_ONE = "source";

    public static Shop shop(String id, String url) {
        return Shop.builder()
                .id(id)
                .locationUrl(url)
                .blocking(Blocking.unblocked)
                .suspension(Suspension.active)
                .categoryId(1)
                .contractId("1")
                .detailsName("name")
                .build();
    }

    public static Contract contract(String id,
                                    Integer termsId,
                                    String legalAgreementId,
                                    String reportActSignerFullName) {
        return Contract.builder()
                .id(id)
                .status(ContractStatus.active)
                .termsId(termsId)
                .legalAgreementId(legalAgreementId)
                .reportActSignerFullName(reportActSignerFullName)
                .build();
    }

    public static Contractor contractor(String id,
                                        String registeredUserEmail,
                                        String russianLegalEntityRegisteredName,
                                        String russianLegalEntityRegisteredInn,
                                        String russianLegalEntityRussianBankAccount,
                                        String internationalLegalEntityLegalName,
                                        String internationalLegalEntityTradingName) {
        return Contractor.builder()
                .id(id)
                .type(getContractorType(registeredUserEmail, russianLegalEntityRegisteredInn,
                        internationalLegalEntityLegalName))
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

    private static LegalEntity getLegalEntity(String russianLegalEntityRegisteredInn,
                                              String internationalLegalEntityLegalName) {
        if (russianLegalEntityRegisteredInn != null) {
            return LegalEntity.russian_legal_entity;
        }
        if (internationalLegalEntityLegalName != null) {
            return LegalEntity.international_legal_entity;
        }
        return null;
    }

    public static Party party(String id, String email) {
        return Party.builder()
                .id(id)
                .email(email)
                .blocking(Blocking.unblocked)
                .suspension(Suspension.active)
                .build();
    }
}
