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
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:j.0="http://open-services.net/ns/rm#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:j.1="http://jazz.net/ns/rm#">
  <rdf:Statement>
    <dcterms:title>Link in REQ01</dcterms:title>
    <rdf:object rdf:resource="http://google.com"/>
    <rdf:predicate rdf:resource="http://open-services.net/ns/rm#implementedBy"/>
    <rdf:subject>
      <j.0:Requirement>
        <j.1:primaryText>&lt;div xmlns="http://www.w3.org/1999/xhtml"&gt;My Primary Text&lt;/div&gt;</j.1:primaryText>
        <j.0:implementedBy rdf:resource="http://google.com"/>
        <oslc:instanceShape rdf:resource="http://localhost:PORT/rm/types/UserRequirement"/>
        <dcterms:title rdf:parseType="Literal">Req01</dcterms:title>
        <dcterms:description rdf:parseType="Literal">Created By EclipseLyo</dcterms:description>
      </j.0:Requirement>
    </rdf:subject>
  </rdf:Statement>
</rdf:RDF>

--------------------------------------------------
POST /rm/oslc/contexts/_1/rm/create
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:j.0="http://open-services.net/ns/rm#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
  <rdf:Statement>
    <dcterms:title>Link in REQ02</dcterms:title>
    <rdf:object rdf:resource="http://bancomer.com"/>
    <rdf:predicate rdf:resource="http://open-services.net/ns/rm#validatedBy"/>
    <rdf:subject>
      <j.0:Requirement>
        <oslc:instanceShape rdf:resource="http://localhost:PORT/rm/types/UserRequirement"/>
        <j.0:validatedBy rdf:resource="http://bancomer.com"/>
        <dcterms:title rdf:parseType="Literal">Req02</dcterms:title>
        <dcterms:description rdf:parseType="Literal">Created By EclipseLyo</dcterms:description>
      </j.0:Requirement>
    </rdf:subject>
  </rdf:Statement>
</rdf:RDF>

--------------------------------------------------
POST /rm/oslc/contexts/_1/rm/create
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:j.0="http://open-services.net/ns/rm#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
  <rdf:Statement>
    <dcterms:title>Link in REQ03</dcterms:title>
    <rdf:object rdf:resource="http://outlook.com"/>
    <rdf:predicate rdf:resource="http://open-services.net/ns/rm#validatedBy"/>
    <rdf:subject>
      <j.0:Requirement>
        <oslc:instanceShape rdf:resource="http://localhost:PORT/rm/types/UserRequirement"/>
        <j.0:validatedBy rdf:resource="http://outlook.com"/>
        <dcterms:title rdf:parseType="Literal">Req03</dcterms:title>
        <dcterms:description rdf:parseType="Literal">Created By EclipseLyo</dcterms:description>
      </j.0:Requirement>
    </rdf:subject>
  </rdf:Statement>
</rdf:RDF>

--------------------------------------------------
POST /rm/oslc/contexts/_1/rm/create
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:j.0="http://open-services.net/ns/rm#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
  <j.0:Requirement>
    <oslc:instanceShape rdf:resource="http://localhost:PORT/rm/types/UserRequirement"/>
    <dcterms:title rdf:parseType="Literal">Req04</dcterms:title>
    <dcterms:description rdf:parseType="Literal">Created By EclipseLyo</dcterms:description>
  </j.0:Requirement>
</rdf:RDF>

--------------------------------------------------
POST /rm/oslc/contexts/_1/rm/create
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:j.0="http://open-services.net/ns/rm#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
  <j.0:RequirementCollection>
    <oslc:instanceShape rdf:resource="http://localhost:PORT/rm/types/Collection"/>
    <dcterms:title rdf:parseType="Literal">Collection01</dcterms:title>
    <dcterms:description rdf:parseType="Literal">Created By EclipseLyo</dcterms:description>
    <j.0:uses rdf:resource="http://localhost:PORT/rm/oslc/rm/req01"/>
  </j.0:RequirementCollection>
