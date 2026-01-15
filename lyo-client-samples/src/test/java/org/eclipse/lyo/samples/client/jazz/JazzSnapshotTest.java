package org.eclipse.lyo.samples.client.jazz;

import static com.diffplug.selfie.Selfie.expectSelfie;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.MediaType;

class JazzSnapshotTest {

    private static final MediaType RDF_XML = MediaType.parse("application/rdf+xml");
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private static String baseUri;
    // We keep track of the loaded fixtures to include them in the snapshot
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

        Path recordedDir = Paths.get("src/test/resources/fixtures/recorded");
        if (Files.exists(recordedDir) && Files.isDirectory(recordedDir)) {
            try {
                if (Files.list(recordedDir).findAny().isPresent()) {
                    RecordedFixturesLoader.loadFixtures(mockServerClient, baseUri, loadedFixtures);
                }
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private boolean useRecordedFixtures() {
        return !loadedFixtures.isEmpty();
    }

    @Test
    void ermSnapshot() throws Exception {
        String projectArea = "JKE Banking (Requirements Management)";
        String context = "/rm";
        String serviceProviderUrl = baseUri + context + "/oslc/contexts/_1/rm/services.xml";
        String queryCapabilityUrl = baseUri + context + "/oslc/contexts/_1/rm/query";
        String creationFactoryUrl = baseUri + context + "/oslc/contexts/_1/rm/create";
        String userRequirementShapeUrl = baseUri + context + "/types/UserRequirement";
        String collectionShapeUrl = baseUri + context + "/types/Collection";
        String folderUrl = baseUri + context + "/folders/1";
        String req01Url = baseUri + context + "/oslc/rm/req01";

        if (!useRecordedFixtures()) {
            setupCommonMocks(context, projectArea, "rootservices_rm.xml", "catalog_rm.xml");

            // Service Provider
            mockServerClient
                    .when(request().withMethod("GET").withPath(context + "/oslc/contexts/_1/rm/services.xml"))
                    .respond(response()
                            .withStatusCode(200)
                            .withContentType(MediaType.APPLICATION_XML)
                            .withBody(loadFixture(
                                    "serviceprovider_rm.xml",
                                    Map.of(
                                            "projectArea", projectArea,
                                            "baseUri", baseUri))));

            // Shapes
            mockServerClient
                    .when(request().withPath(context + "/types/UserRequirement"))
                    .respond(response()
                            .withBody(
                                    loadFixture("shape_requirement.xml", Map.of("baseUri", baseUri)),
                                    MediaType.APPLICATION_XML));
            mockServerClient
                    .when(request().withPath(context + "/types/Collection"))
                    .respond(response()
                            .withBody(
                                    loadFixture("shape_collection.xml", Map.of("baseUri", baseUri)),
                                    MediaType.APPLICATION_XML));

            // Create Resources
            mockServerClient
                    .when(
                            request().withMethod("POST").withPath(context + "/oslc/contexts/_1/rm/create"),
                            Times.exactly(4))
                    .respond(response().withStatusCode(201).withHeader("Location", req01Url));
            mockServerClient
                    .when(request().withMethod("POST").withPath(context + "/oslc/contexts/_1/rm/create"), Times.once())
                    .respond(response()
                            .withStatusCode(201)
                            .withHeader("Location", baseUri + context + "/oslc/rm/col01"));

            // Get REQ01
            mockServerClient
                    .when(request().withMethod("GET").withPath(context + "/oslc/rm/req01"))
                    .respond(response()
                            .withBody(
                                    loadFixture("resource_requirement.xml", Map.of("baseUri", baseUri)),
                                    MediaType.APPLICATION_XML));

            // Generic Query Response
            mockServerClient
                    .when(request().withMethod("GET").withPath(context + "/oslc/contexts/_1/rm/query"))
                    .respond(response()
                            .withBody(
                                    loadFixture("query_empty.xml", Map.of("queryCapabilityUrl", queryCapabilityUrl)),
                                    MediaType.APPLICATION_XML));
        }

        ERMSample.Report report = ERMSample.run(baseUri + context, "ADMIN", "ADMIN", projectArea, true);

        verifySnapshot(report);
    }

    @Test
    void ewmSnapshot() throws Exception {
        String projectArea = "JKE Banking (Change Management)";
        String context = "/ccm";
        String serviceProviderUrl = baseUri + context + "/oslc/contexts/_1/ccm/services.xml";
        String queryCapabilityUrl = baseUri + context + "/oslc/contexts/_1/ccm/query";
        String creationFactoryUrl = baseUri + context + "/oslc/contexts/_1/ccm/create";
        String shapeUrl = baseUri + context + "/oslc/contexts/_1/ccm/shapes/workitem";
        String allowedValuesUrl = baseUri + context + "/oslc/contexts/_1/ccm/allowedValues/filedAgainst";
        String taskUrl = baseUri + context + "/oslc/ccm/task1";

        if (!useRecordedFixtures()) {
            setupCommonMocks(context, projectArea, "rootservices_ccm.xml", "catalog_ccm.xml");

            // Service Provider
            mockServerClient
                    .when(request().withMethod("GET").withPath(context + "/oslc/contexts/_1/ccm/services.xml"))
                    .respond(response()
                            .withBody(
                                    loadFixture(
                                            "serviceprovider_ccm.xml",
                                            Map.of(
                                                    "projectArea", projectArea,
                                                    "baseUri", baseUri)),
                                    MediaType.APPLICATION_XML));

            // Shape and Allowed Values
            mockServerClient
                    .when(request().withPath(context + "/oslc/contexts/_1/ccm/shapes/workitem"))
                    .respond(response()
                            .withBody(
                                    loadFixture("shape_workitem.xml", Map.of("baseUri", baseUri)),
                                    MediaType.APPLICATION_XML));
            mockServerClient
                    .when(request().withPath(context + "/oslc/contexts/_1/ccm/allowedValues/filedAgainst"))
                    .respond(response()
                            .withBody(
                                    loadFixture("allowedvalues_filedAgainst.xml", Map.of("baseUri", baseUri)),
                                    MediaType.APPLICATION_XML));

            // Create and Get
            mockServerClient
                    .when(request().withMethod("POST").withPath(context + "/oslc/contexts/_1/ccm/create"))
                    .respond(response().withStatusCode(201).withHeader("Location", taskUrl));
            mockServerClient
                    .when(request().withMethod("GET").withPath(context + "/oslc/ccm/task1"))
                    .respond(response()
                            .withHeader("ETag", "1")
                            .withBody(
                                    loadFixture("resource_task.xml", Map.of("baseUri", baseUri)),
                                    MediaType.APPLICATION_XML));
            mockServerClient
                    .when(request().withMethod("PUT").withPath(context + "/oslc/ccm/task1"))
                    .respond(response().withStatusCode(200));

            // Query
            mockServerClient
                    .when(request().withMethod("GET").withPath(context + "/oslc/contexts/_1/ccm/query"))
                    .respond(response()
                            .withBody(
                                    loadFixture("query_empty.xml", Map.of("queryCapabilityUrl", queryCapabilityUrl)),
                                    MediaType.APPLICATION_XML));
        }

        EWMSample.Report report = EWMSample.run(baseUri + context, "ADMIN", "ADMIN", projectArea, true);

        verifySnapshot(report);
    }

    @Test
    void etmSnapshot() throws Exception {
        String projectArea = "JKE Banking (Quality Management)";
        String context = "/qm";
        String serviceProviderUrl = baseUri + context + "/oslc/contexts/_1/qm/services.xml";
        String queryCapabilityUrl = baseUri + context + "/oslc/contexts/_1/qm/query";
        String creationFactoryUrl = baseUri + context + "/oslc/contexts/_1/qm/create";
        String tcUrl = baseUri + context + "/oslc/qm/tc1";

        if (!useRecordedFixtures()) {
            setupCommonMocks(context, projectArea, "rootservices_qm.xml", "catalog_qm.xml");

            // Service Provider
            mockServerClient
                    .when(request().withMethod("GET").withPath(context + "/oslc/contexts/_1/qm/services.xml"))
                    .respond(response()
                            .withBody(
                                    loadFixture(
                                            "serviceprovider_qm.xml",
                                            Map.of(
                                                    "projectArea", projectArea,
                                                    "baseUri", baseUri)),
                                    MediaType.APPLICATION_XML));

            // Create and Get
            mockServerClient
                    .when(request().withMethod("POST").withPath(context + "/oslc/contexts/_1/qm/create"))
                    .respond(response().withStatusCode(201).withHeader("Location", tcUrl));
            mockServerClient
                    .when(request().withMethod("GET").withPath(context + "/oslc/qm/tc1"))
                    .respond(response()
                            .withBody(
                                    loadFixture("resource_testcase.xml", Map.of("baseUri", baseUri)),
                                    MediaType.APPLICATION_XML));
            mockServerClient
                    .when(request().withMethod("PUT").withPath(tcUrl.substring(baseUri.length())))
                    .respond(response().withStatusCode(200));

            // Query
            mockServerClient
                    .when(request().withMethod("GET").withPath(context + "/oslc/contexts/_1/qm/query"))
                    .respond(response()
                            .withBody(
                                    loadFixture("query_empty.xml", Map.of("queryCapabilityUrl", queryCapabilityUrl)),
                                    MediaType.APPLICATION_XML));
        }

        ETMSample.Report report = ETMSample.run(baseUri + context, "ADMIN", "ADMIN", projectArea, true);

        verifySnapshot(report);
    }

    private void setupCommonMocks(String context, String projectArea, String rootServicesFile, String catalogFile)
            throws IOException {
        mockServerClient
                .when(request().withPath(context + "/rootservices"))
                .respond(response()
                        .withBody(
                                loadFixture(rootServicesFile, Map.of("baseUri", baseUri)), MediaType.APPLICATION_XML));

        mockServerClient
                .when(request().withPath(context + "/oslc/catalog"))
                .respond(response()
                        .withBody(
                                loadFixture(
                                        catalogFile,
                                        Map.of(
                                                "projectArea", projectArea,
                                                "baseUri", baseUri)),
                                MediaType.APPLICATION_XML));
    }

    private String loadFixture(String filename, Map<String, String> replacements) throws IOException {
        Path path = Paths.get("src/test/resources/fixtures", filename);
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
            String body = req.getBodyAsString();
            if (body != null && !body.isEmpty()) {
                snapshot.append(body).append("\n");
            }
            snapshot.append("--------------------------------------------------\n");
        }

        snapshot.append("\n=== MOCKED RESPONSES (Fixtures) ===\n");
        // Sort keys for deterministic order
        loadedFixtures.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            snapshot.append("--- ").append(entry.getKey()).append(" ---\n");
            snapshot.append(entry.getValue().trim()).append("\n\n");
        });

        // Stabilize ports in the entire snapshot
        String stabilized = snapshot.toString().replace(baseUri, "http://localhost:PORT");

        expectSelfie(stabilized).toMatchDisk();
    }
}
