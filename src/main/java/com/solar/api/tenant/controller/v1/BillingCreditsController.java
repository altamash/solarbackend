package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.billingCredits.BillingCreditsMapper;
import com.solar.api.tenant.mapper.billingCredits.PagedBillingCreditsDTO;
import com.solar.api.tenant.model.billingCredits.BillingCredits;
import com.solar.api.tenant.model.billingCredits.BillingCreditsPostProcessing;
import com.solar.api.tenant.model.billingCredits.SearchParamsBillingCredits;
import com.solar.api.tenant.repository.BillingCreditsPostProcessingRepository;
import com.solar.api.tenant.service.BillingCreditsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("BillingCreditsController")
@RequestMapping(value = "/billingCredits")
public class BillingCreditsController {

    @Autowired
    private BillingCreditsService billingCreditsService;

    @Autowired
    private BillingCreditsPostProcessingRepository billingCreditsPostProcessingRepository;

    /**
     * @param billingCreditsService
     */
    public BillingCreditsController(BillingCreditsService billingCreditsService) {
        this.billingCreditsService = billingCreditsService;
    }

    /**
     * @param keyExtractors
     * @param <T>
     * @return
     */
    private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .collect(Collectors.toList());

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }

    @PostMapping("/searchBy")
    public Object search(@RequestBody SearchParamsBillingCredits searchParamsBillingCredits) {
        Page<BillingCredits> billingCredits = billingCreditsService.comprehensiveSearch(
                PageRequest.of(searchParamsBillingCredits.getPageNumber(), searchParamsBillingCredits.getNoOfPages(), Sort.unsorted()),
                searchParamsBillingCredits);
        if (billingCredits != null) {
            return PagedBillingCreditsDTO.builder()
                    .totalItems(billingCredits.getTotalElements())
                    .billingCreditsDTOList(BillingCreditsMapper.toBillingCreditsDTOs(billingCredits.getContent()))
                    .build();
        }
        return new ObjectMapper().createObjectNode().put("message", "No records found");
    }

    @GetMapping("getBillingCreditsView")
    public List<BillingCreditsPostProcessing> getAll() {
        return billingCreditsPostProcessingRepository.getAll();
    }
}
