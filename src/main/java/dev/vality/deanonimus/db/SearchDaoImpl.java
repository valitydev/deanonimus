package dev.vality.deanonimus.db;

import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Operator;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static dev.vality.deanonimus.constant.OpenSearchConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("LineLength")
public class SearchDaoImpl implements SearchDao {

    @Value("${data.response.limit}")
    Integer responseLimit;

    private final OpenSearchClient openSearchClient;


    @SneakyThrows
    @Override
    public SearchResponse<Party> searchParty(String text) {
        BoolQuery queryBuilder = new BoolQuery.Builder()
                .should(builder -> builder
                        .multiMatch(builder1 -> builder1
                                .operator(Operator.Or)
                                .fields("id", "email")
                                .query(text)
                                .type(TextQueryType.Phrase)))
                .should(builder -> builder
                        .multiMatch(builder1 -> builder1
                                .operator(Operator.Or)
                                .fields("shops.id", "shops.locationUrl", "shops.detailsName")
                                .query(text)
                                .type(TextQueryType.Phrase)))
                .should(builder -> builder
                        .multiMatch(builder1 -> builder1
                                .operator(Operator.Or)
                                .fields("contracts.id", "contracts.legalAgreementId", "contracts.reportActSignerFullName")
                                .query(text)
                                .type(TextQueryType.Phrase)))
                .should(builder -> builder
                        .multiMatch(builder1 -> builder1
                                .operator(Operator.Or)
                                .fields("contractors.id", "contractors.registeredUserEmail", "contractors.russianLegalEntityRegisteredName", "contractors.russianLegalEntityInn", "contractors.russianLegalEntityRussianBankAccount", "contractors.internationalLegalEntityLegalName", "contractors.internationalLegalEntityTradingName")
                                .query(text)
                                .type(TextQueryType.Phrase)))
                .should(builder -> builder
                        .multiMatch(builder1 -> builder1
                                .operator(Operator.Or)
                                .fields("wallets.id", "wallets.name")
                                .query(text)
                                .type(TextQueryType.Phrase)))
                .build();

        return openSearchClient.search(s -> s
                        .size(responseLimit)
                        .index(PARTY_INDEX)
                        .query(new Query.Builder()
                                .bool(queryBuilder)
                                .build()),
                Party.class);
    }
}
