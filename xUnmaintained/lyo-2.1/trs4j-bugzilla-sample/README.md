## Getting started

1. Run the Bugzilla server using Docker: `docker run -p 80:80 --rm smarx008/bugzilla-dev-lyo`. 
2. Run `mvn clean jetty:run-war`
3. Open `http://localhost:8085/OSLC4JBugzilla/services/catalog` (login: `admin`, password: `password`)

## Problems

"Untrusted Authentication Request" from Bugzilla when using QC. Listing SPs is fine.