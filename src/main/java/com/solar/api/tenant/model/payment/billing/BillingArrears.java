package com.solar.api.tenant.model.payment.billing;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "billing_arrears")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingArrears {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long acctId;
    private Long subsId;
    private Double accumulatedArrears;
    private Boolean billed;
    private Long billingHeadId;
    private Date processDate;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
