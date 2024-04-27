package dev.vality.deanonimus;

import dev.vality.damsel.deanonimus.SearchHit;
import dev.vality.damsel.deanonimus.SearchShopHit;
import dev.vality.damsel.deanonimus.SearchWalletHit;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.domain.wallet.Wallet;
import dev.vality.deanonimus.extension.OpensearchContainerExtension;
import dev.vality.deanonimus.handler.DeanonimusServiceHandler;
import dev.vality.deanonimus.service.OpenSearchService;
import lombok.SneakyThrows;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static dev.vality.deanonimus.constant.OpenSearchConstants.PARTY_INDEX;
import static org.junit.jupiter.api.Assertions.*;

public class ReadTest extends AbstractIntegrationTest {

    @Value("${data.response.limit}")
    Integer responseLimit;

    @Value("classpath:index_request.json")
    Resource indexModel;

    @Autowired
    OpenSearchService openSearchService;

    @Autowired
    OpenSearchClient client;

    @Autowired
    DeanonimusServiceHandler deanonimusServiceHandler;

    @Autowired
    TestRestTemplate restTemplate;


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

    @BeforeEach
    void setUp() throws IOException {
        var indices = client.indices();
        if (indices.exists(new ExistsRequest.Builder().index(PARTY_INDEX).build()).value()) {
            indices.delete(new DeleteIndexRequest.Builder().index(PARTY_INDEX).build());
        }
        createIndex();
    }

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
    void searchByPartyIdAdds() throws TException {
        var id = UUID.randomUUID().toString();
        var mail = "asd zxc fgh";
        givenParty(id, mail);
        refreshIndices();
        var searchHits = deanonimusServiceHandler.searchParty(mail);
        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getEmail().contains(mail)));
        searchHits = deanonimusServiceHandler.searchParty(id);
        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getEmail().contains(mail)));
        // match partial field
        searchHits = deanonimusServiceHandler.searchParty("asd z");
        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getEmail().contains(mail)));
        // for keyword match only full field
        searchHits = deanonimusServiceHandler.searchParty(id.substring(0, 8));
        assertTrue(searchHits.isEmpty());
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
    void searchPartyByShopUrl() throws TException {
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
    void searchPartyByShopName() throws TException {
        var party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL, "S2P_BRL");
        refreshIndices();
        var searchHits = deanonimusServiceHandler.searchParty("s2p");

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getShops().values().stream()
                        .anyMatch(shop -> shop.getDetails().getName().contains("S2P"))));
    }

    @Test
    void searchShopTextByShopName() throws TException {
        var party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL, "S2P_BRL");
        refreshIndices();
        var searchHits = deanonimusServiceHandler.searchShopText("s2p");

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getShops().values().stream()
                        .anyMatch(shop -> shop.getDetails().getName().contains("S2P"))));
    }

    @Test
    void searchShopTextByShopNames() {
        var party1 = givenParty(PARTY + "id-1", EMAIL);
        givenShop(party1, SHOP + "id-1", URL, "S2P_BRL");

        var party2 = givenParty(PARTY + "id-2", EMAIL);
        givenShop(party2, SHOP + "id-2", URL, "S2P_BRL");

        refreshIndices();
        var searchHits = deanonimusServiceHandler.searchShopText("s2p");

        assertFalse(searchHits.isEmpty());
        assertEquals(2, searchHits.size());
        var expected = Set.of(SHOP + "id-1", SHOP + "id-2");
        assertTrue(searchHits.stream().allMatch(shopHit -> expected.contains(shopHit.getShop().getId())));
    }

    @Test
    void searchShopTextByPartyId() {
        var party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL, "S2P_BRL");
        refreshIndices();
        var searchHits = deanonimusServiceHandler.searchShopText(PARTY);

        assertFalse(searchHits.isEmpty());
        assertEquals(1, searchHits.size());
        assertTrue(searchHits.stream().anyMatch(shopHit -> shopHit.getParty().getId().equals(PARTY)));
    }

    @Test
    void searchShopTextContainsPartyId() {
        var party = givenParty("party-test-1", EMAIL);
        givenShop(party, SHOP, URL, "S2P_BRL");
        refreshIndices();
        var searchHits = deanonimusServiceHandler.searchShopText("test");

        assertFalse(searchHits.isEmpty());
        assertEquals(1, searchHits.size());
        assertTrue(searchHits.stream().anyMatch(shopHit -> shopHit.getParty().getId().equals("party-test-1")));
    }

    @Test
    void searchShopTextByContractorId() {
        var party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL, "S2P_BRL");
        givenRegisteredUserContractor(party, CONTRACTOR, EMAIL);

        refreshIndices();
        var searchHits = deanonimusServiceHandler.searchShopText(CONTRACTOR);

        assertFalse(searchHits.isEmpty());
        assertEquals(1, searchHits.size());
        assertTrue(searchHits.stream().anyMatch(shopHit -> shopHit.getShop().getId().equals(SHOP)));
    }

    @Test
    void searchShopTextByContractId() {
        var party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL, "S2P_BRL");
        givenContract(party, CONTRACT, 123, "ДГ-123432", "Василий Пупкин");
        refreshIndices();
        var searchHits = deanonimusServiceHandler.searchShopText(PARTY);

        assertFalse(searchHits.isEmpty());
        assertEquals(1, searchHits.size());
        assertTrue(searchHits.stream().anyMatch(shopHit -> shopHit.getShop().getId().equals(SHOP)));
    }

    @Test
    void searchShopTextByEmail() {
        var party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL, "S2P_BRL");
        refreshIndices();
        var searchHits = deanonimusServiceHandler.searchShopText(EMAIL);

        assertFalse(searchHits.isEmpty());
        assertEquals(1, searchHits.size());
        assertTrue(searchHits.stream().anyMatch(shopHit -> shopHit.getShop().getId().equals(SHOP)));
    }

    @Test
    void searchShopTextByPartyIdAndShopId() {

        var party1 = givenParty("party-test-1", EMAIL);
        givenShop(party1, "shop-id-1", URL, "S2P_BRL-1");
        givenShop(party1, "shop-id-2", URL, "S2P_BRL-2");

        var party2 = givenParty("party-id-2", EMAIL);
        givenShop(party2, "shop-test-1", URL, "details-1");
        givenShop(party2, "shop-none-2", URL, "details-2");


        refreshIndices();
        var searchHits = deanonimusServiceHandler.searchShopText("test");

        assertFalse(searchHits.isEmpty());
        assertEquals(3, searchHits.size());

        var expected = Set.of("shop-id-1", "shop-id-2", "shop-test-1");
        assertTrue(searchHits.stream().allMatch(shopHit -> expected.contains(shopHit.getShop().getId())));
    }

    @Test
    void searchShopTextByDiffShop() throws TException {
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
        searchHits = deanonimusServiceHandler.searchShopText(URL + "testlol");
        assertEquals(1, searchHits.size());
    }

    @Test
    void searchShopTextByShopId() throws TException {
        Party party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL);
        refreshIndices();
        List<SearchShopHit> searchShopHits = deanonimusServiceHandler.searchShopText(SHOP);

        assertFalse(searchShopHits.isEmpty());
        assertTrue(searchShopHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getShop().getId().equals(SHOP)));
    }

    @Test
    void searchShopTextContainsShopIds() {
        Party party1 = givenParty(PARTY + "id-1", EMAIL);
        givenShop(party1, "test-id-1", URL);

        Party party2 = givenParty(PARTY + "id-2", EMAIL);
        givenShop(party2, "test-id-2", URL);

        refreshIndices();

        List<SearchShopHit> searchShopHits = deanonimusServiceHandler.searchShopText("test");

        assertFalse(searchShopHits.isEmpty());
        assertEquals(2, searchShopHits.size());

        var expected = Set.of("test-id-1", "test-id-2");
        assertTrue(searchShopHits.stream().allMatch(shopHit -> expected.contains(shopHit.getShop().getId())));
    }

    @Test
    void searchShopTextByShopUrl() throws TException {
        Party party = givenParty(PARTY, EMAIL);
        givenShop(party, SHOP, URL);
        refreshIndices();
        List<SearchShopHit> searchHits = deanonimusServiceHandler.searchShopText(URL);

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getShop().getLocation().getUrl().contains(URL)));
    }

    @Test
    void searchWalletTextByName() throws TException {
        givenParty(PARTY, EMAIL);
        givenWallet(WALLET, PARTY, "S2P");
        refreshIndices();
        List<SearchWalletHit> searchHits = deanonimusServiceHandler.searchWalletText("s2p");

        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getWallet().getName().contains("S2P")));
    }

    @Test
    void searchWalletTextById() throws TException {
        givenParty(PARTY, EMAIL);
        givenWallet(WALLET, PARTY, WALLET_NAME);
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
    void searchByContractorEmailAdds() throws TException {
        var id = UUID.randomUUID().toString();
        var mail = "asd zxc fgh";
        var party = givenParty(PARTY, null);
        givenRegisteredUserContractor(party, id, mail);
        refreshIndices();
        var searchHits = deanonimusServiceHandler.searchParty(mail);
        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(id).getContractor()
                        .getRegisteredUser().getEmail().contains(mail)));
        searchHits = deanonimusServiceHandler.searchParty(id);
        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(id).getContractor()
                        .getRegisteredUser().getEmail().contains(mail)));
        // match partial field
        searchHits = deanonimusServiceHandler.searchParty("asd z");
        assertFalse(searchHits.isEmpty());
        assertTrue(searchHits.stream()
                .anyMatch(partySearchHit -> partySearchHit.getParty().getContractors().get(id).getContractor()
                        .getRegisteredUser().getEmail().contains(mail)));
        // for keyword match only full field
        searchHits = deanonimusServiceHandler.searchParty(id.substring(0, 8));
        assertTrue(searchHits.isEmpty());
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
        party.addShop(TestData.shop(id, url, "name"));
        openSearchService.updateParty(party);
    }

    private void givenShop(Party party, String id, String url, String detailsName) {
        party.addShop(TestData.shop(id, url, detailsName));
        openSearchService.updateParty(party);
    }

    private Wallet givenWallet(String id, String partyId, String name) {
        return openSearchService.createWallet(TestData.wallet(id, partyId, name));
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

    private void createIndex() {
        var url = "http://" +
                OpensearchContainerExtension.OPENSEARCH.getHost() + ":" +
                OpensearchContainerExtension.OPENSEARCH.getFirstMappedPort() + "/" +
                PARTY_INDEX;
        restTemplate.put(url, indexModel);
    }
}
