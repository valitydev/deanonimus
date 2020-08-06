package com.rbkmoney.deanonimus.db;

import com.rbkmoney.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import static com.rbkmoney.deanonimus.constant.ElasticsearchConstants.*;
import static org.elasticsearch.index.query.QueryBuilders.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchDao {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    public SearchHits<Party> searchParty(String text) {

        QueryBuilder builder = boolQuery()
                .should(searchPartyFields(text))
                .should(searchShopFields(text))
                .should(searchContractFields(text))
                .should(searchContractorFields(text));

        Query searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();

        return elasticsearchRestTemplate.search(searchQuery, Party.class);
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
