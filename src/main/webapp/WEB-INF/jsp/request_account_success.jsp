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
      Your cluster account request has been received.<br>
      A staff member of the Centre for eResearch will process the request soon.
    </p>
    <p>
      Once your cluster account has been created, you'll receive an e-mail notification with
      details about how to use the cluster.
    </p>
    <p>
      <div class="infoblock">
        Note that you still have to <a href="${projectRequestUrl}">request a new project
        or membership to an existing project</a> before you can submit jobs to the cluster.<br>
        To view the details of your account, click <a href="view_account">here</a>.
      </div>
    </p>
    
  </body>
  
</html>