</rdf:RDF>

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
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:j.0="http://open-services.net/ns/rm#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:j.1="http://com.ibm.rdm/navigation#"
    xmlns:j.2="http://www.ibm.com/xmlns/rdm/rdf/">
  <rdf:Statement>
    <dcterms:title>Link created by an Eclipse Lyo user</dcterms:title>
    <rdf:object rdf:resource="http://google.com"/>
    <rdf:predicate rdf:resource="http://open-services.net/ns/rm#implementedBy"/>
    <rdf:subject>
      <j.0:Requirement rdf:about="http://localhost:PORT/rm/oslc/rm/req01">
        <j.2:primaryText>My Primary Text</j.2:primaryText>
        <j.1:parent rdf:resource="http://localhost:PORT/rm/folders/1"/>
        <j.0:implementedBy rdf:resource="http://google.com"/>
        <dcterms:title rdf:parseType="Literal">My new Title</dcterms:title>
      </j.0:Requirement>
    </rdf:subject>
  </rdf:Statement>
</rdf:RDF>

--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/query
--------------------------------------------------
GET /rm/oslc/contexts/_1/rm/query
--------------------------------------------------

=== MOCKED RESPONSES (Fixtures) ===
--- catalog_rm.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:dcterms="http://purl.org/dc/terms/">
  <oslc:ServiceProviderCatalog rdf:about="http://localhost:PORT/rm/oslc/catalog">
    <dcterms:title>Requirements Management Catalog</dcterms:title>
    <oslc:serviceProvider>
      <oslc:ServiceProvider rdf:about="http://localhost:PORT/rm/oslc/contexts/_1/rm/services.xml">
        <dcterms:title>JKE Banking (Requirements Management)</dcterms:title>
      </oslc:ServiceProvider>
    </oslc:serviceProvider>
  </oslc:ServiceProviderCatalog>
</rdf:RDF>

--- query_empty.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#">
  <oslc:ResponseInfo rdf:about="http://localhost:PORT/rm/oslc/contexts/_1/rm/query">
    <oslc:totalCount>0</oslc:totalCount>
  </oslc:ResponseInfo>
</rdf:RDF>

--- resource_requirement.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:nav="http://com.ibm.rdm/navigation#"
    xmlns:rm="http://www.ibm.com/xmlns/rdm/rdf/">
  <rdf:Description rdf:about="http://localhost:PORT/rm/oslc/rm/req01">
    <rdf:type rdf:resource="http://open-services.net/ns/rm#Requirement"/>
    <dcterms:title>Req01</dcterms:title>
    <nav:parent rdf:resource="http://localhost:PORT/rm/folders/1"/>
    <rm:primaryText>My Primary Text</rm:primaryText>
  </rdf:Description>
</rdf:RDF>

--- rootservices_rm.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc_rm="http://open-services.net/xmlns/rm/1.0/"
    xmlns:jfs="http://jazz.net/xmlns/prod/jazz/jfs/1.0/">
  <rdf:Description rdf:about="http://localhost:PORT/rm">
    <oslc_rm:rmServiceProviders rdf:resource="http://localhost:PORT/rm/oslc/catalog"/>
    <jfs:oauthRequestTokenUrl rdf:resource="http://localhost:PORT/jts/oauth/requestToken"/>
    <jfs:oauthUserAuthorizationUrl rdf:resource="http://localhost:PORT/jts/oauth/authorize"/>
    <jfs:oauthAccessTokenUrl rdf:resource="http://localhost:PORT/jts/oauth/accessToken"/>
  </rdf:Description>
</rdf:RDF>

