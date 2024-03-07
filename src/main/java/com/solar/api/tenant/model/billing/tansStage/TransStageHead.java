package com.solar.api.tenant.model.billing.tansStage;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trans_stage_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransStageHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tjob_id")
    private Long tjobId;

    @Column(name = "stage_id")
    private Long stageId;

    @Column(name = "var_id")
    private String varId;

    @Column(name = "subs_id")
    private String subsId;
    @Column(name = "parser_code")
    private String parserCode;

    @CreationTimestamp
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "status")
    private String status;

}