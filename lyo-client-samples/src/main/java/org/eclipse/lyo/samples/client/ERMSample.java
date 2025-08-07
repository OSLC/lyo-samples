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
package org.eclipse.lyo.samples.client;

import static org.eclipse.lyo.samples.client.RMSample.printRequirementInfo;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.eclipse.lyo.client.oslc.resources.Requirement;
import org.eclipse.lyo.client.oslc.resources.RequirementCollection;
import org.eclipse.lyo.client.oslc.resources.RmConstants;
import org.eclipse.lyo.client.query.OslcQuery;
import org.eclipse.lyo.client.query.OslcQueryParameters;
import org.eclipse.lyo.client.query.OslcQueryResult;
import org.eclipse.lyo.client.resources.RmUtil;
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
 * Samples of logging in to IBM Enterprise Requirements Manager
 * and running OSLC operations
 *
 * - run an OLSC Requirement query and retrieve OSLC Requirements and de-serialize them as Java objects
 * - TODO:  Add more requirement sample scenarios
 *
 */
public class ERMSample {

    private static final Logger logger = Logger.getLogger(ERMSample.class.getName());

    // Following is a workaround for primaryText issue in DNG ( it is PrimaryText instead of
    // primaryText
    private static final QName PROPERTY_PRIMARY_TEXT_WORKAROUND =
            new QName(RmConstants.JAZZ_RM_NAMESPACE, "PrimaryText");

