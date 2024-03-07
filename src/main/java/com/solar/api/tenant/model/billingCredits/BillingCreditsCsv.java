package com.solar.api.tenant.model.billingCredits;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_credits_csv")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingCreditsCsv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentType;
    private String debtorNumber;
    private String premiseNumber;
    private String subscriberAllocationHistorySubscriberName;
    private String monthlyProductionAllocationinkWh;
    private String tariffRate;
    private String billCredit;
    private String gardenID;
    private String namePlateCapacitykWDC;
    private String calendarMonth;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
