package com.rbkmoney.deanonimus;

import com.rbkmoney.damsel.deanonimus.SearchHit;
import com.rbkmoney.deanonimus.db.PartyRepository;
import com.rbkmoney.deanonimus.domain.Party;
import com.rbkmoney.deanonimus.handler.DeanonimusServiceHandler;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ReadTest extends IntegrationTestBase {

    @Autowired
    PartyRepository partyRepository;

    @Autowired
    DeanonimusServiceHandler deanonimusServiceHandler;

    private static final String PARTY = "party";
    private static final String SHOP = "shop";
    private static final String EMAIL = "email@mail.com";
    private static final String URL = "http://url.com";
    private static final String CONTRACT = "contract";
    private static final String CONTRACTOR = "contractor";
    private static final String INN = "1234234123";
    private static final String ACCOUNT = "9999999999";

    @Before
    public void cleanup() {
        partyRepository.deleteAll();
    }

    @Test
    public void searchByPartyEmail() throws TException {
        givenParty(PARTY, EMAIL);

        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(EMAIL);

        Assert.assertFalse(searchHits.isEmpty());
        Assert.assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getEmail().contains(EMAIL)));
    }

    @Test
    public void searchByShopUrl() throws TException {
        Party party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL);

        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(URL);

        Assert.assertFalse(searchHits.isEmpty());
        Assert.assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getShops().values().stream()
                        .anyMatch(shop -> shop.getLocation().getUrl().contains(URL))));
    }

    @Test
    public void searchByShopId() throws TException {
        Party party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL);

        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(SHOP);

        Assert.assertFalse(searchHits.isEmpty());
        Assert.assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getShops().values().stream()
                        .anyMatch(shop -> shop.getId().equals(SHOP))));
    }

    @Test
    public void searchByContractorEmail() throws TException {
        Party party = givenParty(PARTY, null);
        givenRegisteredUserContractor(party, CONTRACTOR, EMAIL);


        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(EMAIL);

        Assert.assertFalse(searchHits.isEmpty());
        Assert.assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(CONTRACTOR).getContractor().getRegisteredUser().getEmail().contains(EMAIL)));
    }

    @Test
    public void searchByContractorRussianLegalEntityRegisteredNameWithOneWord() throws TException {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);


        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("ЧИ");

        Assert.assertFalse(searchHits.isEmpty());
        Assert.assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(CONTRACTOR).getContractor().getLegalEntity().getRussianLegalEntity().getRegisteredName().contains("ЧИ")));
    }

    @Test
    public void searchByContractorRussianLegalEntityRegisteredNameWithOneMatchingAndOneNotMatchingWord() throws TException {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);


        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("ЧИ ДА");

        Assert.assertFalse(searchHits.isEmpty());
        Assert.assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(CONTRACTOR).getContractor().getLegalEntity().getRussianLegalEntity().getRegisteredName().contains("ЧИ")));
    }

    @Test
    public void searchByContractorRussianLegalEntityRegisteredNameNoMatchingWords() throws TException {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);


        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("ДА");

        Assert.assertTrue(searchHits.isEmpty());
    }

    @Test
    public void searchByContractorInnFullyEqual() throws TException {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);


        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(INN);

        Assert.assertFalse(searchHits.isEmpty());
        Assert.assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(CONTRACTOR).getContractor().getLegalEntity().getRussianLegalEntity().getInn().equals(INN)));
    }


    @Test
    public void searchByContractorInnNotFullyEqual() throws TException {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);


        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(INN.substring(0, 6));

        Assert.assertTrue(searchHits.isEmpty());
    }

    @Test
    public void searchByContractorInternationalLegalEntityLegalName() throws TException {
        Party party = givenParty(PARTY, null);
        givenInternationalContractor(party, CONTRACTOR, "SoMe LeGaL NaMe","ANOTHER TRADING NAME");


        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("legal");

        Assert.assertFalse(searchHits.isEmpty());
        Assert.assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(CONTRACTOR).getContractor().getLegalEntity().getInternationalLegalEntity().getLegalName().equals("SoMe LeGaL NaMe")));
    }

    @Test
    public void searchByContractLegalAgreementId() throws TException {
        Party party = givenParty(PARTY, null);
        givenContract(party, CONTRACT, 123, "ДГ-123432", "Василий Пупкин");


        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("ДГ");

        Assert.assertFalse(searchHits.isEmpty());
        Assert.assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContracts().get(CONTRACT).getLegalAgreement().getId().equals("ДГ-123432")));
    }

    @Test
    public void searchForSeveralParties() throws TException {
        for (int i = 0; i < 10; i++) {
            Party party = givenParty(i + "", i + EMAIL.substring(EMAIL.indexOf("@")));
            givenShop(party, 9 - i + "", URL + i);
        }

        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("1");

        Assert.assertEquals(2, searchHits.size());
        Assert.assertEquals("1", searchHits.get(0).getParty().getId());
        Assert.assertNotNull(searchHits.get(1).getParty().getShops().get("1"));
    }

    private void givenRegisteredUserContractor(Party party,
                                        String id,
                                        String registeredUserEmail) {
        party.addContractor(TestData.contractor(id,
                registeredUserEmail,
                null,
                null,
                null,
                null,
                null));
        partyRepository.save(party);
    }

    private void givenRussianContractor(Party party,
                                        String id,
                                        String russianLegalEntityRegisteredName,
                                        String russianLegalEntityRegisteredInn,
                                        String russianLegalEntityRussianBankAccount) {
        party.addContractor(TestData.contractor(id,
                null,
                russianLegalEntityRegisteredName,
                russianLegalEntityRegisteredInn,
                russianLegalEntityRussianBankAccount,
                null,
                null));
        partyRepository.save(party);
    }

    private void givenShop(Party party, String id, String url) {
        party.addShop(TestData.shop(id, url));
        partyRepository.save(party);
    }

    private Party givenParty(String id, String email) {
        return partyRepository.save(TestData.party(id, email));
    }

    private void givenInternationalContractor(Party party,
                                              String id,
                                              String internationalLegalEntityLegalName,
                                              String internationalLegalEntityTradingName) {
        party.addContractor(TestData.contractor(id,
                null,
                null,
                null,
                null,
                internationalLegalEntityLegalName,
                internationalLegalEntityTradingName));
        partyRepository.save(party);
    }

    private void givenContract(Party party,
                               String id,
                               Integer termsId,
                               String legalAgreementId,
                               String reportActSignerFullName) {
        party.addContract(TestData.contract(id,
                termsId,
                legalAgreementId,
                reportActSignerFullName));
        partyRepository.save(party);
    }

}
