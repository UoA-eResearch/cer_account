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
    // on change of radio button to choose from joining or creating project
    $("input[name='motivation']").change(function() {
      if ($("input[name='motivation']:checked").val() == 'other') {
        $("#other_motivation").css("display", "inline");
      } else {
        $("#other_motivation").css("display", "none");
      }
    });

    $("input[name='currentEnv']").change(function() {
      if ($("input[name='currentEnv']:checked").val() == 'enhanced') {
        $("#limitations").css("display", "inline");
      } else {
        $("#limitations").css("display", "none");
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

  <table cellpadding="10">
    <tbody>
      <tr>
        <td><img src="<%=request.getContextPath()%>/pics/survey.png" /></td>
        <td><h2>Tell us how the Auckland NeSI cluster (Pan) can help with your research</h2></td>
      </tr>
    </tbody>
  </table>

  <form:form method="POST" commandName="survey" onsubmit="beforeSubmit()" action='#'>
    <table>
      <tbody>
        <tr>
          <td><b>Motivation for signing up:</b></td>
        </tr>
        <tr>
          <td><form:radiobutton name="motivation" path="motivation" value="inadequate_equipment"
              label="I have inadequate computational equipment to run my computations" /></td>
        </tr>
        <tr>
          <td><form:radiobutton name="motivation" path="motivation" value="avoid_blocking"
              label="I could run the computations on my laptop/desktop computer, but it would block me from doing other work on the computer" />
          </td>
        </tr>
        <tr>
          <td><form:radiobutton name="motivation" path="motivation" value="recommendation"
              label="I don't really need the cluster, but someone recommended using it" /></td>
        </tr>
        <tr>
          <td><form:radiobutton name="motivation" path="motivation" value="other" label="Other" />
            <div id="other_motivation" style="display: none;">
              <br> <br>Please specify:<br>
              <form:input path="motivation" type="text" size="100" maxlength="100" />
            </div></td>
        </tr>
      </tbody>
    </table>

    <br>
    <b>What is the computational environment to your availability now?</b>
    <br>
    <br>

    <table>
      <tbody>
        <tr>
          <td valign="top"><form:radiobutton name="currentEnv" path="currentEnv" value="standard"
              label="I have a standard desktop/laptop computer" /></td>
        </tr>
        <tr>
          <td valign="top">
            <form:radiobutton name="currentEnv" path="currentEnv" value="enhanced"
              label="I have access to a small cluster or a set of computers to run my jobs on" />

            <div id="limitations" style="display: none;">
              <br>
              <br>
              <b>Please specify your current environment</b>:<br><br>
              <table>
                <tbody>
                  <tr>
                    <td valign="top">I can currently run jobs using max <form:input path="limitations.cpuCores"
                        size="4" maxlength="4" /> CPU cores.
                    </td>
                  </tr>
                  <tr>
                    <td valign="top">I am can currently run jobs using max <form:input path="limitations.memory"
                        size="4" maxlength="4" /> GB memory.
                    </td>
                  </tr>
                  <tr>
                    <td valign="top">I can currently run max <form:input path="limitations.concurrency" size="4"
                        maxlength="4" /> jobs concurrently.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div></td>
        </tr>
      </tbody>
    </table>

    <br>
    <br>
    <input type="submit" value="Submit">

  </form:form>

</body>