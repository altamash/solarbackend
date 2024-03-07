package com.solar.api.saas.service.process.upload.mapper.csgr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionMapping {

    private String externalId;
    private String subscriptionRateMatrixHeadId;
    @JsonIgnore
    private SubscriptionRateMatrixHead subscriptionRateMatrixHead;
    private String sn;
    private String cn;
    private String vcn;
    private String cclas;
    private String pn;
    private String sadd;
    private String mp;
    private String kwdc;
    private String ssdt;
    private String psrc;
    private String pcomp;
    private String dscp;
    private String tenr;
    private String roll;
    private String prjn;
    private String dep;
    private String dsc;

    @Override
    public String toString() {
        return "SubscriptionMapping{" +
                "externalId='" + externalId + '\'' +
                ", subscriptionRateMatrixHeadId='" + subscriptionRateMatrixHeadId + '\'' +
                ", subscriptionRateMatrixHead=" + subscriptionRateMatrixHead +
                ", sn='" + sn + '\'' +
                ", cn='" + cn + '\'' +
                ", vcn='" + vcn + '\'' +
                ", cclas='" + cclas + '\'' +
                ", pn='" + pn + '\'' +
                ", sadd='" + sadd + '\'' +
                ", mp='" + mp + '\'' +
                ", kwdc='" + kwdc + '\'' +
                ", ssdt='" + ssdt + '\'' +
                ", psrc='" + psrc + '\'' +
                ", pcomp='" + pcomp + '\'' +
                ", dscp='" + dscp + '\'' +
                ", tenr='" + tenr + '\'' +
                ", roll='" + roll + '\'' +
                ", prjn='" + prjn + '\'' +
                ", dep='" + dep + '\'' +
                ", dsc='" + dsc + '\'' +
                '}';
    }
}