--- serviceprovider_rm.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:dcterms="http://purl.org/dc/terms/">
  <oslc:ServiceProvider rdf:about="http://localhost:PORT/rm/oslc/contexts/_1/rm/services.xml">
    <dcterms:title>JKE Banking (Requirements Management)</dcterms:title>
    <oslc:service>
      <oslc:Service>
        <oslc:domain rdf:resource="http://open-services.net/ns/rm#"/>
        <oslc:queryCapability>
          <oslc:QueryCapability>
            <dcterms:title>Query Requirements</dcterms:title>
            <oslc:queryBase rdf:resource="http://localhost:PORT/rm/oslc/contexts/_1/rm/query"/>
            <oslc:resourceType rdf:resource="http://open-services.net/ns/rm#Requirement"/>
          </oslc:QueryCapability>
        </oslc:queryCapability>
        <oslc:creationFactory>
          <oslc:CreationFactory>
            <dcterms:title>Create Requirement</dcterms:title>
            <oslc:creation rdf:resource="http://localhost:PORT/rm/oslc/contexts/_1/rm/create"/>
            <oslc:resourceType rdf:resource="http://open-services.net/ns/rm#Requirement"/>
            <oslc:resourceShape rdf:resource="http://localhost:PORT/rm/types/UserRequirement"/>
          </oslc:CreationFactory>
        </oslc:creationFactory>
        <oslc:creationFactory>
          <oslc:CreationFactory>
             <dcterms:title>Create Collection</dcterms:title>
             <oslc:creation rdf:resource="http://localhost:PORT/rm/oslc/contexts/_1/rm/create"/>
             <oslc:resourceType rdf:resource="http://open-services.net/ns/rm#RequirementCollection"/>
             <oslc:resourceShape rdf:resource="http://localhost:PORT/rm/types/Collection"/>
          </oslc:CreationFactory>
        </oslc:creationFactory>
      </oslc:Service>
    </oslc:service>
  </oslc:ServiceProvider>
</rdf:RDF>

--- shape_collection.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:dcterms="http://purl.org/dc/terms/">
  <oslc:ResourceShape rdf:about="http://localhost:PORT/rm/types/Collection">
    <dcterms:title>Collection</dcterms:title>
    <oslc:describes rdf:resource="http://open-services.net/ns/rm#RequirementCollection"/>
  </oslc:ResourceShape>
</rdf:RDF>

--- shape_requirement.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:dcterms="http://purl.org/dc/terms/">
  <oslc:ResourceShape rdf:about="http://localhost:PORT/rm/types/UserRequirement">
    <dcterms:title>User Requirement</dcterms:title>
    <oslc:describes rdf:resource="http://open-services.net/ns/rm#Requirement"/>
    <oslc:property>
      <oslc:Property>
        <oslc:name>title</oslc:name>
        <oslc:propertyDefinition rdf:resource="http://purl.org/dc/terms/title"/>
        <oslc:occurs rdf:resource="http://open-services.net/ns/core#Exactly-one"/>
        <oslc:valueType rdf:resource="http://www.w3.org/2001/XMLSchema#string"/>
      </oslc:Property>
    </oslc:property>
  </oslc:ResourceShape>
</rdf:RDF>


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
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:j.0="http://open-services.net/ns/qm#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
  <rdf:Statement>
    <dcterms:title>Implement accessibility in Pet Store application</dcterms:title>
    <rdf:object rdf:resource="http://cmprovider/changerequest/1"/>
    <rdf:predicate rdf:resource="http://open-services.net/ns/qm#testsChangeRequest"/>
    <rdf:subject>
      <j.0:TestCase>
        <dcterms:title rdf:parseType="Literal">Accessibility verification using a screen reader</dcterms:title>
        <j.0:testsChangeRequest rdf:resource="http://cmprovider/changerequest/1"/>
        <dcterms:description rdf:parseType="Literal">This test case uses a screen reader application to ensure that the web browser content fully complies with accessibility standards</dcterms:description>
      </j.0:TestCase>
    </rdf:subject>
  </rdf:Statement>
</rdf:RDF>

--------------------------------------------------
GET /qm/oslc/qm/tc1
--------------------------------------------------
PUT /qm/oslc/qm/tc1
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:j.0="http://open-services.net/ns/qm#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
  <j.0:TestCase rdf:about="http://localhost:PORT/qm/oslc/qm/tc1">
    <dcterms:title rdf:parseType="Literal">TC (updated)</dcterms:title>
  </j.0:TestCase>
</rdf:RDF>

--------------------------------------------------

