package com.rbkmoney.deanonimus.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    @Field(type = FieldType.Keyword)
    private String id;
    private Blocking blocking;
    private Suspension suspension;
    private String detailsName;
    private String detailsDescription;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "write_url_analyzer")
    private String locationUrl;
    private Integer categoryId;
    private String accountCurrencyCode;
    private Long accountSettlement;
    private Long accountGuarantee;
    private Long accountPayout;
    private String contractId;
    private String payoutToolId;
    private Integer payoutScheduleId;
}
