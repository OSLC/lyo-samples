<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.Service" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.ServiceProviderCatalog" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.ServiceProvider" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.Dialog" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.CreationFactory" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.ResourceShape" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.QueryCapability" %>

<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>

<%
ServiceProviderCatalog fullServiceProviderCatalog = (null == request.getAttribute("fullServiceProviderCatalog") ? null : (ServiceProviderCatalog)request.getAttribute("fullServiceProviderCatalog"));
String serviceProviderCatalogUrl = (null == request.getAttribute("serviceProviderCatalogUrl") ? "" : (String)request.getAttribute("serviceProviderCatalogUrl"));
String rootServicesUrl = (null == request.getAttribute("rootServicesUrl") ? "" : (String)request.getAttribute("rootServicesUrl"));
String urlType = (null == request.getAttribute("urlType") ? "" : (String)request.getAttribute("urlType"));
String selfAssignedSSL = (null == request.getAttribute("selfAssignedSSL") ? "" : (String)request.getAttribute("selfAssignedSSL"));
String username = (null == request.getAttribute("username") ? "" : (String)request.getAttribute("username"));
String password = (null == request.getAttribute("password") ? "" : (String)request.getAttribute("password"));
String consumerKey = (null == request.getAttribute("consumerKey") ? "" : (String)request.getAttribute("consumerKey"));
String consumerSecret = (null == request.getAttribute("consumerSecret") ? "" : (String)request.getAttribute("consumerSecret"));
String serviceProviderTitle = (null == request.getAttribute("serviceProviderTitle") ? "" : (String)request.getAttribute("serviceProviderTitle"));
String oslcResourceType = (null == request.getAttribute("oslcResourceType") ? "" : (String)request.getAttribute("oslcResourceType"));

String consumerName = (null == request.getAttribute("consumerName") ? "" : (String)request.getAttribute("consumerName"));
String approveKeyUrl = (null == request.getAttribute("approveKeyUrl") ? "" : (String)request.getAttribute("approveKeyUrl"));

%>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>SAMPLE DIALOGS</title>
    <link href="<c:url value="/static/css/bootstrap-4.0.0-beta.min.css"/>" rel="stylesheet">
    <link href="<c:url value="/static/css/adaptor.css"/>" rel="stylesheet">

    <script src="<c:url value="/static/js/jquery-3.2.1.min.js"/>"></script>
    <script src="<c:url value="/static/js/popper-1.11.0.min.js"/>"></script>
    <script src="<c:url value="/static/js/bootstrap-4.0.0-beta.min.js"/>"></script>
</head>
<body>
  <div class="container">
    <div class="page-header">
    </div>

<h1> OSLC Client Authentication, Service Discovery & Delegated UIs</h1>

This is a sample OSLC client application that explores the different client authentication alternatives of (1) oauth (2) basic authentication (3) no authentication.<br>
The application also demonstrates how to discover OSLC services.<br>
Finally, it demonstrates how to integrate Delegated-UI iframes into your own web-based application.<br>

<ul>
<li>Service Discovery: The simple java code is based on the <a href="https://github.com/eclipse/lyo.client">OSLC lyo-client SDK</a>. 
Alternatively, one can use the <a href="https://github.com/OSLC/oslc-client">OSLC client API Node.js module</a>.</li>
<li>DUI integration: The code is available as javascript, and hence visible through your web browser.</li>
</ul>

<b>NOTE:</b> 
<ul>
<li>Errors: We don't do much error checking in this application.  See resulting exceptions to work out what goes wrong!</li>
</ul>