=== MOCKED RESPONSES (Fixtures) ===
--- catalog_qm.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:dcterms="http://purl.org/dc/terms/">
  <oslc:ServiceProviderCatalog rdf:about="http://localhost:PORT/qm/oslc/catalog">
    <dcterms:title>Quality Management Catalog</dcterms:title>
    <oslc:serviceProvider>
      <oslc:ServiceProvider rdf:about="http://localhost:PORT/qm/oslc/contexts/_1/qm/services.xml">
        <dcterms:title>JKE Banking (Quality Management)</dcterms:title>
      </oslc:ServiceProvider>
    </oslc:serviceProvider>
  </oslc:ServiceProviderCatalog>
</rdf:RDF>

--- query_empty.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#">
  <oslc:ResponseInfo rdf:about="http://localhost:PORT/qm/oslc/contexts/_1/qm/query">
    <oslc:totalCount>0</oslc:totalCount>
  </oslc:ResponseInfo>
</rdf:RDF>

--- resource_testcase.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:oslc_qm="http://open-services.net/ns/qm#">
  <rdf:Description rdf:about="http://localhost:PORT/qm/oslc/qm/tc1">
    <rdf:type rdf:resource="http://open-services.net/ns/qm#TestCase"/>
    <dcterms:title>TC</dcterms:title>
  </rdf:Description>
</rdf:RDF>

--- rootservices_qm.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc_qm="http://open-services.net/xmlns/qm/1.0/"
    xmlns:jfs="http://jazz.net/xmlns/prod/jazz/jfs/1.0/">
  <rdf:Description rdf:about="http://localhost:PORT/qm">
    <oslc_qm:qmServiceProviders rdf:resource="http://localhost:PORT/qm/oslc/catalog"/>
    <jfs:oauthRequestTokenUrl rdf:resource="http://localhost:PORT/jts/oauth/requestToken"/>
    <jfs:oauthUserAuthorizationUrl rdf:resource="http://localhost:PORT/jts/oauth/authorize"/>
    <jfs:oauthAccessTokenUrl rdf:resource="http://localhost:PORT/jts/oauth/accessToken"/>
  </rdf:Description>
</rdf:RDF>

--- serviceprovider_qm.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:dcterms="http://purl.org/dc/terms/">
  <oslc:ServiceProvider rdf:about="http://localhost:PORT/qm/oslc/contexts/_1/qm/services.xml">
    <dcterms:title>JKE Banking (Quality Management)</dcterms:title>
    <oslc:service>
      <oslc:Service>
        <oslc:domain rdf:resource="http://open-services.net/ns/qm#"/>
        <oslc:queryCapability>
          <oslc:QueryCapability>
            <dcterms:title>Query Test Plans</dcterms:title>
            <oslc:queryBase rdf:resource="http://localhost:PORT/qm/oslc/contexts/_1/qm/query"/>
            <oslc:resourceType rdf:resource="http://open-services.net/ns/qm#TestResultQuery"/>
          </oslc:QueryCapability>
        </oslc:queryCapability>
        <oslc:creationFactory>
          <oslc:CreationFactory>
            <dcterms:title>Create Test Case</dcterms:title>
            <oslc:creation rdf:resource="http://localhost:PORT/qm/oslc/contexts/_1/qm/create"/>
            <oslc:resourceType rdf:resource="http://open-services.net/ns/qm#TestCase"/>
          </oslc:CreationFactory>
        </oslc:creationFactory>
      </oslc:Service>
    </oslc:service>
  </oslc:ServiceProvider>
</rdf:RDF>


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
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:j.0="http://open-services.net/ns/cm#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:j.1="http://jazz.net/xmlns/prod/jazz/rtc/cm/1.0/">
  <rdf:Statement>
    <dcterms:title>Accessibility verification using a screen reader</dcterms:title>
    <rdf:object rdf:resource="http://qmprovider/testcase/1"/>
    <rdf:predicate rdf:resource="http://open-services.net/ns/cm#testedByTestCase"/>
    <rdf:subject>
      <j.0:ChangeRequest>
        <j.1:filedAgainst rdf:resource="http://example.com/cat2"/>
        <j.0:testedByTestCase rdf:resource="http://qmprovider/testcase/1"/>
        <dcterms:type>task</dcterms:type>
        <dcterms:title rdf:parseType="Literal">Implement accessibility in Pet Store application</dcterms:title>
        <dcterms:description rdf:parseType="Literal">Image elements must provide a description in the 'alt' attribute for consumption by screen readers.</dcterms:description>
      </j.0:ChangeRequest>
    </rdf:subject>
  </rdf:Statement>
