package com.solar.api;

public interface AppConstants {

    String CONTENT_TYPE = "text/plain";
    String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";


    String PROFILE_DEVELOPMENT = "dev";
    String PROFILE_STAGING = "stage";
    String PROFILE_PRE_PRODUCTION = "preprod";
    String PROFILE_PRODUCTION = "prod";
    String API_DATABASE_PREFIX = "jdbc:mysql://";

    //TrueUp Reporting
    String CSGR = "CSGR";
    String CSGF = "CSGF";
    String PDF = "PDF";
    String TRUE_UP_PATH = "/report/trueups/";
    String TRUEUP = "2021TrueUp";
    String MIME_TYPE_PDF = ".pdf";
    String TRUE_UP_SUBJECT = "2021 Annual True Up for Your Community Solar Subscription with Novel Energy";

    //Batch
    Long GENERATE_BILLS_ON_ACTIVATION_ID = 1L;
    Long PUBLISH_INVOICE = 4L;
    Long ROLL_OVER_ALL = 5L;
    Long WEATHER_API_FOR_SEVEN_DAY = 47L;
    Long WEATHER_API_FOR_HOURLY_DATA_STORING_IN_DATABASE_ID = 50L;
    String WEATHER_API_FOR_HOURLY_DATA = "WEATHER_API_FOR_HOURLY_DATA_STORING_IN_DATABASE";
    Long WEATHER_API_FOR_FETCHING_SEVEN_DAY = 48L;
    Long AUTO_SUBSCRIPTION_TERMINATION_ID = 6L;
    Long ADHOC_SUBSCRIPTION_TERMINATION_ID = 7L;
    Long BILLING_CREDITS = 9L;
    Long NPVBatch = 10L;
    Long LIFETIME_CALCULATION = 11L;
    Long STAVEM_THROUGH_CSG = 12L;
    Long STAVEM_ROLES = 13L;
    Long HOURS_CALCULATION = 14L;
    Long AGING_REPORT = 15L;
    Long SCHEDULED_INVOICING_ID = 16L;
    Long ADD_MONITOR_READINGS_ID = 18L;

    Long EGAUGE_BATCH_ID = 22L;
    Long SOLRENVIEW_BATCH_ID = 34L;
    Long INDIVIDUAL_INVOICING = 20L;
    Long EMAIL_INVOICING = 21L;
    Long DISABLE_VARIANT = 24L;
    Long DATA_INGESTION = 25L;
    Long MIGRATE_DATA_INGESTION = 26L;
    Long DISABLE_PRODUCT_BATCH = 23L;
    Long DISTRIBUTION_CENTER = 816L;
    String BILLING_CREDITS_STRING = "BillingCredits";
    String STAVEM_THROUGH_CSG_STRING = "StavemThrough";
    String STAVEM_ROLES_STRING = "StavemRoles";
    String BILLING_CREDITS_IMPORT_JOB = "BILLING_CREDITS_IMPORT";
    String PROJECT_PROJECTION_REVENUE = "PROJECT_PROJECTION_REVENUE";
    String ADHOC_SUBSCRIPTION_TERMINATION = "ADHOC_SUBSCRIPTION_TERMINATION";
    String AUTO_SUBSCRIPTION_TERMINATION = "AUTO_SUBSCRIPTION_TERMINATION";
    String ADD_MONITOR_READINGS = "ADD_MONITOR_READINGS";
    String BILLING_BY_TYPE = "SCHEDULED_INVOICING";
    String ROLL_OVER = "ROLL_OVER_ALL";
    String WEATHER_API_FOR_SEVEN_DAYS = "WEATHER_API_FOR_SEVEN_DAYS";
    String WEATHER_API_FOR_FETCHING_SEVEN_DAYS_DATA = "WEATHER_API_FOR_FETCHING_SEVEN_DAYS_DATA";
    String WEATHER_API_FOR_STORING_HOURLY_DATA_IN_DATABASE = "WEATHER_API_FOR_STORING_HOURLY_DATA_IN_DATABASE";

