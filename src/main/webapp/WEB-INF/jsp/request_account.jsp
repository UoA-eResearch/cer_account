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
  </head>

  <body>
    <form:form method="POST" commandName="requestaccount" action='request_account'>
      <h4>Request an account</h4>
      <p/>
      <table cellpadding="5">
        <tbody>
          <tr>
            <td colspan="2">
              <c:if test="${not empty unexpected_error}">
                <div id="unexpected_error" class="errorblock">${unexpected_error}</div>
              </c:if>
              <form:errors path="*" cssClass="errorblock" element="div" />
              <table cellpadding="5">
                <tbody>
                  <tr>
                    <td><nobr>Full name:</nobr></td>
                    <td>
                      <form:input id="fullname" path="fullName" value="${requestaccount.fullName}"
                                  readonly="false"/>
                    </td>
                  </tr>
                  <tr>
                    <td><nobr>Preferred name:</nobr></td>
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
                        If your institution/division/department is not listed, please choose
                        "Other" and specify
                      </p>
                      <div id="other_inst" style="display: none;">
                        Please specify:
                        <form:input id="other_institution" value="" path="otherInstitution" />
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <td><nobr>Contact phone:</nobr></td>
                    <td><form:input id="phone" path="phone" /></td>
                  </tr>
                  <tr>
                    <td><nobr>E-mail address:</nobr></td>
                    <td><form:input id="email" path="email" /></td>
                  </tr>
                  <tr>
                    <td><nobr>Institutional role:</nobr></td>
                    <td valign="top">
                      <form:select path="institutionalRoleId">
                        <form:option value="" label="Please Select" />
                        <form:options items="${institutionalRoles}" />
                      </form:select>
                      <div id="other_institutionalRole" style="display:none;">
                        Please specify:
                        <form:input id="institutionalRole_other_value" value="" path="otherInstitutionalRole" />
                      </div>
                      (at institution specified above)
                    </td>
                  </tr>
                  <tr>
                    <td colspan="2">
                      <form:checkbox id="isNesiStaff" path="isNesiStaff" />
                      I'm a NeSI or Centre for eResearch staff member
                    </td>
                  </tr>
                </tbody>
              </table>
            </td>
          </tr>
        </tbody>
      </table>

      <br><input type="submit" value="Next">
    </form:form>
  </body>

</html>