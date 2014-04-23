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

  <h2>Get started on the Auckland NeSI cluster (Pan) of the Centre for eResearch</h2>

  <p>Getting started on the Auckland NeSI cluster involves the following 3 steps:</p>

  <table cellpadding="10">
    <tbody>
      <tr>
        <td valign="top"><img src="<%=request.getContextPath()%>/pics/create_account.png" /></td>
        <td valign="top"><b>1. Request an account</b><br> Provide us with some basic information about
          yourself.<br> A staff member of the Centre for eResearch will verify your request, and set up an account
          for you.<br> Once the account has been created, you will receive a notification e-mail with basic
          instructions on how to use the Auckland NeSI cluster.<br></td>
      </tr>
      <tr>
        <td valign="top"><img src="<%=request.getContextPath()%>/pics/create_project.png" /></td>
        <td valign="top"><b>2. Tell us what you want to do</b><br> Provide us with some information about the
          research project you want to use the cluster for.<br> You can either join a project which is already
          registered with the Auckland NeSI cluster, or request a new project to be created.<br> A staff member of
          the Centre for eResearch will set up access to the project directory on the cluster filesystem for you.<br>
          Once the access has been set up, you will receive a notification e-mail with basic instructions on how to
          access your project directory.<br></td>
      </tr>
      <tr>
        <td valign="top"><img src="<%=request.getContextPath()%>/pics/survey.png" /></td>
        <td valign="top"><b>3. Tell us how the Auckland NeSI cluster can help with your research</b><br>
          Please tell us what is limiting you.<br> 
          You will be guided through a survey with a few questions about the computing environment
          you are currently using.<br></td>
      </tr>
    </tbody>
  </table>

  <p>Once your account and project has been set up, you will be able to submit jobs on the Auckland NeSI cluster.</p>

  <p>
    <b><a href="requestaccount">Start</a></b>
  </p>

</body>