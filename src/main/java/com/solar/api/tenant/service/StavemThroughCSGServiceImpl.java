package com.solar.api.tenant.service;

import com.solar.api.tenant.model.stavem.StavemThroughCSG;
import com.solar.api.tenant.repository.StavemThroughCSGRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StavemThroughCSGServiceImpl implements StavemThroughCSGService{

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    StavemThroughCSGRepository stavemThroughCSGRepository;

    @Override
    public List<StavemThroughCSG> saveAll(List<StavemThroughCSG> stavemThroughCSGList) {
        return stavemThroughCSGRepository.saveAll(stavemThroughCSGList);
    }

    @Override
    public void deleteAll() {
        try {
            stavemThroughCSGRepository.deleteAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public List<StavemThroughCSG> getAll() {
        return stavemThroughCSGRepository.findAll();
    }
}