</rdf:RDF>

--------------------------------------------------
GET /ccm/oslc/ccm/task1
--------------------------------------------------
PUT /ccm/oslc/ccm/task1
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:j.0="http://open-services.net/ns/cm#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
  <j.0:ChangeRequest rdf:about="http://localhost:PORT/ccm/oslc/ccm/task1">
    <dcterms:title rdf:parseType="Literal">Task (updated)</dcterms:title>
    <dcterms:identifier>1</dcterms:identifier>
  </j.0:ChangeRequest>
</rdf:RDF>

--------------------------------------------------
GET /ccm/oslc/contexts/_1/ccm/services.xml
--------------------------------------------------
GET /ccm/oslc/contexts/_1/ccm/shapes/workitem
--------------------------------------------------
GET /ccm/oslc/contexts/_1/ccm/allowedValues/filedAgainst
--------------------------------------------------
POST /ccm/oslc/contexts/_1/ccm/create
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:j.0="http://open-services.net/ns/cm#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:j.1="http://jazz.net/xmlns/prod/jazz/rtc/cm/1.0/">
  <rdf:Statement>
    <dcterms:title>Global Verifcation Test</dcterms:title>
    <rdf:object rdf:resource="http://qmprovider/testcase/3"/>
    <rdf:predicate rdf:resource="http://open-services.net/ns/cm#testedByTestCase"/>
    <rdf:subject>
      <j.0:ChangeRequest>
        <j.1:filedAgainst rdf:resource="http://example.com/cat1"/>
        <j.0:testedByTestCase rdf:resource="http://qmprovider/testcase/3"/>
        <dcterms:type>defect</dcterms:type>
        <dcterms:title rdf:parseType="Literal">Error logging in</dcterms:title>
        <dcterms:description rdf:parseType="Literal">An error occurred when I tried to log in with a user ID that contained the '@' symbol.</dcterms:description>
      </j.0:ChangeRequest>
    </rdf:subject>
  </rdf:Statement>
</rdf:RDF>

--------------------------------------------------

=== MOCKED RESPONSES (Fixtures) ===
--- allowedvalues_filedAgainst.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
  <oslc:AllowedValues rdf:about="http://localhost:PORT/ccm/oslc/contexts/_1/ccm/allowedValues/filedAgainst">
    <oslc:allowedValue rdf:resource="http://example.com/cat1"/>
    <oslc:allowedValue rdf:resource="http://example.com/cat2"/>
  </oslc:AllowedValues>
</rdf:RDF>

--- catalog_ccm.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:dcterms="http://purl.org/dc/terms/">
  <oslc:ServiceProviderCatalog rdf:about="http://localhost:PORT/ccm/oslc/catalog">
    <dcterms:title>Change Management Catalog</dcterms:title>
    <oslc:serviceProvider>
      <oslc:ServiceProvider rdf:about="http://localhost:PORT/ccm/oslc/contexts/_1/ccm/services.xml">
        <dcterms:title>JKE Banking (Change Management)</dcterms:title>
      </oslc:ServiceProvider>
    </oslc:serviceProvider>
  </oslc:ServiceProviderCatalog>
</rdf:RDF>

--- query_empty.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#">
  <oslc:ResponseInfo rdf:about="http://localhost:PORT/ccm/oslc/contexts/_1/ccm/query">
    <oslc:totalCount>0</oslc:totalCount>
  </oslc:ResponseInfo>
</rdf:RDF>

--- resource_task.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:oslc_cm="http://open-services.net/ns/cm#">
  <rdf:Description rdf:about="http://localhost:PORT/ccm/oslc/ccm/task1">
    <rdf:type rdf:resource="http://open-services.net/ns/cm#ChangeRequest"/>
    <dcterms:title>Task</dcterms:title>
    <dcterms:identifier>1</dcterms:identifier>
  </rdf:Description>
