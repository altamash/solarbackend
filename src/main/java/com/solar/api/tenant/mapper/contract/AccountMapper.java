package com.solar.api.tenant.mapper.contract;

import com.solar.api.tenant.mapper.user.UserMapper;
import com.solar.api.tenant.model.contract.Account;
import com.solar.api.tenant.model.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper {
    public static Account toAccount(AccountDTO accountDTO) {
        return Account.builder()
                .id(accountDTO.getId())
                .user(UserMapper.toUser(accountDTO.getUserDTO()))
                .userName(accountDTO.getUserName())
                .emailAddress(accountDTO.getEmailAddress())
                .dob(accountDTO.getDob())
                .firstName(accountDTO.getFirstName())
                .middleName(accountDTO.getMiddleName())
                .lastName(accountDTO.getLastName())
                .userLevel(accountDTO.getUserLevel())
                .status(accountDTO.getStatus())
                .isDocAttached(accountDTO.getIsDocAttached())
                .createdAt(accountDTO.getCreatedAt())
                .updatedAt(accountDTO.getUpdatedAt())
                .build();
    }

    public static AccountDTO toAccountDTO(Account account) {
        if (account == null) {
            return null;
        }

        return AccountDTO.builder()
                .id(account.getId())
                .userDTO(UserMapper.toUserDTO(account.getUser()))
                .userName(account.getUserName())
                .emailAddress(account.getEmailAddress())
                .dob(account.getDob())
                .firstName(account.getFirstName())
                .middleName(account.getMiddleName())
                .lastName(account.getLastName())
                .userLevel(account.getUserLevel())
                .status(account.getStatus())
                .isDocAttached(account.getIsDocAttached())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    public static List<Account> toAccountList(List<AccountDTO> accountDTOList) {
        return accountDTOList.stream().map(AccountMapper::toAccount).collect(Collectors.toList());
    }

    public static List<AccountDTO> toAccountDTOList(List<Account> accountList) {
        return accountList.stream().map(AccountMapper::toAccountDTO).collect(Collectors.toList());
    }

    public static AccountDTO userToAccountDTO(User user, Boolean isAttachment) {
        if (user == null) {
            return null;
        }
        return AccountDTO.builder()
                .userName(user.getUserName())
                .emailAddress(user.getEmailAddress())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isDocAttached(isAttachment)
                .status(user.getStatus())
                .dob(user.getDataOfBirth())
                // .userLevelPrivilegeDTOList(account.getUserLevelPrivileges() != null ?UserLevelPrivilegeMapper.toUserLevelPrivilegeDTOList(account.getUserLevelPrivileges()) : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static Account accountDTOtoAccount(AccountDTO accountDTO) {
        return Account.builder()
                .id(accountDTO.getId())
                .userName(accountDTO.getUserName())
                .emailAddress(accountDTO.getEmailAddress())
                .dob(accountDTO.getDob())
                .firstName(accountDTO.getFirstName())
                .middleName(accountDTO.getMiddleName())
                .lastName(accountDTO.getLastName())
                .userLevel(accountDTO.getUserLevel())
                .status(accountDTO.getStatus())
                .isDocAttached(accountDTO.getIsDocAttached())
                .createdAt(accountDTO.getCreatedAt())
                .updatedAt(accountDTO.getUpdatedAt())
                .build();
    }

}
