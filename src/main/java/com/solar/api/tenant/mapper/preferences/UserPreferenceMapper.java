package com.solar.api.tenant.mapper.preferences;

import com.solar.api.tenant.model.UserPreference;

import java.util.List;
import java.util.stream.Collectors;

public class UserPreferenceMapper {
    public static UserPreference toUserPreference(UserPreferenceDTO userPreferenceDTO) {
        return UserPreference.builder()
                .id(userPreferenceDTO.getId())
                .userParam(userPreferenceDTO.getUserParam())
                .description(userPreferenceDTO.getDescription())
                .paramType(userPreferenceDTO.getParamType())
                .userType(userPreferenceDTO.getUserType())
                .category(userPreferenceDTO.getCategory())
                .attributeRefId(userPreferenceDTO.getAttributeRefId())
                .tempOverrideEnabled(userPreferenceDTO.getTempOverrideEnabled())
                .createdAt(userPreferenceDTO.getCreatedAt())
                .updatedAt(userPreferenceDTO.getUpdatedAt())
                .build();
    }

    public static UserPreferenceDTO toUserPreferenceDTO(UserPreference userPreference) {
        if (userPreference == null) {
            return null;
        }

        return UserPreferenceDTO.builder()
                .id(userPreference.getId())
                .userParam(userPreference.getUserParam())
                .description(userPreference.getDescription())
                .paramType(userPreference.getParamType())
                .userType(userPreference.getUserType())
                .category(userPreference.getCategory())
                .attributeRefId(userPreference.getAttributeRefId())
                .tempOverrideEnabled(userPreference.getTempOverrideEnabled())
                .createdAt(userPreference.getCreatedAt())
                .updatedAt(userPreference.getUpdatedAt())
                .build();
    }

    public static UserPreference toUpdateUserPreference(UserPreference userPreference, UserPreference userPreferenceUpdate) {
        userPreference.setUserParam(userPreferenceUpdate.getUserParam() == null ? userPreference.getUserParam() :
                userPreferenceUpdate.getUserParam());
        userPreference.setDescription(userPreferenceUpdate.getDescription() == null ? userPreference.getDescription() :
                userPreferenceUpdate.getDescription());
        userPreference.setParamType(userPreferenceUpdate.getParamType() == null ? userPreference.getParamType() :
                userPreferenceUpdate.getParamType());
        userPreference.setUserType(userPreferenceUpdate.getUserType() == null ? userPreference.getUserType() :
                userPreferenceUpdate.getUserType());
        userPreference.setCategory(userPreferenceUpdate.getCategory() == null ? userPreference.getCategory() :
                userPreferenceUpdate.getCategory());
        userPreference.setAttributeRefId(userPreferenceUpdate.getAttributeRefId() == null ? userPreference.getAttributeRefId() :
                userPreferenceUpdate.getAttributeRefId());
        userPreference.setTempOverrideEnabled(userPreferenceUpdate.getTempOverrideEnabled() == null ? userPreference.getTempOverrideEnabled() :
                userPreferenceUpdate.getTempOverrideEnabled());
        return userPreference;
    }

    public static List<UserPreference> toUserPreferenceList(List<UserPreferenceDTO> userPreferenceDTOList) {
        return userPreferenceDTOList.stream().map(UserPreferenceMapper::toUserPreference).collect(Collectors.toList());
    }

    public static List<UserPreferenceDTO> toUserPreferenceDTOList(List<UserPreference> userPreferenceList) {
        return userPreferenceList.stream().map(UserPreferenceMapper::toUserPreferenceDTO).collect(Collectors.toList());
    }
}
