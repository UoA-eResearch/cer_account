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
  function conditional_display() {
    if ($("input[name='choice']:checked").val() == 'JOIN_PROJECT') {
      $("#join_project").css("display", "inline");
      $("#create_project").css("display", "none");
    } else if ($("input[name='choice']:checked").val() == 'CREATE_PROJECT') {
      $("#create_project").css("display", "inline");
      $("#join_project").css("display", "none");
    }
  }
  
  $(document).ready(function() {
    conditional_display();
    // on change of radio button to choose from joining or creating project
    $("input[name='choice']").change(function() {
      conditional_display();
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

  <table cellpadding="10">
    <tbody>
      <tr>
        <td><img src="<%=request.getContextPath()%>/pics/create_project.png" /></td>
        <td><h2>Tell us what you want to do</h2></td>
      </tr>
    </tbody>
  </table>

  <form:form method="POST" commandName="requestproject" onsubmit="beforeSubmit()" action='#'>
    
    <c:if test="${not empty unexpected_error}">
      <div id="unexpected_error" class="errorblock">${unexpected_error}</div>
    </c:if>
    <form:errors path="*" cssClass="errorblock" element="div" />
    
    <table>
      <tbody>
        <tr>
          <td><form:radiobutton name="choice" path="choice" value="JOIN_PROJECT"
              label="I want to join an existing project" /></td>
        </tr>
        <tr>
          <td><form:radiobutton name="choice" path="choice" value="CREATE_PROJECT"
              label="I want to create a new collaborator project" />
          </td>
        </tr>
        <tr>
          <td>
            <br><b>Please note, that, if you work on this project with a group of researchers, 
            the data generated in the context of this project is shared amongst all project
            team members.</b>
          </td>
        </tr>
      </tbody>
    </table>

    <br>
    <div id="join_project" style="display: none;">
      <table cellpadding="5">
        <tbody>
          <tr>
            <td valign="top">
              Please specify the project code of the project you want to join.
              If in doubt, please contact the person who asked you to join.<br>
              Project code:<br><form:input path="projectCode" type="text" />
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div id="create_project" style="display: none;">
      <table cellpadding="5">
        <tbody>
          <tr>
            <td><b>Please describe the project you want to create</b></td>
          </tr>
          <tr>
            <td>
              Project title (max 160 characters including spaces):<br>
              <form:input path="projectTitle" type="text" size="100" maxlength="100"/>
            </td>
          </tr>
          <tr>
            <td>
              Project description (100 - 2500 characters including spaces):<br>
              <form:textarea path="projectDescription" type="text" cols="80" rows="10"/>
            </td>
          </tr>
          
         <c:if test="${requestproject.askForSuperviser}">
            <tr>
              <td>
                <b>Please note, that your supervisor will have access to the data generated in the
                context of this project.<br><br>
                Contact details of your supervisor</b>:<br><br>
                Name:<br>
                <form:input path="superviserName" type="text" size="100" maxlength="100"/>
              </td>
            </tr>
            <tr>
              <td>
                E-mail:<br>
                <form:input path="superviserEmail" type="text" size="100" maxlength="100"/>
              </td>
            </tr>
            <tr>
              <td>
                Phone:<br>
                <form:input path="superviserPhone" type="text" size="100" maxlength="100"/>
              </td>
            </tr>
         </c:if>
         
        </tbody>
      </table>
    </div>

    <br>
    <input type="submit" value="Submit">
    <form:hidden path="askForSuperviser" value="${requestproject.askForSuperviser}"/>

  </form:form>

</body>
