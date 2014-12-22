<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>

  <head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="../style/common.css" type="text/css" />
  </head>

  <body>

    <p>
      <div class="infoblock">
        You are requesting for your cluster account to be closed.
      </div>
    </p>
    <p>
      <b>
        <font color="red">
          Before clicking the button below, make sure you take a copy of any data
          you wish to keep from your home directory and all your project folders!
        </font>
      </b>
    </p>
    <p>
      The following actions will be taken soon after you click on the confirmation link below:
      <ul>
        <li>
          <p>
            Your home directory and all its contents will be permanently deleted.<br>
          </p>
        </li>
        <li>
          <p>
            The projects you are associated with will be updated as follows:
          </p>
          <ul>
            <li>
              <p>
                 For projects where you are one of several team members and not the project owner,
                 your membership will be automatically revoked.
                 These projects will remain open and other members will be able to view and modify
                 any data you leave in the project directory.
              </p>
            </li>
            <li>
              <p>
                Projects where you are the owner and sole member will be closed.
                The corresponding project directories will be deleted, along with their contents.
                If you wish to hand the project over to someone else instead of closing it, please
                <a href="mailto:eresearch@nesi.org.nz?subject=Please hand over my project(s)">contact us</a>.
              </p>
            </li>
            <li>
              <p>
                For projects where you are the owner and there are other members, a Centre for eResearch
                support consultant will attempt to contact you to arrange a handover of the project to
                an eligible researcher.<br>
                If we cannot arrange a handover within a reasonable time, we will close the project and
                delete its directory and contents.
              </p>      
            </li>
          </ul>
        </li>
        <li>
          Your e-mail address will be removed from the Centre for eResearch mailing list. You will
          receive no further notification e-mails from us, except e-mails concerning the deletion
          of your account.
        </li>
      </ul>
    </p>
        
    <p>
      <form action="confirm_account_deletion" style="display: inline;">
        <input type="submit" id="delete_account" value="Confirm account deletion request"/>
      </form>
    </p>

  </body>
  
</html>
