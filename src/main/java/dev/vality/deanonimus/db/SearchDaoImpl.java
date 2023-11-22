package dev.vality.deanonimus.db;

import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.mapping.FieldType;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static dev.vality.deanonimus.constant.ElasticsearchConstants.*;
import static org.opensearch.index.query.QueryBuilders.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchDaoImpl implements SearchDao {

    @Value("${data.response.limit}")
    Integer responseLimit;

    private final OpenSearchClient client;


    @SneakyThrows
    @Override
    public SearchResponse<Party> searchParty(String text) {

        BoolQueryBuilder queryBuilder = boolQuery()
                .should(searchPartyFields(text))
                .should(searchShopFields(text))
                .should(searchContractFields(text))
                .should(searchContractorFields(text));

        return client.search(s -> s.index("party"), Party.class);
    }

    private QueryBuilder searchContractorFields(String text) {
        return nestedQuery(CONTRACTOR_INDEX,
                multiMatchQuery(text,
                        "contractors.id",
                        "contractors.registeredUserEmail",
                        "contractors.russianLegalEntityRegisteredName",
                        "contractors.russianLegalEntityInn",
                        "contractors.russianLegalEntityRussianBankAccount",
                        "contractors.internationalLegalEntityLegalName",
                        "contractors.internationalLegalEntityTradingName"), ScoreMode.Total);
    }

    private QueryBuilder searchContractFields(String text) {
        return nestedQuery(CONTRACT_INDEX,
                multiMatchQuery(text,
                        "contracts.id",
                        "contracts.legalAgreementId",
                        "contracts.reportActSignerFullName"), ScoreMode.Total);
    }


    private QueryBuilder searchPartyFields(String text) {
        return multiMatchQuery(text,
                "id",
                "email"
        );
    }

    private QueryBuilder searchShopFields(String text) {
        return nestedQuery(SHOP_INDEX,
                multiMatchQuery(text,
                        "shops.id",
                        "shops.locationUrl"
                ), ScoreMode.Total);
    }

}
