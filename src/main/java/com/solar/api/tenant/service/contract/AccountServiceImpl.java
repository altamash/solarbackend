package com.solar.api.tenant.service.contract;

import com.google.protobuf.Api;
import com.solar.api.ResponseEntityResult;
import com.solar.api.configuration.JwtTokenUtil;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.ApiAccessLog;
import com.solar.api.tenant.model.ApiAccessLogsRequest;
import com.solar.api.tenant.model.contract.Account;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.ApiAccessLogRepository;
import com.solar.api.tenant.repository.UserRepository;
import com.solar.api.tenant.repository.contract.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ApiAccessLogRepository apiAccessLogRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResponseEntityResult responseEntityResult;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public Account add(Account account, String refCode, List<MultipartFile> multipartFiles) {
        account.setUser(userRepository.findById(account.getUser().getAcctId()).orElseThrow(() -> new NotFoundException(User.class, account.getUser().getAcctId())));
        return accountRepository.save(account);
    }

    @Override
    public Account update(Account account) {
        Account accountData = null;
        if (account.getId() != null) {
            accountData = accountRepository.findById(account.getId()).orElseThrow(() ->
                    new NotFoundException(Account.class, account.getId()));
            accountData = accountRepository.save(accountData);
        }
        return accountData;
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new NotFoundException(Account.class, id));
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account save(Account account) {
        account.setUser(userRepository.findById(account.getUser().getAcctId()).orElseThrow(() -> new NotFoundException(User.class, account.getUser().getAcctId())));
        return accountRepository.save(account);
    }

    @Override
    public Account findByUser(User user) {
        return accountRepository.findByUser(user);
    }

    @Override
    public APIResponse<?> apiAccessLogs(HttpServletRequest request, ApiAccessLogsRequest accessLogsRequest, Integer pageNumber, Integer pageSize,Integer daysAgo) {
        try {
            Map<String, String> userData = jwtTokenUtil.extractInformationFromJwt(request);
            Page<ApiAccessLog> resultPage = null;
            LocalDateTime fromData = null;
            if (daysAgo==null || daysAgo==0) {
                fromData = LocalDateTime.now().minusDays(7);
            }else {
                fromData = LocalDateTime.now().minusDays(daysAgo);
            }
            List<ApiAccessLog> resultDataWithOutPagination = null;
            if (pageNumber == null || pageSize == null) {
                resultDataWithOutPagination= apiAccessLogRepository.findApiAccessLogsForUserWithinLast7Days(Long.valueOf(userData.get("id")), fromData);
                if (!resultDataWithOutPagination.isEmpty()) {
                    return (APIResponse.builder().code(200).warning(null).message(null).error(null).data(resultDataWithOutPagination).build());
                } else {
                    return (APIResponse.builder().code(200).warning(null).message("No Data Found For The Following User.").error(null).data(null).build());
                }
            }else {
                // Call the custom repository method
                Pageable pageable = PageRequest.of(pageNumber, pageSize); // Page number 0, page size 10
                resultPage = apiAccessLogRepository.findApiAccessLogsForUserWithinLast7Days(Long.valueOf(userData.get("id")), fromData, pageable);
                if (!resultPage.isEmpty()) {
                    return (APIResponse.builder().code(200).warning(null).message(null).error(null).data(resultPage.get()).build());
                } else {
                    return (APIResponse.builder().code(200).warning(null).message("No Data Found For The Following User.").error(null).data(null).build());
                }
            }

        } catch (Exception exception) {
            return (APIResponse.builder().code(500).warning(null).message(null).error(exception.getMessage()).data(null).build());
        }
    }
}
