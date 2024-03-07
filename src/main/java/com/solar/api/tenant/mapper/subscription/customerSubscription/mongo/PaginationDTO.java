package com.solar.api.tenant.mapper.subscription.customerSubscription.mongo;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDTO<T> implements Serializable {
    private int pageNumber;
    private int numberOfElements;
    private long totalElements;
    private int totalPages;
    private List<MongoCustomerSubscriptionDTO> subscriptions;

}