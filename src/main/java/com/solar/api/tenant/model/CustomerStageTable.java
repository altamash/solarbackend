package com.solar.api.tenant.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.service.process.upload.v2.customer.mapper.CustomerV2;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "customer_stage_table")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerStageTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 40)
    private String uploadId;
    @Column(length = 1000) // TODO: Check row length in csv file
    private String csvJson;
    @Transient
    private CustomerV2 csvJsonObject;
    @Column(length = 10)
    private String status;
    @Column(length = 10)
    private String uploadType;
    @Column(length = 10)
    private String customerType;
//    @Override
//    public String toString() {
//        return "CustomerStageTable{" +
//                "id=" + id +
//                ", uploadId='" + uploadId + '\'' +
//                ", csvJson='" + csvJson + '\'' +
//                '}';
//    }
}
