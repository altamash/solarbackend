package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.ResponseEntityResult;
import com.solar.api.tenant.mapper.contract.AccountDTO;
import com.solar.api.tenant.mapper.contract.AccountMapper;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.ApiAccessLogsRequest;
import com.solar.api.tenant.service.contract.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("AccountController")
@RequestMapping(value = "/account")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ResponseEntityResult responseEntityResult;

    @PostMapping("/refCode/{refCode}")
    public AccountDTO add(@RequestParam("accountDTO") String accountDTO,
                          @RequestParam(name = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                          @PathVariable String refCode) throws Exception {
        AccountDTO dto = new ObjectMapper().readValue(accountDTO, AccountDTO.class);
        return AccountMapper.toAccountDTO(
                accountService.add(AccountMapper.toAccount(dto), refCode, multipartFiles));
    }

    @PutMapping
    public AccountDTO update(@RequestBody AccountDTO accountDTO) {
        return AccountMapper.toAccountDTO(accountService.update(AccountMapper.toAccount(accountDTO)));
    }

    @GetMapping("/{id}")
    public AccountDTO findById(@PathVariable Long id) {
        return AccountMapper.toAccountDTO(accountService.findById(id));
    }

    @GetMapping
    public List<AccountDTO> findAll() {
        return AccountMapper.toAccountDTOList(accountService.findAll());
    }


    @PostMapping("apilog/data")
    public ResponseEntity<?> apiAccessLogs(HttpServletRequest request
            , @RequestHeader(name = "Comp-Key") Long CompKey
            , @Pattern(regexp = "\\d+", message = "Page number must be a valid integer") @RequestHeader(name = "pageNumber", required = false) Integer pageNumber
            , @Pattern(regexp = "\\d+", message = "Page number must be a valid integer") @RequestHeader(name = "pageSize", required = false) Integer pageSize
            , @RequestHeader(name = "ofDays",required = false) Integer ofDays
            , @RequestBody(required = false) ApiAccessLogsRequest apiAccessRequest) {
        try {
            return responseEntityResult.responseEntity(accountService.apiAccessLogs(request, apiAccessRequest, pageNumber, pageSize, ofDays));
        } catch (Exception exception) {
            return responseEntityResult.responseEntity(APIResponse.builder().code(500).warning(null).message(null).error(exception.getMessage()).data(null).build());
        }
    }
}