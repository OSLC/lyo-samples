╔═ polarionSnapshot ═╗
=== REPORT ===
PolarionSample.Report(rootServicesUrl=http://localhost:PORT/polarion/oslc/rootservices, catalogUrl=http://localhost:PORT/polarion/oslc/catalog, serviceProviderUrl=http://localhost:PORT/polarion/oslc/services/projects/OslcDev, queryResultCount=1, sampleResourceUrl=http://localhost:PORT/polarion/oslc/services/projects/OslcDev/workitems/OD-400)

=== HTTP REQUESTS ===
GET /polarion/oslc/rootservices
Authorization: Bearer my-token
--------------------------------------------------
GET /polarion/oslc/rootservices
Authorization: Bearer my-token
--------------------------------------------------
GET /polarion/oslc/catalog
Authorization: Bearer my-token
--------------------------------------------------
GET /polarion/oslc/services/projects/OslcDev
Authorization: Bearer my-token
--------------------------------------------------
GET /polarion/oslc/services/projects/OslcDev/query
Authorization: Bearer my-token
--------------------------------------------------
GET /polarion/oslc/services/projects/OslcDev/workitems/OD-400
Authorization: Bearer my-token
--------------------------------------------------

=== MOCKED RESPONSES (Fixtures) ===
--- catalog.xml ---
<http://localhost:PORT/polarion/oslc/catalog> <http://open-services.net/ns/core#serviceProvider> <http://localhost:PORT/polarion/oslc/services/projects/OslcDev> .
<http://localhost:PORT/polarion/oslc/catalog> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ServiceProviderCatalog> .
<http://localhost:PORT/polarion/oslc/services/projects/OslcDev> <http://purl.org/dc/terms/title> "OslcDev" .
<http://localhost:PORT/polarion/oslc/services/projects/OslcDev> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ServiceProvider> .

--- query_response.xml ---
<http://localhost:PORT/polarion/oslc/services/projects/OslcDev/query> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ResponseInfo> .
<http://localhost:PORT/polarion/oslc/services/projects/OslcDev/query> <http://www.w3.org/2000/01/rdf-schema#member> <http://localhost:PORT/polarion/oslc/services/projects/OslcDev/workitems/OD-400> .

--- resource.xml ---
<http://localhost:PORT/polarion/oslc/services/projects/OslcDev/workitems/OD-400> <http://purl.org/dc/terms/description> "Sample Requirement description" .
<http://localhost:PORT/polarion/oslc/services/projects/OslcDev/workitems/OD-400> <http://purl.org/dc/terms/title> "OD-400 Requirement" .
<http://localhost:PORT/polarion/oslc/services/projects/OslcDev/workitems/OD-400> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/rm#Requirement> .

--- rootservices.xml ---
<http://localhost:PORT/polarion/oslc/rootservices> <http://jazz.net/xmlns/prod/jazz/jfs/1.0/oauthAccessTokenUrl> <http://localhost:PORT/polarion/oauth/accessToken> .
<http://localhost:PORT/polarion/oslc/rootservices> <http://jazz.net/xmlns/prod/jazz/jfs/1.0/oauthRequestTokenUrl> <http://localhost:PORT/polarion/oauth/requestToken> .
<http://localhost:PORT/polarion/oslc/rootservices> <http://jazz.net/xmlns/prod/jazz/jfs/1.0/oauthUserAuthorizationUrl> <http://localhost:PORT/polarion/oauth/authorize> .
<http://localhost:PORT/polarion/oslc/rootservices> <http://open-services.net/xmlns/cm/1.0/cmServiceProviders> <http://localhost:PORT/polarion/oslc/catalog> .

--- serviceprovider.xml ---
<http://localhost:PORT/polarion/oslc/services/projects/OslcDev> <http://open-services.net/ns/core#service> _:Bb2 .
<http://localhost:PORT/polarion/oslc/services/projects/OslcDev> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#ServiceProvider> .
_:Bb1 <http://open-services.net/ns/core#queryBase> <http://localhost:PORT/polarion/oslc/services/projects/OslcDev/query> .
_:Bb1 <http://open-services.net/ns/core#resourceType> <http://open-services.net/ns/rm#Requirement> .
_:Bb1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#QueryCapability> .
_:Bb2 <http://open-services.net/ns/core#domain> <http://open-services.net/ns/rm#> .
_:Bb2 <http://open-services.net/ns/core#queryCapability> _:Bb1 .
_:Bb2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://open-services.net/ns/core#Service> .


╔═ [end of file] ═╗
