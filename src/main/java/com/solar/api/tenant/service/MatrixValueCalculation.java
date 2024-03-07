package com.solar.api.tenant.service;

import com.solar.api.saas.service.process.calculation.ERateMatrixValuePlaceholder;

public interface MatrixValueCalculation {

    String[] getCalculatedValue(String defaultValue);

    ERateMatrixValuePlaceholder getValueType(String defaultValue);
}
