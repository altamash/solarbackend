package com.solar.api.tenant.mapper.subscription.customerSubscription.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoCustomerSubscriptionMasterDTO<T> implements Serializable {

    private List<MongoCustomerSubscriptionDTO> mongoCustomerSubscriptionDTO;
    private PaginationDTO paginationDTO;


}
