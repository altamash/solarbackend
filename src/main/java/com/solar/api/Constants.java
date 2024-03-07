package com.solar.api;

public interface Constants {

    final class BILLING_CODES {
        public static final String ABCRE = "ABCRE";
    }

    final class DISCOUNT_RATE_CODES {
        public static final String DSC = "DSC";
        public static final String S_DSC = "S_DSC";
        public static final String DSCP = "DSCP";
    }

    final class JASPER_REPORTS {
        public static final String FILE_EXTENSION_PDF = ".pdf";
        public static final String FILE_EXTENSION_JASPER = ".jasper";
        public static final String FILE_EXTENSION_JRXML = ".jrxml";
    }

    final class SUBSCRIPTION_TERMINATION_STATUS {
        public static final String BILL_HEAD_STATUS_DISCONTINUED = "DISCONTINUED";
        public static final String CUSTOMER_SUBSCRIPTION_STATUS_ENDED = "ENDED";
        public static final String CUSTOMER_SUBSCRIPTION_MAPPING_STATUS_ROLL = "NO";
        public static final String TERMINATION_CANCEL = "CANCEL";
    }

    final class SUBSCRIPTION_TERMINATION_RATE_CODES {
        public static final String ROLL = "ROLL";
        public static final String ROLL_DT = "ROLLDT";
    }

    final class CUSTOMER_SUBSCRIPTION_STATUS {
        public static final String SCHEDULED = "SCHEDULED";
        public static final String ACTIVE = "ACTIVE";
    }

    final class TERMINATION_TYPE {
        public static final String ADHOC = "ADHOC";
        public static final String AUTO = "AUTO";
    }

    final class INVERTER_TYPES {
        public static final Long SOLAX_PRODUCTION_RESIDENTIAL = 7l;
        public static final Long SOLAX_PRODUCTION_COMMERCIAL = 8l;
        public static final Long SOLIS_POWER_RESIDENTIAL = 9l;
        public static final Long SOLIS_POWER_COMMERCIAL = 10l;
        public static final Long GOODWE_POWER_RESIDENTIAL = 12l;
        public static final Long GOODWE_POWER_COMMERCIAL = 11l;

    }

    final class MONITOR_PLATFORM {
        //used for graphs
        public static final String SOLAX = "SOLAX";
        public static final String SOLIS = "SOLIS";
        public static final String GOODWE = "GOODWE";
        public static final String EGAUGE = "EGAUGE";
        public static final String SOLRENVIEW = "SOLRENVIEW";

        public static final String TIGO = "TIGO";
        public static final String SOLAR_EDGE = "SOLAREDGE";
    }

    final class RATE_CODES {
        public static final String LATITUDE = "LAT";
        public static final String LONGITUDE = "LON";
        public static final String INVERTER_NUMBER = "INVRT";
        //        public static final String INVERTER_NUMBER_MONGO = "S_PN";
        public static final String LAST_MAINTENANCE_DT = "LMNTDT";
        public static final String MAINTENANCE_INTERVAL = "MNTID";
        public static final String DEVICE_URL = "DURL";
        public static final String ITKNID = "ITKNID";
        public static final String SN = "SN";
        public static final String LGNM = "LGNM";
        public static final String PCD = "PCD";
        public static final String EGDID = "EGDID";
        public static final String DEVICE_NUMBER = "DEVNO";
        public static final String SITEID = "STID";
        public static final String TIMEZONE = "ZN";
        public static final String SVDID = "SVDID"; //solrenview device id
        public static final String QUERY1 = "1";
        public static final String QUERY2 = "2";
        public static final String QUERYNUMBER = "queryNumber";
        public static final String QUERYNUMBER1 = "queryNo=1";
        public static final String QUERYNUMBER2 = "queryNo=2";
        public static final String START_TIME_INTERVAL = "startTimeInterval";
        public static final String END_TIME_INTERVAL = "endTimeInterval";
        public static final String SUB_START_DATE = "S_SSDT";
        public static final String MP = "MP";
        public static final String SITE_ID = "SESITE";
        public static final String SSDT = "SSDT";
        public static final String S_PN = "S_PN";
        public static final String INVRT = "INVRT";
        public static final String BRAND_SOLAX = "SOLAX";
        public static final String BRAND_SOLIS = "SOLIS";
        public static final String BRAND_GOODWE = "GOODWE";
        public static final String BRAND_SOLAR_EDGE = "SOLAREDGE";
        public static final String BRAND_ENPHASE = "ENPHASE";
        public static final String BRAND_SOLRENVEIW = "SOLRENVIEW";
        public static final String BRAND_EGAUGE = "EGAUGE";
        public static final String BRAND_TIGO = "TIGO";
        public static final String AGG = "AGG";
        public static final String STARTDATE = "startDate";
        public static final String ENDDATE = "endDate";
        public static final String SYSID = "SYSID";
        public static final String STID = "STID";
        public static final String S_AUTK = "S_AUTK";
        public static final String MONTH = "month";
        public static final String DAY = "day";
        public static final String HOUR = "hour";
        public static final String MINUTE = "minute";
        public static final String SYS_POW_BASE = "System Power Base";
        public static final String BASE = "BASE";
        public static final String TOTAL = "TOTAL";
        public static final String SYS_ENERGY_BASE = "System Energy Base";
        public static final String MONTHLY_YIELD = "monthlyYield";
        public static final String DAILY_YIELD = "dailyYield";
        public static final String DATE = "date";
        public static final String ANNUAL_YIELD = "annualYield";
        public static final String GROSS_YIELD = "grossYield";
        public static final String CURRENT_VALUE = "currentValue";
        public static final String LOCALDATETIME = "localDateTime";
        public static final String DEVICE_URL_QUERY = "deviceUrlQ";
        public static final String PROJECTION_EFFICIENCY = "PRCTNGEFF";
        public static final String PROJECTION_INDICATOR = "PRJIND";
        public static final String PROJECTION_PERIOD = "PRJPRD";
        public static final String PROJECTION_EFFICIENCY_AT_100 = "HPRJEFF";
        public static final String SECOND_FORMAT = ":00";
        public static final String S_GS = "S_GS";
        public static final String PRCTNGEFF = "PRCTNGEFF";

