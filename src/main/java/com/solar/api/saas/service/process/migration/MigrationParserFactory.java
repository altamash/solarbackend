package com.solar.api.saas.service.process.migration;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.service.process.migration.parser.MigrationParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MigrationParserFactory {

    @Autowired
    private ApplicationContext context;

    public MigrationParser getMigrationParser(String parserLocation) {
        if (EMigrationParserLocation.get(parserLocation) == null) {
            throw new NotFoundException(MigrationParser.class, "associated parser at", parserLocation);
        }
        if (EMigrationParserLocation.get(parserLocation) == EMigrationParserLocation.VISTA_PARSER) {
            return (MigrationParser) context.getBean(EMigrationParserLocation.VISTA_PARSER.getComponentName());
        }
        return null;
    }
}
