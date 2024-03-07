package com.solar.api.tenant.mapper.contract;

import com.solar.api.tenant.model.contract.UserPasswordHistory;

import java.util.List;
import java.util.stream.Collectors;

public class UserPasswordHistoryMapper {
    public static UserPasswordHistory toUserPasswordHistory(UserPasswordHistoryDTO userPasswordHistoryDTO) {
        return UserPasswordHistory.builder()
                .id(userPasswordHistoryDTO.getId())
                .accountId(userPasswordHistoryDTO.getAccountId())
                .password(userPasswordHistoryDTO.getPassword())
                .createdAt(userPasswordHistoryDTO.getCreatedAt())
                .updatedAt(userPasswordHistoryDTO.getUpdatedAt())
                .build();
    }

    public static UserPasswordHistoryDTO toUserPasswordHistoryDTO(UserPasswordHistory userPasswordHistory) {
        if (userPasswordHistory == null) {
            return null;
        }

        return UserPasswordHistoryDTO.builder()
                .id(userPasswordHistory.getId())
                .accountId(userPasswordHistory.getAccountId())
                .password(userPasswordHistory.getPassword())
                .createdAt(userPasswordHistory.getCreatedAt())
                .updatedAt(userPasswordHistory.getUpdatedAt())
                .build();
    }

    public static List<UserPasswordHistory> toUserPasswordHistoryList(List<UserPasswordHistoryDTO> userPasswordHistoryList) {
        return userPasswordHistoryList.stream().map(UserPasswordHistoryMapper::toUserPasswordHistory).collect(Collectors.toList());
    }

    public static List<UserPasswordHistoryDTO> toUserPasswordHistoryDTOList(List<UserPasswordHistory> userPasswordHistoryList) {
        return userPasswordHistoryList.stream().map(UserPasswordHistoryMapper::toUserPasswordHistoryDTO).collect(Collectors.toList());
    }
}
