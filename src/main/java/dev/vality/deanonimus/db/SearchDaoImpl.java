package dev.vality.deanonimus.db;

import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchDaoImpl implements SearchDao {

    @Value("${data.response.limit}")
    private Integer responseLimit;

    private final OpenSearchClient openSearchClient;

    @Override
    public SearchResponse<Party> searchParty(String text) {
        var queryBuilder = new BoolQuery.Builder()
                .should(searchBestFields(text, keywords()),
                        searchPhrasePrefix(text, fields()))
                .build();
        return search(queryBuilder);
    }

    @Override
    public SearchResponse<Party> searchShop(String text) {
        var queryBuilder = new BoolQuery.Builder()
                .should(searchBestFields(text, List.of("shops.id.keyword")),
                        searchPhrasePrefix(text, List.of("shops.locationUrl", "shops.detailsName")))
                .build();
        return search(queryBuilder);
    }

    @Override
    public SearchResponse<Party> searchWallet(String text) {
        var queryBuilder = new BoolQuery.Builder()
                .should(searchBestFields(text, List.of("wallets.id.keyword")),
                        searchPhrasePrefix(text, List.of("wallets.name")))
                .build();
        return search(queryBuilder);
    }

    @SneakyThrows
    private SearchResponse<Party> search(BoolQuery queryBuilder) {
        return openSearchClient.search(
                s -> s
                        .size(responseLimit)
                        .query(new Query.Builder()
                                .bool(queryBuilder)
                                .build()),
                Party.class);
    }

    private List<String> keywords() {
        return List.of(
                "id.keyword",
                "contractors.id.keyword",
                "contractors.russianLegalEntityInn.keyword",
                "contractors.russianLegalEntityRussianBankAccount.keyword",
                "contracts.id.keyword",
                "shops.id.keyword",
                "wallets.id.keyword");
    }

    private List<String> fields() {
        return List.of(
                "email",
                "contractors.registeredUserEmail",
                "contractors.russianLegalEntityRegisteredName",
                "contractors.internationalLegalEntityLegalName",
                "contractors.internationalLegalEntityTradingName",
                "contracts.legalAgreementId",
                "contracts.reportActSignerFullName",
                "shops.locationUrl",
                "shops.detailsName",
                "wallets.name");
    }

    private Query searchBestFields(String text, List<String> fields) {
        return new Query(new MultiMatchQuery.Builder()
                .fields(fields)
                .query(text)
                .type(TextQueryType.BestFields)
                .operator(Operator.Or)
                .build());
    }

    private Query searchPhrasePrefix(String text, List<String> fields) {
        return new Query(new MultiMatchQuery.Builder()
                .fields(fields)
                .query(text)
                .type(TextQueryType.PhrasePrefix)
                .operator(Operator.Or)
                .build());
    }
}