        public static final String GARDEN_START_DATE = "S_CSGSDT";
        public static final String PRJ_STATUS = "PRJ_STATUS";

        public static final String projectOwnerEntityRoleId = "csg_garden_owner_id";

    }


    final class REGULAR_EXP {
        public static final String VAR_BRACKET_OPEN = "\\{\\{";

        public static final String VAR_BRACKET_CLOSE = "\\}\\}";


    }

    final class PROJECT_DEPENDENCIES_RELATED_AT {
        public static final String RELATED_PROJECT = "related.project";
        public static final String RELATED_ACTIVITY = "related.activity";
        public static final String RELATED_TASK = "related.task";
    }

    final class PROJECT_DEPENDENCIES_TYPE {
        public static final String FINISH_START = "Finish-Start";
        public static final String START_START = "Start-Start";
        public static final String RELATED = "Related";
        public static final String FS = "FS";
        public static final String SS = "SS";
    }

    final class PROJECT_DEPENDENCIES_DIRECTIONS {
        public static final String FORWARD = "Forward";
        public static final String REVERSE = "Reverse";
    }

    final class GOODWE_API_HEADER_VALUES {
        public static final String client = "ios";
        public static final String version = "v2.1.0";
        public static final String language = "en";
    }

    final class PAYMENT_MODES {
        public static final String cash = "CASH";
        public static final String cheque = "CHEQUE";
        public static final String ach = "ACH";
        public static final String credit_card = "CREDIT CARD";

    }

    final class INVOICE_STATUS {
        public static final String paid_unreconciled = "PAID-UNRECONCILED";
        public static final String paid_reconciled = "PAID-RECONCILED";
    }

    final class PAYMENT_STATUS {
        public static final String paid = "PAID";
        public static final String failed = "FAILED";
        public static final String completed = "COMPLETED";
        public static final String inprogress = "IN-PROGRESS"; // user for line item that's going to reverse
        public static final String reversal = "REVERSAL"; // its against the reversed entry [ it will hve payment ref id]
        public static final String reversed = "REVERSED"; // for which we sent request of reversal
    }

    final class PAYMENT_DTL_DOC_REF_TYP {
        public static final String PAYMENT_TRANSACTION_DETAIL = "PTDETAIL";

    }

    final class MessageTypes {
        public static final String ERROR = "ERROR";
        public static final String WARNING = "WARNING";
        public static final String MESSAGE = "MESSAGE";

    }

    final class NEW_USER_CONSTANTS {
        //DOC_REF_TYP
        public static final String REGISTER_NEW_USER = "REGNEWUSER";
        public static final String PROFILE = "PROFILE_PHOTO.png";
        public static final String BUSINESS_LOGO = "BUSINESS_LOGO.png";
        public static final String CUSTOMER_TYPE = "Customer Type";
        public static final String PROFILE_PHOTO = "PROFILEPHOTO";

    }

