package org.eclipse.lyo.samples.client;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.eclipse.lyo.samples.client.oslc4j.CMSample;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.mockserver.verify.VerificationTimes;

public class CMSampleTest {

    private ClientAndServer mockServer;

    @BeforeEach
    public void startServer() {
        mockServer = ClientAndServer.startClientAndServer(1080);
    }

    @AfterEach
    public void stopServer() {
        mockServer.stop();
    }

    @Test
    public void testMain() throws Exception {
        // 1. Catalog Request
        String catalogBody =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <oslc:ServiceProviderCatalog xmlns:oslc="http://open-services.net/ns/core#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                    <oslc:serviceProvider>
                        <oslc:ServiceProvider rdf:about="http://localhost:1080/services/serviceProvider">
                            <oslc:details rdf:resource="http://localhost:1080/services/serviceProvider"/>
                            <dcterms:title xmlns:dcterms="http://purl.org/dc/terms/">OSLC Lyo Change Management Service Provider</dcterms:title>
                        </oslc:ServiceProvider>
                    </oslc:serviceProvider>
                </oslc:ServiceProviderCatalog>""";

        mockServer
                .when(request().withMethod("GET").withPath("/catalog/1"))
                .respond(response().withStatusCode(200).withBody(catalogBody, MediaType.APPLICATION_XML));

        // 2. Service Provider Request
        String serviceProviderBody =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <oslc:ServiceProvider
                    rdf:about="http://localhost:1080/services/serviceProvider"
                    xmlns:oslc="http://open-services.net/ns/core#"
                    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:dcterms="http://purl.org/dc/terms/">
                    <dcterms:title>OSLC Lyo Change Management Service Provider</dcterms:title>
                    <oslc:service>
                        <oslc:Service>
                            <oslc:domain rdf:resource="http://open-services.net/ns/cm#"/>
                            <oslc:creationFactory>
                                <oslc:CreationFactory>
                                    <oslc:creation rdf:resource="http://localhost:1080/services/creationFactory"/>
                                    <oslc:resourceType rdf:resource="http://open-services.net/ns/cm#ChangeRequest"/>
                                </oslc:CreationFactory>
                            </oslc:creationFactory>
                            <oslc:queryCapability>
                                <oslc:QueryCapability>
                                    <oslc:queryBase rdf:resource="http://localhost:1080/services/queryCapability"/>
                                    <oslc:resourceType rdf:resource="http://open-services.net/ns/cm#ChangeRequest"/>
                                </oslc:QueryCapability>
                            </oslc:queryCapability>
                        </oslc:Service>
                    </oslc:service>
                </oslc:ServiceProvider>""";

        mockServer
                .when(request().withMethod("GET").withPath("/services/serviceProvider"))
                .respond(response().withStatusCode(200).withBody(serviceProviderBody, MediaType.APPLICATION_XML));

        // 3. Query Capability Request
        String queryResultBody =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF
                    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:oslc="http://open-services.net/ns/core#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
                    <oslc:ResponseInfo rdf:about="http://localhost:1080/services/queryCapability">
                        <dcterms:title xmlns:dcterms="http://purl.org/dc/terms/">Query Results</dcterms:title>
                    </oslc:ResponseInfo>
                    <rdf:Description rdf:about="http://localhost:1080/services/changeRequests/1">
                        <rdf:type rdf:resource="http://open-services.net/ns/cm#ChangeRequest"/>
                        <dcterms:title xmlns:dcterms="http://purl.org/dc/terms/">Change Request 1</dcterms:title>
                    </rdf:Description>
                </rdf:RDF>""";

        mockServer
                .when(request().withMethod("GET").withPath("/services/queryCapability"))
                .respond(response().withStatusCode(200).withBody(queryResultBody, MediaType.APPLICATION_XML));

        // 4. Get single artifact (used in processPagedQueryResults)
        String changeRequestBody1 =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF
                    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:dcterms="http://purl.org/dc/terms/"
                    xmlns:oslc_cm="http://open-services.net/ns/cm#">
                    <oslc_cm:ChangeRequest rdf:about="http://localhost:1080/services/changeRequests/1">
                        <dcterms:identifier>1</dcterms:identifier>
                        <dcterms:title>Change Request 1</dcterms:title>
                        <oslc_cm:status>Open</oslc_cm:status>
                    </oslc_cm:ChangeRequest>
                </rdf:RDF>""";

        mockServer
                .when(request().withMethod("GET").withPath("/services/changeRequests/1"))
                .respond(response().withStatusCode(200).withBody(changeRequestBody1, MediaType.APPLICATION_XML));

        // 5. Get specific ChangeRequest (Scenario B)
        // CMSample.java modified to use localhost:1080 when catalogURL has localhost:1080
        // URL: http://localhost:1080/services/changeRequests/2

        String changeRequestBody2 =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF
                    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:dcterms="http://purl.org/dc/terms/"
                    xmlns:oslc_cm="http://open-services.net/ns/cm#">
                    <oslc_cm:ChangeRequest rdf:about="http://localhost:1080/services/changeRequests/2">
                        <dcterms:identifier>2</dcterms:identifier>
                        <dcterms:title>Change Request 2</dcterms:title>
                        <oslc_cm:status>In Progress</oslc_cm:status>
                    </oslc_cm:ChangeRequest>
                </rdf:RDF>""";

        mockServer
                .when(request().withMethod("GET").withPath("/services/changeRequests/2"))
                .respond(response().withStatusCode(200).withBody(changeRequestBody2, MediaType.APPLICATION_XML));

        // 6. Create ChangeRequest (Scenario C)
        // POST to http://localhost:1080/services/creationFactory

        mockServer
                .when(request().withMethod("POST").withPath("/services/creationFactory"))
                .respond(response()
                        .withStatusCode(201)
                        .withHeader("Location", "http://localhost:1080/services/changeRequests/new"));

        // 7. Update ChangeRequest (Scenario C)
        // PUT to http://localhost:1080/services/changeRequests/new

        String newChangeRequestBody =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF
                    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:dcterms="http://purl.org/dc/terms/"
                    xmlns:oslc_cm="http://open-services.net/ns/cm#">
                    <oslc_cm:ChangeRequest rdf:about="http://localhost:1080/services/changeRequests/new">
                        <dcterms:identifier>new</dcterms:identifier>
                        <dcterms:title>New Change Request</dcterms:title>
                        <oslc_cm:status>Open</oslc_cm:status>
                    </oslc_cm:ChangeRequest>
                </rdf:RDF>""";

        mockServer
                .when(request().withMethod("PUT").withPath("/services/changeRequests/new"))
                .respond(response().withStatusCode(200).withBody(newChangeRequestBody, MediaType.APPLICATION_XML));

        // Run the sample
        String[] args = {
            "-catalogURL", "http://localhost:1080/catalog/1",
            "-providerTitle", "OSLC Lyo Change Management Service Provider",
            "-user", "fred",
            "-password", "pasw0rd",
            "-changeRequestURL", "http://localhost:1080/services/changeRequests/2"
        };
        CMSample.main(args);

        // Verification
        mockServer.verify(request().withMethod("GET").withPath("/catalog/1"), VerificationTimes.once());
        mockServer.verify(
                request().withMethod("GET").withPath("/services/serviceProvider"), VerificationTimes.atLeast(1));
        mockServer.verify(request().withMethod("GET").withPath("/services/queryCapability"), VerificationTimes.once());
        mockServer.verify(request().withMethod("GET").withPath("/services/changeRequests/2"), VerificationTimes.once());
        mockServer.verify(request().withMethod("POST").withPath("/services/creationFactory"), VerificationTimes.once());
        mockServer.verify(
                request().withMethod("PUT").withPath("/services/changeRequests/new"), VerificationTimes.once());
    }
}
