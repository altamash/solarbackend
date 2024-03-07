package com.solar.api.saas.service.process.integration.graphapi;

/*import com.azure.identity.OnBehalfOfCredential;
import com.azure.identity.OnBehalfOfCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;*/

public class OnBehalfOf {

    // https://login.microsoftonline.com/1b10882f-6873-45a5-b4c6-c484fca221ed/oauth2/v2.0/authorize?client_id=26f8385c-28d1-4751-808c-6275cc1b328e@1b10882f-6873-45a5-b4c6-c484fca221ed&response_type=code
    // &redirect_uri=http://localhost:8080/solarapi/code&response_mode=query&scope=offline_access%20https://graph.microsoft.com/.default&state=12345

    /*void getAuthToken() {
        final OnBehalfOfCredential onBehalfOfCredential = new OnBehalfOfCredentialBuilder()
                .clientId("26f8385c-28d1-4751-808c-6275cc1b328e@1b10882f-6873-45a5-b4c6-c484fca221ed")
                .clientSecret("kdj7Q~SCFETn2k5SQqCNbiIKTe14zFOljw_MM") // or .pemCertificate(certificatePath) or .clientSecret("ClientSecret")
                .tenantId("1b10882f-6873-45a5-b4c6-c484fca221ed")
//                .tokenCachePersistenceOptions(tokenCachePersistenceOptions) //Optional: enables the persistent token cache which is disabled by default
                .userAssertion("12345")
                .build();

        final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(Arrays.asList("offline_access", "https://solarinformatics.sharepoint.com/.default"),
                onBehalfOfCredential);

        final GraphServiceClient graphClient = GraphServiceClient
                .builder()
                .authenticationProvider(new TokenCredentialAuthProvider(Arrays.asList("offline_access", "https://solarinformatics.sharepoint.com/.default"), onBehalfOfCredential))
                .buildClient();

        final User me = graphClient.me().buildRequest().get();
    }

    public static void main(String[] args) {
        new OnBehalfOf().getAuthToken();
    }*/
}
