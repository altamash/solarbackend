package com.solar.api.tenant.mapper.contract;

import com.solar.api.tenant.model.contract.UserPasswordHead;

import java.util.List;
import java.util.stream.Collectors;

public class UserPasswordHeadMapper {
    public static UserPasswordHead toUserPasswordHead(UserPasswordHeadDTO userPasswordHeadDTO) {
        return UserPasswordHead.builder()
                .id(userPasswordHeadDTO.getId())
                .accountId(userPasswordHeadDTO.getAccountId())
                .authPlatform(userPasswordHeadDTO.getAuthPlatform())
                .oAuthCode(userPasswordHeadDTO.getOAuthCode())
                .password(userPasswordHeadDTO.getPassword())
                .futureExpiryDate(userPasswordHeadDTO.getFutureExpiryDate())
                .createdAt(userPasswordHeadDTO.getCreatedAt())
                .updatedAt(userPasswordHeadDTO.getUpdatedAt())
                .build();
    }

    public static UserPasswordHeadDTO toUserPasswordHeadDTO(UserPasswordHead userPasswordHead) {
        if (userPasswordHead == null) {
            return null;
        }

        return UserPasswordHeadDTO.builder()
                .id(userPasswordHead.getId())
                .accountId(userPasswordHead.getAccountId())
                .authPlatform(userPasswordHead.getAuthPlatform())
                .oAuthCode(userPasswordHead.getOAuthCode())
                .password(userPasswordHead.getPassword())
                .futureExpiryDate(userPasswordHead.getFutureExpiryDate())
                .createdAt(userPasswordHead.getCreatedAt())
                .updatedAt(userPasswordHead.getUpdatedAt())
                .build();
    }

    public static List<UserPasswordHead> toUserPasswordHeadList(List<UserPasswordHeadDTO> userPasswordHeadList) {
        return userPasswordHeadList.stream().map(UserPasswordHeadMapper::toUserPasswordHead).collect(Collectors.toList());
    }

    public static List<UserPasswordHeadDTO> toUserPasswordHeadDTOList(List<UserPasswordHead> userPasswordHeadList) {
        return userPasswordHeadList.stream().map(UserPasswordHeadMapper::toUserPasswordHeadDTO).collect(Collectors.toList());
    }
}
