<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="java.util.*" %>

<html>
<head>
  <meta charset="utf-8">
  <script src="<%=request.getContextPath()%>/js/jquery-1.8.3.js"></script>
  <script src="<%=request.getContextPath()%>/js/jquery-ui.js"></script>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/style/common.css" type="text/css"/>  
  <link rel="stylesheet" href="<%=request.getContextPath()%>/style/jquery-ui.css" type="text/css"/>
  <script>

  </script>
</head>
<body>

      <h2>HTTP Request Headers</h2>
      <table border="1" cellpadding="4" cellspacing="0">
      <%
         Enumeration names = request.getHeaderNames();
         while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = request.getHeader(name);
      %>
         <tr><td><%= name %></td><td><%= value %></td></tr>
      <%
         }
      %>
      </table>

      <h2>HTTP Request Attributes</h2>
      <table border="1" cellpadding="4" cellspacing="0">
      <%
         names = request.getAttributeNames();
         while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            Object value = request.getAttribute(name);
      %>
         <tr><td><%= name %></td><td><%= value %></td></tr>
      <%
         }
      %>
      </table>

      <h2>HTTP Request Parameters</h2>
      <table border="1" cellpadding="4" cellspacing="0">
      <%
         names = request.getParameterNames();
         while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = request.getParameter(name);
      %>
         <tr><td><%= name %></td><td><%= value %></td></tr>
      <%
         }
      %>
      </table>

</body>
</html>
