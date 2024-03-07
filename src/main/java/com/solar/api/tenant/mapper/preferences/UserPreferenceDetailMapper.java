package com.solar.api.tenant.mapper.preferences;

import com.solar.api.tenant.model.UserPreferenceDetail;

import java.util.List;
import java.util.stream.Collectors;

public class UserPreferenceDetailMapper {
    public static UserPreferenceDetail toUserPreferenceDetail(UserPreferenceDetailDTO userPreferenceDetailDTO) {
        return UserPreferenceDetail.builder()
                .id(userPreferenceDetailDTO.getId())
                .userId(userPreferenceDetailDTO.getUserId())
                .userPreferenceId(userPreferenceDetailDTO.getUserPreferenceId())
                .text(userPreferenceDetailDTO.getText())
                .tempParamOverrideId(userPreferenceDetailDTO.getTempParamOverrideId())
                .num(userPreferenceDetailDTO.getNum())
                .date(userPreferenceDetailDTO.getDate())
                .format(userPreferenceDetailDTO.getFormat())
                .docId(userPreferenceDetailDTO.getDocId())
                .icon(userPreferenceDetailDTO.getIcon())
                .image(userPreferenceDetailDTO.getImage())
                .activeChannel(userPreferenceDetailDTO.getActiveChannel())
                .createdAt(userPreferenceDetailDTO.getCreatedAt())
                .updatedAt(userPreferenceDetailDTO.getUpdatedAt())
                .build();
    }

    public static UserPreferenceDetailDTO toUserPreferenceDetailDTO(UserPreferenceDetail userPreferenceDetail) {
        if (userPreferenceDetail == null) {
            return null;
        }

        return UserPreferenceDetailDTO.builder()
                .id(userPreferenceDetail.getId())
                .userId(userPreferenceDetail.getUserId())
                .userPreferenceId(userPreferenceDetail.getUserPreferenceId())
                .text(userPreferenceDetail.getText())
                .tempParamOverrideId(userPreferenceDetail.getTempParamOverrideId())
                .num(userPreferenceDetail.getNum())
                .date(userPreferenceDetail.getDate())
                .format(userPreferenceDetail.getFormat())
                .docId(userPreferenceDetail.getDocId())
                .icon(userPreferenceDetail.getIcon())
                .image(userPreferenceDetail.getImage())
                .activeChannel(userPreferenceDetail.getActiveChannel())
                .createdAt(userPreferenceDetail.getCreatedAt())
                .updatedAt(userPreferenceDetail.getUpdatedAt())
                .build();
    }

    public static UserPreferenceDetail toUpdateUserPreferenceDetail(UserPreferenceDetail userPreferenceDetail, UserPreferenceDetail userPreferenceDetailUpdate) {
        userPreferenceDetail.setUserId(userPreferenceDetailUpdate.getUserId() == null ? userPreferenceDetail.getUserId() :
                userPreferenceDetailUpdate.getUserId());
        userPreferenceDetail.setUserPreferenceId(userPreferenceDetailUpdate.getUserPreferenceId() == null ? userPreferenceDetail.getUserPreferenceId() :
                userPreferenceDetailUpdate.getUserPreferenceId());
        userPreferenceDetail.setText(userPreferenceDetailUpdate.getText() == null ? userPreferenceDetail.getText() :
                userPreferenceDetailUpdate.getText());
        userPreferenceDetail.setTempParamOverrideId(userPreferenceDetailUpdate.getTempParamOverrideId() == null ? userPreferenceDetail.getTempParamOverrideId() :
                userPreferenceDetailUpdate.getTempParamOverrideId());
        userPreferenceDetail.setNum(userPreferenceDetailUpdate.getNum() == null ? userPreferenceDetail.getNum() :
                userPreferenceDetailUpdate.getNum());
        userPreferenceDetail.setDate(userPreferenceDetailUpdate.getDate() == null ? userPreferenceDetail.getDate() :
                userPreferenceDetailUpdate.getDate());
        userPreferenceDetail.setFormat(userPreferenceDetailUpdate.getFormat() == null ? userPreferenceDetail.getFormat() :
                userPreferenceDetailUpdate.getFormat());
        userPreferenceDetail.setDocId(userPreferenceDetailUpdate.getDocId() == null ? userPreferenceDetail.getDocId() :
                userPreferenceDetailUpdate.getDocId());
        userPreferenceDetail.setIcon(userPreferenceDetailUpdate.getIcon() == null ? userPreferenceDetail.getIcon() :
                userPreferenceDetailUpdate.getIcon());
        userPreferenceDetail.setImage(userPreferenceDetailUpdate.getImage() == null ? userPreferenceDetail.getImage() :
                userPreferenceDetailUpdate.getImage());
        userPreferenceDetail.setActiveChannel(userPreferenceDetailUpdate.getActiveChannel() == null ? userPreferenceDetail.getActiveChannel() :
                userPreferenceDetailUpdate.getActiveChannel());
        return userPreferenceDetail;
    }

    public static List<UserPreferenceDetail> toUserPreferenceDetailList(List<UserPreferenceDetailDTO> userPreferenceDetailDTOList) {
        return userPreferenceDetailDTOList.stream().map(UserPreferenceDetailMapper::toUserPreferenceDetail).collect(Collectors.toList());
    }

    public static List<UserPreferenceDetailDTO> toUserPreferenceDetailDTOList(List<UserPreferenceDetail> userPreferenceDetailList) {
        return userPreferenceDetailList.stream().map(UserPreferenceDetailMapper::toUserPreferenceDetailDTO).collect(Collectors.toList());
    }
}
