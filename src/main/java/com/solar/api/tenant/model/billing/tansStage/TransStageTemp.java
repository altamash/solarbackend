package com.solar.api.tenant.model.billing.tansStage;



import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "trans_stage_temp")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransStageTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "t_job_id")
    private Long tJob_id;

    @Column(name = "meas_id")
    private String measId;

    @Column(name = "meas_code")
    private String measCode;

    @Column(name = "value")
    private String value;

    @Column(name = "trans_val")
    private String transVal;

    @Column(name = "format")
    private String format;

    @Column(name = "level")
    private Integer level;

    @Column(name = "seq")
    private Integer seqNo;

    @Column(name = "by_customer")
    private Boolean byCustomer;
}