package com.solar.api.tenant.model.payment.info;

import com.solar.api.saas.service.process.encryption.StringAttributeConverter;
import com.solar.api.tenant.model.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_info",indexes = {@Index(columnList = "paymentSrcAlias", name = "paymentSrcAlias_index")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "portal_account_id")
    private User portalAccount;
    @Transient
    private Long acctId;

    private Integer sequenceNumber;
    private String paymentSrcAlias;
    private String paymentSource;
    private String accountTitle;
    @Convert(converter = StringAttributeConverter.class)
    private String accountNumber;
    @Convert(converter = StringAttributeConverter.class)
    private String routingNumber;
    private String accountType;
    private String bankName;
    private Boolean primaryIndicator;
    private Boolean ecApproved;

    @Transient
    private String externalId;
    @Transient
    private String action;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
//    @Convert(converter = StringAttributeConverter.class)
//    private String cvv;
//    @Convert(converter = StringAttributeConverter.class)
//    private String cardNumber;
    private String cardProvider;
    private String cardType;
    private Boolean isPrimary;

}
