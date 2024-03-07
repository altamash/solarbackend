package com.solar.api.saas.service.process.integration.graphapi;

public class GraphAPI {

    /*public void init() throws MalformedURLException, ExecutionException, InterruptedException {
        ClientCredentialProvider credentialProvider = new ClientCredentialProvider("26f8385c-28d1-4751-808c-6275cc1b328e@1b10882f-6873-45a5-b4c6-c484fca221ed",
                Arrays.asList("offline_access","https://solarinformatics.sharepoint.com/.default"), "JqQX2PNo9bpM0uEihUPzyrh", "1b10882f-6873-45a5-b4c6-c484fca221ed",
                NationalCloud.Global);
        CompletableFuture<String> authToken = credentialProvider.getAuthorizationTokenAsync(new URL(
                "http://localhost:8080/solarapi/code"));

        AuthorizationCodeProvider authProvider = new AuthorizationCodeProvider("26f8385c-28d1-4751-808c-6275cc1b328e@1b10882f-6873-45a5-b4c6-c484fca221ed",
                Arrays.asList("https://solarinformatics.sharepoint.com/.default"),
                authToken.get(),
                "http://localhost:8080/solarapi/code",
                NationalCloud.Global,
                "1b10882f-6873-45a5-b4c6-c484fca221ed",
                "JqQX2PNo9bpM0uEihUPzyrh");

        GraphServiceClient<Request> graphClient =
                GraphServiceClient
                        .builder()
                        .authenticationProvider(authProvider)
                        .buildClient();
        final Drive result = graphClient
                .me()
                .drive()
                .buildRequest()
                .get();
        System.out.println("Found Drive " + result.id);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, MalformedURLException {
        new GraphAPI().init();
    }*/
}
