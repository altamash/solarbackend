package com.solar.api.tenant.model.ca;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ca_soft_credit_check")
public class CaSoftCreditCheck{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_no")
    private com.solar.api.tenant.model.contract.Entity entity;

    @Column(name = "seq_no")
    private Long sequenceNo;

    @Column(name = "source")
    private String source;

    @Column(name = "reference_no")
    private String referenceNo;

    @Column(name = "credit_status")
    private String creditStatus;

    @Column(name = "checkedBy")
    private Long checkedBy; //logged in user id

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name ="isChecked")
    private Boolean isChecked;
    

}
