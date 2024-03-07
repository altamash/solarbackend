package com.solar.api.saas.service.process.calculation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ERateMatrixValuePlaceholder {

    // Calculation priority: **, Functions(), Dynamic {{}}, Derived [[]]
    // {{}}
    DYNAMIC("^\\{\\{(.*?)\\}\\}$"),
    // **
    DEFAULT("^\\*(.*?)\\*$"),
    // [[]]
    DERIVED("^\\[\\[(.*?)\\]\\]$"),
    // FUNCTION(), FUNCTION(ARGUMENTS)
    FUNCTION(".+\\((.*?)\\)$"),
    ARITHMETIC_EXPRESSION_ELEMENTS("(?<=[-+*/])|(?=[-+*/])"),
    // (CONTENT)
    PARENTHESES_CONTENT("^\\((.*?)\\)$");

    String placeholder;
    Pattern pattern;

    ERateMatrixValuePlaceholder(String placeholder) {
        this.placeholder = placeholder;
        pattern = Pattern.compile(getPlaceholder());
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public static ERateMatrixValuePlaceholder get(String placeholder) {
        return Arrays.stream(values()).filter(value -> placeholder.equalsIgnoreCase(value.placeholder)).findFirst().orElse(null);
    }

    public static boolean isDynamic(String defaultValue) {
        return DYNAMIC.getPattern().matcher(defaultValue).find();
    }

    public static boolean isDefault(String defaultValue) {
        return DEFAULT.getPattern().matcher(defaultValue).find();
    }

    public static boolean isDerived(String defaultValue) {
        return DERIVED.getPattern().matcher(defaultValue).find();
    }

    public static boolean isFunction(String defaultValue) {
        return FUNCTION.getPattern().matcher(defaultValue).find();
    }

    public static boolean isArithmeticExpressionElements(String defaultValue) {
        return ARITHMETIC_EXPRESSION_ELEMENTS.getPattern().matcher(defaultValue).find();
    }

    public static boolean isParenthesesContent(String defaultValue) {
        return PARENTHESES_CONTENT.getPattern().matcher(defaultValue).find();
    }

    public String[] getGroups(String defaultValue) {
        if (this == ARITHMETIC_EXPRESSION_ELEMENTS) {
            return defaultValue.split(getPlaceholder());
        }
        Pattern pattern = getPattern();
        Matcher matcher = pattern.matcher(defaultValue);
        List<String> groups = new ArrayList<>();
        if (matcher.find()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                groups.add(matcher.group(i));
            }
        }
        return groups.toArray(new String[0]);
    }

}
