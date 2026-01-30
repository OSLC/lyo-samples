package org.eclipse.lyo.samples.client.jazz;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.eclipse.lyo.client.OSLCConstants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;

class MockJazzServerTest {

    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private static String baseUri;

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
    }

    @Test
    void ewmSample() throws Exception {
        String projectArea = "JKE Banking (Change Management)";
        String context = "/ccm";
        String rootServicesUrl = baseUri + context + "/rootservices";
        String catalogUrl = baseUri + context + "/oslc/workitems/catalog";
        String serviceProviderUrl = baseUri + context + "/oslc/contexts/_12345/workitems/services.xml";
        String queryCapabilityUrl = baseUri + context + "/oslc/contexts/_12345/workitems/query";
        String creationFactoryUrl = baseUri + context + "/oslc/contexts/_12345/workitems/create";
        String shapeUrl = baseUri + context + "/oslc/contexts/_12345/workitems/shapes/workitem";
        String allowedValuesUrl = baseUri + context + "/oslc/contexts/_12345/workitems/allowedValues/filedAgainst";

        // 1. Rootservices
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/rootservices"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
                                + " xmlns:oslc_cm=\"http://open-services.net/xmlns/cm/1.0/\""
                                + " xmlns:jd=\"http://jazz.net/xmlns/prod/jazz/discovery/1.0/\""
                                + " xmlns:jfs=\"http://jazz.net/xmlns/prod/jazz/jfs/1.0/\">"
                                + "<rdf:Description rdf:about=\"" + rootServicesUrl + "\">"
                                + "<oslc_cm:cmServiceProviders rdf:resource=\"" + catalogUrl + "\"/>"
                                + "<jfs:oauthRequestTokenUrl rdf:resource=\"" + baseUri + "/jts/oauth/requestToken\"/>"
                                + "<jfs:oauthUserAuthorizationUrl rdf:resource=\"" + baseUri
                                + "/jts/oauth/authorize\"/>"
                                + "<jfs:oauthAccessTokenUrl rdf:resource=\"" + baseUri + "/jts/oauth/accessToken\"/>"
                                + "</rdf:Description>"
                                + "</rdf:RDF>"));

        // 2. Catalog
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/oslc/workitems/catalog"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:dcterms=\"http://purl.org/dc/terms/\">"
                                        + "<oslc:ServiceProviderCatalog rdf:about=\"" + catalogUrl + "\">"
                                        + "<oslc:serviceProvider>"
                                        + "<oslc:ServiceProvider rdf:about=\"" + serviceProviderUrl + "\">"
                                        + "<dcterms:title>" + projectArea + "</dcterms:title>"
                                        + "</oslc:ServiceProvider>"
                                        + "</oslc:serviceProvider>"
                                        + "</oslc:ServiceProviderCatalog>"
                                        + "</rdf:RDF>"));

        // 3. Service Provider
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/oslc/contexts/_12345/workitems/services.xml"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:dcterms=\"http://purl.org/dc/terms/\">"
                                        + "<oslc:ServiceProvider rdf:about=\"" + serviceProviderUrl + "\">"
                                        + "<oslc:service>"
                                        + "<oslc:Service>"
                                        + "<oslc:domain rdf:resource=\"" + OSLCConstants.OSLC_CM_V2 + "\"/>"
                                        + "<oslc:queryCapability>"
                                        + "<oslc:QueryCapability>"
                                        + "<oslc:queryBase rdf:resource=\"" + queryCapabilityUrl + "\"/>"
                                        + "<oslc:resourceType rdf:resource=\"" + OSLCConstants.CM_CHANGE_REQUEST_TYPE
                                        + "\"/>"
                                        + "</oslc:QueryCapability>"
                                        + "</oslc:queryCapability>"
                                        + "<oslc:creationFactory>"
                                        + "<oslc:CreationFactory>"
                                        + "<oslc:creation rdf:resource=\"" + creationFactoryUrl + "\"/>"
                                        + "<oslc:resourceType rdf:resource=\"" + OSLCConstants.CM_CHANGE_REQUEST_TYPE
                                        + "\"/>"
                                        + "<oslc:resourceShape rdf:resource=\"" + shapeUrl + "\"/>"
                                        + "</oslc:CreationFactory>"
                                        + "</oslc:creationFactory>"
                                        + "<oslc:creationFactory>"
                                        + "<oslc:CreationFactory>"
                                        + "<oslc:creation rdf:resource=\"" + creationFactoryUrl + "\"/>"
                                        + "<oslc:resourceType rdf:resource=\"" + OSLCConstants.CM_CHANGE_REQUEST_TYPE
                                        + "\"/>"
                                        + "<oslc:usage rdf:resource=\"http://open-services.net/ns/cm#defect\"/>"
                                        + "<oslc:resourceShape rdf:resource=\"" + shapeUrl + "\"/>"
                                        + "</oslc:CreationFactory>"
                                        + "</oslc:creationFactory>"
                                        + "<oslc:creationFactory>"
                                        + "<oslc:CreationFactory>"
                                        + "<oslc:creation rdf:resource=\"" + creationFactoryUrl + "\"/>"
                                        + "<oslc:resourceType rdf:resource=\"" + OSLCConstants.CM_CHANGE_REQUEST_TYPE
                                        + "\"/>"
                                        + "<oslc:usage rdf:resource=\"http://open-services.net/ns/cm#task\"/>"
                                        + "<oslc:resourceShape rdf:resource=\"" + shapeUrl + "\"/>"
                                        + "</oslc:CreationFactory>"
                                        + "</oslc:creationFactory>"
                                        + "</oslc:Service>"
                                        + "</oslc:service>"
                                        + "</oslc:ServiceProvider>"
                                        + "</rdf:RDF>"));

        // 4. Query Capability (Scenario A)
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath(context + "/oslc/contexts/_12345/workitems/query")
                        .withQueryStringParameter("oslc.where", "oslc_cm:closed=false"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">"
                                        + "<oslc:ResponseInfo rdf:about=\"" + queryCapabilityUrl + "\">"
                                        + "<oslc:totalCount>0</oslc:totalCount>"
                                        + "</oslc:ResponseInfo>"
                                        + "</rdf:RDF>"));

        // 5. Query Capability (Scenario B)
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath(context + "/oslc/contexts/_12345/workitems/query")
                        .withQueryStringParameter("oslc.where", "dcterms:identifier=7"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">"
                                        + "<oslc:ResponseInfo rdf:about=\"" + queryCapabilityUrl + "\">"
                                        + "<oslc:totalCount>0</oslc:totalCount>"
                                        + "</oslc:ResponseInfo>"
                                        + "</rdf:RDF>"));

        // 6. Resource Shape
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/oslc/contexts/_12345/workitems/shapes/workitem"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\">"
                                        + "<oslc:ResourceShape rdf:about=\"" + shapeUrl + "\">"
                                        + "<oslc:property>"
                                        + "<oslc:Property>"
                                        + "<oslc:propertyDefinition rdf:resource=\"http://jazz.net/xmlns/prod/jazz/rtc/cm/1.0/filedAgainst\"/>"
                                        + "<oslc:allowedValues rdf:resource=\"" + allowedValuesUrl + "\"/>"
                                        + "</oslc:Property>"
                                        + "</oslc:property>"
                                        + "</oslc:ResourceShape>"
                                        + "</rdf:RDF>"));

        // 7. Allowed Values
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath(context + "/oslc/contexts/_12345/workitems/allowedValues/filedAgainst"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">"
                                        + "<oslc:AllowedValues rdf:about=\"" + allowedValuesUrl + "\">"
                                        + "<oslc:allowedValue rdf:resource=\"http://example.com/category/1\"/>"
                                        + "<oslc:allowedValue rdf:resource=\"http://example.com/category/2\"/>"
                                        + "</oslc:AllowedValues>"
                                        + "</rdf:RDF>"));

        // 8. Create Task
        String taskLocation = baseUri + context + "/oslc/workitems/101";
        mockServerClient
                .when(request().withMethod("POST").withPath(context + "/oslc/contexts/_12345/workitems/create"))
                .respond(response().withStatusCode(201).withHeader("Location", taskLocation));

        // 9. Get Task (for update)
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/oslc/workitems/101"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withHeader("ETag", "\"123\"")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:oslc_cm=\"http://open-services.net/ns/cm#\">"
                                        + "<rdf:Description rdf:about=\"" + taskLocation + "\">"
                                        + "<rdf:type rdf:resource=\"" + OSLCConstants.CM_CHANGE_REQUEST_TYPE + "\"/>"
                                        + "<dcterms:title>Implement accessibility in Pet Store application</dcterms:title>"
                                        + "</rdf:Description>"
                                        + "</rdf:RDF>"));

        // 10. Update Task
        mockServerClient
                .when(request().withMethod("PUT").withPath(context + "/oslc/workitems/101"))
                .respond(response().withStatusCode(200));

        // Run the sample
        EWMSample.main(new String[] {
            "-url", baseUri + context, "-user", "ADMIN", "-password", "ADMIN", "-project", projectArea, "--basic"
        });
    }

    @Test
    void etmSample() throws Exception {
        String projectArea = "JKE Banking (Quality Management)";
        String context = "/qm";
        String rootServicesUrl = baseUri + context + "/rootservices";
        String catalogUrl = baseUri + context + "/oslc/qm/catalog";
        String serviceProviderUrl = baseUri + context + "/oslc/contexts/_12345/qm/services.xml";
        String queryCapabilityUrl = baseUri + context + "/oslc/contexts/_12345/qm/query";
        String creationFactoryUrl = baseUri + context + "/oslc/contexts/_12345/qm/create";

        // 1. Rootservices
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/rootservices"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
                                + " xmlns:oslc_qm=\"http://open-services.net/xmlns/qm/1.0/\""
                                + " xmlns:jd=\"http://jazz.net/xmlns/prod/jazz/discovery/1.0/\""
                                + " xmlns:jfs=\"http://jazz.net/xmlns/prod/jazz/jfs/1.0/\">"
                                + "<rdf:Description rdf:about=\"" + rootServicesUrl + "\">"
                                + "<oslc_qm:qmServiceProviders rdf:resource=\"" + catalogUrl + "\"/>"
                                + "<jfs:oauthRequestTokenUrl rdf:resource=\"" + baseUri + "/jts/oauth/requestToken\"/>"
                                + "<jfs:oauthUserAuthorizationUrl rdf:resource=\"" + baseUri
                                + "/jts/oauth/authorize\"/>"
                                + "<jfs:oauthAccessTokenUrl rdf:resource=\"" + baseUri + "/jts/oauth/accessToken\"/>"
                                + "</rdf:Description>"
                                + "</rdf:RDF>"));

        // 2. Catalog
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/oslc/qm/catalog"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:dcterms=\"http://purl.org/dc/terms/\">"
                                        + "<oslc:ServiceProviderCatalog rdf:about=\"" + catalogUrl + "\">"
                                        + "<oslc:serviceProvider>"
                                        + "<oslc:ServiceProvider rdf:about=\"" + serviceProviderUrl + "\">"
                                        + "<dcterms:title>" + projectArea + "</dcterms:title>"
                                        + "</oslc:ServiceProvider>"
                                        + "</oslc:serviceProvider>"
                                        + "</oslc:ServiceProviderCatalog>"
                                        + "</rdf:RDF>"));

        // 3. Service Provider
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/oslc/contexts/_12345/qm/services.xml"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:dcterms=\"http://purl.org/dc/terms/\">"
                                        + "<oslc:ServiceProvider rdf:about=\"" + serviceProviderUrl + "\">"
                                        + "<oslc:service>"
                                        + "<oslc:Service>"
                                        + "<oslc:domain rdf:resource=\"" + OSLCConstants.OSLC_QM_V2 + "\"/>"
                                        + "<oslc:queryCapability>"
                                        + "<oslc:QueryCapability>"
                                        + "<oslc:queryBase rdf:resource=\"" + queryCapabilityUrl + "\"/>"
                                        + "<oslc:resourceType rdf:resource=\"" + OSLCConstants.QM_TEST_RESULT_QUERY
                                        + "\"/>"
                                        + "</oslc:QueryCapability>"
                                        + "</oslc:queryCapability>"
                                        + "<oslc:creationFactory>"
                                        + "<oslc:CreationFactory>"
                                        + "<oslc:creation rdf:resource=\"" + creationFactoryUrl + "\"/>"
                                        + "<oslc:resourceType rdf:resource=\"" + OSLCConstants.OSLC_QM_V2 + "TestCase"
                                        + "\"/>"
                                        + "</oslc:CreationFactory>"
                                        + "</oslc:creationFactory>"
                                        + "</oslc:Service>"
                                        + "</oslc:service>"
                                        + "</oslc:ServiceProvider>"
                                        + "</rdf:RDF>"));

        // 4. Query Capability (Scenario A: Query passed TestResults)
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath(context + "/oslc/contexts/_12345/qm/query")
                        .withQueryStringParameter(
                                "oslc.where", "oslc_qm:status=\"com.ibm.rqm.execution.common.state.passed\""))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">"
                                        + "<oslc:ResponseInfo rdf:about=\"" + queryCapabilityUrl + "\">"
                                        + "<oslc:totalCount>0</oslc:totalCount>"
                                        + "</oslc:ResponseInfo>"
                                        + "</rdf:RDF>"));

        // 5. Query Capability (Scenario B: Query specific TestResult)
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath(context + "/oslc/contexts/_12345/qm/query")
                        .withQueryStringParameter(
                                "oslc.where",
                                "dcterms:title=\"Consistent_display_of_currency_Firefox_DB2_WAS_Windows_S1\""))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">"
                                        + "<oslc:ResponseInfo rdf:about=\"" + queryCapabilityUrl + "\">"
                                        + "<oslc:totalCount>0</oslc:totalCount>"
                                        + "</oslc:ResponseInfo>"
                                        + "</rdf:RDF>"));

        // 6. Create TestCase
        String testCaseLocation = baseUri + context + "/oslc/testcases/101";
        mockServerClient
                .when(request().withMethod("POST").withPath(context + "/oslc/contexts/_12345/qm/create"))
                .respond(response().withStatusCode(201).withHeader("Location", testCaseLocation));

        // 7. Get TestCase (for update)
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/oslc/testcases/101"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withHeader("ETag", "\"123\"")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:oslc_qm=\"http://open-services.net/ns/qm#\">"
                                        + "<rdf:Description rdf:about=\"" + testCaseLocation + "\">"
                                        + "<rdf:type rdf:resource=\"" + OSLCConstants.OSLC_QM_V2 + "TestCase\"/>"
                                        + "<dcterms:title>Accessibility verification using a screen reader</dcterms:title>"
                                        + "</rdf:Description>"
                                        + "</rdf:RDF>"));

        // 8. Update TestCase
        mockServerClient
                .when(request().withMethod("PUT").withPath(context + "/oslc/testcases/101"))
                .respond(response().withStatusCode(200));

        // Run the sample
        ETMSample.main(new String[] {
            "-url", baseUri + context, "-user", "ADMIN", "-password", "ADMIN", "-project", projectArea, "--basic"
        });
    }

    @Test
    void ermSample() throws Exception {
        String projectArea = "JKE Banking (Requirements Management)";
        String context = "/rm";
        String rootServicesUrl = baseUri + context + "/rootservices";
        String catalogUrl = baseUri + context + "/oslc/rm/catalog";
        String serviceProviderUrl = baseUri + context + "/oslc/contexts/_12345/rm/services.xml";
        String queryCapabilityUrl = baseUri + context + "/oslc/contexts/_12345/rm/query";
        String creationFactoryUrl = baseUri + context + "/oslc/contexts/_12345/rm/create";
        String userRequirementShapeUrl = baseUri + context + "/types/UserRequirement";
        String collectionShapeUrl = baseUri + context + "/types/Collection";
        String folderUrl = baseUri + context + "/folders/1";

        // 1. Rootservices
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/rootservices"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
                                + " xmlns:oslc_rm=\"http://open-services.net/xmlns/rm/1.0/\""
                                + " xmlns:jd=\"http://jazz.net/xmlns/prod/jazz/discovery/1.0/\""
                                + " xmlns:jfs=\"http://jazz.net/xmlns/prod/jazz/jfs/1.0/\">"
                                + "<rdf:Description rdf:about=\"" + rootServicesUrl + "\">"
                                + "<oslc_rm:rmServiceProviders rdf:resource=\"" + catalogUrl + "\"/>"
                                + "<jfs:oauthRequestTokenUrl rdf:resource=\"" + baseUri + "/jts/oauth/requestToken\"/>"
                                + "<jfs:oauthUserAuthorizationUrl rdf:resource=\"" + baseUri
                                + "/jts/oauth/authorize\"/>"
                                + "<jfs:oauthAccessTokenUrl rdf:resource=\"" + baseUri + "/jts/oauth/accessToken\"/>"
                                + "</rdf:Description>"
                                + "</rdf:RDF>"));

        // 2. Catalog
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/oslc/rm/catalog"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:dcterms=\"http://purl.org/dc/terms/\">"
                                        + "<oslc:ServiceProviderCatalog rdf:about=\"" + catalogUrl + "\">"
                                        + "<oslc:serviceProvider>"
                                        + "<oslc:ServiceProvider rdf:about=\"" + serviceProviderUrl + "\">"
                                        + "<dcterms:title>" + projectArea + "</dcterms:title>"
                                        + "</oslc:ServiceProvider>"
                                        + "</oslc:serviceProvider>"
                                        + "</oslc:ServiceProviderCatalog>"
                                        + "</rdf:RDF>"));

        // 3. Service Provider
        // Need to handle the lookup for shapes which iterates this.
        String spBody =
                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:dcterms=\"http://purl.org/dc/terms/\">"
                        + "<oslc:ServiceProvider rdf:about=\"" + serviceProviderUrl + "\">"
                        + "<oslc:service>"
                        + "<oslc:Service>"
                        + "<oslc:domain rdf:resource=\"" + OSLCConstants.OSLC_RM_V2 + "\"/>"
                        + "<oslc:queryCapability>"
                        + "<oslc:QueryCapability>"
                        + "<oslc:queryBase rdf:resource=\"" + queryCapabilityUrl + "\"/>"
                        + "<oslc:resourceType rdf:resource=\"" + OSLCConstants.RM_REQUIREMENT_TYPE + "\"/>"
                        + "</oslc:QueryCapability>"
                        + "</oslc:queryCapability>"
                        + "<oslc:creationFactory>"
                        + "<oslc:CreationFactory>"
                        + "<oslc:creation rdf:resource=\"" + creationFactoryUrl + "\"/>"
                        + "<oslc:resourceType rdf:resource=\"" + OSLCConstants.RM_REQUIREMENT_TYPE + "\"/>"
                        + "<oslc:resourceShape rdf:resource=\"" + userRequirementShapeUrl + "\"/>"
                        + "</oslc:CreationFactory>"
                        + "</oslc:creationFactory>"
                        + "<oslc:creationFactory>"
                        + "<oslc:CreationFactory>"
                        + "<oslc:creation rdf:resource=\"" + creationFactoryUrl + "\"/>"
                        + "<oslc:resourceType rdf:resource=\"" + OSLCConstants.RM_REQUIREMENT_COLLECTION_TYPE + "\"/>"
                        + "<oslc:resourceShape rdf:resource=\"" + collectionShapeUrl + "\"/>"
                        + "</oslc:CreationFactory>"
                        + "</oslc:creationFactory>"
                        + "</oslc:Service>"
                        + "</oslc:service>"
                        + "</oslc:ServiceProvider>"
                        + "</rdf:RDF>";

        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/oslc/contexts/_12345/rm/services.xml"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(spBody));

        // 4. Shapes
        // User Requirement Shape
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/types/UserRequirement"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:dcterms=\"http://purl.org/dc/terms/\">"
                                        + "<oslc:ResourceShape rdf:about=\"" + userRequirementShapeUrl + "\">"
                                        + "<dcterms:title>User Requirement</dcterms:title>"
                                        + "<oslc:describes rdf:resource=\"" + OSLCConstants.RM_REQUIREMENT_TYPE + "\"/>"
                                        + "</oslc:ResourceShape>"
                                        + "</rdf:RDF>"));

        // Collection Shape
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/types/Collection"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:dcterms=\"http://purl.org/dc/terms/\">"
                                        + "<oslc:ResourceShape rdf:about=\"" + collectionShapeUrl + "\">"
                                        + "<dcterms:title>Collection</dcterms:title>"
                                        + "<oslc:describes rdf:resource=\""
                                        + OSLCConstants.RM_REQUIREMENT_COLLECTION_TYPE
                                        + "\"/>"
                                        + "</oslc:ResourceShape>"
                                        + "</rdf:RDF>"));

        // 5. Create Requirements
        // The sample creates 4 requirements and 1 collection.
        // It uses the same creation factory URL for all.
        String req01Url = baseUri + context + "/oslc/rm/req01";
        String req02Url = baseUri + context + "/oslc/rm/req02";
        String req03Url = baseUri + context + "/oslc/rm/req03";
        String req04Url = baseUri + context + "/oslc/rm/req04";
        String col01Url = baseUri + context + "/oslc/rm/col01";

        // Using Times.once() to sequence the responses matching the sequence in ERMSample.java
        // REQ01
        mockServerClient
                .when(request().withMethod("POST").withPath(context + "/oslc/contexts/_12345/rm/create"), Times.once())
                .respond(response().withStatusCode(201).withHeader("Location", req01Url));
        // REQ02
        mockServerClient
                .when(request().withMethod("POST").withPath(context + "/oslc/contexts/_12345/rm/create"), Times.once())
                .respond(response().withStatusCode(201).withHeader("Location", req02Url));
        // REQ03
        mockServerClient
                .when(request().withMethod("POST").withPath(context + "/oslc/contexts/_12345/rm/create"), Times.once())
                .respond(response().withStatusCode(201).withHeader("Location", req03Url));
        // REQ04
        mockServerClient
                .when(request().withMethod("POST").withPath(context + "/oslc/contexts/_12345/rm/create"), Times.once())
                .respond(response().withStatusCode(201).withHeader("Location", req04Url));
        // Collection
        mockServerClient
                .when(request().withMethod("POST").withPath(context + "/oslc/contexts/_12345/rm/create"), Times.once())
                .respond(response().withStatusCode(201).withHeader("Location", col01Url));

        // 6. Get REQ01
        // Need to return properties including nav:parent (root folder)
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/oslc/rm/req01"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:nav=\"http://com.ibm.rdm/navigation#\" xmlns:rm=\"http://www.ibm.com/xmlns/rdm/rdf/\">"
                                        + "<rdf:Description rdf:about=\"" + req01Url + "\">"
                                        + "<rdf:type rdf:resource=\"" + OSLCConstants.RM_REQUIREMENT_TYPE + "\"/>"
                                        + "<dcterms:title>Req01</dcterms:title>"
                                        + "<nav:parent rdf:resource=\"" + folderUrl + "\"/>"
                                        + "<rm:primaryText>My Primary Text</rm:primaryText>"
                                        + "</rdf:Description>"
                                        + "</rdf:RDF>"));

        // 7. Queries
        // Generic response for queries - empty result or single result just to pass
        String emptyQueryResponse =
                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oslc=\"http://open-services.net/ns/core#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">"
                        + "<oslc:ResponseInfo rdf:about=\"" + queryCapabilityUrl + "\">"
                        + "<oslc:totalCount>0</oslc:totalCount>"
                        + "</oslc:ResponseInfo>"
                        + "</rdf:RDF>";

        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/oslc/contexts/_12345/rm/query"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withBody(emptyQueryResponse));

        // 8. Get REQ03 (for update)
        mockServerClient
                .when(request().withMethod("GET").withPath(context + "/oslc/rm/req03"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/rdf+xml")
                        .withHeader("ETag", "\"123\"")
                        .withBody(
                                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dcterms=\"http://purl.org/dc/terms/\">"
                                        + "<rdf:Description rdf:about=\"" + req03Url + "\">"
                                        + "<rdf:type rdf:resource=\"" + OSLCConstants.RM_REQUIREMENT_TYPE + "\"/>"
                                        + "<dcterms:title>Req03</dcterms:title>"
                                        + "</rdf:Description>"
                                        + "</rdf:RDF>"));

        // 9. Update REQ03
        mockServerClient
                .when(request().withMethod("PUT").withPath(context + "/oslc/rm/req03"))
                .respond(response().withStatusCode(200));

        // Run the sample
        ERMSample.main(new String[] {
            "-url", baseUri + context, "-user", "ADMIN", "-password", "ADMIN", "-project", projectArea, "--basic"
        });
    }
}
