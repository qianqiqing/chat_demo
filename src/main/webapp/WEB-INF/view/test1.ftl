<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
     ${value}
     <table>
        <#if data?? && data?size>0)>
            <#list data as em>
                <td>${em.name}</td>
                <td>${em.number}</td>
            </#list>
        </#if>
     </table>
</body>
</html>