    /**
     * Login to the ERM server and perform some OSLC actions
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

        // Parse the command line
        CommandLine cmd = cliParser.parse(options, args);

        if (!validateOptions(cmd)) {
            logger.severe(
                    "Syntax:  java <class_name> -url https://<server>:port/<context>/ -user <user>"
                            + " -password <password> -project \"<project_area>\" [--basic]");
            logger.severe(
                    "Example: java ERMSample -url https://exmple.com:9443/rm -user ADMIN -password"
                            + " ADMIN -project \"JKE Banking (Requirements Management)\"");
            logger.severe(
                    "Example: java ERMSample -url https://jazz.net.example.com/sandbox02-rm/ -user"
                            + " ADMIN -password ADMIN -project \"JKE Banking (Requirements"
                            + " Management)\" --basic");
            return;
        }

        String webContextUrl = cmd.getOptionValue("url");
        String userId = cmd.getOptionValue("user");
        String password = cmd.getOptionValue("password");
        String projectArea = cmd.getOptionValue("project");
        boolean useBasicAuth = cmd.hasOption("basic");

        try {

            // STEP 1: Configure the ClientBuilder as needed for your client application

            // Use HttpClient instead of the default HttpUrlConnection
            ClientConfig clientConfig =
                    new ClientConfig().connectorProvider(new ApacheConnectorProvider());

            // Fixes Invalid cookie header: ... Invalid 'expires' attribute: Thu, 01 Dec 1994
            // 16:00:00 GMT
            clientConfig.property(
                    ApacheClientProperties.REQUEST_CONFIG,
                    RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());
            clientConfig.register(MultiPartFeature.class);

            ClientBuilder clientBuilder = ClientBuilder.newBuilder();

            // IBM jazz-apps use JEE Form based authentication
            // except the Jazz sandbox, it uses Basic/JAS auth. USE ONLY ONE
            if (useBasicAuth) {
                clientConfig.register(HttpAuthenticationFeature.basic(userId, password));
                logger.info("Using Basic authentication");
            }
            clientBuilder.withConfig(clientConfig);

            // Setup SSL support to ignore self-assigned SSL certificates - for testing only!!
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE);
            clientBuilder.sslContext(sslContextBuilder.build());
            clientBuilder.hostnameVerifier(NoopHostnameVerifier.INSTANCE);

            // do not merge the two if's: order of registration is important
            if (!useBasicAuth) {
                clientBuilder.register(new JEEFormAuthenticator(webContextUrl, userId, password));
                logger.info("Using JAS (Forms) authentication");
            }

            // STEP 3: Create a new OslcClient
            OslcClient client = new OslcClient(clientBuilder);

            // STEP 4: Get the URL of the OSLC ChangeManagement service from the rootservices
            // document
            String catalogUrl =
                    new RootServicesHelper(webContextUrl, OSLCConstants.OSLC_RM_V2, client)
                            .getCatalogUrl();

            // STEP 5: Find the OSLC Service Provider for the project area we want to work with
            String serviceProviderUrl = client.lookupServiceProviderUrl(catalogUrl, projectArea);

            // STEP 6: Get the Query Capabilities URL so that we can run some OSLC queries
            String queryCapability =
                    client.lookupQueryCapability(
                            serviceProviderUrl,
                            OSLCConstants.OSLC_RM_V2,
                            OSLCConstants.RM_REQUIREMENT_TYPE);

            // STEP 7: Create base requirements
            // Get the Creation Factory URL for change requests so that we can create one

            String requirementFactory =
                    client.lookupCreationFactory(
                            serviceProviderUrl,
                            OSLCConstants.OSLC_RM_V2,
                            OSLCConstants.RM_REQUIREMENT_TYPE);

            // Get Feature Requirement Type URL
            ResourceShape featureInstanceShape = null;
            ResourceShape collectionInstanceShape = null;
            try {
                try {
                    featureInstanceShape =
                            lookupRequirementsInstanceShapes(
                                    serviceProviderUrl,
                                    OSLCConstants.OSLC_RM_V2,
                                    OSLCConstants.RM_REQUIREMENT_TYPE,
                                    client,
                                    "Feature",
                                    null);
                } catch (IOException | URISyntaxException | OAuthException e) {
                    throw e;
                } catch (ResourceNotFoundException e) {
                    // Feature shape is not defined if SAFe framework is used
                    featureInstanceShape =
                            lookupRequirementsInstanceShapes(
                                    serviceProviderUrl,
                                    OSLCConstants.OSLC_RM_V2,
                                    OSLCConstants.RM_REQUIREMENT_TYPE,
                                    client,
                                    "User Requirement",
                                    null);
                }

                collectionInstanceShape =
                        RmUtil.lookupRequirementsInstanceShapes(
                                serviceProviderUrl,
                                OSLCConstants.OSLC_RM_V2,
                                OSLCConstants.RM_REQUIREMENT_COLLECTION_TYPE,
                                client,
                                "Collection");

                // We need to use Resource shapes to properly handle date attributes,
                // so they aren't interpreted as dateTime.
                // The following 4 lines will enable the logic to properly handle date attributes
                List<ResourceShape> shapes = new ArrayList<>();
                shapes.add(featureInstanceShape);
                shapes.add(collectionInstanceShape);
                OSLC4JUtils.setShapes(shapes);
                OSLC4JUtils.setInferTypeFromShape("true");
            } catch (IOException | URISyntaxException | OAuthException e) {
                throw new RuntimeException(e);
            } catch (ResourceNotFoundException e) {
                //				throw new RuntimeException(e);
                logger.warning(
                        "OSLC Server does not provide Collection and Feature (or User Requirement)"
                                + " instance shapes");
                logger.log(Level.FINE, "Exception", e);
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
                    logger.warning("Cannot create resources without access to shapes, skipping");
                } else {
                    // Create REQ01
                    requirement = new Requirement();
                    requirement.setInstanceShape(featureInstanceShape.getAbout());
                    requirement.setTitle("Req01");

                    // Decorate the PrimaryText
                    primaryText = "My Primary Text";
                    org.w3c.dom.Element obj = convertStringToHTML(primaryText);
                    requirement.getExtendedProperties().put(RmConstants.PROPERTY_PRIMARY_TEXT, obj);

                    requirement.setDescription("Created By EclipseLyo");
                    requirement.addImplementedBy(
                            new Link(new URI("http://google.com"), "Link in REQ01"));
                    // Create the Requirement
                    try (Response creationResponse =
                            client.createResource(
                                    requirementFactory,
                                    requirement,
                                    OslcMediaType.APPLICATION_RDF_XML,
                                    OslcMediaType.APPLICATION_RDF_XML)) {
                        if (creationResponse.getStatus()
                                == Response.Status.FORBIDDEN.getStatusCode()) {
                            throw new IllegalStateException(
                                    "Server is refusing the requests on security grounds.");
                        }
                        req01URL =
                                creationResponse.getStringHeaders().getFirst(HttpHeaders.LOCATION);
                    }

                    // Create REQ02
                    requirement = new Requirement();
                    requirement.setInstanceShape(featureInstanceShape.getAbout());
                    requirement.setTitle("Req02");
                    requirement.setDescription("Created By EclipseLyo");
                    requirement.addValidatedBy(
                            new Link(new URI("http://bancomer.com"), "Link in REQ02"));
                    // Create the change request
                    try (Response creationResponse =
                            client.createResource(
                                    requirementFactory,
                                    requirement,
                                    OslcMediaType.APPLICATION_RDF_XML,
                                    OslcMediaType.APPLICATION_RDF_XML)) {
                        if (creationResponse.getStatus()
                                == Response.Status.FORBIDDEN.getStatusCode()) {
                            throw new IllegalStateException(
                                    "Server is refusing the requests on security grounds.");
                        }
                        req02URL =
                                creationResponse.getStringHeaders().getFirst(HttpHeaders.LOCATION);
                    }

                    // Create REQ03
                    requirement = new Requirement();
                    requirement.setInstanceShape(featureInstanceShape.getAbout());
                    requirement.setTitle("Req03");
                    requirement.setDescription("Created By EclipseLyo");
                    requirement.addValidatedBy(
                            new Link(new URI("http://outlook.com"), "Link in REQ03"));
                    // Create the change request
                    try (Response creationResponse =
                            client.createResource(
                                    requirementFactory,
                                    requirement,
                                    OslcMediaType.APPLICATION_RDF_XML,
                                    OslcMediaType.APPLICATION_RDF_XML)) {
                        if (creationResponse.getStatus()
                                == Response.Status.FORBIDDEN.getStatusCode()) {
                            throw new IllegalStateException(
                                    "Server is refusing the requests on security grounds.");
                        }
                        req03URL =
                                creationResponse.getStringHeaders().getFirst(HttpHeaders.LOCATION);
                    }

                    // Create REQ04
                    requirement = new Requirement();
                    requirement.setInstanceShape(featureInstanceShape.getAbout());
                    requirement.setTitle("Req04");
                    requirement.setDescription("Created By EclipseLyo");

                    // Create the Requirement
                    try (Response creationResponse =
                            client.createResource(
                                    requirementFactory,
                                    requirement,
                                    OslcMediaType.APPLICATION_RDF_XML,
                                    OslcMediaType.APPLICATION_RDF_XML)) {
                        if (creationResponse.getStatus()
                                == Response.Status.FORBIDDEN.getStatusCode()) {
                            throw new IllegalStateException(
                                    "Server is refusing the requests on security grounds.");
                        }
                        req04URL =
                                creationResponse.getStringHeaders().getFirst(HttpHeaders.LOCATION);
                    }

                    // Now create a collection
                    // Create REQ04
                    collection = new RequirementCollection();

                    collection.addUses(new URI(req03URL));
                    collection.addUses(new URI(req04URL));

                    if (collectionInstanceShape != null) {
                        collection.setInstanceShape(collectionInstanceShape.getAbout());
                    }
                    collection.setTitle("Collection01");
                    collection.setDescription("Created By EclipseLyo");
                    // Create the collection
                    try (Response creationResponse =
                            client.createResource(
                                    requirementFactory,
                                    collection,
                                    OslcMediaType.APPLICATION_RDF_XML,
                                    OslcMediaType.APPLICATION_RDF_XML)) {
                        if (creationResponse.getStatus()
                                == Response.Status.FORBIDDEN.getStatusCode()) {
                            throw new IllegalStateException(
                                    "Server is refusing the requests on security grounds.");
                        }
                        reqcoll01URL =
                                creationResponse.getStringHeaders().getFirst(HttpHeaders.LOCATION);
                    }

                    // Check that everything was properly created
                    if (req01URL == null
                            || req02URL == null
                            || req03URL == null
                            || req04URL == null
                            || reqcoll01URL == null) {
                        throw new Exception("Failed to create an artifact");
                    }
                }
            }

            // GET the root folder based on First requirement created
            Response getResponse = client.getResource(req01URL, OslcMediaType.APPLICATION_RDF_XML);
            requirement = getResponse.readEntity(Requirement.class);

            // Display attributes based on the Resource shape
            Map<QName, Object> requestExtProperties = requirement.getExtendedProperties();
            for (QName qname : requestExtProperties.keySet()) {
                Property attr =
                        featureInstanceShape.getProperty(
                                new URI(qname.getNamespaceURI() + qname.getLocalPart()));
                String name = null;
                if (attr != null) {
                    name = attr.getTitle();
                    if (name != null) {
                        System.out.println(
                                name + " = " + requirement.getExtendedProperties().get(qname));
                    }
                }
            }

            // Save the URI of the root folder in order to used it easily
            rootFolder =
                    (URI)
                            requirement
                                    .getExtendedProperties()
                                    .get(RmConstants.PROPERTY_PARENT_FOLDER);
            Object changedPrimaryText =
                    (Object)
                            requirement
                                    .getExtendedProperties()
                                    .get(RmConstants.PROPERTY_PRIMARY_TEXT);
            if (changedPrimaryText == null) {
                // Check with the workaround
                changedPrimaryText =
                        (Object)
                                requirement
                                        .getExtendedProperties()
                                        .get(PROPERTY_PRIMARY_TEXT_WORKAROUND);
            }
            String primarytextString = null;
            if (changedPrimaryText != null) {
                primarytextString =
                        changedPrimaryText
                                .toString(); // Handle the case where Primary Text is returned as
                // XMLLiteral
            }

            if ((primarytextString != null) && (!primarytextString.contains(primaryText))) {
                logger.log(Level.SEVERE, "Error getting primary Text");
            }

            // QUERIES
            // SCENARIO 01  Do a query for type= Requirement
            OslcQueryParameters queryParams = new OslcQueryParameters();
            queryParams.setPrefix("rdf=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
            queryParams.setWhere("rdf:type=<http://open-services.net/ns/rm#Requirement>");
            OslcQuery query = new OslcQuery(client, queryCapability, 10, queryParams);
            OslcQueryResult result = query.submit();
            result.getRawResponse().bufferEntity();
            boolean processAsJavaObjects = false;
            int resultsSize = result.getMembersUrls().length;
            processPagedQueryResults(result, client, processAsJavaObjects);
            System.out.println("\n------------------------------\n");
            System.out.println("Number of Results for SCENARIO 01 = " + resultsSize + "\n");

            // SCENARIO 02 	Do a query for type= Requirements and for it folder container =
            // rootFolder
            queryParams = new OslcQueryParameters();
            queryParams.setPrefix(
                    "nav=<http://com.ibm.rdm/navigation#>,rdf=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
            queryParams.setWhere(
                    "rdf:type=<http://open-services.net/ns/rm#Requirement> and nav:parent=<"
                            + rootFolder
                            + ">");
            query = new OslcQuery(client, queryCapability, 10, queryParams);
            result = query.submit();
            processAsJavaObjects = false;
            result.getRawResponse().bufferEntity();
            resultsSize = result.getMembersUrls().length;
            processPagedQueryResults(result, client, processAsJavaObjects);
            System.out.println("\n------------------------------\n");
            System.out.println("Number of Results for SCENARIO 02 = " + resultsSize + "\n");

            // SCENARIO 03	Do a query for title
            queryParams = new OslcQueryParameters();
            queryParams.setPrefix("dcterms=<http://purl.org/dc/terms/>");
            queryParams.setWhere("dcterms:title=\"Req04\"");
            query = new OslcQuery(client, queryCapability, 10, queryParams);
            result = query.submit();
            result.getRawResponse().bufferEntity();
            resultsSize = result.getMembersUrls().length;
            processAsJavaObjects = false;
            processPagedQueryResults(result, client, processAsJavaObjects);
            System.out.println("\n------------------------------\n");
            System.out.println("Number of Results for SCENARIO 03 = " + resultsSize + "\n");

            // SCENARIO 04	Do a query for the link that is implemented
            queryParams = new OslcQueryParameters();
            queryParams.setPrefix("oslc_rm=<http://open-services.net/ns/rm#>");
            queryParams.setWhere("oslc_rm:implementedBy=<http://google.com>");
            query = new OslcQuery(client, queryCapability, 10, queryParams);
            result = query.submit();
            result.getRawResponse().bufferEntity();
            resultsSize = result.getMembersUrls().length;
            processAsJavaObjects = false;
            processPagedQueryResults(result, client, processAsJavaObjects);
            System.out.println("\n------------------------------\n");
            System.out.println("Number of Results for SCENARIO 04 = " + resultsSize + "\n");

            // SCENARIO 05	Do a query for the links that is validated
            queryParams = new OslcQueryParameters();
            queryParams.setPrefix("oslc_rm=<http://open-services.net/ns/rm#>");
            queryParams.setWhere(
                    "oslc_rm:validatedBy in [<http://bancomer.com>,<http://outlook.com>]");
            query = new OslcQuery(client, queryCapability, 10, queryParams);
            result = query.submit();
            result.getRawResponse().bufferEntity();
            resultsSize = result.getMembersUrls().length;
            processAsJavaObjects = false;
            processPagedQueryResults(result, client, processAsJavaObjects);
            System.out.println("\n------------------------------\n");
            System.out.println("Number of Results for SCENARIO 05 = " + resultsSize + "\n");

            // SCENARIO 06 Do a query for it container folder and for the link that is implemented
            queryParams = new OslcQueryParameters();
            queryParams.setPrefix(
                    "nav=<http://com.ibm.rdm/navigation#>,oslc_rm=<http://open-services.net/ns/rm#>");
            queryParams.setWhere(
                    "nav:parent=<"
                            + rootFolder
                            + "> and oslc_rm:validatedBy=<http://bancomer.com>");
            query = new OslcQuery(client, queryCapability, 10, queryParams);
            result = query.submit();
            result.getRawResponse().bufferEntity();
            resultsSize = result.getMembersUrls().length;
            processAsJavaObjects = false;
            processPagedQueryResults(result, client, processAsJavaObjects);
            System.out.println("\n------------------------------\n");
            System.out.println("Number of Results for SCENARIO 06 = " + resultsSize + "\n");

            // GET resources from req03 in order edit its values
            getResponse = client.getResource(req03URL, OslcMediaType.APPLICATION_RDF_XML);
            requirement = getResponse.readEntity(Requirement.class);
            // Get the eTAG, we need it to update
            String etag = getResponse.getStringHeaders().getFirst(OSLCConstants.ETAG);
            requirement.setTitle("My new Title");
            requirement.addImplementedBy(
                    new Link(new URI("http://google.com"), "Link created by an Eclipse Lyo user"));

            // Update the requirement with the proper etag
            Response updateResponse =
                    client.updateResource(
                            req03URL,
                            requirement,
                            OslcMediaType.APPLICATION_RDF_XML,
                            OslcMediaType.APPLICATION_RDF_XML,
                            etag);

            updateResponse.readEntity(String.class);

            /*Do a query in order to see if the requirement have changed*/
            // SCENARIO 07 Do a query for the new title just changed
            queryParams = new OslcQueryParameters();
            queryParams.setPrefix("dcterms=<http://purl.org/dc/terms/>");
            queryParams.setWhere("dcterms:title=\"My new Title\"");
            query = new OslcQuery(client, queryCapability, 10, queryParams);
            result = query.submit();
            result.getRawResponse().bufferEntity();
            resultsSize = result.getMembersUrls().length;
            processAsJavaObjects = false;
            processPagedQueryResults(result, client, processAsJavaObjects);
            System.out.println("\n------------------------------\n");
            System.out.println("Number of Results for SCENARIO 07 = " + resultsSize + "\n");

