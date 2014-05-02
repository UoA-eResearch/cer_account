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
      <div class="errorblock">
        You are requesting for your cluster account to be closed.
      </div>
    </p>
    
    <p>
      <b>The following actions will be taken soon after you click on the confirmation link below</b>:
      <ul>
        <li><p>Your home directory and all data in your home directory will be deleted.</p></li>
        <li>
          <p>
            The projects registered with the Centre for eResearch, that you were working on, will be updated
            in the following way:
          </p>
          <ul>
            <li>
              <p>Your membership on all projects you are a member of will be cancelled.</p>
            </li>
            <li>
              <p>
                Projects where you are the only member will be closed and the corresponding project
                directories, and all data in these project directories, will be deleted.
              </p>        
            </li>
            <li>
              <p>
                Projects where you are NOT the only member will remain open.<br>
                Your data in the project directories will NOT be deleted and will be available to the
                other project team members.
              </p>      
            </li>
          </ul>
        </li>
        <li>
          Your e-mail address will be removed from the Centre for eResearch e-mail list and you will
          no longer receive any notification e-mails from us, except perhaps in the context of
          the deletion of your account.
        </li>
      </ul>
    </p>
        
    <p><b><a href="confirm_account_deletion">Confirm account deletion request</a></b></p>

  </body>
  
</html>