    final class LOCATION_TYPE {
        public static final String SITE = "Site";
        public static final String BILLING = "Billing";
        public static final String MAILING = "Mailing";
        public static final String DEFAULT_ADDRESS = "Default_Address";
        public static final String EMPLOYEE_ADDRESS = "Employee_Address";

    }

    final class LOCATION_CATEGORY_CONSTANTS {
        public static final String ENTITY = "ENTITY";
        public static final String USER = "USER";
        public static final String UTILITY = "UTILITY";


    }

    final class CUSTOMER_ACQ {
        public static final String CA_USER = "CaUser";
        public static final String CA_UTILITY = "CaUtility";
        public static final String CA_DOCUMENT_SIGNED = "CaDocumentSigned";
        public static final String LEAD_TYPE = "Customer Type";
    }

    final class E_GAUGE {
        public static final String DID = "did";
        public static final String TYPE = "t";
        public static final String POWER = "P";
        public static final Integer THIRTY = 30;
        public static final Integer TWENTY_FIVE = 25;
        public static final Integer TWENTY = 20;
        public static final Integer FIFTEEN = 15;
        public static final Integer TEN = 10;
        public static final Integer FIVE = 5;
        public static final Integer ZERO = 0;
        public static final String GMT = "GMT";
        public static final String SEPARATOR = ",";
        public static final String URL_PARAM = "?T=";
    }

    final class TENANT_CONFIG_CATEGORY {
        public static final String POWER_MONITER = "PowerMoniter";
        public static final String PARAM_ISMONGOENABLED = "isMongoEnabled";
    }

    final class CONTRACT_TEMPLATE_CONSTANTS {
        public static final String CUSTOMER_ACQUISITION = "Customer Acquisition";
    }

    final class SOLAR_EDGE {
        public static final String PAGE_SIZE = "100";
        public static final String URL = "https://monitoringapi.solaredge.com/";
        public static final String SITES = "sites/list?";
        public static final String EQUIPMENT = "equipment/";
        public static final String SITE = "site/";
        public static final String METERS = "/meters";
        public static final String ENERGY = "/energy";
        public static final String OVERVIEW = "/overview";
        public static final int THOUSAND = 1000;
        public static final String SYSTEM_DATE_FORMAT = "yyyy-MM-dd";
        public static final String SYSTEM_TIME_FORMAT = "HH:mm:ss";
    }

    final class HOOK_CONSTANT {
        public static final String REQUEST_A_DEMO = "submit_request_a_demo";
        public static final String SUBMIT_INTEREST = "submit_interest";
        public static final String REGISTER_YOUR_INTEREST = "register_your_interest";
        public static final String REJECT_SIGN_UP_REQUEST = "reject_sign_up_request";

        public static final String FORGET_PASSWORD_HOOK = "forget_password";

    }

    final class MESSAGE_TEMPLATE {
        public static final String SUBMIT_REQUEST_A_DEMO_SALES = "submit_request_a_demo_sales";
        public static final String SUBMIT_REQUEST_A_DEMO_USER = "submit_request_a_demo_user";

        public static final String REGISTER_YOUR_INTEREST_USER = "Register_Your_Interest_User";
        public static final String REGISTER_YOUR_INTEREST_SALES = "Register_Your_Interest_Sales";
        public static final String REGISTER_YOUR_INTEREST_PASSWORD = "Register_Your_Interest_Password";
        public static final String SALES_EMAIL = "sales@solaramps.com";
        public static final String FACEBOOK = "/devpublic/template/email/facebook.svg";
        public static final String APPLE = "/devpublic/template/email/apple-app-store.png";
        public static final String GOOGLE_PLAY = "/devpublic/template/email/google-app-store.png";
        public static final String LINKEDIN = "/devpublic/template/email/linkedin.svg";
        public static final String YOUTUBE = "/devpublic/template/email/youtube.svg";
        public static final String TWITTER = "/devpublic/template/email/twitter.svg";
        public static final String SOLAR_AMPS = "/devpublic/template/email/new-logo.png";
        public static final Long DEMO_SALES_TEAM_EMAIL_SOURCE_ID = 2L;
        public static final Long INTEREST_SALES_TEAM_EMAIL_SOURCE_ID = 1L;
        public static final String REJECT_SIGN_UP_REQUEST = "reject_sign_up_request";

        public static final String FORGET_PASSWORD = "Forget_Password";


    }

