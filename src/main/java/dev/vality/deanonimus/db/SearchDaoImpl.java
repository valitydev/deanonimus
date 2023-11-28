package dev.vality.deanonimus.db;

import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Operator;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.TextQueryType;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.util.ObjectBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static dev.vality.deanonimus.constant.OpenSearchConstants.PARTY_INDEX;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("LineLength")
public class SearchDaoImpl implements SearchDao {

    private static final TextQueryType TEXT_QUERY_TYPE = TextQueryType.Phrase;
    private static final Operator OPERATOR = Operator.Or;
    private static final int DEFAULT_SLOP = 0;
    private static final int DEFAULT_PREFIX_LENGTH = 0;
    private static final int DEFAULT_MAX_EXPANSIONS = 50;
    private static final List<String> PARTY_FIELDS = List.of("id", "email");
    private static final List<String> SHOPS_FIELDS = List.of("shops.id", "shops.locationUrl", "shops.detailsName");
    private static final List<String> CONTRACTS_FIELDS = List.of("contracts.id", "contracts.legalAgreementId", "contracts.reportActSignerFullName");
    private static final List<String> CONTRACTORS_FIELDS = List.of("contractors.id", "contractors.registeredUserEmail", "contractors.russianLegalEntityRegisteredName", "contractors.russianLegalEntityInn", "contractors.russianLegalEntityRussianBankAccount", "contractors.internationalLegalEntityLegalName", "contractors.internationalLegalEntityTradingName");
    private static final List<String> WALLETS_FIELDS = List.of("wallets.id", "wallets.name");

    private final OpenSearchClient openSearchClient;

    @Value("${data.response.limit}")
    private Integer responseLimit;

    @SneakyThrows
    @Override
    public SearchResponse<Party> searchParty(String text) {
        BoolQuery queryBuilder = new BoolQuery.Builder()
                .should(getMultiMatchQuery(PARTY_FIELDS, text))
                .should(getMultiMatchQuery(SHOPS_FIELDS, text))
                .should(getMultiMatchQuery(CONTRACTS_FIELDS, text))
                .should(getMultiMatchQuery(CONTRACTORS_FIELDS, text))
                .should(getMultiMatchQuery(WALLETS_FIELDS, text))
                .build();

        return openSearchClient.search(s -> s
                        .size(responseLimit)
                        .index(PARTY_INDEX)
                        .query(new Query.Builder()
                                .bool(queryBuilder)
                                .build()),
                Party.class);
    }

    private Function<Query.Builder, ObjectBuilder<Query>> getMultiMatchQuery(List<String> fields, String text) {
        return builder -> builder
                .multiMatch(builder1 -> builder1
                        .type(TEXT_QUERY_TYPE)
                        .operator(OPERATOR)
                        .slop(DEFAULT_SLOP)
                        .prefixLength(DEFAULT_PREFIX_LENGTH)
                        .maxExpansions(DEFAULT_MAX_EXPANSIONS)
                        .fields(fields)
                        .query(text));
    }
}
