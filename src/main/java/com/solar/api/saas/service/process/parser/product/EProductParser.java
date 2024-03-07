package com.solar.api.saas.service.process.parser.product;

import com.solar.api.saas.service.process.parser.product.csg.ppa.postpaid.InitiatorCSGPPAPost;
import com.solar.api.saas.service.process.parser.product.csgrpre.InitiatorCSGRPre;
import com.solar.api.saas.service.process.parser.product.pwgenbill.InitiatorPWGenBill;

import java.util.Arrays;

public enum EProductParser {

    CSG_PRE_P("CSG_POST_P", InitiatorCSGF.class), // (ARR, periodic, "com.solar.api.tenant.service.process.billing.csg.csgf.InitiatorCSGF")
    BILL_CREDIT_IMPORT("BILL_CREDIT_IMPORT", BillCreditImport.class),
    CSG_POST_P("CSG_POST_P", InitiatorVOS.class), // (VOS, periodic, "com.solar.api.tenant.service.process.billing.csg.csgr.InitiatorVOS")
    BILL_CREDIT_IMPORT_VOS("BILL_CREDIT_IMPORT_VOS", BillCreditImportVOS.class),
    CSG_R_PRE("CSG_R_PRE", InitiatorCSGRPre.class),
    ROOF_POST_AR("CSG_POST_P", null),
    DG_PWG_BILL("PW_GEN_BILL", InitiatorPWGenBill.class), // (accrued)
    CSG_PPA_POST("CSG_PPA_POST", InitiatorCSGPPAPost.class);

    /*CSG_POST_P("CSG_POST_P", InitiatorVOS.class.getName()), // (VOS, periodic, "com.solar.api.tenant.service.process.billing.csg.csgr.InitiatorVOS")
    CSG_PRE_P("CSG_PRE_P", InitiatorCSGF.class.getName()), // (ARR, periodic, "com.solar.api.tenant.service.process.billing.csg.csgf.InitiatorCSGF")
    ROOF_POST_AR("CSG_POST_P", "TBD"); // (accrued)*/

    String code;
    Class clazz;

    EProductParser(String code, Class clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public String getCode() {
        return code;
    }

    public Class getClazz() {
        return clazz;
    }

    public static EProductParser get(String code) {
        return Arrays.stream(values()).filter(value -> code.equalsIgnoreCase(value.code)).findFirst().orElse(null);
    }

}
