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

    var institutionalRole = $("#institutionalRoleId option:selected").val();
    var institution = $("#institution").val();
    
    if (institutionalRole == 4) {
      $("#other_institutionalRole").css("display", "inline");
    } else {
      $("#other_institutionalRole").css("display", "none");
    }

    if (institution == "Other") {
      $("#other_inst").css("display", "inline");
    } else {
      $("#other_inst").css("display", "none");
    }

    // make fields visible/invisible dependent on applicants institution
    $("#institution").change(function() {
      if ($("#institution").val() == "Other") {
        $("#other_inst").css("display", "inline");
      } else {
        $("#other_inst").css("display", "none");
      }
    });

    // make fields visible/invisible dependent on applicants institutionalRole
    $("#institutionalRoleId").change(function() {
      var institutionalRole = $("#institutionalRoleId option:selected").val();
      if (institutionalRole == 4) {
        $("#other_institutionalRole").css("display", "inline");
      } else {
        $("#other_institutionalRole").css("display", "none");
      }
    });
  });
</script>
<style>
.errorblock {
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}
</style>
</head>

<body>

  <form:form method="POST" commandName="requestaccount" action='requestaccount'>

    <!-- Applicants information -->

    <table cellpadding="10">
      <tbody>
        <tr>
          <td><img src="<%=request.getContextPath()%>/pics/create_account.png" /></td>
          <td><h2>Request an account on the Auckland NeSI cluster (Pan)</h2></td>
        </tr>
        <tr>
          <td colspan="2">
            <c:if test="${not empty unexpected_error}">
              <div id="unexpected_error" class="errorblock">${unexpected_error}</div>
            </c:if>
            <form:errors path="*" cssClass="errorblock" element="div" />
            <table cellpadding="5">
              <tbody>
                <tr>
                  <td>Full name:</td>
                  <td><form:input id="fullname" path="fullName" value="${requestaccount.fullName}" readonly="true"/></td>
                </tr>
                <tr>
                  <td>Preferred name:</td>
                  <td><form:input id="preferredname" path="preferredName" /></td>
                </tr>
                <tr>
                  <td valign="top">Affiliation:<br>
                  <td>
                    <form:select path="institution">
                      <form:option value="" label="Please Select" />
                      <form:options items="${affiliations}" />
                    </form:select>
                    <p>
                     If your institution/division/department is not listed, please choose "Other" and specify
                    </p>
                    <div id="other_inst" style="display: none;">
                      <br>
                      Please specify:
                      <form:input id="other_institution" value="" path="otherInstitution" />
                    </div></td>
                </tr>
                <tr>
                  <td>Contact phone number:</td>
                  <td><form:input id="phone" path="phone" /></td>
                </tr>
                <tr>
                  <td>E-mail address:</td>
                  <td><form:input id="email" path="email" /></td>
                </tr>
                <tr>
                  <td>Institutional role:
                  </td>
                  <td valign="top"><form:select path="institutionalRoleId">
                      <form:option value="" label="Please Select" />
                      <form:options items="${institutionalRoles}" />
                    </form:select>
                    <div id="other_institutionalRole" style="display:none;">
                      <br>
                      Please specify:
                      <form:input id="institutionalRole_other_value" value="" path="otherInstitutionalRole" />
                    </div>
                    (at institution specified above)
                  </td>
                </tr>
              </tbody>
            </table></td>
        </tr>
      </tbody>
    </table>

    <br>
    <input type="submit" value="Next">

  </form:form>

</body>