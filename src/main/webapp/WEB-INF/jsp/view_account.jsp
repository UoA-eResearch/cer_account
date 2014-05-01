<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>

  <head>
    <meta charset="utf-8">
    <script src="<%=request.getContextPath()%>/js/jquery-1.8.3.js"></script>
    <script src="<%=request.getContextPath()%>/js/jquery-ui.js"></script>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style/common.css" type="text/css" />
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style/jquery-ui.css" type="text/css" />
  </head>

  <body>
    <p>
      <c:if test="${not empty message}">
        <div class="infoblock">${message}</div>
      </c:if>
    </p>
    <p><b>Current details of your account</b>:</p>
    <table cellpadding="5">
      <tbody>
        <tr>
          <td>Full name:</td>
          <td>${person.fullName}</td>
        </tr>
        <c:if test="${not empty person.preferredName}">
          <tr>
            <td>Preferred name:</td>
            <td>${person.preferredName}</td>
          </tr>
        </c:if>
        <tr>
          <td>Institution:</td>
          <td>${person.institution}</td>
        </tr>
        <tr>
          <td>Division:</td>
          <td>${person.division}</td>
        </tr>
        <tr>
          <td>Department:</td>
          <td>${person.department}</td>
        </tr>
        <c:if test="${not empty institutionalRoleName}">
          <tr>
            <td>Institutional Role:</td>
            <td>${institutionalRoleName}</td>
          </tr>
        </c:if>
        <tr>
          <td>E-mail:</td>
          <td>${person.email}</td>
        </tr>
        <tr>
          <td>Phone:</td>
          <td>${person.phone}</td>
        </tr>
        <c:if test="${not empty accountStatus}">
          <tr>
            <td>Account Status:</td>
            <td>${accountStatus}</td>
          </tr>
        </c:if>
        <c:if test="${not empty clusterAccounts}">
          <tr>
            <td valign="top">Cluster Account Names:</td>
            <td>
              <c:forEach items="${clusterAccounts}" var="clusterAccount">
                ${clusterAccount}<br>
              </c:forEach>      
            </td>
          </tr>
        </c:if>
      </tbody>
    </table>

    <br><br>
    <p>
      <a href="edit_account">Edit Account Details</a><br>
      <a href="request_account_deletion">Request Account Deletion</a>
    </p>
 
  </body>
  
</html>