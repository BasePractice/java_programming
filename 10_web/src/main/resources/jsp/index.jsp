<%@ page import="ru.mirea.ippo.web.service.VersionService" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="true" %>
<html>
<head><title>Version</title>
<body>
<h1>Hello <%=request.getAttribute("user")%>!
</h1>
<ul>
    <%
        List<VersionService.Version> versions = (List<VersionService.Version>) request.getAttribute("versions");
        for (VersionService.Version version : versions) { %>
    <li><%=version.product%>:  <%=version.major%>.<%=version.minor%>.<%=version.build%>
    </li>
    <%}%>
</ul>
</body>
</html>
