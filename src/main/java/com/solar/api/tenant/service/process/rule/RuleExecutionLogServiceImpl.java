package com.solar.api.tenant.service.process.rule;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.process.rule.RuleExecutionLogMapper;
import com.solar.api.tenant.model.process.rule.RuleExecutionLog;
import com.solar.api.tenant.repository.RuleExecutionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class RuleExecutionLogServiceImpl implements RuleExecutionLogService {

    @Autowired
    private RuleExecutionLogRepository repository;

    @Override
    public RuleExecutionLog save(RuleExecutionLog ruleExecutionLog) {
        return repository.save(ruleExecutionLog);
    }

    @Override
    public RuleExecutionLog update(RuleExecutionLog ruleExecutionLog) {
        RuleExecutionLog executionLogFromDb = repository.getOne(ruleExecutionLog.getId());
        ruleExecutionLog = RuleExecutionLogMapper.toUpdatedRuleExecutionLog(executionLogFromDb, ruleExecutionLog);
        return repository.save(ruleExecutionLog);
    }

    @Override
    public List<RuleExecutionLog> save(List<RuleExecutionLog> ruleExecutionLogs) {
        return repository.saveAll(ruleExecutionLogs);
    }

    @Override
    public RuleExecutionLog findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(RuleExecutionLog.class, id));
    }

    @Override
    public List<RuleExecutionLog> findByBillId(Long id) {
        return repository.findRuleExecutionLogByBillId(id);
    }

    @Override
    public List<RuleExecutionLog> findBySubscriptionMatrixRef(String subscriptionMatrixRef) {
        return repository.findBySubscriptionMatrixRef(subscriptionMatrixRef);
    }

    @Override
    public List<RuleExecutionLog> findAll() {
        return repository.findAll();
    }

    @Override
    public List<RuleExecutionLog> findAllByBillIdAndRateCodeOrderByIdAsc(Long billId, String rateCode) {
        return repository.findAllByBillIdAndRateCodeOrderByIdAsc(billId, rateCode);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
    @Override
    public List<RuleExecutionLog> findAllByBillIdAndRateCodeOrderByIdDesc(Long billId, String rateCode) {
        return repository.findAllByBillIdAndRateCodeOrderByIdDesc(billId, rateCode);
    }
}
