package dev.vality.deanonimus.util;

import dev.vality.damsel.domain.InternationalLegalEntity;
import dev.vality.damsel.domain.RussianBankAccount;
import dev.vality.damsel.domain.RussianLegalEntity;
import dev.vality.deanonimus.domain.Contractor;
import dev.vality.deanonimus.domain.ContractorType;
import dev.vality.deanonimus.domain.LegalEntity;
import dev.vality.geck.common.util.TBaseUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractorUtil {

    public static Contractor convertContractor(String partyId,
                                               dev.vality.damsel.domain.Contractor contractorSource,
                                               String contractorId) {
        Contractor contractor = new Contractor();
        contractor.setId(contractorId);
        contractor.setPartyId(partyId);
        contractor.setType(TBaseUtil.unionFieldToEnum(contractorSource, ContractorType.class));
        if (contractorSource.isSetRegisteredUser()) {
            contractor.setRegisteredUserEmail(contractorSource.getRegisteredUser().getEmail());
        } else if (contractorSource.isSetLegalEntity()) {
            contractor.setLegalEntity(TBaseUtil.unionFieldToEnum(contractorSource.getLegalEntity(), LegalEntity.class));
            if (contractorSource.getLegalEntity().isSetRussianLegalEntity()) {
                RussianLegalEntity russianLegalEntity = contractorSource.getLegalEntity().getRussianLegalEntity();
                contractor.setRussianLegalEntityRegisteredName(russianLegalEntity.getRegisteredName());
                contractor.setRussianLegalEntityRegisteredNumber(russianLegalEntity.getRegisteredNumber());
                contractor.setRussianLegalEntityInn(russianLegalEntity.getInn());
                contractor.setRussianLegalEntityActualAddress(russianLegalEntity.getActualAddress());
                contractor.setRussianLegalEntityPostAddress(russianLegalEntity.getPostAddress());
                RussianBankAccount russianBankAccount = russianLegalEntity.getRussianBankAccount();
                contractor.setRussianLegalEntityRussianBankAccount(russianBankAccount.getAccount());
                contractor.setRussianLegalEntityRussianBankName(russianBankAccount.getBankName());
                contractor.setRussianLegalEntityRussianBankPostAccount(russianBankAccount.getBankPostAccount());
                contractor.setRussianLegalEntityRussianBankBik(russianBankAccount.getBankBik());
            } else if (contractorSource.getLegalEntity().isSetInternationalLegalEntity()) {
                InternationalLegalEntity internationalLegalEntity =
                        contractorSource.getLegalEntity().getInternationalLegalEntity();
                contractor.setInternationalLegalEntityLegalName(internationalLegalEntity.getLegalName());
                contractor.setInternationalLegalEntityTradingName(internationalLegalEntity.getTradingName());
                contractor.setInternationalLegalEntityRegisteredAddress(
                        internationalLegalEntity.getRegisteredAddress());
                contractor.setInternationalLegalEntityActualAddress(internationalLegalEntity.getActualAddress());
                contractor.setInternationalLegalEntityRegisteredNumber(internationalLegalEntity.getRegisteredNumber());
            }
        }
        return contractor;
    }

}
