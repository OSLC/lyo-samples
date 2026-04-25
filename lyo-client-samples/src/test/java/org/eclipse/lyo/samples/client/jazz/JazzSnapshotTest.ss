╔═ ermSnapshot ═╗
=== REPORT ===
ERMSample.Report(req01Url=/oslc/rm/req01, req02Url=/oslc/rm/req01, req03Url=/oslc/rm/req01, req04Url=/oslc/rm/req01, reqColl01Url=/oslc/rm/col01, scenario01Count=0, scenario02Count=0, scenario03Count=0, scenario04Count=0, scenario05Count=0, scenario06Count=0, scenario07Count=0, scenario08Count=0)

=== HTTP REQUESTS ===
GET /rm/rootservices
--------------------------------------------------
GET /rm/oslc/catalog
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/services.xml
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/services.xml
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/services.xml
--------------------------------------------------
GET /rm/types/UserRequirement
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/services.xml
--------------------------------------------------
GET /rm/types/UserRequirement
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/services.xml
--------------------------------------------------
GET /rm/types/Collection
--------------------------------------------------
POST /rm/oslc/contexts/_1/rm/create
_:Bb1 <http://jazz.net/ns/rm#primaryText> "<div xmlns=\"http://www.w3.org/1999/xhtml\">My Primary Text</div>" .
_:Bb1 <http://open-services.net/ns/core#instanceShape> <http://localhost:PORT/rm/types/UserRequirement> .
_:Bb1 <http://open-services.net/ns/rm#implementedBy> <http://google.com> .
_:Bb1 <http://purl.org/dc/terms/description> "Created By EclipseLyo"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://purl.org/dc/terms/title> "Req01"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/rm#Requirement> .
_:Bb2 <http://purl.org/dc/terms/title> "Link in REQ01" .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> <http://google.com> .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> <http://open-services.net/ns/rm#implementedBy> .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> _:Bb1 .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> .
--------------------------------------------------
POST /rm/oslc/contexts/_1/rm/create
_:Bb1 <http://open-services.net/ns/core#instanceShape> <http://localhost:PORT/rm/types/UserRequirement> .
_:Bb1 <http://open-services.net/ns/rm#validatedBy> <http://bancomer.com> .
_:Bb1 <http://purl.org/dc/terms/description> "Created By EclipseLyo"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://purl.org/dc/terms/title> "Req02"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/rm#Requirement> .
_:Bb2 <http://purl.org/dc/terms/title> "Link in REQ02" .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> <http://bancomer.com> .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> <http://open-services.net/ns/rm#validatedBy> .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> _:Bb1 .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> .
--------------------------------------------------
POST /rm/oslc/contexts/_1/rm/create
_:Bb1 <http://open-services.net/ns/core#instanceShape> <http://localhost:PORT/rm/types/UserRequirement> .
_:Bb1 <http://open-services.net/ns/rm#validatedBy> <http://outlook.com> .
_:Bb1 <http://purl.org/dc/terms/description> "Created By EclipseLyo"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://purl.org/dc/terms/title> "Req03"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/rm#Requirement> .
_:Bb2 <http://purl.org/dc/terms/title> "Link in REQ03" .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> <http://outlook.com> .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> <http://open-services.net/ns/rm#validatedBy> .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> _:Bb1 .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> .
--------------------------------------------------
POST /rm/oslc/contexts/_1/rm/create
_:Bb1 <http://open-services.net/ns/core#instanceShape> <http://localhost:PORT/rm/types/UserRequirement> .
_:Bb1 <http://purl.org/dc/terms/description> "Created By EclipseLyo"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://purl.org/dc/terms/title> "Req04"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/rm#Requirement> .
--------------------------------------------------
POST /rm/oslc/contexts/_1/rm/create
_:Bb1 <http://open-services.net/ns/core#instanceShape> <http://localhost:PORT/rm/types/Collection> .
_:Bb1 <http://open-services.net/ns/rm#uses> <http://localhost:PORT/rm/oslc/rm/req01> .
_:Bb1 <http://purl.org/dc/terms/description> "Created By EclipseLyo"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://purl.org/dc/terms/title> "Collection01"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/rm#RequirementCollection> .
--------------------------------------------------
GET /rm/oslc/rm/req01
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/query
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/query
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/query
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/query
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/query
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/query
--------------------------------------------------
GET /rm/oslc/rm/req01
--------------------------------------------------
PUT /rm/oslc/rm/req01
<http://localhost:PORT/rm/oslc/rm/req01> <http://com.ibm.rdm/navigation#parent> <http://localhost:PORT/rm/folders/1> .
<http://localhost:PORT/rm/oslc/rm/req01> <http://open-services.net/ns/rm#implementedBy> <http://google.com> .
<http://localhost:PORT/rm/oslc/rm/req01> <http://purl.org/dc/terms/title> "My new Title"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
<http://localhost:PORT/rm/oslc/rm/req01> <http://www.ibm.com/xmlns/rdm/rdf/primaryText> "My Primary Text" .
<http://localhost:PORT/rm/oslc/rm/req01> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/rm#Requirement> .
_:Bb1 <http://purl.org/dc/terms/title> "Link created by an Eclipse Lyo user" .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> <http://google.com> .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> <http://open-services.net/ns/rm#implementedBy> .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> <http://localhost:PORT/rm/oslc/rm/req01> .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> .
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/query
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/query
--------------------------------------------------

