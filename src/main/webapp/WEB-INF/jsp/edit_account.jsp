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
        var institution = $("#institution").val();

        if (institution == "Other") {
          $("#other_inst").css("display", "inline");
        } else {
          $("#other_inst").css("display", "none");
        }

        function adjustInstitution() {
            if ($("#institution").val() == "Other") {
                $("#other_inst").css("display", "inline");
            } else {
                $("#other_inst").css("display", "none");
            }        	
        }
        // make fields visible/invisible dependent on applicants institution 
        $("#institution").change(function() {adjustInstitution();});
        $("input:reset").click(function() { 
            this.form.reset();
            adjustInstitution();
        });
      });
    </script>
  </head>

  <body>
    <form:form method="POST" commandName="person" action='edit_account'>
      <h4>Edit account details</h4>
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
                      <form:input id="fullname" path="fullName" value="${fullName}" readonly="false"/>
                    </td>
                  </tr>
                  <tr>
                    <td><nobr>Preferred name:</nobr></td>
                    <td><form:input id="preferredname" path="preferredName" value="${preferredName}"/></td>
                  </tr>
                  <tr>
                    <td valign="top">Affiliation:<br>
                    <td>
                      <form:select id="institution" path="institution" items="${affiliations}" />
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
                      <form:select path="institutionalRoleId" items="${institutionalRoles}" />
                      (at institution specified above)
                    </td>
                  </tr>
                </tbody>
              </table>
            </td>
          </tr>
        </tbody>
      </table>

      <br>
      <input type="reset" value="Reset"/>
      <input type="submit" value="Submit"/>
      <a href="view_account"><input type="button" value="Cancel"/></a>
    </form:form>
  </body>

</html>