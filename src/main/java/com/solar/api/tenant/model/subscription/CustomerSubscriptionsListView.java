package com.solar.api.tenant.model.subscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSubscriptionsListView {

    @Id
    @Column(name = "account_id")
    BigInteger accountId;
    @Column(name = "first_name")
    String firstName;
    @Column(name = "last_name")
    String lastName;
    @Column(name = "subscription_id")
    BigInteger subscriptionId;
    @Column(name = "subscription_status")
    String subscriptionStatus;
    @Column(name = "subscription_rate_matrix_id")
    BigInteger subscriptionRateMatrixId;
    @Column(name = "subscription_type")
    String subscriptionType;
    @Column(name = "garden_name")
    String gardenName;
    @Column(name = "garden_src")
    String gardenSrc;
    @Column(name = "premise_no")
    String premiseNo;
    @Column(name = "start_date")
    String startDate;
}