=== MOCKED RESPONSES (Fixtures) ===
--- catalog_rm.xml ---
<http://localhost:PORT/rm/oslc/catalog> <http://open-services.net/ns/core#serviceProvider> <http://localhost:PORT/rm/oslc/contexts/_1/rm/services.xml> .
<http://localhost:PORT/rm/oslc/catalog> <http://purl.org/dc/terms/title> "Requirements Management Catalog" .
<http://localhost:PORT/rm/oslc/catalog> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ServiceProviderCatalog> .
<http://localhost:PORT/rm/oslc/contexts/_1/rm/services.xml> <http://purl.org/dc/terms/title> "JKE Banking (Requirements Management)" .
<http://localhost:PORT/rm/oslc/contexts/_1/rm/services.xml> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ServiceProvider> .

--- query_empty.xml ---
<http://localhost:PORT/rm/oslc/contexts/_1/rm/query> <http://open-services.net/ns/core#totalCount> "0" .
<http://localhost:PORT/rm/oslc/contexts/_1/rm/query> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ResponseInfo> .

--- resource_requirement.xml ---
<http://localhost:PORT/rm/oslc/rm/req01> <http://com.ibm.rdm/navigation#parent> <http://localhost:PORT/rm/folders/1> .
<http://localhost:PORT/rm/oslc/rm/req01> <http://purl.org/dc/terms/title> "Req01" .
<http://localhost:PORT/rm/oslc/rm/req01> <http://www.ibm.com/xmlns/rdm/rdf/primaryText> "My Primary Text" .
<http://localhost:PORT/rm/oslc/rm/req01> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/rm#Requirement> .

--- rootservices_rm.xml ---
<http://localhost:PORT/rm> <http://jazz.net/xmlns/prod/jazz/jfs/1.0/oauthAccessTokenUrl> <http://localhost:PORT/jts/oauth/accessToken> .
<http://localhost:PORT/rm> <http://jazz.net/xmlns/prod/jazz/jfs/1.0/oauthRequestTokenUrl> <http://localhost:PORT/jts/oauth/requestToken> .
<http://localhost:PORT/rm> <http://jazz.net/xmlns/prod/jazz/jfs/1.0/oauthUserAuthorizationUrl> <http://localhost:PORT/jts/oauth/authorize> .
<http://localhost:PORT/rm> <http://open-services.net/xmlns/rm/1.0/rmServiceProviders> <http://localhost:PORT/rm/oslc/catalog> .

