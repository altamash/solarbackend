package com.solar.api.saas.service.process.migration;

import com.solar.api.saas.service.process.migration.parser.vista.VistaCustomersMigrationParser;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum EMigrationParserLocation {

    VISTA_PARSER("VISTA PARSER", VistaCustomersMigrationParser.class.getPackage().getName(),
            VistaCustomersMigrationParser.class.getSimpleName());

    String name;
    String location;
    String componentName;

    EMigrationParserLocation(String name, String location, String componentName) {
        this.name = name;
        this.location = location;
        this.componentName = componentName;
    }

    public static EMigrationParserLocation getByName(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }

    public static EMigrationParserLocation
    get(String location) {
        return Arrays.stream(values()).filter(value -> location.equalsIgnoreCase(value.location)).findFirst().orElse(null);
    }
}
