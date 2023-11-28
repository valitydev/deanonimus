package dev.vality.deanonimus;

import dev.vality.damsel.deanonimus.SearchHit;
import dev.vality.damsel.deanonimus.SearchShopHit;
import dev.vality.damsel.deanonimus.SearchWalletHit;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.handler.DeanonimusServiceHandler;
import dev.vality.deanonimus.service.OpenSearchService;
import lombok.SneakyThrows;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReadTest extends AbstractIntegrationTest {

    @Value("${data.response.limit}")
    Integer responseLimit;

    @Autowired
    OpenSearchService openSearchService;

    @Autowired
    OpenSearchClient client;

    @Autowired
    DeanonimusServiceHandler deanonimusServiceHandler;

    private static final String PARTY = "party";
    private static final String SHOP = "shop";
    private static final String EMAIL = "email@mail.com";
    private static final String URL = "http://url.com";
    private static final String CONTRACT = "contract";
    private static final String CONTRACTOR = "contractor";
    private static final String WALLET = "wallet";
    private static final String WALLET_NAME = "wallet_name";
    private static final String INN = "1234234123";
    private static final String ACCOUNT = "9999999999";


    @Test
    void searchByPartyId() throws TException {
        givenParty(PARTY, EMAIL);
        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(PARTY);

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getEmail().contains(EMAIL)));
    }

    @Test
    void searchByPartyIdWithoutTokens() throws TException {
        givenParty(PARTY + "-test-kek", EMAIL + "1");
        givenParty(PARTY + "-test-lol", EMAIL + "2");
        givenParty(PARTY + "-test-rofl", EMAIL + "3");
        givenParty(PARTY + "-test-ricardo", EMAIL + "4");
        givenParty(PARTY + "-test-milos", EMAIL + "5");
        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(PARTY + "-test-lol");

        assertEquals(1, searchHits.size());
    }

    @Test
    void searchByPartyEmail() throws TException {
        givenParty(PARTY, EMAIL);
        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(EMAIL);

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getEmail().contains(EMAIL)));
    }

    @Test
    void searchByShopUrl() throws TException {
        Party party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL);
        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(URL);

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getShops().values().stream()
                        .anyMatch(shop -> shop.getLocation().getUrl().contains(URL))));
    }

    @Test
    void searchByShopId() throws TException {
        Party party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP + "kek", URL + "testkek");
        givenShop(party, SHOP + "lol", URL + "testlol");
        refreshIndices();
        List<SearchShopHit> searchHits = deanonimusServiceHandler.searchShopText(SHOP + "kek");
        assertFalse(searchHits.isEmpty());
        assertEquals(1, searchHits.size());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getShops().values().stream()
                        .anyMatch(shop -> shop.getId().equals(SHOP + "kek"))));
        searchHits = deanonimusServiceHandler.searchShopText(URL + "test");
        assertEquals(2, searchHits.size());
    }

    @Test
    void searchShopByShopId() throws TException {
        Party party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL);
        refreshIndices();
        List<SearchShopHit> searchShopHits = deanonimusServiceHandler.searchShopText(SHOP);

        assertFalse(searchShopHits.isEmpty());
        assertTrue(searchShopHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getShop().getId().equals(SHOP)));
    }

    @Test
    void searchShopByShopUrl() throws TException {
        Party party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL);
        refreshIndices();
        List<SearchShopHit> searchHits = deanonimusServiceHandler.searchShopText(URL);

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getShop().getLocation().getUrl().contains(URL)));
    }

    @Test
    void searchWalletByName() throws TException {
        Party party = givenParty(PARTY, EMAIL);
        givenWallet(party, WALLET, WALLET_NAME);
        refreshIndices();
        List<SearchWalletHit> searchHits = deanonimusServiceHandler.searchWalletText(WALLET_NAME);

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getWallet().getName().contains(WALLET_NAME)));
    }

    @Test
    void searchWalletById() throws TException {
        Party party = givenParty(PARTY, EMAIL);
        givenWallet(party, WALLET, WALLET_NAME);
        refreshIndices();
        List<SearchWalletHit> searchHits = deanonimusServiceHandler.searchWalletText(WALLET);

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getWallet().getId().contains(WALLET)));
    }

    @Test
    void searchByContractorEmail() throws TException {
        Party party = givenParty(PARTY, null);
        givenRegisteredUserContractor(party, CONTRACTOR, EMAIL);

        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(EMAIL);

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(CONTRACTOR).getContractor()
                        .getRegisteredUser().getEmail().contains(EMAIL)));
    }

    @Test
    void searchByContractorRussianLegalEntityRegisteredNameWithOneWord() throws TException {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);

        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("ЧИ");

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(CONTRACTOR).getContractor()
                        .getLegalEntity().getRussianLegalEntity().getRegisteredName().contains("ЧИ")));
    }

    @Test
    void searchByContractorRussianLegalEntityRegisteredNameWithOneMatchingAndOneNotMatchingWord()
            throws TException {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);

        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("ЧИ ИЛИ");

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(CONTRACTOR).getContractor()
                        .getLegalEntity().getRussianLegalEntity().getRegisteredName().contains("ЧИ")));
    }

    @Test
    void searchByContractorRussianLegalEntityRegisteredNameNoMatchingWords() throws TException {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);

        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("ДА");

        assertTrue(searchHits.isEmpty());
    }

    @Test
    void searchByContractorInnFullyEqual() throws TException {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);

        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(INN);

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(CONTRACTOR).getContractor()
                        .getLegalEntity().getRussianLegalEntity().getInn().equals(INN)));
    }


    @Test
    void searchByContractorInnNotFullyEqual() throws TException {
        Party party = givenParty(PARTY, null);
        givenRussianContractor(party, CONTRACTOR, "ООО \"ЧИ ИЛИ НЕ ЧИ\"", INN, ACCOUNT);

        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty(INN.substring(0, 6));

        assertTrue(searchHits.isEmpty());
    }

    @Test
    void searchByContractorInternationalLegalEntityLegalName() throws TException {
        Party party = givenParty(PARTY, null);
        givenInternationalContractor(party, CONTRACTOR, "SoMe LeGaL NaMe", "ANOTHER TRADING NAME");

        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("legal");

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(CONTRACTOR).getContractor()
                        .getLegalEntity().getInternationalLegalEntity().getLegalName().equals("SoMe LeGaL NaMe")));
    }

    @Test
    void searchByContractLegalAgreementId() throws TException {
        Party party = givenParty(PARTY, null);
        givenContract(party, CONTRACT, 123, "ДГ-123432", "Василий Пупкин");

        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("ДГ");

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContracts().get(CONTRACT).getLegalAgreement()
                        .getId().equals("ДГ-123432")));
    }

    @Test
    void searchForSeveralParties() throws TException {
        for (int i = 0; i < 10; i++) {
            Party party = givenParty(i + "", i + EMAIL.substring(EMAIL.indexOf("@")));
            givenShop(party, 9 - i + "", URL + i);
        }
        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("1");

        assertEquals(2, searchHits.size());
        assertEquals("1", searchHits.get(0).getParty().getId());
        assertNotNull(searchHits.get(1).getParty().getShops().get("1"));
    }

    @Test
    void responseLimitApplied() throws TException {
        for (int i = 0; i < 30; i++) {
            Party party = givenParty(i + "", EMAIL);
            givenShop(party, 29 - i + "", URL + i);
        }
        refreshIndices();
        List<SearchHit> searchHits = deanonimusServiceHandler.searchParty("email");

        assertEquals((long) responseLimit, searchHits.size());
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
        openSearchService.updateParty(party);
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
        openSearchService.updateParty(party);
    }

    private void givenShop(Party party, String id, String url) {
        party.addShop(TestData.shop(id, url));
        openSearchService.updateParty(party);
    }

    private void givenWallet(Party party, String id, String name) {
        party.addWallet(TestData.wallet(id, name));
        openSearchService.updateParty(party);
    }

    private Party givenParty(String id, String email) {
        return openSearchService.createParty(TestData.party(id, email));
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
        openSearchService.updateParty(party);
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
        openSearchService.updateParty(party);
    }

    @SneakyThrows
    private void refreshIndices() {
        client.indices().refresh();
    }
}
