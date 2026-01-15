/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *  SPDX-License-Identifier: EPL-1.0 OR BSD-3-Clause
 */
package org.eclipse.lyo.samples.client.polarion;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.eclipse.lyo.client.OSLCConstants;
import org.eclipse.lyo.client.OslcClient;
import org.eclipse.lyo.client.RootServicesHelper;
import org.eclipse.lyo.client.exception.RootServicesException;
import org.eclipse.lyo.client.query.OslcQuery;
import org.eclipse.lyo.client.query.OslcQueryParameters;
import org.eclipse.lyo.client.query.OslcQueryResult;
import org.eclipse.lyo.samples.client.jazz.FixtureRecorderFilter;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 * Samples of logging in to Polarion and running OSLC operations.
 *
 * <p>Assumes Polarion is configured with OSLC support and accessible via a PAT (Personal Access Token).
 */
@Slf4j
public class PolarionSample {

    @lombok.Data
    public static class Report {
        private String rootServicesUrl;
        private String catalogUrl;
        private String serviceProviderUrl;
        private int queryResultCount;
        private String sampleResourceUrl;
    }

    public static class BearerAuthFilter implements ClientRequestFilter {
        private final String token;

        public BearerAuthFilter(String token) {
            this.token = token;
        }

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            requestContext.getHeaders().add("Authorization", "Bearer " + token);
        }
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options();

        options.addOption(
                "url", true, "Root Services URL (e.g. https://polarion.example.com/polarion/oslc/rootservices)");
        options.addOption("token", true, "Personal Access Token (PAT)");
        options.addOption("project", true, "Project Name (Service Provider Title)");

        CommandLineParser cliParser = new GnuParser();

        try {
            CommandLine cmd = cliParser.parse(options, args);

            if (!validateOptions(cmd)) {
                log.error("Syntax:  java <class_name> -url <rootservices_url> -token <pat> -project <project_name>");
                return;
            }

            String rootServicesUrl = cmd.getOptionValue("url");
            String token = cmd.getOptionValue("token");
            String projectName = cmd.getOptionValue("project");

            run(rootServicesUrl, token, projectName);

        } catch (ParseException e) {
            log.error("Error parsing command line arguments", e);
        } catch (Exception e) {
            log.error("Error running Polarion Sample", e);
            if (Boolean.getBoolean("lyo.test.mode")) {
                throw e;
            }
        }
    }

    public static Report run(String rootServicesUrl, String token, String projectName) throws Exception {
        Report report = new Report();
        report.setRootServicesUrl(rootServicesUrl);

        // STEP 1: Configure Client
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());

        if (Boolean.getBoolean("lyo.record.fixtures")) {
            clientConfig.register(new FixtureRecorderFilter());
        }

        clientConfig.property(
                ApacheClientProperties.REQUEST_CONFIG,
                RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());
        clientConfig.register(MultiPartFeature.class);

        // Register Bearer Auth Filter
        clientConfig.register(new BearerAuthFilter(token));

        ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(clientConfig);

        // SSL Setup (Unsafe for production, okay for samples/testing)
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        sslContextBuilder.loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE);
        clientBuilder.sslContext(sslContextBuilder.build());
        clientBuilder.hostnameVerifier(NoopHostnameVerifier.INSTANCE);

        OslcClient client = new OslcClient(clientBuilder);

        // STEP 2: Get Catalog URL from Root Services
        // Polarion usually exposes OSLC CM 2.0 or RM 2.0 services.
        // The rootservices document should have the catalog link.

        // RootServicesHelper appends '/rootservices' to the URL, so we strip it if provided.
        String baseUrl = rootServicesUrl;
        if (baseUrl.endsWith("/rootservices")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - "/rootservices".length());
        } else if (baseUrl.endsWith("/rootservices/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - "/rootservices/".length());
        }

        String catalogUrl;
        try {
            catalogUrl = new RootServicesHelper(baseUrl, OSLCConstants.OSLC_RM_V2, client).getCatalogUrl();
        } catch (RootServicesException e) {
            // Fallback to CM if RM is not found
            catalogUrl = new RootServicesHelper(baseUrl, OSLCConstants.OSLC_CM_V2, client).getCatalogUrl();
        }
        report.setCatalogUrl(catalogUrl);
        log.info("Catalog URL: {}", catalogUrl);

        // STEP 3: Find Service Provider
        String serviceProviderUrl = client.lookupServiceProviderUrl(catalogUrl, projectName);
        report.setServiceProviderUrl(serviceProviderUrl);
        log.info("Service Provider URL: {}", serviceProviderUrl);

        // STEP 4: Query for Requirements (or Work Items which act as requirements in Polarion sometimes)
        // We'll look for RM Requirement Type first.
        String queryCapability = client.lookupQueryCapability(
                serviceProviderUrl, OSLCConstants.OSLC_RM_V2, OSLCConstants.RM_REQUIREMENT_TYPE);

        if (queryCapability == null) {
            // Fallback to CM ChangeRequest
            queryCapability = client.lookupQueryCapability(
                    serviceProviderUrl, OSLCConstants.OSLC_CM_V2, OSLCConstants.CM_CHANGE_REQUEST_TYPE);
        }

        if (queryCapability != null) {
            log.info("Query Capability URL: {}", queryCapability);
            OslcQueryParameters queryParams = new OslcQueryParameters();
            // Just get a few items
            OslcQuery query = new OslcQuery(client, queryCapability, 5, queryParams);
            OslcQueryResult result = query.submit();

            report.setQueryResultCount(result.getMembersUrls().length);
            log.info("Found {} results.", result.getMembersUrls().length);

            if (result.getMembersUrls().length > 0) {
                String resourceUrl = result.getMembersUrls()[0];
                report.setSampleResourceUrl(resourceUrl);
                log.info("Fetching resource: {}", resourceUrl);

                try (Response response = client.getResource(resourceUrl, OSLCConstants.CT_RDF)) {
                    // We just consume it to make sure it works
                    response.readEntity(String.class);
                    log.info("Successfully fetched resource.");
                }
            }
        } else {
            log.warn("No suitable Query Capability found.");
        }

        return report;
    }

    private static boolean validateOptions(CommandLine cmd) {
        return cmd.hasOption("url") && cmd.hasOption("token") && cmd.hasOption("project");
    }
}
