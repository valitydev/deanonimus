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

import static com.rbkmoney.deanonimus.constant.ElasticsearchConstants.SHOP_INDEX;
import static org.elasticsearch.index.query.QueryBuilders.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchDao {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    public SearchHits<Party> searchParty(String text) {

        QueryBuilder builder = boolQuery()
                .should(searchShopFields(text))
                .should(searchPartyFields(text));

        Query searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();

        return elasticsearchRestTemplate.search(searchQuery, Party.class);
    }


    private QueryBuilder searchShopFields(String text) {
        return nestedQuery(SHOP_INDEX,
                multiMatchQuery(text,
                        "shops.locationUrl"
                ), ScoreMode.None);
    }

    private QueryBuilder searchPartyFields(String text) {
        return multiMatchQuery(text,
                "email"

        );
    }

}
