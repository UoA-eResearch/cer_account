<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>

  <head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="../style/common.css" type="text/css" />
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

    <p>
      <form action="edit_account" method="GET" style="display: inline;">
        <input type="submit" id="edit_account" value="Edit Account Details"/>
      </form>
      <form action="request_account_deletion" method="GET" style="display: inline;">
        <input type="submit" id="delete_account" value="Request Account Deletion"/>
      </form>
    </p>

  </body>
  
</html>
