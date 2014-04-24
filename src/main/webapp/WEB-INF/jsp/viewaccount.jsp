<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<meta charset="utf-8">
<script src="<%=request.getContextPath()%>/js/jquery-1.8.3.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery-ui.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/style/common.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/style/jquery-ui.css" type="text/css" />
<script type="text/javascript">
  $(document).ready(function() {

  });
</script>
</head>

<body>

  <p>
    <b>Details of your account with the Centre for eResearch</b>:
  </p>
  <table cellpadding="5">
    <tbody>
      <tr>
        <td>Full name:</td>
        <td>${fullName}</td>
      </tr>
      <tr>
        <td>Preferred name:</td>
        <td>${preferredName}</td>
      </tr>
      <tr>
        <td>Institution:</td>
        <td>${institution}</td>
      </tr>
      <tr>
        <td>Division:</td>
        <td>${division}</td>
      </tr>
      <tr>
        <td>Department:</td>
        <td>${department}</td>
      </tr>
      <tr>
        <td>Institutional Role:</td>
        <td>${institutionalRoleName}</td>
      </tr>
      <tr>
        <td>E-mail:</td>
        <td>${email}</td>
      </tr>
      <tr>
        <td>Phone:</td>
        <td>${phone}</td>
      </tr>
      <tr>
        <td valign="top">Cluster Account Names:</td>
        <td>
          <c:forEach items="${clusterAccounts}" var="clusterAccount">
            ${clusterAccount}<br>
          </c:forEach>      
        </td>
      </tr>
    </tbody>
  </table>

</body>