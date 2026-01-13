/*
 * Copyright (c) 2012, 2014 IBM Corporation.
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
 *     Samuel Padgett      - handle test case creation errors
 *     Samuel Padgett      - update command line example to use correct context
 *     Samuel Padgett      - set member property for RTM query results
 */
package org.eclipse.lyo.samples.client.jazz;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.eclipse.lyo.client.JEEFormAuthenticator;
import org.eclipse.lyo.client.OSLCConstants;
import org.eclipse.lyo.client.OslcClient;
import org.eclipse.lyo.client.RootServicesHelper;
import org.eclipse.lyo.client.exception.RootServicesException;
import org.eclipse.lyo.client.query.OslcQuery;
import org.eclipse.lyo.client.query.OslcQueryParameters;
import org.eclipse.lyo.client.query.OslcQueryResult;
import org.eclipse.lyo.oslc.domains.qm.TestCase;
import org.eclipse.lyo.oslc.domains.qm.TestResult;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Samples of logging in to IBM Enterprise Test Manager and running OSLC operations
 *
 * <p>- run an OLSC TestResult query and retrieve OSLC TestResults and de-serialize them as Java objects - retrieve an
 * OSLC TestResult and print it as XML - create a new TestCase - update an existing TestCase
 */
public class ETMSample {

    private static final Logger logger = LoggerFactory.getLogger(ETMSample.class.getName());

