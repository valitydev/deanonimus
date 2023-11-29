package dev.vality.deanonimus.db;

import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.domain.Shop;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static dev.vality.deanonimus.constant.OpenSearchConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchDaoImpl implements SearchDao {

    @Value("${data.response.limit}")
    Integer responseLimit;

    private final OpenSearchClient openSearchClient;


    @SneakyThrows
    @Override
    public SearchResponse<Party> searchParty(String text) {

        BoolQuery queryBuilder = new BoolQuery.Builder()
                .should(searchPartyFields(text),
                        searchShopFields(text),
                        searchContractFields(text),
                        searchContractorFields(text),
                        searchWalletFields(text))
                .build();

        return openSearchClient.search(s -> s
                        .size(responseLimit)
                        .query(new Query.Builder()
                                .bool(queryBuilder)
                                .build()),
                Party.class);
    }

    private Query searchContractorFields(String text) {
        return new NestedQuery.Builder()
                .path(CONTRACTOR_INDEX)
                .query(new Query(new MultiMatchQuery.Builder()
                        .fields("contractors.id",
                                "contractors.registeredUserEmail",
                                "contractors.russianLegalEntityRegisteredName",
                                "contractors.russianLegalEntityInn",
                                "contractors.russianLegalEntityRussianBankAccount",
                                "contractors.internationalLegalEntityLegalName",
                                "contractors.internationalLegalEntityTradingName")
                        .query(text)
                        .type(TextQueryType.Phrase)
                        .build()))
                .scoreMode(ChildScoreMode.Sum)
                .build().query();
    }

    private Query searchContractFields(String text) {
        return new NestedQuery.Builder()
                .path(CONTRACT_INDEX)
                .query(new Query(new MultiMatchQuery.Builder()
                        .fields("contracts.id",
                                "contracts.legalAgreementId",
                                "contracts.reportActSignerFullName")
                        .query(text)
                        .type(TextQueryType.Phrase)
                        .build()))
                .scoreMode(ChildScoreMode.Sum)
                .build().query();
    }

    private Query searchPartyFields(String text) {
        return new Query(new MultiMatchQuery.Builder()
                .fields("id",
                        "email")
                .query(text)
                .type(TextQueryType.Phrase)
                .build());
    }

    private Query searchShopFields(String text) {
        return new NestedQuery.Builder()
                .path(SHOP_INDEX)
                .query(new Query(new MultiMatchQuery.Builder()
                        .fields("shops.id",
                                "shops.locationUrl",
                                "shops.detailsName")
                        .query(text)
                        .type(TextQueryType.Phrase)
                        .build()))
                .scoreMode(ChildScoreMode.Sum)
                .build().query();
    }

    private Query searchWalletFields(String text) {
        return new NestedQuery.Builder()
                .path(WALLET_INDEX)
                .query(new Query(new MultiMatchQuery.Builder()
                        .fields("wallets.id",
                                "wallets.name")
                        .query(text)
                        .type(TextQueryType.Phrase)
                        .build()))
                .scoreMode(ChildScoreMode.Sum)
                .build().query();
    }

}
