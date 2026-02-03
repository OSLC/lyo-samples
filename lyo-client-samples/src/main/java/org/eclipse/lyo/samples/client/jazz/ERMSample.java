/*
 * Copyright (c) 2012-2022 IBM Corporation and contributors.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *  Contributors:
 *
 *     Michael Fiedler     - initial API and implementation
 *     Gabriel Ruelas      - Fix handling of Rich text, include parsing extended properties
 */
package org.eclipse.lyo.samples.client.jazz;

import static org.eclipse.lyo.samples.client.oslc4j.RMSample.printRequirementInfo;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.extern.slf4j.Slf4j;
import net.oauth.OAuthException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.eclipse.lyo.client.JEEFormAuthenticator;
import org.eclipse.lyo.client.OSLCConstants;
import org.eclipse.lyo.client.OslcClient;
import org.eclipse.lyo.client.RootServicesHelper;
import org.eclipse.lyo.client.exception.ResourceNotFoundException;
import org.eclipse.lyo.client.exception.RootServicesException;
import org.eclipse.lyo.client.query.OslcQuery;
import org.eclipse.lyo.client.query.OslcQueryParameters;
import org.eclipse.lyo.client.query.OslcQueryResult;
import org.eclipse.lyo.client.resources.RmUtil;
import org.eclipse.lyo.oslc.domains.rm.Requirement;
import org.eclipse.lyo.oslc.domains.rm.RequirementCollection;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import org.eclipse.lyo.oslc4j.core.model.*;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Samples of logging in to IBM Enterprise Requirements Manager and running OSLC operations
 *
 * <p>- run an OLSC Requirement query and retrieve OSLC Requirements and de-serialize them as Java objects - TODO: Add
 * more requirement sample scenarios
 */
@Slf4j
public class ERMSample {

    // Constants not present in standard oslc-domains or need replacement
    // RmConstants were in oslc-java-client-resources.
    // JAZZ_RM_NAMESPACE = "http://jazz.net/xmlns/prod/jazz/rm/1.0/"
    // PROPERTY_PRIMARY_TEXT = new QName(JAZZ_RM_NAMESPACE, "primaryText")
    // PROPERTY_PARENT_FOLDER = new QName(JAZZ_RM_NAMESPACE, "parent")
    // NAMESPACE_URI_XHTML = "http://www.w3.org/1999/xhtml"

    public static final String JAZZ_RM_NAMESPACE = "http://jazz.net/ns/rm#";
    public static final QName PROPERTY_PRIMARY_TEXT = new QName(JAZZ_RM_NAMESPACE, "primaryText");
    public static final QName PROPERTY_PARENT_FOLDER = new QName(JAZZ_RM_NAMESPACE, "parent");
    public static final String NAMESPACE_URI_XHTML = "http://www.w3.org/1999/xhtml";

    // Following is a workaround for primaryText issue in DNG ( it is PrimaryText instead of
    // primaryText
    private static final QName PROPERTY_PRIMARY_TEXT_WORKAROUND = new QName(JAZZ_RM_NAMESPACE, "PrimaryText");

    @lombok.Data
    public static class Report {
        private String req01Url;
        private String req02Url;
        private String req03Url;
        private String req04Url;
        private String reqColl01Url;
        private int scenario01Count;
        private int scenario02Count;
        private int scenario03Count;
        private int scenario04Count;
        private int scenario05Count;
        private int scenario06Count;
        private int scenario07Count;
        private int scenario08Count;
    }

    /**
     * Login to the ERM server and perform some OSLC actions
     *
     * @param args
     * @throws ParseException
     */
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("url", true, "url");
        options.addOption("user", true, "user ID");
        options.addOption("password", true, "password");
        options.addOption("project", true, "project area");
        options.addOption("b", "basic", false, "Use Basic auth (use if JAS is enabled)");

        CommandLineParser cliParser = new GnuParser();
        CommandLine cmd = cliParser.parse(options, args);

