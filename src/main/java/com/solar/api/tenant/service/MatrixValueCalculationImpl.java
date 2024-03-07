package com.solar.api.tenant.service;

import com.solar.api.saas.service.process.calculation.ERateMatrixValuePlaceholder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.solar.api.saas.service.process.calculation.ERateMatrixValuePlaceholder.*;

@Service
//@Transactional("tenantTransactionManager")
public class MatrixValueCalculationImpl implements MatrixValueCalculation {

    @Override
    public String[] getCalculatedValue(String defaultValue) {
        String[] groups = new String[0];
        if (isDynamic(defaultValue)) {
            groups = DYNAMIC.getGroups(defaultValue);
        } else if (isDefault(defaultValue)) {
            groups = DEFAULT.getGroups(defaultValue);
        } else if (isDerived(defaultValue)) {
            groups = DERIVED.getGroups(defaultValue);
        } else if (isFunction(defaultValue)) {
            groups = FUNCTION.getGroups(defaultValue);
        } else if (isArithmeticExpressionElements(defaultValue)) {
            groups = ARITHMETIC_EXPRESSION_ELEMENTS.getGroups(defaultValue);
        } else if (isParenthesesContent(defaultValue)) {
            groups = PARENTHESES_CONTENT.getGroups(defaultValue);
        } else {
        }
        return groups;
    }

    @Override
    public ERateMatrixValuePlaceholder getValueType(String defaultValue) {
        if (isDynamic(defaultValue)) {
            return DYNAMIC;
        } else if (isDefault(defaultValue)) {
            return DEFAULT;
        } else if (isDerived(defaultValue)) {
            return DERIVED;
        } else if (isFunction(defaultValue)) {
            return FUNCTION;
        } else if (isArithmeticExpressionElements(defaultValue)) {
            return ARITHMETIC_EXPRESSION_ELEMENTS;
        } else if (isParenthesesContent(defaultValue)) {
            return PARENTHESES_CONTENT;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(roundUp(2.7));
        String[] result = new MatrixValueCalculationImpl().getCalculatedValue("{{ROUND_DOWN(NOW()-FDAM)}}");
        Arrays.stream(result).forEach(s -> {
            System.out.println(s);
        });
    }

    static int roundUp(Double doubl) {
        Double result = Math.ceil(doubl);
        return result.intValue();
    }
}