    final class CA_TAB {
        public static final String LEAD = "LEAD";
        public static final String NEW_REQUEST = "NEW-REQUEST";//self-signup
        public static final String PROSPECT = "PROSPECT";
        public static final String COMPLETED = "COMPLETED";
        public static final String REQUEST_PENDING = "REQUEST_PENDING";
        public static final String APPROVAL_PENDING = "APPROVAL_PENDING";
        public static final String CONTRACT_PENDING = "CONTRACT_PENDING";
        public static final String CLOSED = "CLOSED";
        public static final String RESOLVED = "RESOLVED";
        public static final String DEFERRED = "DEFERRED";
    }

    final class SCHEMA {
        public static final String SAAS_SCHEMA = "saas";
    }

    final class PHYSICAL_LOCATIONS {
        public static final String STATUS_ACTIVE = "ACTIVE";
        public static final String STATUS_INACTIVE = "INACTIVE";
    }

    final class WEATHER_API {
        public static final String isWeatherApiEnabled = "1"; //true or false
        public static final String isWeatherApiEnabledParam = "isWeatherApiEnabled";
        public static final String WeatherApiKey = "WeatherApiKey";
        public static final String WeatherTemp = "WeatherTemperature";
        public static final String BrandId = "BrandIdAlert";

    }

    final class MONITORING_DASHBOARD_CONSTANTS {
        public static final String YEARLY = "YEARLY";

        public static final String MONTHLY = "MONTHLY";
        public static final String QUARTERLY = "QUARTERLY";
        public static final String DAILY = "DAILY";
        public static final String WEEKLY = "WEEKLY";

        public static final String NUMBER_OF_SITES_ALLOWED = "NumberOfSitesAllowed";

    }

    final class ALERTS {
        public static final String MONGO_SUBSCRIPTION_ID = "MONGO_SUBSCRIPTION_ID";
        public static final String MONGO_GARDEN_ID = "MONGO_GARDEN_ID";
        public static final String PRJTNM = "PRJTNM";
        public static final String PROJECTION = "PROJECTION";
        public static final String SITE_LOCATION_ID = "SITE_LOCATION_ID";
        public static final String PROJECTION_EFFICIENCY = "PROJECTION_EFFICIENCY";
        public static final String EFFICIENCY_DECREASE = "efficiency_decrease";
        public static final String GARDEN_PERFORMANCE_CRITERIA = "GardenPerformanceCriteria";

        public static final String UNDER_PERFORMANCE_CATEGORY_TEXT = "Site Performance Alert";
        public static final String OUTAGES_CATEGORY_TEXT = "Communication Alert";

        public static final String SOLARAMP_QUEUE_API = "/commons/queue/notification";
        public static final String PERFORMANCE_TENANT_CONFIG_PARAM = "PerformanceEmail";
        public static final String UNDER_PERFORMANCE_TENANT_CONFIG_PARAM = "UnderPerformanceEmail";
        public static final String WEATHER_EMAIL = "WeatherEmail";

        public static final String PERFORMANCE_EMAIL_SUBJECT = "Performance-Alert";
        public static final String UNDER_PERFORMANCE_EMAIL_SUBJECT = "Under-Performance-Alert";

        public static final String OUTAGE_TOEMAIL_TENANT_CONFIG_PARAM = "OutagesToEmail";
        public static final String WEATHER_TOEMAIL_TENANT_CONFIG_PARAM = "WeatherToEmail";
        public static final String PERFORMANCE_TOEMAIL_TENANT_CONFIG_PARAM = "PerformanceToEmail";
        public static final String UNDER_PERFORMANCE_TOEMAIL_TENANT_CONFIG_PARAM = "UnderPerformanceToEmail";
        public static final String OUTAGES_TENANT_CONFIG_PARAM = "OutagesEmail";

        public static final String TYPE = "type";
        public static final String UNDER_PERFORMANCE_TYPE_TEXT = "Low Performance";
        public static final String OUTAGES_TYPE_TEXT = "Warning";
        public static final String ALERT_DURATION = "duration";
        public static final String DESCRIPTION = "description";

        public static final String AEG = "AEG";
        public static final String MGE = "MGE";
        public static final String YGE = "YGE";

        public static final String IMPACT = "impact";
        public static final String RESOLUTION = "resolution";
        public static final String OUTAGES_RESOLUTION = "Switch on your system; If you are having issues in turning on your system please contact the support team for further assistance";
        public static final String ALERT_IMPACT = "alert_impact";
        public static final String UNDER_PERFORMANCE_DECSRIPTION_TEXT = "Your inverter is off and system is not generating any power.";
        public static final String OUTAGES_DECSRIPTION_TEXT = "Your inverter is off and the system is not generation any power";