        if (!validateOptions(cmd)) {
            log.error("Syntax:  java <class_name> -url https://<server>:port/<context>/ -user <user>"
                    + " -password <password> -project \"<project_area>\" [--basic]");
            log.error("Example: java ERMSample -url https://exmple.com:9443/rm -user ADMIN -password"
                    + " ADMIN -project \"JKE Banking (Requirements Management)\"");
            log.error("Example: java ERMSample -url https://jazz.net.example.com/sandbox02-rm/ -user"
                    + " ADMIN -password ADMIN -project \"JKE Banking (Requirements" + " Management)\" --basic");
            return;
        }

        String webContextUrl = cmd.getOptionValue("url");
        String userId = cmd.getOptionValue("user");
        String password = cmd.getOptionValue("password");
        String projectArea = cmd.getOptionValue("project");
        boolean useBasicAuth = cmd.hasOption("basic");

        try {
            run(webContextUrl, userId, password, projectArea, useBasicAuth);
        } catch (RootServicesException re) {
            log.error("Unable to access the Jazz rootservices document at: {}/rootservices", webContextUrl, re);
            if (Boolean.getBoolean("lyo.test.mode")) {
                throw new RuntimeException(re);
            }
            System.exit(1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (Boolean.getBoolean("lyo.test.mode")) {
                throw new RuntimeException(e);
            }
            System.exit(1);
        }
    }

    public static Report run(
            String webContextUrl, String userId, String password, String projectArea, boolean useBasicAuth)
            throws Exception {
        Report report = new Report();
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        if (Boolean.getBoolean("lyo.record.fixtures")) {
            clientConfig.register(new FixtureRecorderFilter());
        }
        clientConfig.property(
                ApacheClientProperties.REQUEST_CONFIG,
                RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());
        clientConfig.register(MultiPartFeature.class);

        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        if (useBasicAuth) {
            clientConfig.register(HttpAuthenticationFeature.basic(userId, password));
            log.info("Using Basic authentication");
        }
        clientBuilder.withConfig(clientConfig);

        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        sslContextBuilder.loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE);
        clientBuilder.sslContext(sslContextBuilder.build());
        clientBuilder.hostnameVerifier(NoopHostnameVerifier.INSTANCE);

        if (!useBasicAuth) {
            clientBuilder.register(new JEEFormAuthenticator(webContextUrl, userId, password));
            log.info("Using JAS (Forms) authentication");
        }

        OslcClient client = new OslcClient(clientBuilder);
        String catalogUrl = new RootServicesHelper(webContextUrl, OSLCConstants.OSLC_RM_V2, client).getCatalogUrl();
        String serviceProviderUrl = client.lookupServiceProviderUrl(catalogUrl, projectArea);
        String queryCapability = client.lookupQueryCapability(
                serviceProviderUrl, OSLCConstants.OSLC_RM_V2, OSLCConstants.RM_REQUIREMENT_TYPE);
        String requirementFactory = client.lookupCreationFactory(
                serviceProviderUrl, OSLCConstants.OSLC_RM_V2, OSLCConstants.RM_REQUIREMENT_TYPE);

        ResourceShape featureInstanceShape = null;
        ResourceShape collectionInstanceShape = null;
        try {
            try {
                featureInstanceShape = lookupRequirementsInstanceShapes(
                        serviceProviderUrl,
                        OSLCConstants.OSLC_RM_V2,
                        OSLCConstants.RM_REQUIREMENT_TYPE,
                        client,
                        "Feature",
                        null);
            } catch (IOException | URISyntaxException | OAuthException e) {
                throw e;
            } catch (ResourceNotFoundException e) {
                featureInstanceShape = lookupRequirementsInstanceShapes(
                        serviceProviderUrl,
                        OSLCConstants.OSLC_RM_V2,
                        OSLCConstants.RM_REQUIREMENT_TYPE,
                        client,
                        "User Requirement",
                        null);
            }

            collectionInstanceShape = RmUtil.lookupRequirementsInstanceShapes(
                    serviceProviderUrl,
                    OSLCConstants.OSLC_RM_V2,
                    OSLCConstants.RM_REQUIREMENT_COLLECTION_TYPE,
                    client,
                    "Collection");

            List<ResourceShape> shapes = new ArrayList<>();
            shapes.add(featureInstanceShape);
            shapes.add(collectionInstanceShape);
            OSLC4JUtils.setShapes(shapes);
            OSLC4JUtils.setInferTypeFromShape("true");
        } catch (IOException | URISyntaxException | OAuthException e) {
            throw new RuntimeException(e);
        } catch (ResourceNotFoundException e) {
            log.warn("OSLC Server does not provide Collection and Feature (or User Requirement) instance shapes");
            log.debug("Exception", e);
        }

