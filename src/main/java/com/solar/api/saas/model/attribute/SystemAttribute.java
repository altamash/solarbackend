package com.solar.api.saas.model.attribute;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_attribute")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20)
    private String attribute; //varchar(20) PK
    @Column(length = 125)
    private String attributeDescription; //varchar(125)
    @Column(length = 45, unique = true)
    private String attributeKey; //varchar(45) PK
    @Column(length = 1000)
    private String attributeValue; //varchar(45) PK
    @Lob
    private byte[] attributeValueLob; //varchar(45) PK
    @Column(length = 45)
    private String parentAttribute; //varchar(45)
    @Column(length = 45)
    private String attributeType; //varchar(45)
    @Column(length = 45)
    private String referenceObject; //varchar(45)

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
