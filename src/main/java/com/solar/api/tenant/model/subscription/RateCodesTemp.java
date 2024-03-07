package com.solar.api.tenant.model.subscription;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "rate_codes_temp")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateCodesTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accountTd;
    private Long subscriptionId;
    private String SN;
    private String CN;
    private String VCN;
    private String CCLAS;
    private String PN;
    private String SADD;
    private String MP;
    private String KWDC;
    private String SSDT;
    private String PSRC;
    private String PCOMP;
    private String DSCP;
    private String TENR;
    private String ROLL;
    private String CSGSDT;
    private String DEP;
    private String GLCR;
    private String GLDR;
    private String GNSIZE;
    private String GOWN;
    private String SCSG;
    private String SCSGN;
    private String SPGM;
    private String UTCOMP;
    private String PRJN;
    private String DSC;
    private String SSYR;
    private String VSDT;
    private String VYR;
    private String YLD;
    private String SRTE;
    private String FDAM;
    private String OPYR;
    private String TNR1;
    private String DSCM;
    private String ROLLDT;

}
