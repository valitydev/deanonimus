package com.rbkmoney.deanonimus;

import com.rbkmoney.deanonimus.db.PartyRepository;
import com.rbkmoney.deanonimus.db.SearchDao;
import com.rbkmoney.deanonimus.domain.Party;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;

public class ReadTest extends IntegrationTestBase {

    @Autowired
    PartyRepository partyRepository;

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    SearchDao searchDao;

    public static final String PARTY = "party";
    public static final String SHOP = "shop";
    public static final String EMAIL = "email@mail.com";
    public static final String URL = "http://url.com";
    public static final String CONTRACT = "contract";
    public static final String CONTRACTOR = "contractor";
    public static final String INN = "1234234123";
    public static final String ACCOUNT = "9999999999";

    @Before
    public void cleanup() {
        partyRepository.deleteAll();
    }

    @Test
    public void searchByPartyEmail() {
        givenParty(PARTY, EMAIL);

        SearchHits<Party> searchHits = searchDao.searchParty(EMAIL);

        Assert.assertTrue(searchHits.hasSearchHits());
        Assert.assertTrue(searchHits.get()
                .anyMatch(partySearchHit -> partySearchHit.getContent().getEmail().contains(EMAIL)));
    }

    @Test
    public void searchByShopUrl() {
        Party party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL);

        SearchHits<Party> searchHits = searchDao.searchParty(URL);

        Assert.assertTrue(searchHits.hasSearchHits());
        Assert.assertTrue(searchHits.get()
                .anyMatch(partySearchHit -> partySearchHit.getContent().getShops().stream()
                        .anyMatch(shop -> shop.getLocationUrl().contains(URL))));
    }

    @Test
    public void searchByShopId() {
        Party party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL);

        SearchHits<Party> searchHits = searchDao.searchParty(SHOP);

        Assert.assertTrue(searchHits.hasSearchHits());
        Assert.assertTrue(searchHits.get()
                .anyMatch(partySearchHit -> partySearchHit.getContent().getShops().stream()
                        .anyMatch(shop -> shop.getId().equals(SHOP))));
    }

    @Test
    public void searchByContractorEmail() {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, EMAIL, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);


        SearchHits<Party> searchHits = searchDao.searchParty(EMAIL);

        Assert.assertTrue(searchHits.hasSearchHits());
        Assert.assertTrue(searchHits.get()
                .anyMatch(partySearchHit -> partySearchHit.getContent().getContractorById(CONTRACTOR).orElseThrow().getRegisteredUserEmail().contains(EMAIL)));
    }

    @Test
    public void searchByContractorRussianLegalEntityRegisteredNameWithOneWord() {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, EMAIL, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);


        SearchHits<Party> searchHits = searchDao.searchParty("ЧИ");

        Assert.assertTrue(searchHits.hasSearchHits());
        Assert.assertTrue(searchHits.get()
                .anyMatch(partySearchHit -> partySearchHit.getContent().getContractorById(CONTRACTOR).orElseThrow().getRussianLegalEntityRegisteredName().contains("ЧИ")));
    }

    @Test
    public void searchByContractorRussianLegalEntityRegisteredNameWithOneMatchingAndOneNotMatchingWord() {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, EMAIL, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);


        SearchHits<Party> searchHits = searchDao.searchParty("ЧИ ДА");

        Assert.assertTrue(searchHits.hasSearchHits());
        Assert.assertTrue(searchHits.get()
                .anyMatch(partySearchHit -> partySearchHit.getContent().getContractorById(CONTRACTOR).orElseThrow().getRussianLegalEntityRegisteredName().contains("ЧИ")));
    }

    @Test
    public void searchByContractorRussianLegalEntityRegisteredNameNoMatchingWords() {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, EMAIL, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);


        SearchHits<Party> searchHits = searchDao.searchParty("ДА");

        Assert.assertFalse(searchHits.hasSearchHits());
    }

    @Test
    public void searchByContractorInnFullyEqual() {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, EMAIL, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);


        SearchHits<Party> searchHits = searchDao.searchParty(INN);

        Assert.assertTrue(searchHits.hasSearchHits());
        Assert.assertTrue(searchHits.get()
                .anyMatch(partySearchHit -> partySearchHit.getContent().getContractorById(CONTRACTOR).orElseThrow().getRussianLegalEntityInn().equals(INN)));
    }


    @Test
    public void searchByContractorInnNotFullyEqual() {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, EMAIL, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);


        SearchHits<Party> searchHits = searchDao.searchParty(INN.substring(0, 6));

        Assert.assertFalse(searchHits.hasSearchHits());
    }

    @Test
    public void searchByContractorInternationalLegalEntityLegalName() {
        Party party = givenParty(PARTY, null);
        givenInternationalContractor(party, CONTRACTOR, EMAIL, "SoMe LeGaL NaMe","ANOTHER TRADING NAME");


        SearchHits<Party> searchHits = searchDao.searchParty("legal");

        Assert.assertTrue(searchHits.hasSearchHits());
        Assert.assertTrue(searchHits.get()
                .anyMatch(partySearchHit -> partySearchHit.getContent().getContractorById(CONTRACTOR).orElseThrow().getInternationalLegalEntityLegalName().equals("SoMe LeGaL NaMe")));
    }

    @Test
    public void searchByContractLegalAgreementId() {
        Party party = givenParty(PARTY, null);
        givenContract(party, CONTRACT, 123, "ДГ-123432", "Василий Пупкин");


        SearchHits<Party> searchHits = searchDao.searchParty("ДГ");

        Assert.assertTrue(searchHits.hasSearchHits());
        Assert.assertTrue(searchHits.get()
                .anyMatch(partySearchHit -> partySearchHit.getContent().getContractById(CONTRACT).orElseThrow().getLegalAgreementId().equals("ДГ-123432")));
    }

    @Test
    public void searchForSeveralParties() {
        for (int i = 0; i < 10; i++) {
            Party party = givenParty(i + "", i + EMAIL.substring(EMAIL.indexOf("@")));
            givenShop(party, 9 - i + "", URL + i);
        }

        SearchHits<Party> searchHits = searchDao.searchParty("1");

        Assert.assertEquals(2, searchHits.getTotalHits());
        Assert.assertEquals("1", searchHits.getSearchHit(0).getContent().getId());
        Assert.assertTrue(searchHits.getSearchHit(1).getContent().getShopById("1").isPresent());
    }

    private void givenRussianContractor(Party party,
                                        String id,
                                        String registeredUserEmail,
                                        String russianLegalEntityRegisteredName,
                                        String russianLegalEntityRegisteredInn,
                                        String russianLegalEntityRussianBankAccount) {
        party.addContractor(TestData.contractor(id,
                registeredUserEmail,
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
                                              String registeredUserEmail,
                                              String internationalLegalEntityLegalName,
                                              String internationalLegalEntityTradingName) {
        party.addContractor(TestData.contractor(id,
                registeredUserEmail,
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
