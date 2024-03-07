package com.solar.api.configuration;

// TODO: To be refactored
public class PowerBIConfig {

    // Set this to true, to show debug statements in console
    public static final boolean DEBUG = false;

    //	Two possible Authentication methods:
    //	- For authentication with master user credential choose MasterUser as AuthenticationType.
    //	- For authentication with app secret choose ServicePrincipal as AuthenticationType.
    //	More details here: https://aka.ms/EmbedServicePrincipal
    public static final String authenticationType = "ServicePrincipal";

    //	Common configuration properties for both authentication types
    // Enter workspaceId / groupId
    public static final String workspaceId = "77f174e7-788e-4fe9-9042-cf5d7c1a1ccc";

    // The id of the report to embed.
    public static final String reportId = "77abc549-4fe0-4ab7-a513-62a1e45f2186";

    // Enter Application Id / Client Id
    public static final String clientId = "142b2d5c-ce65-441a-9c61-c51c7aafdeef";

    // Enter MasterUser credentials
    public static final String pbiUsername = "report_user@solarinformatics.com";
    public static final String pbiPassword = "Report!23";

    // Enter ServicePrincipal credentials
    public static final String tenantId = "1b10882f-6873-45a5-b4c6-c484fca221ed";
    public static final String appSecret = "bYK7Q~.MhqYi3M~02rqD.52uRRb4CDOfjZFz7";
//    public static final String appSecret = "_-Cy-LE_n2z74sZT3KFL04V-SdvRj6jsvZ";

    //	DO NOT CHANGE
    public static final String authorityUrl = "https://login.microsoftonline.com/";
    public static final String scopeUrl = "https://analysis.windows.net/powerbi/api/.default";


    private PowerBIConfig() {
        //Private Constructor will prevent the instantiation of this class directly
        throw new IllegalStateException("Config class");
    }
}
