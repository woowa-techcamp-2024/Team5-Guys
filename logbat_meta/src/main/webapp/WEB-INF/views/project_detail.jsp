<%@ include file="/WEB-INF/views/common_head.jsp" %>
<body>
<h1>Project Details: ${project.name}</h1>

<h2>Apps</h2>
<table border="1">
    <tr>
        <th>App ID</th>
        <th>App Name</th>
        <th>Actions</th>
    </tr>
    <c:forEach var="app" items="${apps}">
        <tr>
            <td>${app}</td>
            <td>${app.id}</td>
            <td>
                <a href="v1/projects/apps/info/${app.id}">View</a>
                <a href="v1/projects/apps/update/${app.id}">Edit</a>
                <a href="v1/projects/apps/delete/${project.id}/${app.id}">Delete</a>
            </td>
        </tr>
    </c:forEach>
</table>
<a href="projects/apps/create?projectId=${project.id}">Create New App</a>
</body>
</html>