    String NOTIFICATION_FOR_AUTO_SUBSCRIPTION_TERMINATION = "NOTIFICATION_FOR_AUTO_SUBSCRIPTION_TERMINATION";
    String INVOICE_PDF_BATCH_JOB = "INVOICE_PDF";
    String INDIVIDUAL_INVOICE = "INDIVIDUAL_INVOICE";
    String BULK_INVOICE = "BULK_INVOICE";

    String BULK_CALCULATE = "BULK_CALCULATE";
    String SOLAX_BATCH = "SOLAX_BATCH";
    String SOLIS_BATCH = "SOLIS_BATCH";
    String GOODWE_BATCH = "GOODWE_BATCH";
    String BULK_SKIP = "BULK_SKIP";
    String BULK_DISCOUNT = "BULK_DISCOUNT";
    String BULK_GENERATE_INVOICE = "BULK_GENERATE_INVOICE";
    String BULK_PUBLISH_INVOICE = "BULK_PUBLISH_INVOICE";
    String UPDATE_PROJECT_STATUS = "UPDATE_PROJECT_STATUS";
    String HK_EMAIL_QUEUE = "HK_EMAIL_QUEUE";
    String HK_EMAIL = "HK_EMAIL";

    String PENDING_BILLS_BATCH = "PENDING_BILLS";
    String ACTIVATION_BATCH = "ACTIVATION_BATCH";
    String EGAUGE_GRAPH_DATA = "EGAUGE_GRAPH_DATA";
    String DATA_INGESTION_BATCH = "DATA_INGESTION";
    String GENERATE_BILLING_CREDITS_BATCH = "BILLING_CREDITS_BATCH";
    String MIGRATE_DATA_INGESTION_BATCH = "MIGRATE_DATA_INGESTION";
    String STAVEM_THROUGH_CSG_IMPORT_JOB = "STAVEM_THROUGH_CSG_IMPORT";
    String STAVEM_ROLES_IMPORT_JOB = "STAVEM_ROLES_IMPORT";
    String STAVEM_ROLES_IMPORT = "STAVEM_ROLES_IMPORT";
    String BILLING_CREDITS_PATH = "/billing/credits/csv";
    String PROJECTION_REVENUE_PATH = "/reports/estimated_revenue_report";
    String INVOICE_REPORT_PATH = "/report/invoice/output_format/pdf";
    String PROJECT_DOCUMENT_PATH = "/project/documentation";
    String WORK_ORDER_DOCUMENT_PATH = "/workOrder/documentation";
    String PROJECT_DELETED_DOCUMENT_PATH = "/project/documentation/deleted_documents";
    String STAVEM_THROUGH_CSG_PATH = "/stavem/csv";
    String STAVEM_ROLES_PATH = "/stavem/roles/csv";
    String MIME_TYPE_CSV = ".csv";
    String CUSTOMER_SUPPORT_PATH = "/customer/support";
    String CONTRACT_FILE_PATH = "/contract";

    String WEATHER_API_URL_FOR_TWO_DAYS_HOURLY_DATA = "http://api.weatherapi.com/v1/forecast.json?q=";
    String WEATHER_API_URL_FOR_SEVEN_DAYS_DATA = "http://api.weatherapi.com/v1/forecast.json?q=";
    String SAVE_ACQUISITION_PROJECT_IN_MONGO_API = "/acquisition/saveOrUpdateAcquisitionProject";
    String UPDATE_ACQUISITION_PROJECT_IN_MONGO_API = "/acquisition/addOrUpdateAcquistionSection/%s/%s";
    String DISABLE_PROJECTION_IN_MONGO = "/product/v1/activateProjection";
    String SHOW_SECTION_CONTENT_MONGO_API = "/acquisition/showSectionContent/%s";
    String SHOW_ACQUISITION_TEMPLATE_MONGO_API = "/acquisition/showAcquisitionTemplate/%s";