<div class="accordion" id="alternatives">
  <div class="card">
    <div class="card-header" id="altOne">
      <h2 class="mb-0">
        <button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">
          Alt1 - Discover through a rootServices URL, with OAuth
        </button>
      </h2>
    </div>
    <div id="collapseOne" class="collapse" aria-labelledby="altOne" data-parent="#alternatives">
      <div class="card-body">
	    <form action="<%= request.getContextPath() %>/services/discovery">
	      rootServicesUrl (necessary):<br>
	      <input type="text" name="rootServicesUrl" size="75" <%if(!StringUtils.isEmpty(rootServicesUrl)) {%>value="<%=rootServicesUrl%>"<%}%>><br>
	      <input type="checkbox" name="selfAssignedSSL" value="true" <%if(!StringUtils.isEmpty(selfAssignedSSL)) {%>checked<%}%>> Server configured with a self-assigned SSL certificate<br>
	      consumerKey: (What is that? <a href="<%=  request.getContextPath() + "/services/discovery/requestconsumerkey" %>" target="_blank">Click here</a> for a walkthrough on how to obtain your consumer key/secret.)<br>
	      <input type="text" name="consumerKey" <%if(!StringUtils.isEmpty(consumerKey)) {%>value="<%=consumerKey%>"<%}%>><br>
	      consumerSecret:<br>
	      <input type="text" name="consumerSecret" <%if(!StringUtils.isEmpty(consumerSecret)) {%>value="<%=consumerSecret%>"<%}%>><br>
	      Filter on ServiceProvider Title:<br>
	      <input type="text" name="serviceProviderTitle" size="75" <%if(!StringUtils.isEmpty(serviceProviderTitle)) {%>value="<%=serviceProviderTitle%>"<%}%>><br>
	      Filter on OSLC Resource Type:<br>
	      <input type="text" name="oslcResourceType" size="75" <%if(!StringUtils.isEmpty(oslcResourceType)) {%>value="<%=oslcResourceType%>"<%}%>><br>
	      <input type="submit" value="Submit">
	    </form>
      </div>
    </div>
  </div>

  <div class="card">
    <div class="card-header" id="altThree">
      <h2 class="mb-0">
        <button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
          Alt2 - Discover through a rootServices URL, with basic authentication
        </button>
      </h2>
    </div>
    <div id="collapseThree" class="collapse" aria-labelledby="altThree" data-parent="#alternatives">
      <div class="card-body">
	    <form action="<%= request.getContextPath() %>/services/discovery/rootWithBasicAuthentication">
	      rootServicesUrl (necessary):<br>
	      <input type="text" name="rootServicesUrl" size="75" <%if(!StringUtils.isEmpty(rootServicesUrl)) {%>value="<%=rootServicesUrl%>"<%}%>><br>
	      <input type="checkbox" name="selfAssignedSSL" value="true" <%if(!StringUtils.isEmpty(selfAssignedSSL)) {%>checked<%}%>> Server configured with a self-assigned SSL certificate<br>
	      username: (if both username & password are empty, we don't adopt authentication. Otherwise, basic authentication is adopted.<br>
	      <input type="text" name="username" <%if(!StringUtils.isEmpty(username)) {%>value="<%=username%>"<%}%>><br>
	      password:<br>
	      <input type="password" name="password" <%if(!StringUtils.isEmpty(password)) {%>value="<%=password%>"<%}%>><br>
	      Filter on ServiceProvider Title:<br>
	      <input type="text" name="serviceProviderTitle" size="75" <%if(!StringUtils.isEmpty(serviceProviderTitle)) {%>value="<%=serviceProviderTitle%>"<%}%>><br>
	      Filter on OSLC Resource Type:<br>
	      <input type="text" name="oslcResourceType" size="75" <%if(!StringUtils.isEmpty(oslcResourceType)) {%>value="<%=oslcResourceType%>"<%}%>><br>
	      <input type="submit" value="Submit">
	    </form>
      </div>
    </div>
  </div>
  
  <div class="card">
    <div class="card-header" id="altFour">
      <h2 class="mb-0">
        <button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseFour" aria-expanded="false" aria-controls="collapseFour">
          Alt3 - Discover through a serviceProviderCatalog, with basic authentication
        </button>
      </h2>
    </div>
    <div id="collapseFour" class="collapse" aria-labelledby="altFour" data-parent="#alternatives">
      <div class="card-body">
	    <form action="<%= request.getContextPath() %>/services/discovery/catalog">
	      serviceProviderCatalogUrl (necessary):<br>
	      <input type="text" name="serviceProviderCatalogUrl" size="75" <%if(!StringUtils.isEmpty(serviceProviderCatalogUrl)) {%>value="<%=serviceProviderCatalogUrl%>"<%}%>><br>
	      <input type="checkbox" name="selfAssignedSSL" value="true" <%if(!StringUtils.isEmpty(selfAssignedSSL)) {%>checked<%}%>> Server configured with a self-assigned SSL certificate<br>
	      username: (if both username & password are empty, we don't adopt authentication. Otherwise, basic authentication is adopted.<br>
	      <input type="text" name="username" <%if(!StringUtils.isEmpty(username)) {%>value="<%=username%>"<%}%>><br>
	      password:<br>
	      <input type="password" name="password" <%if(!StringUtils.isEmpty(password)) {%>value="<%=password%>"<%}%>><br>
	      Filter on ServiceProvider Title:<br>
	      <input type="text" name="serviceProviderTitle" size="75" <%if(!StringUtils.isEmpty(serviceProviderTitle)) {%>value="<%=serviceProviderTitle%>"<%}%>><br>
	      Filter on OSLC Resource Type:<br>
	      <input type="text" name="oslcResourceType" size="75" <%if(!StringUtils.isEmpty(oslcResourceType)) {%>value="<%=oslcResourceType%>"<%}%>><br>
	      <input type="submit" value="Submit">
	    </form>
      </div>
    </div>
  </div>
  
    <div class="card">
    <div class="card-header" id="altFive">
      <h2 class="mb-0">
        <button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseFive" aria-expanded="false" aria-controls="collapseFive">
          Alt4 - Discover through a serviceProviderCatalog, without authentication
        </button>
      </h2>
    </div>
    <div id="collapseFive" class="collapse" aria-labelledby="altFive" data-parent="#alternatives">
      <div class="card-body">
	    <form action="<%= request.getContextPath() %>/services/discovery/catalog">
	      serviceProviderCatalogUrl (necessary):<br>
	      <input type="text" name="serviceProviderCatalogUrl" size="75" <%if(!StringUtils.isEmpty(serviceProviderCatalogUrl)) {%>value="<%=serviceProviderCatalogUrl%>"<%}%>><br>
	      <input type="checkbox" name="selfAssignedSSL" value="true" <%if(!StringUtils.isEmpty(selfAssignedSSL)) {%>checked<%}%>> Server configured with a self-assigned SSL certificate<br>
	      Filter on ServiceProvider Title:<br>
	      <input type="text" name="serviceProviderTitle" size="75" <%if(!StringUtils.isEmpty(serviceProviderTitle)) {%>value="<%=serviceProviderTitle%>"<%}%>><br>
	      Filter on OSLC Resource Type:<br>
	      <input type="text" name="oslcResourceType" size="75" <%if(!StringUtils.isEmpty(oslcResourceType)) {%>value="<%=oslcResourceType%>"<%}%>><br>
	      <input type="submit" value="Submit">
	    </form>
      </div>
    </div>
  </div>  
</div>
<h2>Results</h2>
    <%if (null != fullServiceProviderCatalog) {%>
    <b>Service Provider Catalog:</b> rdf:about=<a href="<%=fullServiceProviderCatalog.getAbout()%>" target="_blank"><%=fullServiceProviderCatalog.getAbout()%></a><br>
    <%
    for (ServiceProvider serviceProvider : fullServiceProviderCatalog.getServiceProviders()) {
        if (0 == serviceProvider.getServices().length) {
            continue;
        }
        %>
        &emsp;<b>Service Provider:</b> Title="<%=serviceProvider.getTitle()%>" rdf:about=<a href="<%=serviceProvider.getAbout()%>" target="_blank"><%=serviceProvider.getAbout()%></a><br>
        <%
        for (Service service:serviceProvider.getServices()) {
            Dialog[] selectionDialogs = service.getSelectionDialogs();
            Dialog[] creationDialogs = service.getCreationDialogs();
            if (0 == (selectionDialogs.length + creationDialogs.length)) {
                continue;
            }
            %>
            &emsp;&emsp;<b>Service:</b> <%= selectionDialogs.length%> SelectionDialogs and <%= creationDialogs.length%> CreationDialogs<br>
            <%
            for (Dialog dialog : selectionDialogs) {
                %>
                &emsp;&emsp;&emsp;<b>Selection Dialog:</b> Title=<%=dialog.getTitle()%>
                <a class="btn btn-primary btn-sm" href="<%= request.getContextPath() %>/selectiondialogsampleclient.jsp?selectionUri=<%= URLEncoder.encode(dialog.getDialog().toString(), "UTF-8") %>&height=<%= dialog.getHintHeight() %>&width=<%= dialog.getHintWidth() %>" role="button" target="_blank">Sample Selection Client</a>
                <br>
                &emsp;&emsp;&emsp;&emsp;hintWidth=<%=dialog.getHintWidth()%> hintHeight=<%=dialog.getHintHeight()%>
                <br>
                &emsp;&emsp;&emsp;&emsp;ResourceTypes=
                <%
            	for (int i = 0; i < dialog.getResourceTypes().length; i++) {
    				URI resourceTypeUri = dialog.getResourceTypes()[i];
  				%>
  				<a href="<%=resourceTypeUri.toString()%>" target="_blank"><%=resourceTypeUri.toString()%></a>
                <%
    			}
				%>
                <br>
                <%
            }
            for (Dialog dialog : creationDialogs) {
                %>
                &emsp;&emsp;&emsp;<b>Creation Dialog:</b> Title=<%=dialog.getTitle()%>
                <a class="btn btn-primary btn-sm" href="<%= request.getContextPath() %>/creationdialogsampleclient.jsp?creationUri=<%= URLEncoder.encode(dialog.getDialog().toString(), "UTF-8") %>&height=<%= dialog.getHintHeight() %>&width=<%= dialog.getHintWidth() %>" role="button" target="_blank">Sample Creation Client</a>
                <br>
                &emsp;&emsp;&emsp;&emsp;hintWidth=<%=dialog.getHintWidth()%> hintHeight=<%=dialog.getHintHeight()%>
                <br>
                &emsp;&emsp;&emsp;&emsp;ResourceTypes=
                <%
            	for (int i = 0; i < dialog.getResourceTypes().length; i++) {
    				URI resourceTypeUri = dialog.getResourceTypes()[i];
  				%>
  				<a href="<%=resourceTypeUri.toString()%>" target="_blank"><%=resourceTypeUri.toString()%></a>
                <%
    			}
				%>
                <br>
                <%
            }
        }
    }
    %>
    <%}%>
  </div>
  <footer class="footer">
    <div class="container">
      <p class="text-muted">
        With the support of <a href="http://eclipse.org/lyo">Eclipse Lyo</a>.
      </p>
    </div>
  </footer>
</body>
</html>