        Requirement requirement = null;
        RequirementCollection collection = null;
        URI rootFolder = null;

        String req01URL = null;
        String req02URL = null;
        String req03URL = null;
        String req04URL = null;
        String reqcoll01URL = null;

        String primaryText = null;
        if (requirementFactory != null) {
            if (featureInstanceShape == null) {
                log.warn("Cannot create resources without access to shapes, skipping");
            } else {
                requirement = new Requirement();
                requirement.setInstanceShape(Collections.singleton(new Link(featureInstanceShape.getAbout())));
                requirement.setTitle("Req01");

                primaryText = "My Primary Text";
                org.w3c.dom.Element obj = convertStringToHTML(primaryText);
                requirement.getExtendedProperties().put(PROPERTY_PRIMARY_TEXT, obj);

                requirement.setDescription("Created By EclipseLyo");
                requirement.addImplementedBy(new Link(new URI("http://google.com"), "Link in REQ01"));
                try (Response creationResponse = client.createResource(
                        requirementFactory,
                        requirement,
                        OslcMediaType.APPLICATION_RDF_XML,
                        OslcMediaType.APPLICATION_RDF_XML)) {
                    if (creationResponse.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
                        throw new IllegalStateException("Server is refusing the requests on security grounds.");
                    }
                    req01URL = creationResponse.getStringHeaders().getFirst(HttpHeaders.LOCATION);
                    report.setReq01Url(relativize(req01URL, webContextUrl));
                }

                requirement = new Requirement();
                requirement.setInstanceShape(Collections.singleton(new Link(featureInstanceShape.getAbout())));
                requirement.setTitle("Req02");
                requirement.setDescription("Created By EclipseLyo");
                requirement.addValidatedBy(new Link(new URI("http://bancomer.com"), "Link in REQ02"));
                try (Response creationResponse = client.createResource(
                        requirementFactory,
                        requirement,
                        OslcMediaType.APPLICATION_RDF_XML,
                        OslcMediaType.APPLICATION_RDF_XML)) {
                    if (creationResponse.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
                        throw new IllegalStateException("Server is refusing the requests on security grounds.");
                    }
                    req02URL = creationResponse.getStringHeaders().getFirst(HttpHeaders.LOCATION);
                    report.setReq02Url(relativize(req02URL, webContextUrl));
                }

                requirement = new Requirement();
                requirement.setInstanceShape(Collections.singleton(new Link(featureInstanceShape.getAbout())));
                requirement.setTitle("Req03");
                requirement.setDescription("Created By EclipseLyo");
                requirement.addValidatedBy(new Link(new URI("http://outlook.com"), "Link in REQ03"));
                try (Response creationResponse = client.createResource(
                        requirementFactory,
                        requirement,
                        OslcMediaType.APPLICATION_RDF_XML,
                        OslcMediaType.APPLICATION_RDF_XML)) {
                    if (creationResponse.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
                        throw new IllegalStateException("Server is refusing the requests on security grounds.");
                    }
                    req03URL = creationResponse.getStringHeaders().getFirst(HttpHeaders.LOCATION);
                    report.setReq03Url(relativize(req03URL, webContextUrl));
                }

                requirement = new Requirement();
                requirement.setInstanceShape(Collections.singleton(new Link(featureInstanceShape.getAbout())));
                requirement.setTitle("Req04");
                requirement.setDescription("Created By EclipseLyo");
                try (Response creationResponse = client.createResource(
                        requirementFactory,
                        requirement,
                        OslcMediaType.APPLICATION_RDF_XML,
                        OslcMediaType.APPLICATION_RDF_XML)) {
                    if (creationResponse.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
                        throw new IllegalStateException("Server is refusing the requests on security grounds.");
                    }
                    req04URL = creationResponse.getStringHeaders().getFirst(HttpHeaders.LOCATION);
                    report.setReq04Url(relativize(req04URL, webContextUrl));
                }

                collection = new RequirementCollection();
                collection.addUses(new Link(new URI(req03URL)));
                collection.addUses(new Link(new URI(req04URL)));
                if (collectionInstanceShape != null) {
                    collection.setInstanceShape(Collections.singleton(new Link(collectionInstanceShape.getAbout())));
                }
                collection.setTitle("Collection01");
                collection.setDescription("Created By EclipseLyo");
                try (Response creationResponse = client.createResource(
                        requirementFactory,
                        collection,
                        OslcMediaType.APPLICATION_RDF_XML,
                        OslcMediaType.APPLICATION_RDF_XML)) {
                    if (creationResponse.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
                        throw new IllegalStateException("Server is refusing the requests on security grounds.");
                    }
                    reqcoll01URL = creationResponse.getStringHeaders().getFirst(HttpHeaders.LOCATION);
                    report.setReqColl01Url(relativize(reqcoll01URL, webContextUrl));
                }

                if (req01URL == null
                        || req02URL == null
                        || req03URL == null
                        || req04URL == null
                        || reqcoll01URL == null) {
                    throw new Exception("Failed to create an artifact");
                }
            }
        }

