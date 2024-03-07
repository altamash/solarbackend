package com.solar.api.saas.service.process.integration.graphapi;

/*import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;

import java.util.Arrays;
import java.util.List;*/

public class InteractiveBrowserCredentialExample {

    /*static void init() {
        final InteractiveBrowserCredential interactiveBrowserCredential = new InteractiveBrowserCredentialBuilder()
                .clientId("26f8385c-28d1-4751-808c-6275cc1b328e@1b10882f-6873-45a5-b4c6-c484fca221ed")
                .tenantId("1b10882f-6873-45a5-b4c6-c484fca221ed")
                .redirectUrl("http://localhost:8080/solarapi/code")
                .build();

        List<String> scopes = Arrays.asList("offline_access", "https://solarinformatics.sharepoint.com/.default");

        final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(scopes, interactiveBrowserCredential);

        final GraphServiceClient graphClient =
                GraphServiceClient
                        .builder()
                        .authenticationProvider(tokenCredentialAuthProvider)
                        .buildClient();

        final User me = graphClient.me().buildRequest().get();
    }

    public static void main(String[] a) {
        init();
    }*/
}
