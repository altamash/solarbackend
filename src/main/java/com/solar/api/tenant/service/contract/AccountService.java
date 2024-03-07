package com.solar.api.tenant.service.contract;

import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.ApiAccessLogsRequest;
import com.solar.api.tenant.model.contract.Account;
import com.solar.api.tenant.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AccountService {
    Account add(Account account, String refCode, List<MultipartFile> multipartFiles);

    Account update(Account account);

    Account findById(Long id);

    List<Account> findAll();

    Account save(Account account);

    Account findByUser(User user);

    APIResponse<?> apiAccessLogs(HttpServletRequest request, ApiAccessLogsRequest accessLogsRequest,Integer pageNumber,Integer pageSize,Integer days);

}
