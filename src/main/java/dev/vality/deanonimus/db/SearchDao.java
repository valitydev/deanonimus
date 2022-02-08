package dev.vality.deanonimus.db;

import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.constant.ElasticsearchConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchDao {

    @Value("${data.response.limit}")
    Integer responseLimit;

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    public SearchHits<Party> searchParty(String text) {

        QueryBuilder builder = boolQuery()
                .should(searchPartyFields(text))
                .should(searchShopFields(text))
                .should(searchContractFields(text))
                .should(searchContractorFields(text));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(builder)
                .withPageable(PageRequest.of(0, responseLimit))
                .build();

        return elasticsearchRestTemplate.search(searchQuery, Party.class);
    }

    private QueryBuilder searchContractorFields(String text) {
        return nestedQuery(ElasticsearchConstants.CONTRACTOR_INDEX,
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
        return nestedQuery(ElasticsearchConstants.CONTRACT_INDEX,
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
        return nestedQuery(ElasticsearchConstants.SHOP_INDEX,
                multiMatchQuery(text,
                        "shops.id",
                        "shops.locationUrl"
                ), ScoreMode.Total);
    }

}
