package dev.vality.deanonimus.db;

import dev.vality.deanonimus.domain.wallet.Wallet;
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
public class WalletDao {

    @Value("${data.response.limit}")
    private Integer responseLimit;

    private final OpenSearchClient openSearchClient;

    public SearchResponse<Wallet> searchWallet(String text) {
        var queryBuilder = new BoolQuery.Builder()
                .should(searchBestFields(text, keywords()),
                        searchPhrasePrefix(text, fields()))
                .build();
        return search(queryBuilder);
    }

    @SneakyThrows
    private SearchResponse<Wallet> search(BoolQuery queryBuilder) {
        return openSearchClient.search(
                s -> s
                        .size(responseLimit)
                        .query(new Query.Builder()
                                .bool(queryBuilder)
                                .build()),
                Wallet.class);
    }

    private List<String> keywords() {
        return List.of(
                "id.keyword",
                "externalId.keyword",
                "identityId.keyword",
                "partyId.keyword");
    }

    private List<String> fields() {
        return List.of("name", "partyId");
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