        public static final String CRITICAL_SEVERITY = "Critical";
        public static final String OUTAGES_IMPACT = "No power Production";
        public static final String PROJECTED_PRODUCTION = "Projected_production";
        public static final String BAR1_HEIGHT = "bar1height";
        public static final String BAR2_HEIGHT = "bar2height";
        public static final String BAR1_WIDTH = "bar1width";
        public static final String BAR2_WIDTH = "bar2width";
        public static final String BAR3_WIDTH = "bar3width";
        public static final String AEG_BAR_WIDTH = "age_width";
        public static final String YGE_BAR_WIDTH = "yge_width";
        public static final String MGE_BAR_WIDTH = "mge_width";
        public static final String INTERVAL_VAL = "intervalVal";
        public static final String INTERVAL_VAL_HORIZONTAL = "intervalValHorizontal";
        public static final String ACTUAL_PRODUCTION = "Actual_production";
        public static final String REPORT_DATE = "report_date";
        public static final String PERFORMANCE = "perf";
        public static final String PERFORMANCE_INDICATOR = "perf_indicator";
        public static final String PERFORMANCE_CRITERIA = "performance_criteria";

        public static final String MP = "platform";

        public static final String GARDEN_NAME = "name";
        public static final String NOT_FOUND = "NOT FOUND";
        public static final String ACTIVE = "ACTIVE";
        public static final String SYSTEM_SIZE = "size";
        public static final String LOCATION = "loc";
//        public static final String OFFTAKER = "offtaker";

        public static final String CATEGORY = "category";
        public static final String PLATFORM_NOT_SPECIFIED = "Platform is not specified";
        public static final String SIZE_NOT_SPECIFIED = "Size is not specified";
        public static final String LOCATION_NOT_SPECIFIED = "Location is not specified";
    }

    final class PROJECTION_REVENUE {
        public static final String BILLING_CATEGORY = "Monthly Production (MPA)";
        public static final String REVENUE_CATEGORY = "Revenue Report";
        public static final String REVENUE_TYPE = "Project Estimated Revenue Report";
        public static final String DESCRIPTION = "The following report is generated based on the projected production of this garden and outlines the expected revenue for the upcoming three months.";
        public static final String PROJECTION_REVENUE_HOOK = "projection_revenue";
        public static final String INVOICE_TEMPLATE = "Project_Projection_Est_Reveune";
        public static final String EFFICIENCY = "efficiency";
        public static final String AMOUNT_1 = "amount_1";
        public static final String AMOUNT_2 = "amount_2";
        public static final String AMOUNT_3 = "amount_3";
        public static final String CODE_REF_TYPE = "Project_Estimated_Revenue_Report";
        public static final String S_GS = "S_GS";
    }

    final class MEASURES_FOR_LEAD_GENERATION {
        public static final String F_NAME = "FNAME";
        public static final String L_NAME = "LNAME";
        public static final String PHONE_NUMBER = "PHNE_NUM";
        public static final String ZIP_CODE = "ZIP_CD";
        public static final String DATE_OF_BIRTH = "DOB";
        public static final String EXECUTION_DATE = "EXE_DT";

        public static final String EMAIL = "EMAIL";
        public static final String UTILITY_PROVIDER = "UTL_PRVDR";
        public static final String LEGAL_NAME = "LEGAL_NAME";
        public static final String WEBSITE = "WEBSITE";
        public static final String CONTACT_PERSON_EMAIL = "EMAIL";
        public static final String CONTACT_PERSON_DESIGNATION = "CPDGTN";
        public static final String LEGAL_BUISNESS_NAME = "LBNAME";


    }

    final class PRIVILEGE_LEVELS {
        public static final Integer ADMIN_PRIV_LEVEL = 8;
        public static final String SUPER_ADMIN_PRIV_LEVEL = "9";
    }

    final class TENANT_CONFIG {
        public static final String VISIBLITY_OF_SOFT_CREDIT_ICON = "visiblitySoftCreditIcon";
    }

    final class PORTAL_ATTRIBUTE_VALUE {
        public static final String UTILITY_COMPANY = "Utility Company";
    }

    final class CODE {
        public static final String DEFAULT_VALUE = "default_value";
    }

    final class UPLOAD_TYPE {
        public static final String CUSTOMER = "Customer";
        public static final String PROSPECT = "Prospect";
        public static final String LEAD = "Lead";

    }

    final class USER_GROUP {
        public static final String STATUS_CLOSED = "CLOSED";

    }

    final class MESSAGE {
        public static final String PROSPECT_CONVERTED_MESSAGE = "Prospect has now been converted into a customer. Review details in customer management.";
        public static final String ALREADY_HAS_ROLE_CUSTOMER="User already has ROLE_CUSTOMER";

        public static final String ERROR_MESSAGE="Unexpected error occurred";

    }
}


