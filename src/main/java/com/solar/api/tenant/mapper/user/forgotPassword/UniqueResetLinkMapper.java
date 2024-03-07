package com.solar.api.tenant.mapper.user.forgotPassword;

import com.solar.api.tenant.model.user.UniqueResetLink;

import java.util.List;
import java.util.stream.Collectors;

public class UniqueResetLinkMapper {

    public static UniqueResetLink toUniqueResetLink(UniqueResetLinkDTO uniqueResetLinkDTO) {
        if (uniqueResetLinkDTO == null) {
            return null;
        }
        return UniqueResetLink.builder()
                .id(uniqueResetLinkDTO.getId())
                .tenantId(uniqueResetLinkDTO.getTenantId())
                .usedIndicator(uniqueResetLinkDTO.getUsedIndicator())
                .userAccount(uniqueResetLinkDTO.getId())
                .uniqueText(uniqueResetLinkDTO.getUniqueText())
                .generatedOn(uniqueResetLinkDTO.getGeneratedOn())
                .build();
    }

    public static UniqueResetLinkDTO toUniqueResetLinkDTO(UniqueResetLink uniqueResetLink) {
        if (uniqueResetLink == null) {
            return null;
        }
        return UniqueResetLinkDTO.builder()
                .id(uniqueResetLink.getId())
                .tenantId(uniqueResetLink.getTenantId())
                .usedIndicator(uniqueResetLink.getUsedIndicator())
                .userAccount(uniqueResetLink.getId())
                .uniqueText(uniqueResetLink.getUniqueText())
                .generatedOn(uniqueResetLink.getGeneratedOn())
                .build();
    }

    /**
     * @param uniqueResetLinkDTOS
     * @return
     */
    public static List<UniqueResetLink> toUniqueResetLink(List<UniqueResetLinkDTO> uniqueResetLinkDTOS) {
        return uniqueResetLinkDTOS.stream().map(a -> toUniqueResetLink(a)).collect(Collectors.toList());
    }

    /**
     * @param uniqueResetLinks
     * @return
     */
    public static List<UniqueResetLinkDTO> toUniqueResetLinkDTOs(List<UniqueResetLink> uniqueResetLinks) {
        return uniqueResetLinks.stream().map(a -> toUniqueResetLinkDTO(a)).collect(Collectors.toList());
    }
}
