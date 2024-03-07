package com.solar.api.tenant.mapper.subscription.customerSubscription.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoProductDTO {
    private String product;
    private String productImagesUrl;
}

