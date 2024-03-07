package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.tenant.mapper.contract.*;
import com.solar.api.tenant.service.contract.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ContractController")
@RequestMapping(value = "/contract")
public class ContractController {
    @Autowired
    private ContractService contractService;

    @PostMapping("/entity/{entityId}/refCode/{refCode}")
    public ContractDTO add(@RequestHeader("Authorization") String authorization,
                           @RequestParam("contractDTO") String contractDTO,
                           @PathVariable Long entityId,
                           @PathVariable String refCode,
                           @RequestParam(name = "multipartFiles", required = false) List<MultipartFile> multipartFiles)
            throws Exception {
        ContractDTO dto = new ObjectMapper().readValue(contractDTO, ContractDTO.class);
        return ContractMapper.toContractDTO(
                contractService.add(authorization, ContractMapper.toContract(dto), entityId, refCode, multipartFiles));
    }

    @PutMapping
    public ContractDTO update(@RequestHeader("Authorization") String authorization, @RequestBody ContractDTO contractDTO) {
        return ContractMapper.toContractDTO(
                contractService.update(authorization, ContractMapper.toContract(contractDTO)));
    }

    @GetMapping("/{id}")
    public ContractDTO findById(@PathVariable Long id) {
        return ContractMapper.toContractDTO(contractService.findById(id));
    }

    @GetMapping
    public List<ContractDTO> findAll() {
        return ContractMapper.toContractDTOList(contractService.findAll());
    }

    // Contract Mapping
    @PostMapping
    public List<ContractMappingDTO> addContractMapping(@RequestParam("contractId") Long contractId,
                                                       @RequestParam("subscriptionIdsCSV") String subscriptionIdsCSV) {
        List<Long> subscriptionIds =
                Arrays.stream(subscriptionIdsCSV.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        return ContractMappingMapper.toContractMappingDTOList(contractService.addContractMapping(contractId, subscriptionIds));
    }

    @PostMapping("/userId/{userId}")
    public ContractDTO addMasterAgreementToSelfRegisterUser(@RequestHeader("Authorization") String authorization,
                           @PathVariable Long userId) {
      return  contractService.addMasterAgreementToSelfRegisterUser(authorization,userId);

    }
    @GetMapping("/getAllContractsByEntityId")
    public List<ContractByEntityDTO> getAllContractsByEntityId(@RequestParam("entityId") Long entityId) {
        return contractService.getAllContractsByEntityId(entityId);
    }

}
