<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<%@page import="org.eclipse.lyo.oslc4j.core.model.ServiceProvider" %>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%
String creationDialogUri = request.getParameter("creationUri");
String height = request.getParameter("height");
String width = request.getParameter("width");
creationDialogUri += "#oslc-core-postMessage-1.0";
%>
<html>
<head>
  <title>Creation Dialog client</title>

  <link href="<c:url value="/static/css/bootstrap-4.0.0-beta.min.css"/>" rel="stylesheet">
  <link href="<c:url value="/static/css/adaptor.css"/>" rel="stylesheet">

  <script src="<c:url value="/static/js/jquery-3.2.1.min.js"/>"></script>
  <script src="<c:url value="/static/js/popper-1.11.0.min.js"/>"></script>
  <script src="<c:url value="/static/js/bootstrap-4.0.0-beta.min.js"/>"></script>
  <script src="<c:url value="/static/js/preview.js"/>"></script>
<style>
#delegatedUI {
  width: <%=width%>;
  height: <%=height%>; 
}
body {
    background-image:url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' version='1.1' height='150px' width='500px'><text x='50' y='150' fill='red' font-size='25'>This is your Application!</text></svg>");
}
</style>
</head>
<body>

<nav class="navbar sticky-top navbar-light bg-light">
  <div class="container">
    <a class="navbar-brand" href="<c:url value="/services/discovery"/>">Discovery</a>
  </div>
</nav>

<div class="container">

  <div class="page-header">
    <h1>Creation Dialog client</h1>
  </div>

  <div class="row">
    <div class="col-md-6 col-md-offset-3">
      <div class="panel panel-primary">
        <div class="panel-heading">Creation Dialog frame</div>
        <div class="panel-body">
          <iframe src="<%= creationDialogUri %>" id="delegatedUI"></iframe>
        </div>
        <div class="panel-footer">
          <p>URI: <em style="word-wrap:break-word;"><%= creationDialogUri %></em></p>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-md-6 col-md-offset-3">
      <div class="panel panel-success">
        <div class="panel-heading">Creation Dialog results</div>
        <div class="panel-body" id="results">
            <ul></ul>
        </div>
      </div>
    </div>
  </div>

</div>

<footer class="footer">
  <div class="container">
    <p class="text-muted">
      OSLC Adaptor was generated using <a href="http://eclipse.org/lyo">Eclipse Lyo</a>.
    </p>
  </div>
</footer>

<script type="text/javascript">
  $(function () {
    function handleMessage(message) {
      var results = JSON.parse(message);
      var firstResult = {
        label: results["oslc:results"][0]["oslc:label"],
        uri: results["oslc:results"][0]["rdf:resource"]
      };
      handleOslcSelection(firstResult);
    }

    function handleOslcSelection(resource) {
        $("#results ul").append('<li><a href="' + resource.uri + '" class="oslc-resource-link"><span>' + resource.label + '</span></a></li>');
        
        var previewLink = $("#results ul > li").last();
        previewLink.popover({
          container: "body",
          content: "Loading...",
          delay: {"show": 120, "hide": 60},
          html: true,
          placement: "auto",
          trigger: "hover"
        });


        previewLink.on("show.bs.popover", function () {
          var uiElem = $(this);
          var popoverElem = uiElem.data('bs.popover');

          $.ajax({
            type: "GET",
            url: resource.uri,
            dataType: "xml",
            xhrFields: {
                withCredentials: true
             },
            accepts: {
              xml: "application/x-oslc-compact+xml"
            },
            success: function (data) {
              try {
                var previewData = parsePreview(data);
                var html = "<iframe src='" + previewData.uri + "' ";
                var w = previewData.width ? previewData.width : "45em";
                var h = previewData.height ? previewData.height : "11em";
                html += " style='border:0px; height:" + h + "; width:" + w + "'";
                html += "></iframe>";

                uiElem.attr('data-original-title', previewData.title);
                uiElem.attr('data-content', html);
                popoverElem.setContent();
              } catch (e) {
                uiElem.attr('data-original-title', "Error");
                uiElem.attr('data-content', '<b>Error parsing preview dialog info</b>');
                popoverElem.setContent();
              }
            },
            error: function (xhr, status, err) {
              uiElem.attr('data-original-title', "Error");
              uiElem.attr('data-content', '<b>Error loading the preview dialog</b>');
              popoverElem.setContent();
            }
          });
        })
      }

      window.addEventListener('message', function (e) {
        var HEADER = "oslc-response:";
        if (e.source == document.getElementById("delegatedUI").contentWindow
            && e.data.indexOf(HEADER) == 0) {
          handleMessage(e.data.slice(HEADER.length));
        }
      }, false);
    });
  </script>

</body>
</html>
