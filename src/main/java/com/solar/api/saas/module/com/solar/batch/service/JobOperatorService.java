package com.solar.api.saas.module.com.solar.batch.service;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class JobOperatorService implements JobOperator {

    @Override
    public List<Long> getExecutions(long l) throws NoSuchJobInstanceException {
        return null;
    }

    @Override
    public List<Long> getJobInstances(String s, int i, int i1) throws NoSuchJobException {
        return null;
    }

    @Override
    public Set<Long> getRunningExecutions(String s) throws NoSuchJobException {
        return null;
    }

    @Override
    public String getParameters(long l) throws NoSuchJobExecutionException {
        return null;
    }

    @Override
    public Long start(String s, String s1) throws NoSuchJobException, JobInstanceAlreadyExistsException, JobParametersInvalidException {
        return null;
    }

    @Override
    public Long restart(long l) throws JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, NoSuchJobException, JobRestartException, JobParametersInvalidException {
        return null;
    }

    @Override
    public Long startNextInstance(String s) throws NoSuchJobException, JobParametersNotFoundException, JobRestartException, JobExecutionAlreadyRunningException, JobInstanceAlreadyCompleteException, UnexpectedJobExecutionException, JobParametersInvalidException {
        return null;
    }

    @Override
    public boolean stop(long l) throws NoSuchJobExecutionException, JobExecutionNotRunningException {
        return false;
    }

    @Override
    public String getSummary(long l) throws NoSuchJobExecutionException {
        return null;
    }

    @Override
    public Map<Long, String> getStepExecutionSummaries(long l) throws NoSuchJobExecutionException {
        return null;
    }

    @Override
    public Set<String> getJobNames() {
        return null;
    }

    @Override
    public JobExecution abandon(long l) throws NoSuchJobExecutionException, JobExecutionAlreadyRunningException {
        return null;
    }
}