</rdf:RDF>

--- rootservices_ccm.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc_cm="http://open-services.net/xmlns/cm/1.0/"
    xmlns:jfs="http://jazz.net/xmlns/prod/jazz/jfs/1.0/">
  <rdf:Description rdf:about="http://localhost:PORT/ccm">
    <oslc_cm:cmServiceProviders rdf:resource="http://localhost:PORT/ccm/oslc/catalog"/>
    <jfs:oauthRequestTokenUrl rdf:resource="http://localhost:PORT/jts/oauth/requestToken"/>
    <jfs:oauthUserAuthorizationUrl rdf:resource="http://localhost:PORT/jts/oauth/authorize"/>
    <jfs:oauthAccessTokenUrl rdf:resource="http://localhost:PORT/jts/oauth/accessToken"/>
  </rdf:Description>
</rdf:RDF>

--- serviceprovider_ccm.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:dcterms="http://purl.org/dc/terms/">
  <oslc:ServiceProvider rdf:about="http://localhost:PORT/ccm/oslc/contexts/_1/ccm/services.xml">
    <dcterms:title>JKE Banking (Change Management)</dcterms:title>
    <oslc:service>
      <oslc:Service>
        <oslc:domain rdf:resource="http://open-services.net/ns/cm#"/>
        <oslc:queryCapability>
          <oslc:QueryCapability>
            <dcterms:title>Query Change Requests</dcterms:title>
            <oslc:queryBase rdf:resource="http://localhost:PORT/ccm/oslc/contexts/_1/ccm/query"/>
            <oslc:resourceType rdf:resource="http://open-services.net/ns/cm#ChangeRequest"/>
          </oslc:QueryCapability>
        </oslc:queryCapability>
        <oslc:creationFactory>
          <oslc:CreationFactory>
            <dcterms:title>Create Task</dcterms:title>
            <oslc:creation rdf:resource="http://localhost:PORT/ccm/oslc/contexts/_1/ccm/create"/>
            <oslc:resourceType rdf:resource="http://open-services.net/ns/cm#ChangeRequest"/>
            <oslc:usage rdf:resource="http://open-services.net/ns/cm#task"/>
            <oslc:resourceShape rdf:resource="http://localhost:PORT/ccm/oslc/contexts/_1/ccm/shapes/workitem"/>
          </oslc:CreationFactory>
        </oslc:creationFactory>
        <oslc:creationFactory>
          <oslc:CreationFactory>
            <dcterms:title>Create Defect</dcterms:title>
            <oslc:creation rdf:resource="http://localhost:PORT/ccm/oslc/contexts/_1/ccm/create"/>
            <oslc:resourceType rdf:resource="http://open-services.net/ns/cm#ChangeRequest"/>
            <oslc:usage rdf:resource="http://open-services.net/ns/cm#defect"/>
            <oslc:resourceShape rdf:resource="http://localhost:PORT/ccm/oslc/contexts/_1/ccm/shapes/workitem"/>
          </oslc:CreationFactory>
        </oslc:creationFactory>
      </oslc:Service>
    </oslc:service>
  </oslc:ServiceProvider>
</rdf:RDF>

--- shape_workitem.xml ---
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:dcterms="http://purl.org/dc/terms/">
  <oslc:ResourceShape rdf:about="http://localhost:PORT/ccm/oslc/contexts/_1/ccm/shapes/workitem">
    <dcterms:title>Work Item</dcterms:title>
    <oslc:property>
      <oslc:Property>
        <oslc:name>filedAgainst</oslc:name>
        <oslc:propertyDefinition rdf:resource="http://jazz.net/xmlns/prod/jazz/rtc/cm/1.0/filedAgainst"/>
        <oslc:allowedValues rdf:resource="http://localhost:PORT/ccm/oslc/contexts/_1/ccm/allowedValues/filedAgainst"/>
      </oslc:Property>
    </oslc:property>
  </oslc:ResourceShape>
</rdf:RDF>


╔═ [end of file] ═╗
