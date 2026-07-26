package org.eclipse.lyo.samples.client;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.client.OAuthClient;
import net.oauth.client.OAuthResponseMessage;
import net.oauth.client.httpclient4.HttpClient4;
import net.oauth.client.httpclient4.HttpClientPool;
import org.eclipse.lyo.client.OslcClient;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth 1.0a client for the discovery sample. The stock Lyo client performs an
 * OAuth 1.0-style callback exchange; this client keeps the callback and
 * verifier
 * in the OAuth 1.0a locations so it can exercise OAuth 1.0a+j servers.
 */
public final class OAuth1aStrictClient extends OslcClient {
    private static final Logger logger = LoggerFactory.getLogger(OAuth1aStrictClient.class);

    private final OAuthAccessor accessor;
    private final String realm;

    public OAuth1aStrictClient(OAuthAccessor accessor, String realm, ClientBuilder clientBuilder) {
        super(clientBuilder);
        this.accessor = accessor;
        this.realm = realm == null || realm.isBlank() ? "Jazz" : realm;
    }

    /**
     * Starts the OAuth 1.0a dance or completes it after the authorization redirect.
     */
    public Optional<String> performOAuthNegotiation(
            String callbackUrl, String callbackToken, String callbackVerifier)
            throws IOException, OAuthException, URISyntaxException {
        if (accessor.requestToken == null) {
            logger.info(
                    "Requesting an OAuth 1.0a request token from {}.",
                    accessor.consumer.serviceProvider.requestTokenURL);
            OAuthMessage response =
                    oauthClient()
                            .getRequestTokenResponse(
                                    accessor,
                                    OAuthMessage.GET,
                                    OAuth.newList(OAuth.OAUTH_CALLBACK, callbackUrl));
            if (!"true".equals(response.getParameter(OAuth.OAUTH_CALLBACK_CONFIRMED))) {
                throw new OAuthProblemException(
                        "oauth_callback_confirmed was not returned by the server");
            }
            logger.info(
                    "OAuth 1.0a request token obtained; callback was signed into /requestToken.");
            return Optional.of(
                    accessor.consumer.serviceProvider.userAuthorizationURL
                            + "?oauth_token="
                            + OAuth.percentEncode(accessor.requestToken));
        }

        if (accessor.accessToken == null) {
            if (!accessor.requestToken.equals(callbackToken)) {
                throw new OAuthProblemException(
                        "Callback oauth_token does not match the outstanding request token");
            }
            if (callbackVerifier == null || callbackVerifier.isBlank()) {
                throw new OAuthProblemException(
                        "OAuth 1.0a callback did not include oauth_verifier");
            }

            OAuthMessage request =
                    accessor.newRequestMessage(
                            OAuthMessage.POST,
                            accessor.consumer.serviceProvider.accessTokenURL,
                            OAuth.newList(
                                    OAuth.OAUTH_TOKEN,
                                    accessor.requestToken,
                                    OAuth.OAUTH_VERIFIER,
                                    callbackVerifier));
            logger.info(
                    "Exchanging the authorized OAuth 1.0a request token at {} using an"
                            + " Authorization header.",
                    accessor.consumer.serviceProvider.accessTokenURL);
            OAuthResponseMessage response =
                    oauthClient().access(request, net.oauth.ParameterStyle.AUTHORIZATION_HEADER);
            if (response.getHttpResponse().getStatusCode() / 100 != 2) {
                String wwwAuthenticate = response.getHttpResponse().getHeader("WWW-Authenticate");
                logger.error(
                        "OAuth 1.0a access-token exchange failed: HTTP {}; WWW-Authenticate: {}",
                        response.getHttpResponse().getStatusCode(),
                        wwwAuthenticate == null ? "<absent>" : wwwAuthenticate);
                OAuthProblemException problem = response.toOAuthProblemException();
                problem.setParameter("www_authenticate", wwwAuthenticate);
                throw problem;
            }
            response.requireParameters(OAuth.OAUTH_TOKEN, OAuth.OAUTH_TOKEN_SECRET);
            accessor.accessToken = response.getParameter(OAuth.OAUTH_TOKEN);
            accessor.tokenSecret = response.getParameter(OAuth.OAUTH_TOKEN_SECRET);
            logger.info("OAuth 1.0a access token obtained successfully.");
        }
        return Optional.empty();
    }

    @Override
    public Response getResource(String url) {
        return super.getResource(url, signedHeaders(url, HttpMethod.GET));
    }

    private Map<String, String> signedHeaders(String url, String method) {
        if (accessor.accessToken == null) {
            throw new IllegalStateException("OAuth negotiation has not completed");
        }
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put(
                    "Authorization",
                    accessor.newRequestMessage(method, url, null).getAuthorizationHeader(realm));
            return headers;
        } catch (IOException | OAuthException | URISyntaxException e) {
            throw new IllegalStateException("Unable to sign OAuth request", e);
        }
    }

    private OAuthClient oauthClient() {
        return new OAuthClient(
                new HttpClient4(
                        new HttpClientPool() {
                            @Override
                            public org.apache.http.client.HttpClient getHttpClient(
                                    java.net.URL url) {
                                return ApacheConnectorProvider.getHttpClient(getClient());
                            }
                        }));
    }
}