    String SHOW_ACTIVITY_DETAIL_MONGO_API = "/project/showHierarchySectionDetail/%s/%s?loggedInUserAcctId={loggedInUserAcctId}";

    String BATCH_EMAIL_SUBJECT_RUNTIME = "RUNTIME EXCEPTION";
    String BATCH_EMAIL_SUBJECT_ADD_SCHEDULE = "ADD SCHEDULE EXCEPTION";
    String BATCH_EMAIL_SUBJECT_JOB_MARKED_ABANDONED = "ABANDONED JOBS";
    String BATCH_EMAIL_SUBJECT_JOB_MARKED_RESTARTED = "RESTARTED JOBS";
    String BATCH_EMAIL_SUBJECT_CRON_INVALID = "INVALID CRON EXPRESSION";
    String BATCH_EMAIL_SUBJECT_JOB_SCHEDULED = "JOB SCHEDULED";
    String BATCH_EMAIL_SUBJECT_JOB_SCHEDULE_DYNAMIC = "DYNAMIC SCHEDULE JOB";

    String PAYMENT_DETAIL_PATH = "/payment/detail";

    String PUBLISH_INVOICE_WORK_FLOW = "WorkFlow";

    /**
     * Email Constants
     */
    String TESTNA_TOEMAIL = "testna@solarinformatics.com";
    String BCC_NOVEL = "customerservice@novelenergy.biz";
    String SOLAR_TEST_EMAIL = "testna@solarinformatics.com";
    String SALES_EMAIL = "sales@solaramps.com";

    /**
     * SendGrid
     * Dynamic Email Template ID
     * True Ups
     * https://mc.sendgrid.com/dynamic-templates/d-d0e1c8b8965e4f829c2eb09686eca504/version/963930fc-f610-4221-9615
     * -27d8baffd338/editor
     */
    String TRUE_UP_ARR_EMAIL_TEMPLATE = "d-d0e1c8b8965e4f829c2eb09686eca504";
    String TEST_EMAIL_TEMPLATE = "d-6c5044ab11ad4214aa8b3840fe26988f";
    String EmailIdParamTenantConfig = "EmailTemplateId";
    String EmailSubjectParamTenantConfig = "EmailSubject";
    String EmailBCCParamTenantConfig = "EmailBCC";
    String EmailANameParamTenantConfig = "EmailAName";
    String EmailAStreetParamTenantConfig = "EmailAStreet";
    String EmailAApartmentParamTenantConfig = "EmailAApartment";
    String EmailAStateParamTenantConfig = "EmailAState";
    String PDFGeneratePowerBiDelay = "PowerBiDelay";
    String AutoCalculationScheduleTenantConfig = "Auto Calculation Schedule";
    String BillReAttemptCount = "Bill Calc Re-Attempt Count";
    String BillCalcSchedule = "Bill Calculation Schedule";
    String RemitNameParamTenantConfig = "remitName";
    String RemitAddressParamTenantConfig = "remitAddress";
    String FooterNoteParamTenantConfig = "Note";
    /**
     * SendGrid
     * Dynamic Email Template ID
     * True Ups
     * https://mc.sendgrid.com/dynamic-templates/d-c6dd7b41d9024fa8b573910c57d37f00/version/a75221e4-8004-4956-9beb
     * -f1e5e8238581/editor
     */
    String TRUE_UP_VOS_EMAIL_TEMPLATE = "d-c6dd7b41d9024fa8b573910c57d37f00";


    /**
     * EventTypes
     */
    String EMAIL_EVENT = "EMAIL";

    /**
     * EventTypes
     */
    String BATCH_EMAIL_NOTIFICATION = "m.shariq@solarinformatics.com";
    String BATCH_EMAIL_NOTIFICATION_TWO = "syed.ahmed@solarinformatics.com";
    String MONITOR_API_TO_EMAIL_NOTIFICATION = "muneeb.butt@solarinformatics.com";
    String MONITOR_API_CC_EMAIL_NOTIFICATION = "abdullah.masood@solarinformatics.com";
    String MONITOR_API_CC2_EMAIL_NOTIFICATION = "sana.siraj@solarinformatics.com";

