# deanonimus
Сервис для полнотекстового поиска party

актуальные примеры с spring data elastic + opensearch

https://github.com/opensearch-project/spring-data-opensearch/blob/main/spring-data-opensearch-examples/spring-boot-gradle/src/main/java/org/opensearch/data/example/service/MarketplaceInitializer.java  
https://github.com/M-Razavi/Spring-Data-OpenSearch-Example

полезные ссылки  
https://opensearch.org/docs/latest/query-dsl/full-text/index/  
https://opensearch.org/docs/latest/query-dsl/full-text/multi-match/  
  
сниппеты 
```java
getMappingResponse.get("party").mappings().properties().get("id").text().fields().get("keyword").keyword()

search.hits().hits().get(0).source()

var criteria = QueryBuilders.boolQuery()
        .must(QueryBuilders.matchQuery("authorName", author))
        .must(QueryBuilders.matchQuery("title", title));

SearchRequest searchRequest = new SearchRequest("books");
        searchRequest.source().query(criteria);

        try {
SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return Arrays.stream(response.getHits().getHits())
        .map(hit -> new ObjectMapper().convertValue(hit.getSourceAsMap(), Book.class))
        .collect(Collectors.toList());
        } catch (IOException e) {
        throw new RuntimeException("Error executing search", e);
        }
```