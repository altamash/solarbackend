package com.solar.api.saas.service.process.rule;

import com.solar.api.saas.service.process.parser.product.EParserLocation;
import com.solar.api.saas.service.process.parser.product.EProductParser;
import com.solar.api.saas.service.process.parser.product.csg.ppa.postpaid.InitiatorCSGPPAPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RulesFactory {

    @Autowired
    private ApplicationContext context;

    public RulesInitiator getRulesInitiator(String code, boolean isLegacy) {
        if (isLegacy) {
            if (EParserLocation.get(code) == EParserLocation.INITIATOR_CSGF) {
                return (RulesInitiator) context.getBean("initiatorCSGF");
            } else if (EParserLocation.get(code) == EParserLocation.BILL_CREDIT_IMPORT) {
                return (RulesInitiator) context.getBean("billCreditImport");
            } else if (EParserLocation.get(code) == EParserLocation.INITIATOR_CSGR) {
                return (RulesInitiator) context.getBean("initiatorVOS");
            } else if (EParserLocation.get(code) == EParserLocation.BILL_CREDIT_IMPORT_CSGR) {
                return (RulesInitiator) context.getBean("billCreditImportVOS");
            } else if (EParserLocation.get(code) == EParserLocation.BILL_CREDIT_IMPORT_JSON) {
                return (RulesInitiator) context.getBean("billingCreditsService");
            } else if (EParserLocation.get(code) == EParserLocation.DG_PWG_BILL) {
                return (RulesInitiator) context.getBean("initiatorPWGenBill");
            } else if (EParserLocation.get(code) == EParserLocation.CSG_PPA_POST) {
                return context.getBean(InitiatorCSGPPAPost.class);
            }
            return null;
        }
        if (EProductParser.get(code) == EProductParser.CSG_PRE_P) {
            return (RulesInitiator) context.getBean(EProductParser.CSG_PRE_P.getClazz());
        } else if (EProductParser.get(code) == EProductParser.BILL_CREDIT_IMPORT) {
            return (RulesInitiator) context.getBean(EProductParser.BILL_CREDIT_IMPORT.getClazz());
        } else if (EProductParser.get(code) == EProductParser.CSG_POST_P) {
            return (RulesInitiator) context.getBean(EProductParser.CSG_POST_P.getClazz());
        } else if (EProductParser.get(code) == EProductParser.BILL_CREDIT_IMPORT_VOS) {
            return (RulesInitiator) context.getBean(EProductParser.BILL_CREDIT_IMPORT_VOS.getClazz());
        } else if (EProductParser.get(code) == EProductParser.CSG_R_PRE) {
            return (RulesInitiator) context.getBean(EProductParser.CSG_R_PRE.getClazz());
        } else if (EProductParser.get(code) == EProductParser.DG_PWG_BILL) {
            return (RulesInitiator) context.getBean(EProductParser.DG_PWG_BILL.getClazz());
        } else if (EProductParser.get(code) == EProductParser.CSG_PPA_POST) {
            return (RulesInitiator) context.getBean(EProductParser.CSG_PPA_POST.getClazz());
        }
        return null;
    }
}