    /**
     * SendGrid
     * Dynamic Email Template ID
     * Auto Termination Notification
     */
    String AUTO_TERMINATION_NOTIFICATION_TEMPLATE = "d-2c9783ec05204ff1a983b85f6a3d3825";

    String JOB_SUCCESS_SUBMISSION = "Your billing job request is successfully submitted. It may take several minutes " +
            "to execute depending on data.";

    /**
     * Tenant schema table overriding saas schema table must start at this value plus one
     */
    Long SAAS_RESERVED_AUTO_INCREMENT = 10000l;

    String LIFETIME_SAVINGS_CODE = "ABSAV";
    String LIFETIME_PRODUCTION_CODE = "MPA";
    Double TPLMULTIPLIER = 0.0117;
    String TPF = "TPF";

    /**
     * Scheduled Jobs
     * STATE
     */
    String QUEUED = "QUEUED";
    String PENDING = "PENDING";
    String SCHEDULED = "SCHEDULED";
    String RUNNING = "RUNNING";
    String ABANDONED = "ABANDONED";
    String RESTARTED = "RESTARTED";


    /**
     * Scheduled Jobs
     * ExecutionParameters
     */
    String STATUS_ACTIVE = "ACTIVE";
    String STATUS_INACTIVE = "INACTIVE";
    String SCHEDULED_INVOICING = "SCHEDULED_INVOICING";
    String SCHEDULING_INVOICE = "SCHEDULING_INVOICE";

    //INVOICING
    String subscriptionCode = "subscriptionCode";
    String subscriptionRateMatrixIdsCSV = "subscriptionRateMatrixIdsCSV";
    String billingMonth = "billingMonth";
    String jobName = "jobName";
    String date = "date";
    String type = "type";
    String compKey = "compKey";
    String monitorPlatform = "monitorPlatform";
    String CSG_INVOICING = "csg_invoicing";
    String BILL_INVOICE_EMAIL = "bill_invoice_email";
    String SUBMIT_INTEREST = "submit_interest";

    //POWER_MONITORING
    String subscriptionType_PW = "subscriptionType";
    String jobManagerTenantId = "jobManagerTenantId";

    /**
     * ProjectHead
     * ProjectDetail
     * Constants
     */
    String PROJECT_LEVEL = "PROJECT";
    String ACTIVITY_LEVEL = "ACTIVITY";
    String TASK_LEVEL = "TASK";
    String CREATE = "CREATE";
    String UPDATE = "UPDATE";
    String PROJECT_MANAGER = "Project Manager";


    /**
     * Billing Constants
     */
    String INVOICE_REPORT_ID = "INVRPTID";
    String SYSTEM_INVOICE_TEMPLATE = "S_INVT";
    String SYSTEM_INVOICE_HTML_TEMPLATE = "INVRPTID";
    String PAYMENT_INITIAL_MSG = "Processing....";
    /**
     * Billing Credit Constants
     */
    String MANUAL = "MANUAL";
    String POWER_MONITORING = "POWER_MONITORING";
    String PROJECTION = "PROJECTION";
    String CSV = "CSV";
    String CREDIT_CODE_TYPE_S = "S";
    /**
     * SkipFlag
     * BillingHead
     */
    Long SKIPPED = 1L;
    Long UNSKIPPED = 0L;

    /**
     * Contracts
     */
    String ACTIVE_STATUS = "ACTIVE";
    String INACTIVE_STATUS = "INACTIVE";
    String ERROR_MSG_UPGRADE_ACCT = "Please upgrade your account";


    /**
     * Calculation Tracker Constants
     */

    String REF_TYPE_VARIANT = "VARIANT";