            // SCENARIO 08	Do a query for implementedBy links
            queryParams = new OslcQueryParameters();
            queryParams.setPrefix("oslc_rm=<http://open-services.net/ns/rm#>");
            queryParams.setWhere("oslc_rm:implementedBy=<http://google.com>");
            query = new OslcQuery(client, queryCapability, 10, queryParams);
            result = query.submit();
            result.getRawResponse().bufferEntity();
            resultsSize = result.getMembersUrls().length;
            processAsJavaObjects = false;
            processPagedQueryResults(result, client, processAsJavaObjects);
            System.out.println("\n------------------------------\n");
            System.out.println("Number of Results for SCENARIO 08 = " + resultsSize + "\n");
        } catch (RootServicesException re) {
            logger.log(
                    Level.SEVERE,
                    "Unable to access the Jazz rootservices document at: "
                            + webContextUrl
                            + "/rootservices",
                    re);
            System.exit(1);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            System.exit(1);
        }
    }

    private static Element convertStringToHTML(String primaryText) {
        try {
            Document document =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element divElement = document.createElementNS(RmConstants.NAMESPACE_URI_XHTML, "div");
            divElement.setTextContent(primaryText);
            return divElement;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void processPagedQueryResults(
            OslcQueryResult result, OslcClient client, boolean asJavaObjects) {
        int page = 1;
        // For now, just show first 5 pages
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

    private static void processCurrentPage(
            OslcQueryResult result, OslcClient client, boolean asJavaObjects) {
        result.getRawResponse().bufferEntity();
        for (String resultsUrl : result.getMembersUrls()) {
            System.out.println(resultsUrl);

            try (Response response = client.getResource(resultsUrl, OSLCConstants.CT_RDF)) {
                // Get a single artifact by its URL
                if (response != null) {
                    response.bufferEntity();
                    // De-serialize it as a Java object
                    if (asJavaObjects) {
                        Requirement req = response.readEntity(Requirement.class);
                        printRequirementInfo(req); // print a few attributes
                    } else {
                        // Just print the raw RDF/XML (or process the XML as desired)
                        processRawResponse(response);
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unable to process artfiact at url: " + resultsUrl, e);
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

        if (!(cmd.hasOption("url")
                && cmd.hasOption("user")
                && cmd.hasOption("password")
                && cmd.hasOption("project"))) {

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
        Response response =
                client.getResource(
                        serviceProviderUrl, null, OSLCConstants.CT_RDF, configurationContext);
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
                                            response =
                                                    client.getResource(
                                                            typeURI.toString(),
                                                            null,
                                                            OSLCConstants.CT_RDF,
                                                            configurationContext);
                                            ResourceShape resourceShape =
                                                    response.readEntity(ResourceShape.class);
                                            if (Arrays.stream(resourceShape.getDescribes())
                                                    .anyMatch(
                                                            uri ->
                                                                    oslcResourceType.equals(
                                                                            uri.toString()))) {
                                                if (requiredInstanceShape.equals(
                                                        resourceShape.getTitle())) {
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
            throw new IllegalArgumentException(
                    "There is more than one 'requiredInstanceShape' shape");
        } else {
            return shapes.getFirst();
        }
    }
}
