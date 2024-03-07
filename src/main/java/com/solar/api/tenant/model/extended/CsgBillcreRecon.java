package com.solar.api.tenant.model.extended;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "csg_billcre_recon")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsgBillcreRecon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ReconId;
    private Long subscriptionId;
    private String periodStartDate;
    private String periodEndDate;
    private Double totalCredits;
    private Double totalPayment;
    private Double totalBilled;
    private Double subscriptionCost;
    private Double balance;
    private String premiseNo;
    private String gardenId;
    private String gardenName;
    private String status;
    private String reportURI;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
