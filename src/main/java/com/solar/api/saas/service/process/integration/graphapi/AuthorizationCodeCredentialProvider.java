package com.solar.api.saas.service.process.integration.graphapi;

/*import com.azure.identity.AuthorizationCodeCredential;
import com.azure.identity.AuthorizationCodeCredentialBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;*/

public class AuthorizationCodeCredentialProvider {

    /*private static String getAccessToken(String authorizationCode) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/token", "1b10882f-6873-45a5-b4c6-c484fca221ed"));
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.addHeader("cache-control", "no-cache");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("client_id", "26f8385c-28d1-4751-808c-6275cc1b328e@1b10882f-6873-45a5-b4c6-c484fca221ed"));
        nvps.add(new BasicNameValuePair("client_secret", "kdj7Q~SCFETn2k5SQqCNbiIKTe14zFOljw_MM"));
        nvps.add(new BasicNameValuePair("scope", "https://graph.microsoft.com/.default"));
        nvps.add(new BasicNameValuePair("code", authorizationCode));
        nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nvps.add(new BasicNameValuePair("redirect_uri", "http://localhost:8080/solarapi/code"));
        request.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
        HttpResponse response = client.execute(request);
        final JsonNode jsonNode = new ObjectMapper().readTree(response.getEntity().getContent());
        return jsonNode.get("access_token").textValue();
    }

    private static String getAccessToken2(String authorizationCode) {

        final AuthorizationCodeCredential authCodeCredential = new AuthorizationCodeCredentialBuilder()
                .clientId("26f8385c-28d1-4751-808c-6275cc1b328e@1b10882f-6873-45a5-b4c6-c484fca221ed")
                .clientSecret("kdj7Q~SCFETn2k5SQqCNbiIKTe14zFOljw_MM") //required for web apps, do not set for native apps
                .authorizationCode(authorizationCode)
                .redirectUrl("http://localhost:8080/solarapi/code")
                .build();

        final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(Arrays.asList("offline_access", "https://solarinformatics.sharepoint.com/.default"), authCodeCredential);

        final GraphServiceClient graphClient =
                GraphServiceClient
                        .builder()
                        .authenticationProvider(tokenCredentialAuthProvider)
                        .buildClient();

        graphClient.drive();
        return null;
    }

    public static void main(String[] args) throws IOException {
        String authorizationCode = "0.AVkAL4gQG3NopUW0xsSE_KIh7Vw4-CbRKFFHgIxidcwbMo5ZANY.AQABAAIAAAD--DLA3VO7QrddgJg7WevrB3eI8505QLbFSvUBh5bZk3PQj7AszYvyZ4Sb_t7c6IFMWFTavIf6AIjCxRt1QObz_Mds9IGGaSi31vMZ_kGO3ZP0SN9u1YjrE8juAm3hnLp3dIiVpWVd-QqbZnqpMjq7drL6RDFy5utJ3Y1VgFAa6nNQipfbp-csZ1FLxvz16_A7It-tei5Wba36_XfyJed9a-K3sI45CpEHO5MYbh066n8trBFwfmo0BnoqueS5q87hq-uS9RPSzZ0YJgllwuyA764xoVKIkYXQniyqzPIZFdfdl9zoYZ7V5VB_jYOf9gWymHSPEWmKWpzOvb-YeBnj8X7M5QlsZob0WJIBjgwopbLPSRujuvehlYNNpkl5-feHR7TFpXtC5RZDGuxDJ0SdF9pIXxLpQEAFDfYkUvoVSvqA-hdIeNRHlSb26tUwCioPQllIORcKMA7VD97miZOQ77HTIQdrRRMiY02E4yvVtSY0JX8OS_U6JvTKCwNvrx4b1hp2pwEc1NZTm2EcNSckgHnAM-1X7jEPzhiS_lcGlAR4qmOReCBfxP6_7ClTStDskZJhOqTDMDRKP2lzy8N9Jtpq5lxN4EJbSKd1qpPH-XTe0nhGVzjNYdKA1CtOv0XDFqwXjBypgy52G5dYUyAfIAA";
        getAccessToken(authorizationCode);
//        new AuthorizationCodeCredentialProvider().getAccessToken("0.AVkAL4gQG3NopUW0xsSE_KIh7Vw4-CbRKFFHgIxidcwbMo5ZANY.AQABAAIAAAD--DLA3VO7QrddgJg7WevrDWpHAflVZQrvcKC4hntRfEy9jsTTiu9PLLQJUWIguUrk4GV0bv6iss8hp8Xr6PXPeu1Z40dEj3OZiEM-nM8R8dxUFXcLscyg-0KHHwr4HEnlILu-v11XO0iVIWKPRH8THYeOzryeALqOh6BHH4fLVTRT-Fo785sgkfLrAapHNCDskCirPa1aPfh5QSLuM_XZUZEkVjuKPlAFSpvTufgH-vxNHrozFoVOn0ZsE4FIKK8kIc39B1ZeOhggT7bs9Fxme8op1ex_hLKs1TPwk1i1ZE2DIj0NJVHa4xfG6fl-PruhdKUympHtWzkPR5gh11Q7GekkzFRCr3idXdnIIn9JeeCzuumSvvzLJ_s8YDD5aO1gIEvQfLQw3_RydK7gzYN1tzpOhwf_5PHY7eXCvPkHXNkRFCYdxckMf1gjUKXdQLtsJssDRqlIaLk3o7hqQy6CHUzKLb8-UhAjW8-goob8AigyPwRyAtR21BFXkY7BM8evk2TloE5DQJGwG_qCzsC1DbntjAuEyFf3XqwiY1w9NACEWYthT3wC9KWV2Q4fI2RSYQ2xKETFSZlAEFQZKoZwnTndCbqXPxYUcAIhW3kbEybYkAT0hUb1uXDZdzZ1hTKEash16LoT4bs-Ns63NowXnFUf6s0uO3WhKFvBFGtQeyAA");
    }*/
}
