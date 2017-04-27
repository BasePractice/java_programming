<#-- @ftlvariable name="versions" type="java.util.List<ru.mirea.ippo.web.service.VersionService.Version>" -->
<#-- @ftlvariable name="user" type="java.lang.String" -->
<html>
<head><title>Version</title>
<body>
<h1>Hello ${user}!</h1>
<ul>
<#list versions as version>
<li>${version.product}: ${version.major}.${version.minor}.${version.build}
</#list>
</ul>
</body>
</html>