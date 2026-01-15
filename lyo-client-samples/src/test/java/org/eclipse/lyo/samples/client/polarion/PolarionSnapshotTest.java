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

import static com.diffplug.selfie.Selfie.expectSelfie;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.lyo.samples.client.SnapshotUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.MediaType;

class PolarionSnapshotTest {

    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private static String baseUri;
    private Map<String, String> loadedFixtures = new HashMap<>();

    @BeforeAll
    static void startServer() {
        System.setProperty("lyo.test.mode", "true");
        mockServer = startClientAndServer(0);
        mockServerClient = new MockServerClient("localhost", mockServer.getPort());
        baseUri = "http://localhost:" + mockServer.getPort();
    }

    @AfterAll
    static void stopServer() {
        mockServer.stop();
        System.clearProperty("lyo.test.mode");
    }

    @BeforeEach
    void reset() {
        mockServerClient.reset();
        loadedFixtures.clear();
    }

    @Test
    void polarionSnapshot() throws Exception {
        String projectName = "OslcDev";
        String rootServicesPath = "/polarion/oslc/rootservices";
        String catalogPath = "/polarion/oslc/catalog";
        String spPath = "/polarion/oslc/services/projects/OslcDev";
        String queryPath = "/polarion/oslc/services/projects/OslcDev/query";
        String resourcePath = "/polarion/oslc/services/projects/OslcDev/workitems/OD-400";

        // Mocks
        mockServerClient
                .when(request().withMethod("GET").withPath(rootServicesPath))
                .respond(response()
                        .withBody(
                                loadFixture("rootservices.xml", Map.of("baseUri", baseUri)),
                                MediaType.APPLICATION_XML));

        mockServerClient
                .when(request().withMethod("GET").withPath(catalogPath))
                .respond(response()
                        .withBody(
                                loadFixture("catalog.xml", Map.of("baseUri", baseUri, "projectName", projectName)),
                                MediaType.APPLICATION_XML));

        mockServerClient
                .when(request().withMethod("GET").withPath(spPath))
                .respond(response()
                        .withBody(
                                loadFixture("serviceprovider.xml", Map.of("baseUri", baseUri)),
                                MediaType.APPLICATION_XML));

        mockServerClient
                .when(request().withMethod("GET").withPath(queryPath))
                .respond(response()
                        .withBody(
                                loadFixture("query_response.xml", Map.of("baseUri", baseUri)),
                                MediaType.APPLICATION_XML));

        mockServerClient
                .when(request().withMethod("GET").withPath(resourcePath))
                .respond(response()
                        .withBody(loadFixture("resource.xml", Map.of("baseUri", baseUri)), MediaType.APPLICATION_XML));

        PolarionSample.Report report = PolarionSample.run(baseUri + rootServicesPath, "my-token", projectName);

        verifySnapshot(report);
    }

    private String loadFixture(String filename, Map<String, String> replacements) throws IOException {
        Path path = Path.of("src/test/resources/fixtures/polarion", filename);
        // We will create these files next
        if (!Files.exists(path)) {
            throw new RuntimeException("Fixture not found: " + path);
        }
        String content = Files.readString(path);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        loadedFixtures.put(filename, content);
        return content;
    }

    private void verifySnapshot(Object report) {
        StringBuilder snapshot = new StringBuilder();

        snapshot.append("=== REPORT ===\n");
        snapshot.append(report.toString()).append("\n\n");

        snapshot.append("=== HTTP REQUESTS ===\n");
        HttpRequest[] requests = mockServerClient.retrieveRecordedRequests(request());
        for (HttpRequest req : requests) {
            snapshot.append(req.getMethod().getValue())
                    .append(" ")
                    .append(req.getPath().getValue())
                    .append("\n");

            if (req.containsHeader("Authorization")) {
                snapshot.append("Authorization: ")
                        .append(req.getFirstHeader("Authorization"))
                        .append("\n");
            }

            String body = req.getBodyAsString();
            if (body != null && !body.isEmpty()) {
                if (isRdfBody(body)) {
                    snapshot.append(SnapshotUtils.stabilizeRdf(body));
                } else {
                    snapshot.append(body);
                }
                snapshot.append("\n");
            }
            snapshot.append("--------------------------------------------------\n");
        }

        snapshot.append("\n=== MOCKED RESPONSES (Fixtures) ===\n");
        loadedFixtures.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            snapshot.append("--- ").append(entry.getKey()).append(" ---\n");
            String body = entry.getValue().trim();
            if (isRdfBody(body)) {
                snapshot.append(SnapshotUtils.stabilizeRdf(body));
            } else {
                snapshot.append(body);
            }
            snapshot.append("\n\n");
        });

        String stabilized = snapshot.toString().replace(baseUri, "http://localhost:PORT");
        expectSelfie(stabilized).toMatchDisk();
    }

    private boolean isRdfBody(String body) {
        return body.contains("<rdf:RDF")
                || body.contains("PREFIX ")
                || body.contains("@prefix")
                || body.contains("prefix ")
                || body.contains("@base")
                || body.contains("BASE ");
    }
}
