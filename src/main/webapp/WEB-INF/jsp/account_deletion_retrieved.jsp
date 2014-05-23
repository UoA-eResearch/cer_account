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
      <c:choose>
        <c:when test="${not empty error}">
          <div class="errorblock">${error}</div>
        </c:when>
        <c:otherwise>
          ${message}
        </c:otherwise>
      </c:choose>
    </p>
    
  </body>
  
</html>
