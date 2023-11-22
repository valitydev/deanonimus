package dev.vality.deanonimus.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shop {
    private String id;
    private Blocking blocking;
    private Suspension suspension;
    private String detailsName;
    private String detailsDescription;
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