--- serviceprovider_rm.xml ---
<http://localhost:PORT/rm/oslc/contexts/_1/rm/services.xml> <http://open-services.net/ns/core#service> _:Bb4 .
<http://localhost:PORT/rm/oslc/contexts/_1/rm/services.xml> <http://purl.org/dc/terms/title> "JKE Banking (Requirements Management)" .
<http://localhost:PORT/rm/oslc/contexts/_1/rm/services.xml> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ServiceProvider> .
_:Bb1 <http://open-services.net/ns/core#creation> <http://localhost:PORT/rm/oslc/contexts/_1/rm/create> .
_:Bb1 <http://open-services.net/ns/core#resourceShape> <http://localhost:PORT/rm/types/Collection> .
_:Bb1 <http://open-services.net/ns/core#resourceType> <http://open-services.net/ns/rm#RequirementCollection> .
_:Bb1 <http://purl.org/dc/terms/title> "Create Collection" .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#CreationFactory> .
_:Bb2 <http://open-services.net/ns/core#creation> <http://localhost:PORT/rm/oslc/contexts/_1/rm/create> .
_:Bb2 <http://open-services.net/ns/core#resourceShape> <http://localhost:PORT/rm/types/UserRequirement> .
_:Bb2 <http://open-services.net/ns/core#resourceType> <http://open-services.net/ns/rm#Requirement> .
_:Bb2 <http://purl.org/dc/terms/title> "Create Requirement" .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#CreationFactory> .
_:Bb3 <http://open-services.net/ns/core#queryBase> <http://localhost:PORT/rm/oslc/contexts/_1/rm/query> .
_:Bb3 <http://open-services.net/ns/core#resourceType> <http://open-services.net/ns/rm#Requirement> .
_:Bb3 <http://purl.org/dc/terms/title> "Query Requirements" .
_:Bb3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#QueryCapability> .
_:Bb4 <http://open-services.net/ns/core#creationFactory> _:Bb1 .
_:Bb4 <http://open-services.net/ns/core#creationFactory> _:Bb2 .
_:Bb4 <http://open-services.net/ns/core#domain> <http://open-services.net/ns/rm#> .
_:Bb4 <http://open-services.net/ns/core#queryCapability> _:Bb3 .
_:Bb4 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#Service> .

--- shape_collection.xml ---
<http://localhost:PORT/rm/types/Collection> <http://open-services.net/ns/core#describes> <http://open-services.net/ns/rm#RequirementCollection> .
<http://localhost:PORT/rm/types/Collection> <http://purl.org/dc/terms/title> "Collection" .
<http://localhost:PORT/rm/types/Collection> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ResourceShape> .

--- shape_requirement.xml ---
<http://localhost:PORT/rm/types/UserRequirement> <http://open-services.net/ns/core#describes> <http://open-services.net/ns/rm#Requirement> .
<http://localhost:PORT/rm/types/UserRequirement> <http://open-services.net/ns/core#property> _:Bb1 .
<http://localhost:PORT/rm/types/UserRequirement> <http://purl.org/dc/terms/title> "User Requirement" .
<http://localhost:PORT/rm/types/UserRequirement> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ResourceShape> .
_:Bb1 <http://open-services.net/ns/core#name> "title" .
_:Bb1 <http://open-services.net/ns/core#occurs> <http://open-services.net/ns/core#Exactly-one> .
_:Bb1 <http://open-services.net/ns/core#propertyDefinition> <http://purl.org/dc/terms/title> .
_:Bb1 <http://open-services.net/ns/core#valueType> <http://www.w3.org/2001/XMLSchema#string> .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#Property> .


╔═ etmSnapshot ═╗
=== REPORT ===
ETMSample.Report(testcaseLocation=/oslc/qm/tc1, scenarioACount=0)

=== HTTP REQUESTS ===
GET /qm/rootservices
--------------------------------------------------
GET /qm/oslc/catalog
--------------------------------------------------
GET /qm/oslc/contexts/_1/qm/services.xml
--------------------------------------------------
GET /qm/oslc/contexts/_1/qm/query
--------------------------------------------------
GET /qm/oslc/contexts/_1/qm/query
--------------------------------------------------
GET /qm/oslc/contexts/_1/qm/services.xml
--------------------------------------------------
POST /qm/oslc/contexts/_1/qm/create
_:Bb1 <http://open-services.net/ns/qm#testsChangeRequest> <http://cmprovider/changerequest/1> .
_:Bb1 <http://purl.org/dc/terms/description> "This test case uses a screen reader application to ensure that the web browser content fully complies with accessibility standards"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://purl.org/dc/terms/title> "Accessibility verification using a screen reader"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/qm#TestCase> .
_:Bb2 <http://purl.org/dc/terms/title> "Implement accessibility in Pet Store application" .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> <http://cmprovider/changerequest/1> .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> <http://open-services.net/ns/qm#testsChangeRequest> .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> _:Bb1 .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> .
--------------------------------------------------
GET /qm/oslc/qm/tc1
--------------------------------------------------
PUT /qm/oslc/qm/tc1
<http://localhost:PORT/qm/oslc/qm/tc1> <http://purl.org/dc/terms/title> "TC (updated)"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
<http://localhost:PORT/qm/oslc/qm/tc1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/qm#TestCase> .
--------------------------------------------------

