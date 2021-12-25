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

 
<h2>If working with OAuth, you need to make sure you know your Consumer key/secret.</h2>
A consumer/application would need to first register as a consumer with the target server, in order to be able to access its information. This is a one-off registration.<br>
If you don't know your consumer key/secret, follow the 3 steps below to acquire new ones.

<ul>
<li>consumerKey: <strong><%if(!StringUtils.isEmpty(consumerKey)) {%><%=consumerKey%><%} else {%>missing<%}%></strong></li>
<li>consumerSecret: <strong><%if(!StringUtils.isEmpty(consumerSecret)) {%><%=consumerSecret%><%} else {%>missing<%}%></strong></li>
</ul>

<br>
<strong>Step 1.</strong> Use this form to acquire register as a consumer. You can decide on any consumer name & secret.
<form action="<%= request.getContextPath() %>/services/discovery/requestconsumerkey">
  rootServicesUrl (necessary):<br>
  <input type="text" name="rootServicesUrl" size="75" <%if(!StringUtils.isEmpty(rootServicesUrl)) {%>value="<%=rootServicesUrl%>"<%}%>><br>
  <input type="checkbox" name="selfAssignedSSL" value="true" <%if(!StringUtils.isEmpty(selfAssignedSSL)) {%>checked<%}%>> Server configured with a self-assigned SSL certificate<br>
  consumerName:<br>
  <input type="text" name="consumerName" <%if(!StringUtils.isEmpty(consumerName)) {%>value="<%=consumerName%>"<%}%>><br>
  consumerSecret:<br>
  <input type="text" name="consumerSecret" <%if(!StringUtils.isEmpty(consumerSecret)) {%>value="<%=consumerSecret%>"<%}%>><br>
  <input type="submit" value="Request Key">
</form>

<br>
<strong>Step 2.</strong> Once you obtain the consumer key/secret, make sure you your consumer key is approved by the provider.
<ul>
<%if(!StringUtils.isEmpty(approveKeyUrl)) {%>
<li>It seems that you have already requested a consumer key. Now you need to make sure this key is approved.</li>
<li>Click <a href="<%=approveKeyUrl%>" target="_blank">here</a> to login to server (as admin) and approve this consumer.</li>
<%}%>
</ul>

<br>
<strong>Step 3.</strong> Now record the consumer key & secret above, and use them when discovering OSLC services.

<footer class="footer">
  <div class="container">
    <p class="text-muted">
      With the support of <a href="http://eclipse.org/lyo">Eclipse Lyo</a>.
    </p>
  </div>
</footer>
</body>
</html>
