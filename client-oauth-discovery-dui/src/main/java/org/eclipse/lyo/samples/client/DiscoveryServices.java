package org.eclipse.lyo.samples.client;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.eclipse.lyo.client.IOslcClient;
import org.eclipse.lyo.client.OSLCConstants;
import org.eclipse.lyo.client.OslcClient;
import org.eclipse.lyo.client.OslcClientBuilder;
import org.eclipse.lyo.client.OslcClientFactory;
import org.eclipse.lyo.client.OslcOAuthClient;
import org.eclipse.lyo.client.OslcOAuthClientBuilder;
import org.eclipse.lyo.client.RootServicesHelper;
import org.eclipse.lyo.client.UnderlyingHttpClient;
import org.eclipse.lyo.client.exception.ResourceNotFoundException;
import org.eclipse.lyo.oslc4j.core.model.Dialog;
import org.eclipse.lyo.oslc4j.core.model.Service;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.model.ServiceProviderCatalog;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("discovery")
public class DiscoveryServices {
    @Context private HttpServletRequest httpServletRequest;
    @Context private HttpServletResponse httpServletResponse;

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryServices.class);

    /**
     * Return a catalog, with the details (services) of each of its ServiceProviders.
     * Filter the catalog to only include SPs with a given title.
     * Filter each SP, so that only the dialogs of the provided oslcResourceType are included.
     * @param client
     * @param catalog
     * @param serviceProviderTitle
     * @param oslcResourceType
     * @return
     * @throws ResourceNotFoundException
     */
    private ServiceProviderCatalog populateServiceProviderCatalog(
            final IOslcClient client,
            final ServiceProviderCatalog catalog,
            final String serviceProviderTitle,
            final String oslcResourceType)
            throws ResourceNotFoundException {
        ServiceProviderCatalog populatedCatalog = new ServiceProviderCatalog();
        populatedCatalog.setAbout(catalog.getAbout());
        for (ServiceProvider serviceProvider : catalog.getServiceProviders()) {
            if ((StringUtils.isEmpty(serviceProviderTitle))
                    || (!StringUtils.isEmpty(serviceProvider.getTitle())
                            && serviceProvider.getTitle().equalsIgnoreCase(serviceProviderTitle))) {
                String serviceProviderUrl = serviceProvider.getAbout().toString();
                Response response = client.getResource(serviceProviderUrl);
                if (response.getStatus() != HttpStatus.SC_OK) {
                    logger.warn(
                            "Cannot read {} status: {}", serviceProviderUrl, response.getStatus());
                    throw new ResourceNotFoundException(serviceProviderUrl, "serviceProvider");
                }
                ServiceProvider sp = response.readEntity(ServiceProvider.class);
                populatedCatalog.addServiceProvider(sp);
            }
        }

        if (!StringUtils.isEmpty(oslcResourceType)) {
            for (ServiceProvider serviceProvider : populatedCatalog.getServiceProviders()) {
                for (Service service : serviceProvider.getServices()) {
                    Dialog[] selectionDialogs = lookupSelectionDialogs(service, oslcResourceType);
                    Dialog[] creationDialogs = lookupCreationDialogs(service, oslcResourceType);
                    service.setSelectionDialogs(selectionDialogs);
                    service.setCreationDialogs(creationDialogs);
                }
            }
        }

        return populatedCatalog;
    }

    public Dialog[] lookupDialogs(
            final Service service, Dialog[] dialogs, final String oslcResourceType) {
        List<Dialog> selectedDialogs = new ArrayList<Dialog>();
        for (Dialog dialog : dialogs) {
            for (URI resourceType : dialog.getResourceTypes()) {
                if (resourceType.toString() != null
                        && resourceType.toString().equals(oslcResourceType)) {
                    selectedDialogs.add(dialog);
                }
            }
        }
        return selectedDialogs.toArray(new Dialog[selectedDialogs.size()]);
    }

    public Dialog[] lookupSelectionDialogs(final Service service, final String oslcResourceType) {
        return lookupDialogs(service, service.getSelectionDialogs(), oslcResourceType);
    }

    public Dialog[] lookupCreationDialogs(final Service service, final String oslcResourceType) {
        return lookupDialogs(service, service.getCreationDialogs(), oslcResourceType);
    }

    private ClientBuilder configureClientBuilder(final boolean selfAssignedSSL)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        // Use HttpClient instead of the default HttpUrlConnection
        ApacheConnectorProvider apacheConnectorProvider = new ApacheConnectorProvider();

        ClientConfig clientConfig =
                new ClientConfig()
                        .connectorProvider(apacheConnectorProvider)
                        .property(ApacheClientProperties.DISABLE_COOKIES, false);
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        clientBuilder.withConfig(clientConfig);

        // Setup SSL support to ignore self-assigned SSL certificates - for testing only!!
        if (selfAssignedSSL) {
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE);
            clientBuilder.sslContext(sslContextBuilder.build());
            clientBuilder.hostnameVerifier(NoopHostnameVerifier.INSTANCE);
        }

        return clientBuilder;
    }

    private ClientBuilder configureClientBuilder(
            final String username, final String password, final boolean selfAssignedSSL)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        ClientBuilder clientBuilder = configureClientBuilder(selfAssignedSSL);

        // Use preemptive Basic authentication
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            HttpAuthenticationFeature authFeature =
                    HttpAuthenticationFeature.basic(username, password);
            clientBuilder.register(authFeature);
        }
        return clientBuilder;
    }

    public static void bindClientToSession(
            HttpServletRequest request, OslcOAuthClient client, String consumerKey) {
        request.getSession().setAttribute("client", client);
        request.getSession().setAttribute("consumerKey", consumerKey);
    }

    public static OslcOAuthClient getClientFromSession(
            HttpServletRequest request, String consumerKey) {
        HttpSession session = request.getSession();
        Object key = session.getAttribute("consumerKey");
        if (consumerKey.equalsIgnoreCase(String.valueOf(key))) {
            return (OslcOAuthClient) session.getAttribute("client");
        } else {
            // reset the client for a new consumer key
            session.setAttribute("client", null);
            return null;
        }
    }

    private String getCompleteUri(HttpServletRequest httpServletRequest) {
        UriBuilder uriBuilder = UriBuilder.fromUri(httpServletRequest.getRequestURL().toString());
        String queryString = httpServletRequest.getQueryString();
        if (null != queryString) {
            uriBuilder.replaceQuery(queryString);
        }
        URI uri = uriBuilder.build();
        return uri.toString();
    }

    @GET
    // @Path("root")
    @Produces(MediaType.TEXT_HTML)
    public void root(
            @QueryParam("rootServicesUrl") String rootServicesUrl,
            @QueryParam("selfAssignedSSL") String selfAssignedSSL,
            @QueryParam("consumerKey") String consumerKey,
            @QueryParam("consumerSecret") String consumerSecret,
            @QueryParam("serviceProviderTitle") String serviceProviderTitle,
            @QueryParam("oslcResourceType") String oslcResourceType)
            throws Exception {

        httpServletRequest.setAttribute("rootServicesUrl", rootServicesUrl);
        httpServletRequest.setAttribute("selfAssignedSSL", selfAssignedSSL);
        httpServletRequest.setAttribute("consumerKey", consumerKey);
        httpServletRequest.setAttribute("consumerSecret", consumerSecret);
        httpServletRequest.setAttribute("serviceProviderTitle", serviceProviderTitle);
        httpServletRequest.setAttribute("oslcResourceType", oslcResourceType);

        boolean ignoreSelfAssignedSSL = false;
        if (!StringUtils.isEmpty(selfAssignedSSL)) {
            ignoreSelfAssignedSSL = true;
        }

        if (!StringUtils.isEmpty(rootServicesUrl)) {
            // RootServicesHelper wants to add its own "rootservices" to the end of the url. Like it
            // or not.
            if (rootServicesUrl.endsWith("rootservices")) {
                rootServicesUrl =
                        rootServicesUrl.substring(
                                0,
                                rootServicesUrl.length() - String.valueOf("rootservices").length());
            }

            ClientBuilder clientBuilder = configureClientBuilder(ignoreSelfAssignedSSL);

            // Initialize a Jazz rootservices helper
            OslcClient rootServicesClient = new OslcClient(clientBuilder);
            RootServicesHelper rootService =
                    new RootServicesHelper(
                            rootServicesUrl, OSLCConstants.OSLC_RM_V2, rootServicesClient);

            // Create a new OSLC OAuth capable client
            OslcOAuthClient client = getClientFromSession(httpServletRequest, consumerKey);
            if (null == client) {
                OslcOAuthClientBuilder oAuthClientBuilder =
                        OslcClientFactory.oslcOAuthClientBuilder();
                oAuthClientBuilder.setFromRootService(rootService);
                oAuthClientBuilder.setOAuthConsumer("", consumerKey, consumerSecret);
                oAuthClientBuilder.setClientBuilder(clientBuilder);
                oAuthClientBuilder.setUnderlyingHttpClient(
                        new UnderlyingHttpClient() {
                            @Override
                            public HttpClient get(Client client) {
                                return ApacheConnectorProvider.getHttpClient(client);
                            }
                        });
                client = (OslcOAuthClient) oAuthClientBuilder.build();
                bindClientToSession(httpServletRequest, client, consumerKey);
            }

            try {
                Optional<String> performOAuthNegotiation =
                        client.performOAuthNegotiation(getCompleteUri(httpServletRequest));
                if (performOAuthNegotiation.isPresent()) {
                    httpServletResponse.sendRedirect(performOAuthNegotiation.get());
                    return;
                }
            } catch (Exception e) {
                String errorMessage = handleOAuthError(e, rootServicesUrl);
                logger.error(
                        "OAuth negotiation failed for {}: {}", rootServicesUrl, errorMessage, e);
                httpServletRequest.setAttribute("errorMessage", errorMessage);
                httpServletRequest.setAttribute("errorDetails", e.getMessage());
                RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/discovery.jsp");
                rd.forward(httpServletRequest, httpServletResponse);
                return;
            }

            // Get details about the serviceProviderCatalog
            String serviceProviderCatalogUrl = rootService.getCatalogUrl();
            Response response = null;
            try {
                response = client.getResource(serviceProviderCatalogUrl);
            } catch (IllegalStateException e) {
                try {
                    client =
                            resetClientNegotiation(
                                    httpServletRequest,
                                    rootService,
                                    consumerKey,
                                    consumerSecret,
                                    clientBuilder);
                    Optional<String> negotiation =
                            client.performOAuthNegotiation(getCompleteUri(httpServletRequest));
                    if (negotiation.isPresent()) {
                        httpServletResponse.sendRedirect(negotiation.get());
                        return;
                    } else {
                        throw new IllegalStateException(
                                "Newly initialised client cannot have negotiation passed");
                    }
                } catch (Exception retryException) {
                    String errorMessage = handleOAuthError(retryException, rootServicesUrl);
                    logger.error(
                            "OAuth re-negotiation failed for {}: {}",
                            rootServicesUrl,
                            errorMessage,
                            retryException);
                    httpServletRequest.setAttribute("errorMessage", errorMessage);
                    httpServletRequest.setAttribute("errorDetails", retryException.getMessage());
                    RequestDispatcher rd =
                            httpServletRequest.getRequestDispatcher("/discovery.jsp");
                    rd.forward(httpServletRequest, httpServletResponse);
                    return;
                }
            } catch (Exception e) {
                String errorMessage = handleServiceCatalogError(e, serviceProviderCatalogUrl);
                logger.error(
                        "Failed to retrieve service catalog from {}: {}",
                        serviceProviderCatalogUrl,
                        errorMessage,
                        e);
                httpServletRequest.setAttribute("errorMessage", errorMessage);
                httpServletRequest.setAttribute("errorDetails", e.getMessage());
                RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/discovery.jsp");
                rd.forward(httpServletRequest, httpServletResponse);
                return;
            }

            if (response.getStatus() < 200 || response.getStatus() >= 400) {
                String errorMessage =
                        String.format(
                                "Failed to retrieve service catalog from %s. HTTP Status: %d %s",
                                serviceProviderCatalogUrl,
                                response.getStatus(),
                                getHttpStatusDescription(response.getStatus()));

                // Try to get response body for additional error details
                String responseBody = "";
                try {
                    responseBody = response.readEntity(String.class);
                    if (responseBody != null && !responseBody.trim().isEmpty()) {
                        errorMessage += "\nServer response: " + responseBody;
                    }
                } catch (Exception e) {
                    logger.debug("Could not read response body", e);
                }

                logger.warn(
                        "Cannot read {} status: {} - {}",
                        serviceProviderCatalogUrl,
                        response.getStatus(),
                        responseBody);
                httpServletRequest.setAttribute("errorMessage", errorMessage);
                httpServletRequest.setAttribute(
                        "errorDetails",
                        String.format(
                                "HTTP %d from %s",
                                response.getStatus(), serviceProviderCatalogUrl));
                RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/discovery.jsp");
                rd.forward(httpServletRequest, httpServletResponse);
                return;
            }

            ServiceProviderCatalog serviceProviderCatalog =
                    response.readEntity(ServiceProviderCatalog.class);

            // Find the relevant Service Providers and Services we want to work with
            ServiceProviderCatalog fullServiceProviderCatalog =
                    populateServiceProviderCatalog(
                            client, serviceProviderCatalog, serviceProviderTitle, oslcResourceType);
            httpServletRequest.setAttribute(
                    "fullServiceProviderCatalog", fullServiceProviderCatalog);
        }

        RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/discovery.jsp");
        rd.forward(httpServletRequest, httpServletResponse);
    }

    private OslcOAuthClient resetClientNegotiation(
            HttpServletRequest request,
            RootServicesHelper rootService,
            String consumerKey,
            String consumerSecret,
            ClientBuilder clientBuilder) {
        OslcOAuthClientBuilder oAuthClientBuilder = OslcClientFactory.oslcOAuthClientBuilder();
        oAuthClientBuilder.setFromRootService(rootService);
        oAuthClientBuilder.setOAuthConsumer("", consumerKey, consumerSecret);
        oAuthClientBuilder.setClientBuilder(clientBuilder);
        oAuthClientBuilder.setUnderlyingHttpClient(
                client -> ApacheConnectorProvider.getHttpClient(client));
        OslcOAuthClient client = (OslcOAuthClient) oAuthClientBuilder.build();
        bindClientToSession(httpServletRequest, client, consumerKey);
        return client;
    }

    @GET
    @Path("rootWithBasicAuthentication")
    @Produces(MediaType.TEXT_HTML)
    public void rootWithBasicAuthentication(
            @QueryParam("rootServicesUrl") String rootServicesUrl,
            @QueryParam("selfAssignedSSL") String selfAssignedSSL,
            @QueryParam("username") String username,
            @QueryParam("password") String password,
            @QueryParam("serviceProviderTitle") String serviceProviderTitle,
            @QueryParam("oslcResourceType") String oslcResourceType)
            throws Exception {

        httpServletRequest.setAttribute("rootServicesUrl", rootServicesUrl);
        httpServletRequest.setAttribute("selfAssignedSSL", selfAssignedSSL);
        httpServletRequest.setAttribute("username", username);
        httpServletRequest.setAttribute("password", password);
        httpServletRequest.setAttribute("serviceProviderTitle", serviceProviderTitle);
        httpServletRequest.setAttribute("oslcResourceType", oslcResourceType);

        boolean ignoreSelfAssignedSSL = false;
        if (!StringUtils.isEmpty(selfAssignedSSL)) {
            ignoreSelfAssignedSSL = true;
        }

        if (!StringUtils.isEmpty(rootServicesUrl)) {
            // RootServicesHelper wants to add its own "rootservices" to the end of the url. Like it
            // or not.
            if (rootServicesUrl.endsWith("rootservices")) {
                rootServicesUrl =
                        rootServicesUrl.substring(
                                0,
                                rootServicesUrl.length() - String.valueOf("rootservices").length());
            }

            // Create a new OSLC client
            OslcClientBuilder oslcClientBuilder = OslcClientFactory.oslcClientBuilder();
            ClientBuilder clientBuilder =
                    configureClientBuilder(username, password, ignoreSelfAssignedSSL);
            oslcClientBuilder.setClientBuilder(clientBuilder);
            IOslcClient client = oslcClientBuilder.build();

            // Initialize a Jazz rootservices helper
            OslcClient rootServicesClient = new OslcClient(clientBuilder);
            RootServicesHelper rootServicesHelper =
                    new RootServicesHelper(
                            rootServicesUrl, OSLCConstants.OSLC_RM_V2, rootServicesClient);

            // Get details about the serviceProviderCatalog
            String serviceProviderCatalogUrl = rootServicesHelper.getCatalogUrl();
            Response response = null;
            try {
                response = client.getResource(serviceProviderCatalogUrl);
            } catch (Exception e) {
                String errorMessage = handleServiceCatalogError(e, serviceProviderCatalogUrl);
                logger.error(
                        "Failed to retrieve service catalog from {}: {}",
                        serviceProviderCatalogUrl,
                        errorMessage,
                        e);
                httpServletRequest.setAttribute("errorMessage", errorMessage);
                httpServletRequest.setAttribute("errorDetails", e.getMessage());
                RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/discovery.jsp");
                rd.forward(httpServletRequest, httpServletResponse);
                return;
            }

            if (response.getStatus() < 200 || response.getStatus() >= 400) {
                String errorMessage =
                        String.format(
                                "Failed to retrieve service catalog from %s. HTTP Status: %d %s",
                                serviceProviderCatalogUrl,
                                response.getStatus(),
                                getHttpStatusDescription(response.getStatus()));

                // Try to get response body for additional error details
                String responseBody = "";
                try {
                    responseBody = response.readEntity(String.class);
                    if (responseBody != null && !responseBody.trim().isEmpty()) {
                        errorMessage += "\nServer response: " + responseBody;
                    }
                } catch (Exception e) {
                    logger.debug("Could not read response body", e);
                }

                logger.warn(
                        "Cannot read {} status: {} - {}",
                        serviceProviderCatalogUrl,
                        response.getStatus(),
                        responseBody);
                httpServletRequest.setAttribute("errorMessage", errorMessage);
                httpServletRequest.setAttribute(
                        "errorDetails",
                        String.format(
                                "HTTP %d from %s",
                                response.getStatus(), serviceProviderCatalogUrl));
                RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/discovery.jsp");
                rd.forward(httpServletRequest, httpServletResponse);
                return;
            }
            ServiceProviderCatalog serviceProviderCatalog =
                    response.readEntity(ServiceProviderCatalog.class);

            // Find the relevant Service Providers and Services we want to work with
            ServiceProviderCatalog fullServiceProviderCatalog =
                    populateServiceProviderCatalog(
                            client, serviceProviderCatalog, serviceProviderTitle, oslcResourceType);
            httpServletRequest.setAttribute(
                    "fullServiceProviderCatalog", fullServiceProviderCatalog);
        }

        RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/discovery.jsp");
        rd.forward(httpServletRequest, httpServletResponse);
    }

    @GET
    @Path("catalog")
    @Produces(MediaType.TEXT_HTML)
    public void catalog(
            @QueryParam("serviceProviderCatalogUrl") String serviceProviderCatalogUrl,
            @QueryParam("selfAssignedSSL") String selfAssignedSSL,
            @QueryParam("username") String username,
            @QueryParam("password") String password,
            @QueryParam("serviceProviderTitle") String serviceProviderTitle,
            @QueryParam("oslcResourceType") String oslcResourceType)
            throws Exception {

        httpServletRequest.setAttribute("serviceProviderCatalogUrl", serviceProviderCatalogUrl);
        httpServletRequest.setAttribute("selfAssignedSSL", selfAssignedSSL);
        httpServletRequest.setAttribute("username", username);
        httpServletRequest.setAttribute("password", password);
        httpServletRequest.setAttribute("serviceProviderTitle", serviceProviderTitle);
        httpServletRequest.setAttribute("oslcResourceType", oslcResourceType);

        boolean ignoreSelfAssignedSSL = false;
        if (!StringUtils.isEmpty(selfAssignedSSL)) {
            ignoreSelfAssignedSSL = true;
        }

        if (!StringUtils.isEmpty(serviceProviderCatalogUrl)) {

            // Create a new OSLC client
            OslcClientBuilder oslcClientBuilder = OslcClientFactory.oslcClientBuilder();
            ClientBuilder clientBuilder =
                    configureClientBuilder(username, password, ignoreSelfAssignedSSL);
            oslcClientBuilder.setClientBuilder(clientBuilder);
            IOslcClient client = oslcClientBuilder.build();

            // Get details about the serviceProviderCatalog
            Response response = null;
            try {
                response = client.getResource(serviceProviderCatalogUrl);
            } catch (Exception e) {
                String errorMessage = handleServiceCatalogError(e, serviceProviderCatalogUrl);
                logger.error(
                        "Failed to retrieve service catalog from {}: {}",
                        serviceProviderCatalogUrl,
                        errorMessage,
                        e);
                httpServletRequest.setAttribute("errorMessage", errorMessage);
                httpServletRequest.setAttribute("errorDetails", e.getMessage());
                RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/discovery.jsp");
                rd.forward(httpServletRequest, httpServletResponse);
                return;
            }

            if (response.getStatus() < 200 || response.getStatus() >= 400) {
                String errorMessage =
                        String.format(
                                "Failed to retrieve service catalog from %s. HTTP Status: %d %s",
                                serviceProviderCatalogUrl,
                                response.getStatus(),
                                getHttpStatusDescription(response.getStatus()));

                // Try to get response body for additional error details
                String responseBody = "";
                try {
                    responseBody = response.readEntity(String.class);
                    if (responseBody != null && !responseBody.trim().isEmpty()) {
                        errorMessage += "\nServer response: " + responseBody;
                    }
                } catch (Exception e) {
                    logger.debug("Could not read response body", e);
                }

                logger.warn(
                        "Cannot read {} status: {} - {}",
                        serviceProviderCatalogUrl,
                        response.getStatus(),
                        responseBody);
                httpServletRequest.setAttribute("errorMessage", errorMessage);
                httpServletRequest.setAttribute(
                        "errorDetails",
                        String.format(
                                "HTTP %d from %s",
                                response.getStatus(), serviceProviderCatalogUrl));
                RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/discovery.jsp");
                rd.forward(httpServletRequest, httpServletResponse);
                return;
            }
            ServiceProviderCatalog serviceProviderCatalog =
                    response.readEntity(ServiceProviderCatalog.class);

            // Find the relevant Service Providers and Services we want to work with
            ServiceProviderCatalog fullServiceProviderCatalog =
                    populateServiceProviderCatalog(
                            client, serviceProviderCatalog, serviceProviderTitle, oslcResourceType);
            httpServletRequest.setAttribute(
                    "fullServiceProviderCatalog", fullServiceProviderCatalog);
        }

        RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/discovery.jsp");
        rd.forward(httpServletRequest, httpServletResponse);
    }

    @GET
    @Path("requestconsumerkey")
    @Produces(MediaType.TEXT_HTML)
    public void requestConsumerKey(
            @QueryParam("rootServicesUrl") String rootServicesUrl,
            @QueryParam("selfAssignedSSL") String selfAssignedSSL,
            @QueryParam("consumerName") String consumerName,
            @QueryParam("consumerSecret") String consumerSecret)
            throws Exception {

        httpServletRequest.setAttribute("rootServicesUrl", rootServicesUrl);
        httpServletRequest.setAttribute("selfAssignedSSL", selfAssignedSSL);
        httpServletRequest.setAttribute("consumerName", consumerName);
        httpServletRequest.setAttribute("consumerSecret", consumerSecret);

        boolean ignoreSelfAssignedSSL = false;
        if (!StringUtils.isEmpty(selfAssignedSSL)) {
            ignoreSelfAssignedSSL = true;
        }

        if (!StringUtils.isEmpty(rootServicesUrl)) {

            if (rootServicesUrl.endsWith("rootservices")) {
                rootServicesUrl =
                        rootServicesUrl.substring(
                                0,
                                rootServicesUrl.length() - String.valueOf("rootservices").length());
            }

            ClientBuilder clientBuilder = configureClientBuilder(ignoreSelfAssignedSSL);

            // Initialize a Jazz rootservices helper
            OslcClient rootServicesClient = new OslcClient(clientBuilder);
            RootServicesHelper rootServicesHelper =
                    new RootServicesHelper(
                            rootServicesUrl, OSLCConstants.OSLC_RM_V2, rootServicesClient);

            String consumerKey =
                    rootServicesHelper.requestConsumerKey(consumerName, consumerSecret);
            String approveKeyUrl = rootServicesHelper.getConsumerApprovalUrl(consumerKey);

            httpServletRequest.setAttribute("consumerKey", consumerKey);
            httpServletRequest.setAttribute("approveKeyUrl", approveKeyUrl);
        }

        RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/consumer.jsp");
        rd.forward(httpServletRequest, httpServletResponse);
    }

    /**
     * Handle OAuth-related errors and return a user-friendly error message
     */
    private String handleOAuthError(Exception e, String rootServicesUrl) {
        String baseMessage = "OAuth authentication failed for " + rootServicesUrl;

        if (e.getCause() != null
                && e.getCause().getClass().getSimpleName().equals("OAuthProblemException")) {
            String problemType = extractOAuthProblem(e.getCause().getMessage());
            switch (problemType) {
                case "signature_invalid":
                    return baseMessage
                            + ": Invalid OAuth signature. Please check your consumer key and"
                            + " secret.";
                case "consumer_key_unknown":
                    return baseMessage
                            + ": Unknown consumer key. Please verify your consumer key is correct"
                            + " and registered with the server.";
                case "timestamp_refused":
                    return baseMessage
                            + ": OAuth timestamp was refused. Please check your system clock is"
                            + " synchronized.";
                case "nonce_used":
                    return baseMessage
                            + ": OAuth nonce was already used. Please retry the request.";
                case "token_expired":
                    return baseMessage + ": OAuth token has expired. Please re-authenticate.";
                default:
                    return baseMessage + ": " + problemType.replace("_", " ");
            }
        } else if (e.getClass().getSimpleName().equals("ProcessingException")) {
            return baseMessage
                    + ": Network error occurred. Please check the server URL and your network"
                    + " connection.";
        } else if (e.getMessage() != null && e.getMessage().contains("HTTP")) {
            return baseMessage + ": Server communication error - " + e.getMessage();
        } else {
            return baseMessage
                    + ": "
                    + (e.getMessage() != null ? e.getMessage() : "Unknown error occurred");
        }
    }

    /**
     * Handle service catalog retrieval errors
     */
    private String handleServiceCatalogError(Exception e, String catalogUrl) {
        String baseMessage = "Failed to retrieve service catalog from " + catalogUrl;

        if (e.getClass().getSimpleName().equals("ProcessingException")) {
            return baseMessage
                    + ": Network error occurred. Please check the server URL and your network"
                    + " connection.";
        } else if (e.getMessage() != null && e.getMessage().contains("HTTP")) {
            return baseMessage + ": Server communication error - " + e.getMessage();
        } else {
            return baseMessage
                    + ": "
                    + (e.getMessage() != null ? e.getMessage() : "Unknown error occurred");
        }
    }

    /**
     * Extract OAuth problem type from exception message
     */
    private String extractOAuthProblem(String message) {
        if (message == null) return "unknown_error";

        // Look for oauth_problem= in the message
        int problemStart = message.indexOf("oauth_problem=");
        if (problemStart >= 0) {
            problemStart += "oauth_problem=".length();
            int problemEnd = message.indexOf(",", problemStart);
            if (problemEnd < 0) problemEnd = message.indexOf("&", problemStart);
            if (problemEnd < 0) problemEnd = message.length();

            String problem = message.substring(problemStart, problemEnd).trim();
            // Remove quotes if present
            if (problem.startsWith("\"") && problem.endsWith("\"")) {
                problem = problem.substring(1, problem.length() - 1);
            }
            return problem;
        }

        return "unknown_error";
    }

    /**
     * Get human-readable description for HTTP status codes
     */
    private String getHttpStatusDescription(int statusCode) {
        switch (statusCode) {
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized - Authentication required";
            case 403:
                return "Forbidden - Access denied";
            case 404:
                return "Not Found - Resource does not exist";
            case 405:
                return "Method Not Allowed";
            case 408:
                return "Request Timeout";
            case 409:
                return "Conflict";
            case 410:
                return "Gone - Resource no longer available";
            case 429:
                return "Too Many Requests - Rate limit exceeded";
            case 500:
                return "Internal Server Error";
            case 502:
                return "Bad Gateway";
            case 503:
                return "Service Unavailable";
            case 504:
                return "Gateway Timeout";
            default:
                return statusCode >= 400 && statusCode < 500
                        ? "Client Error"
                        : statusCode >= 500 ? "Server Error" : "Unknown Status";
        }
    }
}
