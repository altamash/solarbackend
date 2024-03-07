package com.solar.api.tenant.mapper.ca;

import com.solar.api.tenant.model.ca.CaReferralInfo;
import com.solar.api.tenant.model.contract.Entity;

public class CaReferralInfoMapper {

    public static CaReferralInfo toCaReferralInfo(CaReferralInfoDTO caReferralInfoDTO){
        return CaReferralInfo.builder()
                .id(caReferralInfoDTO.getId())
                .source(caReferralInfoDTO.getSource())
                .repId(caReferralInfoDTO.getRepresentativeId())
                .entity(new Entity(caReferralInfoDTO.getEntityId()))
                .promoCode(caReferralInfoDTO.getPromoCode())
        .build();
    }
    public static CaReferralInfo toCaReferralInfoV2(CaReferralInfoDTO caReferralInfoDTO){
        return CaReferralInfo.builder()
                .id(caReferralInfoDTO.getId())
                .source(caReferralInfoDTO.getSource())
//                .repId(caReferralInfoDTO.getRepresentativeId())
                .entity(new Entity(caReferralInfoDTO.getEntityId()))
                .promoCode(caReferralInfoDTO.getPromoCode())
                .build();
    }
    public static CaReferralInfo toUpdateCaReferralInfo(CaReferralInfo caReferralInfoData, CaReferralInfo caReferralInfo){
      /*  caReferralInfo.setEntity(caReferralInfo.getUserId()!=null?caReferralInfo.getUserId():caReferralInfoData.getUserId());
        caReferralInfo.setAssist(caReferralInfo.getAssist()!=null?caReferralInfo.getAssist():caReferralInfoData.getAssist());
        caReferralInfo.setHearFrom(caReferralInfo.getHearFrom()!=null?caReferralInfo.getHearFrom():caReferralInfoData.getHearFrom());
        caReferralInfo.setPromoCode(caReferralInfo.getPromoCode()!=null?caReferralInfo.getPromoCode():caReferralInfoData.getPromoCode());
        caReferralInfo.setRepresentativeId(caReferralInfo.getRepresentativeId()!=null?caReferralInfo.getRepresentativeId():caReferralInfoData.getRepresentativeId());
        caReferralInfo.setRepresentativeName(caReferralInfo.getRepresentativeName()!=null?caReferralInfo.getRepresentativeName():caReferralInfoData.getRepresentativeName());
        */
        return caReferralInfo;

    }
    public static CaReferralInfoDTO toCaReferralInfoDTO(CaReferralInfo caReferralInfo){
        if (caReferralInfo==null){
            return null;
        }
        return CaReferralInfoDTO.builder()
                .id(caReferralInfo.getId())
                .source(caReferralInfo.getSource())
                .representativeId(caReferralInfo.getRepId())
                .entityId(caReferralInfo.getEntity().getId())
                .promoCode(caReferralInfo.getPromoCode())
                .build();
    }
}
