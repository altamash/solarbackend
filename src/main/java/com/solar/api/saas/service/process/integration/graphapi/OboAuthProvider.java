package com.solar.api.saas.service.process.integration.graphapi;

/*import com.google.common.hash.Hashing;
import com.microsoft.aad.msal4j.*;
import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import javax.annotation.Nonnull;
import javax.security.auth.message.AuthException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;*/

public class OboAuthProvider
//        extends BaseAuthenticationProvider
{
    /*@Value("${security.oauth2.client.authority}")
    private String authority;

//    @Value("${security.oauth2.client.client-id}")
    private String clientId = "26f8385c-28d1-4751-808c-6275cc1b328e";

//    @Value("${security.oauth2.client.client-secret}")
    private String secret = "kdj7Q~SCFETn2k5SQqCNbiIKTe14zFOljw_MM";

    @Value("${aad.graphDefaultScope}")
    private String scope = "offline_access,https://solarinformatics.sharepoint.com/.default";

    @Autowired
    CacheManager cacheManager;

    @SneakyThrows
    @NotNull
    @Override
    public CompletableFuture<String> getAuthorizationTokenAsync(@Nonnull URL url) {

        // Gets incoming access token and generates cache key. The cache key will be used to store
        // the tokens for the incoming request.
        String authToken = this.getAccessTokenFromRequest();
        String cacheKey = Hashing.sha256().hashString(authToken, StandardCharsets.UTF_8).toString();

        IAuthenticationResult authResult;
        ConfidentialClientApplication application;
        try {
            application = ConfidentialClientApplication
                    .builder(clientId, ClientCredentialFactory.createFromSecret(secret))
                    .authority(authority)
                    .build();

            String cachedTokens = cacheManager.getCache("tokens").get(cacheKey, String.class);
            if (cachedTokens != null) {
                application.tokenCache().deserialize(cachedTokens);
            }

            OnBehalfOfParameters parameters =
                    OnBehalfOfParameters.builder(Collections.singleton(scope),
                            new UserAssertion(authToken))
                            .build();
            authResult = application.acquireToken(parameters).join();

        } catch (Exception ex) {
            throw new AuthException(String.format("Error acquiring token from AAD: %s", ex.getMessage()));
        }

        cacheManager.getCache("tokens").put(cacheKey, application.tokenCache().serialize());
        return CompletableFuture.completedFuture(authResult.accessToken());
    }

    *//**
     * Retrieves the access token token included in the incoming request. This access token will
     * be exchanged for an access token to access Microsoft Graph, on behalf of the user that is
     * signed in the web application.
     *//*
    private String getAccessTokenFromRequest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String res = null;
        if (authentication != null) {
            res = ((OAuth2AuthenticationDetails) authentication.getDetails()).getTokenValue();
        }
        return res;
    }*/
}