    String variantId = "variantId";
    String productId = "productId";
    String projectionId = "projectionId";
    String PRODUCT_VARIANT = "productId-variantId";
    String VARIANT_PROJECTION = "variantId-projectionId";

    final class ProjectBGColors {
        public static final String projectDefault = "#FCF0CF";
        public static final String activityDefault = "#CFFCFA";
        public static final String taskDefault = "#EDEDED";
        public static final String onCompletion = "#E6FFDE";
    }

    //register User
    String REGISTER_NEW_USER_PATH = "/user/";
    String REGISTER_NEW_USER_PROFILE_PATH = "profile/";
    String REGISTER_NEW_USER_BUSINESS_INFO_PATH = "business/";
    String REGISTER_NEW_USER_BUSINESS_LOGO_PATH = "businessLogo/";

    String DEV_PUBLIC_CONTAINER = "devpublic";

    /**
     * WorkFlows
     * Invoice Template
     */
    String BILLING_CODE = "billing_code";
    String BILLING_CODE_VALUE = "billing_code_value";
    String HISTORY_BILLING_MONTH = "history_billing_month";
    String HISTORY_BILLING_VALUE = "history_billing_value";

    String PERCENTILE = "percentile";
    String YIELD = "yield";
    String PROD_DET_DATE = "prod_det_date";

    String ITE_BILLING_HISTORY_3 = "<div class=\"row white-backg\"><div class=\"label col-6\">{{history_billing_month}}</div><div class=\"text col-6 text-end\">{{history_billing_value}}</div></div>";
    String ITE_BILLING_HISTORY_4 = "<div class=\"row white-backg\"><div class=\"label col-6\">{{history_billing_month}}</div><div class=\"text col-6 text-end\">{{history_billing_value}}</div></div>";
    //    String ITE_BILLING_DETAILS_1 = "<div class=\"label\">{{billing_code}}</div><div class=\"text\">{{billing_code_value}}</div>";
    String ITE_BILLING_DETAILS_1 = "<div class=\"px-3 d-flex justify-content-between white-backg\"><div class=\"label\">{{billing_code}}</div><div class=\"text\">{{billing_code_value}}</div></div>";
    String ITE_MONTHLY_PRODUCTION_2 = "<div class=\"bar\" style=\"--bar-value:{{percentile}}%;\" data-name=\"{{yield}}kWh\" title=\"{{prod_det_date}}\"></div>";
    String ITE_PROJECT_PROJECTION_DETAILS_1 = "<tr><td>{{efficiency}}</td><td>{{amount_1}}</td><td>{{amount_2}}</td><td>{{amount_3}}</td></tr>";

    interface PATHS {
        String DOC_SIGNING_TEMPLATE = "/template/docSigning";
        String ENTITY_PROFILE_PICTURE = "/entityProfile";
        String ORG_LOGO = "/orgLogo";
        String COMMUNICATION_LOG = "/communicationLog";

    }

    String WORK_ORDER = "WORKORDER";
    /**
     * Tenant config parameter
     */
    String EMAIL_DOMAIN = "EmailDomain";

    String DATA_FOUND_SUCCESSFULLY = "Data found successfully";
    String DATA_SAVED_SUCCESSFULLY = "Data saved successfully";
    String DATA_NOT_FOUND = "Data not found";
    String INVALID_PARAMETER = "Invalid parameters";

    String DISABLE_JOB_SUCCESS_SUBMISSION = "Your disable product job request is successfully submitted. It may take several minutes to execute depending on data. ";
    String DISABLE_JOB_FAILURE_SUBMISSION = "Product can n't be disabled as it has Active gardens/subscriptions";

    String STRIPE_BANK_ACCOUNT = "Stripe Bank Account";
    String STRIPE_API_KEY = "StripeApiKey";
    String STRIPE_PAYMENT_INTENT_DTO = "stripePaymentIntentDTO";


    String MP_PORTAL_ATTR_NAME = "Monitoring Platform";
    String POWER_DATA_EXPORT = "Power Data Export";


