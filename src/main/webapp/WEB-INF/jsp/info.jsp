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
   <div class="infoblock">
     You don't yet have a cluster account.
   </div>
  </p>
  <p>
   Getting started on the Auckland NeSI cluster involves the following steps:
  </p>

  <table cellpadding="15">
    <tbody>
      <tr>
        <td valign="top">
          <b>1. Request an account</b>
          <p>
            Provide some basic information about yourself.<br>
            A staff member of the Centre for eResearch will verify your request, and set
            up an account for you.
          </p>
        </td>
      </tr>
      <tr>
        <td valign="top">
          <b>2. Tell us what you want to do and how the cluster can help with that</b>
          <p>
            Provide information about the research project you want to use the cluster for.<br/>
            You have the options to
            <ul>
              <li>Join a project which is already registered with the Centre for eResearch.</li>
              <li>Request a new project to be created.</li>
            </ul>
          </p>
        </td>
      </tr>
      <!--
      <tr>
        <td valign="top">
          <b>3. Tell us how the Auckland NeSI cluster can help with your research</b><br>
          Please tell us what is limiting you.<br> 
          You will be guided through a survey with a few questions about the computing 
          environment you are currently using.<br>
        </td>
      </tr>
      -->
    </tbody>
  </table>

  <p>
    Once your account and project have been set up, you will be able to submit jobs on the 
    Auckland NeSI cluster.
  </p>

  <p>
    <b><a href="requestaccount">Request an account</a></b>
  </p>

</body>