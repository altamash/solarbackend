package com.solar.api.tenant.service;

import com.solar.api.tenant.model.stavem.StavemThroughCSG;

import java.util.List;

public interface StavemThroughCSGService {

    List<StavemThroughCSG> saveAll(List<StavemThroughCSG> stavemThroughCSGList);

    void deleteAll();

    List<StavemThroughCSG> getAll();

}