        Response getResponse = client.getResource(req01URL, OslcMediaType.APPLICATION_RDF_XML);
        requirement = getResponse.readEntity(Requirement.class);

        Map<QName, Object> requestExtProperties = requirement.getExtendedProperties();
        for (QName qname : requestExtProperties.keySet()) {
            Property attr = featureInstanceShape.getProperty(new URI(qname.getNamespaceURI() + qname.getLocalPart()));
            String name = null;
            if (attr != null) {
                name = attr.getTitle();
                if (name != null) {
                    System.out.println(
                            name + " = " + requirement.getExtendedProperties().get(qname));
                }
            }
        }

        rootFolder = (URI) requirement.getExtendedProperties().get(PROPERTY_PARENT_FOLDER);
        Object changedPrimaryText = (Object) requirement.getExtendedProperties().get(PROPERTY_PRIMARY_TEXT);
        if (changedPrimaryText == null) {
            changedPrimaryText = (Object) requirement.getExtendedProperties().get(PROPERTY_PRIMARY_TEXT_WORKAROUND);
        }
        String primarytextString = null;
        if (changedPrimaryText != null) {
            primarytextString = changedPrimaryText.toString();
        }

        if ((primarytextString != null) && (!primarytextString.contains(primaryText))) {
            log.error("Error getting primary Text");
        }

