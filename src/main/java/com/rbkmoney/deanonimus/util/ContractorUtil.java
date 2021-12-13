package com.rbkmoney.deanonimus.util;

import com.rbkmoney.damsel.domain.InternationalLegalEntity;
import com.rbkmoney.damsel.domain.RussianBankAccount;
import com.rbkmoney.damsel.domain.RussianLegalEntity;
import com.rbkmoney.deanonimus.domain.Contractor;
import com.rbkmoney.deanonimus.domain.ContractorType;
import com.rbkmoney.deanonimus.domain.LegalEntity;
import com.rbkmoney.geck.common.util.TBaseUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractorUtil {

    public static Contractor convertContractor(String partyId,
                                               com.rbkmoney.damsel.domain.Contractor contractorSource,
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
