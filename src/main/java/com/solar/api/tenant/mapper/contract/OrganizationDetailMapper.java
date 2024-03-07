package com.solar.api.tenant.mapper.contract;

import com.solar.api.tenant.model.contract.OrganizationDetail;

public class OrganizationDetailMapper {

    public static OrganizationDetail toOrganizationDetail(Long orgId,Long refid){

        return OrganizationDetail.builder()
                .orgId(orgId)
//                .refType("User")
//                .associationType("Unit manager")
//                .refId(refid)
//                .gardenDescription("")
                .build();

    }

}