        OslcQueryParameters queryParams = new OslcQueryParameters();
        queryParams.setPrefix("rdf=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        queryParams.setWhere("rdf:type=<http://open-services.net/ns/rm#Requirement>");
        OslcQuery query = new OslcQuery(client, queryCapability, 10, queryParams);
        OslcQueryResult result = query.submit();
        boolean processAsJavaObjects = false;
        int resultsSize = result.getMembersUrls().length;
        report.setScenario01Count(resultsSize);
        processPagedQueryResults(result, client, processAsJavaObjects);
        System.out.println("\n------------------------------\n");
        System.out.println("Number of Results for SCENARIO 01 = " + resultsSize + "\n");

        queryParams = new OslcQueryParameters();
        queryParams.setPrefix("nav=<http://com.ibm.rdm/navigation#>,rdf=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        queryParams.setWhere(
                "rdf:type=<http://open-services.net/ns/rm#Requirement> and nav:parent=<" + rootFolder + ">");
        query = new OslcQuery(client, queryCapability, 10, queryParams);
        result = query.submit();
        processAsJavaObjects = false;
        resultsSize = result.getMembersUrls().length;
        report.setScenario02Count(resultsSize);
        processPagedQueryResults(result, client, processAsJavaObjects);
        System.out.println("\n------------------------------\n");
        System.out.println("Number of Results for SCENARIO 02 = " + resultsSize + "\n");

        queryParams = new OslcQueryParameters();
        queryParams.setPrefix("dcterms=<http://purl.org/dc/terms/>");
        queryParams.setWhere("dcterms:title=\"Req04\"");
        query = new OslcQuery(client, queryCapability, 10, queryParams);
        result = query.submit();
        resultsSize = result.getMembersUrls().length;
        report.setScenario03Count(resultsSize);
        processAsJavaObjects = false;
        processPagedQueryResults(result, client, processAsJavaObjects);
        System.out.println("\n------------------------------\n");
        System.out.println("Number of Results for SCENARIO 03 = " + resultsSize + "\n");

        queryParams = new OslcQueryParameters();
        queryParams.setPrefix("oslc_rm=<http://open-services.net/ns/rm#>");
        queryParams.setWhere("oslc_rm:implementedBy=<http://google.com>");
        query = new OslcQuery(client, queryCapability, 10, queryParams);
        result = query.submit();
        resultsSize = result.getMembersUrls().length;
        report.setScenario04Count(resultsSize);
        processAsJavaObjects = false;
        processPagedQueryResults(result, client, processAsJavaObjects);
        System.out.println("\n------------------------------\n");
        System.out.println("Number of Results for SCENARIO 04 = " + resultsSize + "\n");

        queryParams = new OslcQueryParameters();
        queryParams.setPrefix("oslc_rm=<http://open-services.net/ns/rm#>");
        queryParams.setWhere("oslc_rm:validatedBy in [<http://bancomer.com>,<http://outlook.com>]");
        query = new OslcQuery(client, queryCapability, 10, queryParams);
        result = query.submit();
        resultsSize = result.getMembersUrls().length;
        report.setScenario05Count(resultsSize);
        processAsJavaObjects = false;
        processPagedQueryResults(result, client, processAsJavaObjects);
        System.out.println("\n------------------------------\n");
        System.out.println("Number of Results for SCENARIO 05 = " + resultsSize + "\n");

        queryParams = new OslcQueryParameters();
        queryParams.setPrefix("nav=<http://com.ibm.rdm/navigation#>,oslc_rm=<http://open-services.net/ns/rm#>");
        queryParams.setWhere("nav:parent=<" + rootFolder + "> and oslc_rm:validatedBy=<http://bancomer.com>");
        query = new OslcQuery(client, queryCapability, 10, queryParams);
        result = query.submit();
        resultsSize = result.getMembersUrls().length;
        report.setScenario06Count(resultsSize);
        processAsJavaObjects = false;
        processPagedQueryResults(result, client, processAsJavaObjects);
        System.out.println("\n------------------------------\n");
        System.out.println("Number of Results for SCENARIO 06 = " + resultsSize + "\n");

        getResponse = client.getResource(req03URL, OslcMediaType.APPLICATION_RDF_XML);
        requirement = getResponse.readEntity(Requirement.class);
        String etag = getResponse.getStringHeaders().getFirst(OSLCConstants.ETAG);
        requirement.setTitle("My new Title");
        requirement.addImplementedBy(new Link(new URI("http://google.com"), "Link created by an Eclipse Lyo user"));

        Response updateResponse = client.updateResource(
                req03URL, requirement, OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_RDF_XML, etag);
        updateResponse.readEntity(String.class);

        queryParams = new OslcQueryParameters();
        queryParams.setPrefix("dcterms=<http://purl.org/dc/terms/>");
        queryParams.setWhere("dcterms:title=\"My new Title\"");
        query = new OslcQuery(client, queryCapability, 10, queryParams);
        result = query.submit();
        resultsSize = result.getMembersUrls().length;
        report.setScenario07Count(resultsSize);
        processAsJavaObjects = false;
        processPagedQueryResults(result, client, processAsJavaObjects);
        System.out.println("\n------------------------------\n");
        System.out.println("Number of Results for SCENARIO 07 = " + resultsSize + "\n");

        queryParams = new OslcQueryParameters();
        queryParams.setPrefix("oslc_rm=<http://open-services.net/ns/rm#>");
        queryParams.setWhere("oslc_rm:implementedBy=<http://google.com>");
        query = new OslcQuery(client, queryCapability, 10, queryParams);
        result = query.submit();
        resultsSize = result.getMembersUrls().length;
        report.setScenario08Count(resultsSize);
        processAsJavaObjects = false;
        processPagedQueryResults(result, client, processAsJavaObjects);
        System.out.println("\n------------------------------\n");
        System.out.println("Number of Results for SCENARIO 08 = " + resultsSize + "\n");
        return report;
    }