    enum EZone {
        // https://time.is
        // https://en.wikipedia.org/wiki/List_of_tz_database_time_zones
        US_CENTRAL("US/Central"),     // UTC -5, America/Chicago (IANA), SolarEdge
        US_DARIEN_CT("US/Eastern"),   // UTC -4, America/New_York (IANA), Enphase
        AMERICA_NEW_YORK("America/New_York");   // UTC -4, America/New_York
        String name;

        EZone(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    String PRIMARY_INDEX_LOCATION_TRUE = "Y";
    String PRIMARY_INDEX_LOCATION_FALSE = "N";

    String DAILY = "DAILY";
    String DAY_WISE = "DAY WISE";
    String MONTHLY = "MONTHLY";

    final class MessageTemplate {
        public static final String INVOICE_TEMPLATE_MPA = "INVOICE_TEMPLATE_MPA";
    }

    String PROJECTION_IMPORT_JOB = "PROJECTION_IMPORT";
    Long PROJECTION_IMPORT = 42l;

    String PROJECTION_PATH = "/projection/csv";

    String PROJECTION_STRING = "Projection";

    String mongoSubId = "mongoSubId";

    final class FileMapperName {
        public static final String PROJECTION_YEARLY_FILE = "Projection Yearly File";
        public static final String PROJECTION_MONTHLY_FILE = "Projection Monthly File";
        public static final String PROJECTION_QUARTERLY_FILE = "Projection Quarterly File";
        public static final String PROJECTION_DAILY_FILE = "Projection Daily File";
    }

    /**
     * Manual Billing Credits
     */
    String UTILITY_CREDITS = "Utility Credit";
    String MPA = "MPA";

    final class WidgetData {
        public static final Double TRESS_PLANTED_MULTIPLIER = 0.15; // (gross yield is in (kWh) )
        public static final Double PHONE_CHARGE_MULTIPLIER = 133.0; //(gross yield is in (kWh) )
        public static final Double CO2_REDUCTION_IN_TONS_MULTIPLIER = 0.0005; //(gross yield is in (kWh) )
        public static final Double CAR_CHARGE_MULTIPLIER = 0.0444; //(gross yield is in (kWh) )
        public static final Double MILES_COVER_MULTIPLIER = 4.0; //(gross yield is in (kWh) )
        public static final Double BARRELS_COVER_MULTIPLIER = 0.000588; //(gross yield is in (kWh) )

    }
    final class OrganizationManagement {
        public static final String ORGANIZATION = "ORGANIZATION";
        public static final String ERROR = "Error";
        public static final String MASTER = "MASTER";
        public static final String SAVE = "SAVE";
        public static final String UPDATE = "UPDATE";
        public static final String MASTER_ORG_UPDATE_VALIDATION_MESSAGE = "Master org can only be updated";
        public static final String ORG_UPDATE_SUCCESS_MESSAGE = "Organization updated successfully";
        public static final String ORG_UPDATE_ERROR_MESSAGE = "Error updating organization: ";
        public static final String COMPANY_PREF_NOT_FOUND = "CompanyPreference not found with ID: ";
        public static final String ORG_NOT_FOUND = "No organization found ";
        public static final String MASTER_ORG_NOT_FOUND = "No master organization found ";
        public static final String SUB_ORG_PARENT_VALIDATION_MESSAGE = "No parent organization selected ";
        public static final String CONFIGURATION_NOT_FOUND = "No configuration found ";
        public static final String CONFIGURATION_VALIDATION_MESSAGE = "Configurations can only be added to master organization";
        public static final String CONFIGURATION_SAVE_SUCCESS_MESSAGE = "Configurations saved successfully";
        public static final String CONFIGURATION_UPDATE_SUCCESS_MESSAGE = "Configurations updated successfully";
        public static final String CONFIGURATION_UPDATE_ERROR_MESSAGE = "Error updating configurations: ";
        public static final String INVALID_REQ_TYPE = "Invalid request type";
        public static final String JSON_PARSE_ERROR = "Error parsing JSON: ";
        public static final String LANDING_PAGE_IMAGES = "LANDING_PAGE";
        public static final String MOBILE_LANDING_PAGE_IMAGES = "MOBILE_LANDING_PAGE";
        public static final String MOBILE = "MOBILE";
        public static final String LANDING = "LANDING";
        public static final String IMG_PARSE_ERROR = "Error processing images: ";
        public static final String IMAGE_SUCCESS_MESSAGE = "Images processed successfully";
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
        public static final String MASTER_ORG_DISABLE_SUCCESS_MESSAGE = "Master Org Disabled successfully";
        public static final String MASTER_ORG_DISABLE_VALIDATION_MESSAGE = "Master Org Disable validation successfully";
        public static final String MASTER_ORG_ENABLE_SUCCESS_MESSAGE = "Master Org Enabled successfully";
        public static final String SUB_ORG_DISABLE_VALIDATION_MESSAGE = "Organization Enabled validation successfully";
        public static final String SUB_ORG_ENABLE_SUCCESS_MESSAGE = "Organization Enabled successfully";
        public static final String SUB_ORG_DISABLE_SUCCESS_MESSAGE = "Organization Disabled successfully";
        public static final String ORG_SAVE_SUCCESS_MESSAGE = "Organization Save successfully";
        public static final String BUSINESS_UNIT_SAVE_SUCCESS_MESSAGE = "Business Unit Save successfully";
        public static final String BUSINESS_UNIT_UPDATE_SUCCESS_MESSAGE = "Business Unit Updated successfully";



    }
    final class WorkOrderManagement {
        public static final String WORK_ORDER_TEMPLATE = "workOrderTemplate";
        public static final String WORK_ORDER = "workOrder";
        public static final String CONTENT = "content";
        public static final String SECTIONS = "sections";
        public static final String NO_TEMPLATE_FOUND_MESSAGE = "No template found!";
        public static final String CHANNEL = "channel";
        public static final String ENTITY_ID = "entityId";
        public static final String VARIANT_ID = "variantId";
        public static final String WORK_ORDER_SUMMARY = "work_order_summary";
        public static final String NAME = "name";
        public static final String PRIORITY = "priority";
        public static final String STATUS = "status";
        public static final String TICKET_NUMBER = "ticket_number";
        public static final String TYPE = "type";
        public static final String CONTRACT_TYPE = "contract_type";
        public static final String CUSTOMER_REQUEST = "Customer Request";
        public static final String CUSTOMER_SUPPORT = "Customer Support";
        public static final String SERVICE_REQUEST = "Service Request";
        public static final String ESTIMATED_HOURS = "estimated_hours";
        public static final String PLANNED_DATE_TIME = "planned_date_time";
        public static final String NULL = "NULL";
        public static final String WORK_ORDER_NOT_FOUND_ERROR = "Error: Work order not found";
        public static final String WORK_ORDER_PROCESSING_ERROR = "Error processing work order: ";
    }
    final class Payment {
        public static final String CATEGORY = "Payment";
        public static final String DESCRIRPTION = "Customer Payment Integration";
        public static final String FORMAT = "TEXT";
        public static final String VARTYPE = "TEXT";
    }
    final class DashboardWidgets {
        public static final String ERROR = "ERROR ";
        public static final String HEAD_OFFICE = "HO";
        public static final String CUSTOMER = "CUSTOMER";
        public static final String WELCOME_TEXT = "Welcome " ;
        public static final String TREES = " Tress" ;
        public static final String TONS = " TONs" ;
        public static final String BARRELS = " Barrels" ;
        public static final String CARS = " Cars" ;
        public static final String MILES = " Miles" ;
        public static final String PHONES = " Phones" ;
        public static final String SITES = " Sites" ;
        public static final String LOCATIONS = " Locations";
        public static final String PLATFORMS = " Platforms";
    }
}