=== MOCKED RESPONSES (Fixtures) ===
--- catalog_qm.xml ---
<http://localhost:PORT/qm/oslc/catalog> <http://open-services.net/ns/core#serviceProvider> <http://localhost:PORT/qm/oslc/contexts/_1/qm/services.xml> .
<http://localhost:PORT/qm/oslc/catalog> <http://purl.org/dc/terms/title> "Quality Management Catalog" .
<http://localhost:PORT/qm/oslc/catalog> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ServiceProviderCatalog> .
<http://localhost:PORT/qm/oslc/contexts/_1/qm/services.xml> <http://purl.org/dc/terms/title> "JKE Banking (Quality Management)" .
<http://localhost:PORT/qm/oslc/contexts/_1/qm/services.xml> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ServiceProvider> .

--- query_empty.xml ---
<http://localhost:PORT/qm/oslc/contexts/_1/qm/query> <http://open-services.net/ns/core#totalCount> "0" .
<http://localhost:PORT/qm/oslc/contexts/_1/qm/query> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ResponseInfo> .

--- resource_testcase.xml ---
<http://localhost:PORT/qm/oslc/qm/tc1> <http://purl.org/dc/terms/title> "TC" .
<http://localhost:PORT/qm/oslc/qm/tc1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/qm#TestCase> .

--- rootservices_qm.xml ---
<http://localhost:PORT/qm> <http://jazz.net/xmlns/prod/jazz/jfs/1.0/oauthAccessTokenUrl> <http://localhost:PORT/jts/oauth/accessToken> .
<http://localhost:PORT/qm> <http://jazz.net/xmlns/prod/jazz/jfs/1.0/oauthRequestTokenUrl> <http://localhost:PORT/jts/oauth/requestToken> .
<http://localhost:PORT/qm> <http://jazz.net/xmlns/prod/jazz/jfs/1.0/oauthUserAuthorizationUrl> <http://localhost:PORT/jts/oauth/authorize> .
<http://localhost:PORT/qm> <http://open-services.net/xmlns/qm/1.0/qmServiceProviders> <http://localhost:PORT/qm/oslc/catalog> .

--- serviceprovider_qm.xml ---
<http://localhost:PORT/qm/oslc/contexts/_1/qm/services.xml> <http://open-services.net/ns/core#service> _:Bb3 .
<http://localhost:PORT/qm/oslc/contexts/_1/qm/services.xml> <http://purl.org/dc/terms/title> "JKE Banking (Quality Management)" .
<http://localhost:PORT/qm/oslc/contexts/_1/qm/services.xml> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ServiceProvider> .
_:Bb1 <http://open-services.net/ns/core#creation> <http://localhost:PORT/qm/oslc/contexts/_1/qm/create> .
_:Bb1 <http://open-services.net/ns/core#resourceType> <http://open-services.net/ns/qm#TestCase> .
_:Bb1 <http://purl.org/dc/terms/title> "Create Test Case" .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#CreationFactory> .
_:Bb2 <http://open-services.net/ns/core#queryBase> <http://localhost:PORT/qm/oslc/contexts/_1/qm/query> .
_:Bb2 <http://open-services.net/ns/core#resourceType> <http://open-services.net/ns/qm#TestResultQuery> .
_:Bb2 <http://purl.org/dc/terms/title> "Query Test Plans" .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#QueryCapability> .
_:Bb3 <http://open-services.net/ns/core#creationFactory> _:Bb1 .
_:Bb3 <http://open-services.net/ns/core#domain> <http://open-services.net/ns/qm#> .
_:Bb3 <http://open-services.net/ns/core#queryCapability> _:Bb2 .
_:Bb3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#Service> .


╔═ ewmSnapshot ═╗
=== REPORT ===
EWMSample.Report(taskLocation=/oslc/ccm/task1, defectLocation=/oslc/ccm/task1, scenarioACount=0)