    private static String relativize(String url, String webContextUrl) {
        if (url == null || webContextUrl == null) return url;
        if (url.startsWith(webContextUrl)) {
            return url.substring(webContextUrl.length());
        }
        String base = webContextUrl.endsWith("/") ? webContextUrl : webContextUrl + "/";
        if (url.startsWith(base)) {
            return url.substring(base.length());
        }
        return url;
    }

    private static Element convertStringToHTML(String primaryText) {
        try {
            Document document =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element divElement = document.createElementNS(NAMESPACE_URI_XHTML, "div");
            divElement.setTextContent(primaryText);
            return divElement;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void processPagedQueryResults(OslcQueryResult result, OslcClient client, boolean asJavaObjects) {
        int page = 1;
        do {
            System.out.println("\nPage " + page + ":\n");
            processCurrentPage(result, client, asJavaObjects);
            if (result.hasNext() && page < 5) {
                result = result.next();
                page++;
            } else {
                break;
            }
        } while (true);
    }

    private static void processCurrentPage(OslcQueryResult result, OslcClient client, boolean asJavaObjects) {
        for (String resultsUrl : result.getMembersUrls()) {
            System.out.println(resultsUrl);

            try (Response response = client.getResource(resultsUrl, OSLCConstants.CT_RDF)) {
                if (response != null) {
                    response.bufferEntity();
                    if (asJavaObjects) {
                        Requirement req = response.readEntity(Requirement.class);
                        printRequirementInfo(req);
                    } else {
                        processRawResponse(response);
                    }
                }
            } catch (Exception e) {
                log.error("Unable to process artfiact at url: {}", resultsUrl, e);
            }
        }
    }

    private static void processRawResponse(Response response) throws IOException {
        InputStream is = response.readEntity(InputStream.class);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        String line = null;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println();
        response.readEntity(String.class);
    }

    private static boolean validateOptions(CommandLine cmd) {
        boolean isValid = true;
        if (!(cmd.hasOption("url") && cmd.hasOption("user") && cmd.hasOption("password") && cmd.hasOption("project"))) {
            isValid = false;
        }
        return isValid;
    }

    public static ResourceShape lookupRequirementsInstanceShapes(
            final String serviceProviderUrl,
            final String oslcDomain,
            final String oslcResourceType,
            OslcClient client,
            String requiredInstanceShape,
            String configurationContext)
            throws IOException, URISyntaxException, ResourceNotFoundException, OAuthException {
        List<ResourceShape> shapes = new ArrayList<>();
        Response response = client.getResource(serviceProviderUrl, null, OSLCConstants.CT_RDF, configurationContext);
        ServiceProvider serviceProvider = response.readEntity(ServiceProvider.class);
        if (serviceProvider != null) {
            for (Service service : serviceProvider.getServices()) {
                URI domain = service.getDomain();
                if (domain != null && domain.toString().equals(oslcDomain)) {
                    CreationFactory[] creationFactories = service.getCreationFactories();
                    if (creationFactories != null && creationFactories.length > 0) {
                        for (CreationFactory creationFactory : creationFactories) {
                            for (URI resourceType : creationFactory.getResourceTypes()) {
                                if (resourceType.toString() != null
                                        && resourceType.toString().equals(oslcResourceType)) {
                                    URI[] instanceShapes = creationFactory.getResourceShapes();
                                    if (instanceShapes != null) {
                                        for (URI typeURI : instanceShapes) {
                                            response = client.getResource(
                                                    typeURI.toString(),
                                                    null,
                                                    OSLCConstants.CT_RDF,
                                                    configurationContext);
                                            ResourceShape resourceShape = response.readEntity(ResourceShape.class);
                                            if (Arrays.stream(resourceShape.getDescribes())
                                                    .anyMatch(uri -> oslcResourceType.equals(uri.toString()))) {
                                                if (requiredInstanceShape.equals(resourceShape.getTitle())) {
                                                    shapes.add(resourceShape);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (shapes.size() == 0) {
            throw new ResourceNotFoundException(serviceProviderUrl, "InstanceShapes");
        } else if (shapes.size() > 1) {
            throw new IllegalArgumentException("There is more than one 'requiredInstanceShape' shape");
        } else {
            return shapes.getFirst();
        }
    }
}