    /**
     * Login to the ETM server and perform some OSLC actions
     *
     * @param args
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException {

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
            logger.error("Syntax:  java <class_name> -url https://<server>:port/<context>/ -user <user>"
                    + " -password <password> -project \"<project_area>\" [--basic]");
            logger.error("Example: java ETMSample -url https://example.com:9443/qm -user ADMIN -password ADMIN"
                    + " -project \"JKE Banking (Quality Management)\"");
            logger.error("Example: java ETMSample -url https://jazz.net/sandbox01-qm/ -user ADMIN -password ADMIN"
                    + " -project \"JKE Banking (Quality Management)\" --basic");
            return;
        }

        String webContextUrl = cmd.getOptionValue("url");
        String userId = cmd.getOptionValue("user");
        String password = cmd.getOptionValue("password");
        String projectArea = cmd.getOptionValue("project");
        boolean useBasicAuth = cmd.hasOption("basic");

        try {
            // STEP 1: Configure the ClientBuilder as needed for your client application
            ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
            clientConfig.property(
                    ApacheClientProperties.REQUEST_CONFIG,
                    RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());
            clientConfig.register(MultiPartFeature.class);

            ClientBuilder clientBuilder = ClientBuilder.newBuilder();
            if (useBasicAuth) {
                clientConfig.register(HttpAuthenticationFeature.basic(userId, password));
                logger.info("Using Basic authentication");
            }
            clientBuilder.withConfig(clientConfig);

            // JEE Form authentication only if not using Basic
            if (!useBasicAuth) {
                clientBuilder.register(new JEEFormAuthenticator(webContextUrl, userId, password));
                logger.info("Using JAS (Forms) authentication");
            }

            // STEP 3: Create a new OslcClient
            OslcClient client = new OslcClient(clientBuilder);

            // STEP 4: Get the URL of the OSLC QM service from the rootservices document
            String catalogUrl = new RootServicesHelper(webContextUrl, OSLCConstants.OSLC_QM_V2, client).getCatalogUrl();
            logger.info("Using catalog URI {}", catalogUrl);

            // STEP 5: Find the OSLC Service Provider for the project area we want to work with
            String serviceProviderUrl = client.lookupServiceProviderUrl(catalogUrl, projectArea);

            // STEP 6: Get the Query Capabilities URL so that we can run some OSLC queries
            String queryCapability = client.lookupQueryCapability(
                    serviceProviderUrl, OSLCConstants.OSLC_QM_V2, OSLCConstants.QM_TEST_RESULT_QUERY);

            // SCENARIO A: Query passed TestResults (paged)
            OslcQueryParameters queryParams = new OslcQueryParameters();
            queryParams.setWhere("oslc_qm:status=\"com.ibm.rqm.execution.common.state.passed\"");
            OslcQuery query = new OslcQuery(client, queryCapability, 10, queryParams);
            logger.info("Running query: {}", query.getQueryUrl());
            OslcQueryResult result = query.submit();
            result.setMemberProperty(OSLCConstants.OSLC_QM_V2 + "testResult");
            processPagedQueryResults(result, client, true);

            System.out.println("\n------------------------------\n");

            // SCENARIO B: Query a specific TestResult selecting a subset of attributes
            OslcQueryParameters queryParams2 = new OslcQueryParameters();
            queryParams2.setWhere("dcterms:title=\"Consistent_display_of_currency_Firefox_DB2_WAS_Windows_S1\"");
            queryParams2.setSelect("dcterms:identifier,dcterms:title,dcterms:creator,dcterms:created,oslc_qm:status");
            OslcQuery query2 = new OslcQuery(client, queryCapability, queryParams2);
            OslcQueryResult result2 = query2.submit();
            result2.setMemberProperty(OSLCConstants.OSLC_QM_V2 + "testResult");
            Response rawResponse = result2.getRawResponse();
            processRawResponse(rawResponse);
            rawResponse.close();

            // SCENARIO C: TestCase creation and update
            TestCase testcase = new TestCase();
            testcase.setTitle("Accessibility verification using a screen reader");
            testcase.setDescription("This test case uses a screen reader application to ensure that the web browser"
                    + " content fully complies with accessibility standards");
            testcase.addTestsChangeRequest(new Link(
                    new URI("http://cmprovider/changerequest/1"), "Implement accessibility in Pet Store application"));
            testcase.setTypes(Collections.singleton(URI.create(OSLCConstants.OSLC_QM_V2 + "TestCase")));

            String testcaseCreation = client.lookupCreationFactory(
                    serviceProviderUrl,
                    OSLCConstants.OSLC_QM_V2,
                    testcase.getTypes().iterator().next().toString());
            Response creationResponse = client.createResource(
                    testcaseCreation, testcase, OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_RDF_XML);
            String testcaseLocation = creationResponse.getStringHeaders().getFirst(HttpHeaders.LOCATION);
            if (creationResponse.getStatus() != HttpStatus.SC_CREATED) {
                System.err.println(
                        "ERROR: Could not create the test case (status " + creationResponse.getStatus() + ")\n");
                System.err.println(creationResponse.readEntity(String.class));
                if (Boolean.getBoolean("lyo.test.mode")) {
                    throw new RuntimeException("Creation failed");
                }
                System.exit(1);
            }
            creationResponse.readEntity(String.class);
            System.out.println("Test Case created at location " + testcaseLocation);

            testcase = client.getResource(testcaseLocation).readEntity(TestCase.class);
            testcase.setTitle(testcase.getTitle() + " (updated)");
            String updateUrl = testcase.getAbout() + "?oslc.properties=dcterms:title";
            Response updateResponse = client.updateResource(
                    updateUrl, testcase, OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_RDF_XML);
            updateResponse.readEntity(String.class);

        } catch (RootServicesException re) {
            logger.error("Unable to access the Jazz rootservices document at: {}/rootservices", webContextUrl, re);
            if (Boolean.getBoolean("lyo.test.mode")) {
                throw new RuntimeException(re);
            }
        } catch (Exception e) {
            logger.error("Unknown error", e);
            if (Boolean.getBoolean("lyo.test.mode")) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void processPagedQueryResults(OslcQueryResult result, OslcClient client, boolean asJavaObjects) {
        int page = 1;
        do {
            System.out.println("\nPage " + page + ":\n");
            processCurrentPage(result, client, asJavaObjects);
            if (result.hasNext()) {
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
                    if (asJavaObjects) {
                        TestResult tr = response.readEntity(TestResult.class);
                        printTestResultInfo(tr);
                    } else {
                        processRawResponse(response);
                    }
                }
            } catch (Exception e) {
                logger.error("Unable to process artifact at url: {}", resultsUrl, e);
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
    }

    private static void printTestResultInfo(TestResult tr) {
        if (tr != null) {
            System.out.println(
                    "ID: " + tr.getIdentifier() + ", Title: " + tr.getTitle() + ", Status: " + tr.getStatus());
        }
    }

    private static boolean validateOptions(CommandLine cmd) {
        return cmd.hasOption("url") && cmd.hasOption("user") && cmd.hasOption("password") && cmd.hasOption("project");
    }
}