=== HTTP REQUESTS ===
GET /ccm/rootservices
--------------------------------------------------
GET /ccm/oslc/catalog
--------------------------------------------------
GET /ccm/oslc/contexts/_1/ccm/services.xml
--------------------------------------------------
GET /ccm/oslc/contexts/_1/ccm/query
--------------------------------------------------
GET /ccm/oslc/contexts/_1/ccm/query
--------------------------------------------------
GET /ccm/oslc/contexts/_1/ccm/services.xml
--------------------------------------------------
GET /ccm/oslc/contexts/_1/ccm/shapes/workitem
--------------------------------------------------
GET /ccm/oslc/contexts/_1/ccm/allowedValues/filedAgainst
--------------------------------------------------
POST /ccm/oslc/contexts/_1/ccm/create
_:Bb1 <http://jazz.net/xmlns/prod/jazz/rtc/cm/1.0/filedAgainst> <http://example.com/cat2> .
_:Bb1 <http://open-services.net/ns/cm#testedByTestCase> <http://qmprovider/testcase/1> .
_:Bb1 <http://purl.org/dc/terms/description> "Image elements must provide a description in the 'alt' attribute for consumption by screen readers."^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://purl.org/dc/terms/title> "Implement accessibility in Pet Store application"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://purl.org/dc/terms/type> "task" .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/cm#ChangeRequest> .
_:Bb2 <http://purl.org/dc/terms/title> "Accessibility verification using a screen reader" .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> <http://qmprovider/testcase/1> .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> <http://open-services.net/ns/cm#testedByTestCase> .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> _:Bb1 .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> .
--------------------------------------------------
GET /ccm/oslc/ccm/task1
--------------------------------------------------
PUT /ccm/oslc/ccm/task1
<http://localhost:PORT/ccm/oslc/ccm/task1> <http://purl.org/dc/terms/identifier> "1" .
<http://localhost:PORT/ccm/oslc/ccm/task1> <http://purl.org/dc/terms/title> "Task (updated)"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
<http://localhost:PORT/ccm/oslc/ccm/task1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/cm#ChangeRequest> .
--------------------------------------------------
GET /ccm/oslc/contexts/_1/ccm/services.xml
--------------------------------------------------
GET /ccm/oslc/contexts/_1/ccm/shapes/workitem
--------------------------------------------------
GET /ccm/oslc/contexts/_1/ccm/allowedValues/filedAgainst
--------------------------------------------------
POST /ccm/oslc/contexts/_1/ccm/create
_:Bb1 <http://jazz.net/xmlns/prod/jazz/rtc/cm/1.0/filedAgainst> <http://example.com/cat1> .
_:Bb1 <http://open-services.net/ns/cm#testedByTestCase> <http://qmprovider/testcase/3> .
_:Bb1 <http://purl.org/dc/terms/description> "An error occurred when I tried to log in with a user ID that contained the '@' symbol."^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://purl.org/dc/terms/title> "Error logging in"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> .
_:Bb1 <http://purl.org/dc/terms/type> "defect" .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/cm#ChangeRequest> .
_:Bb2 <http://purl.org/dc/terms/title> "Global Verification Test" .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> <http://qmprovider/testcase/3> .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> <http://open-services.net/ns/cm#testedByTestCase> .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> _:Bb1 .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> .
--------------------------------------------------

=== MOCKED RESPONSES (Fixtures) ===
--- allowedvalues_filedAgainst.xml ---
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/allowedValues/filedAgainst> <http://open-services.net/ns/core#allowedValue> <http://example.com/cat1> .
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/allowedValues/filedAgainst> <http://open-services.net/ns/core#allowedValue> <http://example.com/cat2> .
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/allowedValues/filedAgainst> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#AllowedValues> .

--- catalog_ccm.xml ---
<http://localhost:PORT/ccm/oslc/catalog> <http://open-services.net/ns/core#serviceProvider> <http://localhost:PORT/ccm/oslc/contexts/_1/ccm/services.xml> .
<http://localhost:PORT/ccm/oslc/catalog> <http://purl.org/dc/terms/title> "Change Management Catalog" .
<http://localhost:PORT/ccm/oslc/catalog> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ServiceProviderCatalog> .
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/services.xml> <http://purl.org/dc/terms/title> "JKE Banking (Change Management)" .
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/services.xml> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ServiceProvider> .

--- query_empty.xml ---
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/query> <http://open-services.net/ns/core#totalCount> "0" .
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/query> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ResponseInfo> .

