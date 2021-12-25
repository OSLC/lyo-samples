# Bugzilla adaptor (legacy)

> The Lyo Bugzilla adapter can now be found in the OSLC4JBugzilla project in the Lyo doc Git repository: https://github.com/OSLC/lyo-adaptor-bugzilla

## Getting started

1. Create `oslc4j-bugzilla-sample/src/main/resources/bugz.properties` file, use `oslc4j-bugzilla-sample/src/main/resources/bugz_example.properties` as an example.
1. Run the Bugzilla server using Docker: `docker run -p 80:80 --rm smarx008/bugzilla-dev-lyo`
1. Run the web app: `mvn clean jetty:run-war`
1. Open the OSLC adaptor page http://localhost:8080/OSLC4JBugzilla/services/1/changeRequests (login: `admin`, password: `password`)
1. Create a bug via http://localhost/bugzilla/enter_bug.cgi (login: `admin`, password: `password`)
1. Refresh http://localhost:8080/OSLC4JBugzilla/services/1/changeRequests. You should now see your bug report.

To get the RDF representation:

```
curl --location --request GET 'http://localhost:8080/OSLC4JBugzilla/services/1/changeRequests' \
--header 'Accept: text/turtle;q=1.0,application/rdf+xml;q=0.9,application/n-triples;q=0.8,application/ld+json;q=0.3' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ='
```

Expected response:

```
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:bugz="http://www.bugzilla.org/rdf#"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:oslc_data="http://open-services.net/ns/servicemanagement/1.0/"
    xmlns:oslc="http://open-services.net/ns/core#"
    xmlns:oslc_scm="http://open-services.net/ns/scm#"
    xmlns:oslc_cm="http://open-services.net/ns/cm#"
    xmlns:oslc_qm="http://open-services.net/ns/qm#"
    xmlns:oslc_rm="http://open-services.net/ns/rm#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:foaf="http://xmlns.com/foaf/0.1/" >
    <rdf:Description rdf:about="http://localhost:8080/OSLC4JBugzilla/services/1/changeRequests/1">
        <dcterms:created rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">2021-12-25T01:51:02Z</dcterms:created>
        <dcterms:title rdf:parseType="Literal">test me</dcterms:title>
        <oslc:serviceProvider rdf:resource="http://localhost:8080/OSLC4JBugzilla/services/serviceProviders/1"/>
        <dcterms:identifier>1</dcterms:identifier>
        <bugz:priority>---</bugz:priority>
        <oslc_cm:status>CONFIRMED</oslc_cm:status>
        <bugz:version>unspecified</bugz:version>
        <rdf:type rdf:resource="http://open-services.net/ns/cm#ChangeRequest"/>
        <bugz:operatingSystem>Mac OS</bugz:operatingSystem>
        <oslc_cm:severity>Unclassified</oslc_cm:severity>
        <dcterms:modified rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">2021-12-25T01:51:02Z</dcterms:modified>
        <bugz:platform>PC</bugz:platform>
        <bugz:component>TestComponent</bugz:component>
        <dcterms:contributor rdf:resource="http://localhost:8080/OSLC4JBugzilla/person?mbox=admin"/>
    </rdf:Description>
    <rdf:Description rdf:about="http://100.120.3.53:8080/OSLC4JBugzilla/services/1/changeRequests">
        <rdfs:member rdf:resource="http://localhost:8080/OSLC4JBugzilla/services/1/changeRequests/1"/>
        <oslc:totalCount rdf:datatype="http://www.w3.org/2001/XMLSchema#int">1</oslc:totalCount>
        <rdf:type rdf:resource="http://open-services.net/ns/core#ResponseInfo"/>
    </rdf:Description>
    <rdf:Description rdf:about="http://localhost:8080/OSLC4JBugzilla/person?mbox=admin">
        <rdf:type rdf:resource="http://xmlns.com/foaf/0.1/Person"/>
    </rdf:Description>
</rdf:RDF>
```