--- resource_task.xml ---
<http://localhost:PORT/ccm/oslc/ccm/task1> <http://purl.org/dc/terms/identifier> "1" .
<http://localhost:PORT/ccm/oslc/ccm/task1> <http://purl.org/dc/terms/title> "Task" .
<http://localhost:PORT/ccm/oslc/ccm/task1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/cm#ChangeRequest> .

--- rootservices_ccm.xml ---
<http://localhost:PORT/ccm> <http://jazz.net/xmlns/prod/jazz/jfs/1.0/oauthAccessTokenUrl> <http://localhost:PORT/jts/oauth/accessToken> .
<http://localhost:PORT/ccm> <http://jazz.net/xmlns/prod/jazz/jfs/1.0/oauthRequestTokenUrl> <http://localhost:PORT/jts/oauth/requestToken> .
<http://localhost:PORT/ccm> <http://jazz.net/xmlns/prod/jazz/jfs/1.0/oauthUserAuthorizationUrl> <http://localhost:PORT/jts/oauth/authorize> .
<http://localhost:PORT/ccm> <http://open-services.net/xmlns/cm/1.0/cmServiceProviders> <http://localhost:PORT/ccm/oslc/catalog> .

--- serviceprovider_ccm.xml ---
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/services.xml> <http://open-services.net/ns/core#service> _:Bb4 .
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/services.xml> <http://purl.org/dc/terms/title> "JKE Banking (Change Management)" .
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/services.xml> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ServiceProvider> .
_:Bb1 <http://open-services.net/ns/core#creation> <http://localhost:PORT/ccm/oslc/contexts/_1/ccm/create> .
_:Bb1 <http://open-services.net/ns/core#resourceShape> <http://localhost:PORT/ccm/oslc/contexts/_1/ccm/shapes/workitem> .
_:Bb1 <http://open-services.net/ns/core#resourceType> <http://open-services.net/ns/cm#ChangeRequest> .
_:Bb1 <http://open-services.net/ns/core#usage> <http://open-services.net/ns/cm#defect> .
_:Bb1 <http://purl.org/dc/terms/title> "Create Defect" .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#CreationFactory> .
_:Bb2 <http://open-services.net/ns/core#creation> <http://localhost:PORT/ccm/oslc/contexts/_1/ccm/create> .
_:Bb2 <http://open-services.net/ns/core#resourceShape> <http://localhost:PORT/ccm/oslc/contexts/_1/ccm/shapes/workitem> .
_:Bb2 <http://open-services.net/ns/core#resourceType> <http://open-services.net/ns/cm#ChangeRequest> .
_:Bb2 <http://open-services.net/ns/core#usage> <http://open-services.net/ns/cm#task> .
_:Bb2 <http://purl.org/dc/terms/title> "Create Task" .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#CreationFactory> .
_:Bb3 <http://open-services.net/ns/core#queryBase> <http://localhost:PORT/ccm/oslc/contexts/_1/ccm/query> .
_:Bb3 <http://open-services.net/ns/core#resourceType> <http://open-services.net/ns/cm#ChangeRequest> .
_:Bb3 <http://purl.org/dc/terms/title> "Query Change Requests" .
_:Bb3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#QueryCapability> .
_:Bb4 <http://open-services.net/ns/core#creationFactory> _:Bb1 .
_:Bb4 <http://open-services.net/ns/core#creationFactory> _:Bb2 .
_:Bb4 <http://open-services.net/ns/core#domain> <http://open-services.net/ns/cm#> .
_:Bb4 <http://open-services.net/ns/core#queryCapability> _:Bb3 .
_:Bb4 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#Service> .

--- shape_workitem.xml ---
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/shapes/workitem> <http://open-services.net/ns/core#property> _:Bb1 .
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/shapes/workitem> <http://purl.org/dc/terms/title> "Work Item" .
<http://localhost:PORT/ccm/oslc/contexts/_1/ccm/shapes/workitem> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ResourceShape> .
_:Bb1 <http://open-services.net/ns/core#allowedValues> <http://localhost:PORT/ccm/oslc/contexts/_1/ccm/allowedValues/filedAgainst> .
_:Bb1 <http://open-services.net/ns/core#name> "filedAgainst" .
_:Bb1 <http://open-services.net/ns/core#propertyDefinition> <http://jazz.net/xmlns/prod/jazz/rtc/cm/1.0/filedAgainst> .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#Property> .


╔═ [end of file] ═╗
