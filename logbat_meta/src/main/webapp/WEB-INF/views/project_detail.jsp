<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common_head.jsp" %>
<body>
<main class="container">
    <header>
        <h1>Project Details: ${project.name}</h1>
    </header>

    <nav>
        <ul>
            <li><a href="https://view.logbat.info" role="button">모니터링 화면 보기</a></li>
        </ul>
    </nav>

    <section>
        <h2>Apps</h2>
        <table>
            <thead>
            <tr>
                <th>App Name</th>
                <th>App Key</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="app" items="${apps}">
                <tr>
                    <td>${app.name}</td>
                    <td>${app.token}</td>
                    <td>
<%--                        <a href="v1/projects/apps/info/${app.id}">View</a>--%>
<%--                        <a href="v1/projects/apps/update/${app.id}">Edit</a>--%>
                        <a href="#" onclick="confirmDelete('${app.name}', '${pageContext.request.contextPath}/projects/${project.id}/apps/${app.id}/delete'); return false;">Delete</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </section>

    <footer>
        <form action="${pageContext.request.contextPath}/projects/apps/create" method="post">
            <input type="hidden" name="projectId" value="${project.id}">
            <button type="submit" >Create New App</button>
        </form>
    </footer>
</main>

<script>
    function confirmDelete(appName, deleteUrl) {
        if (confirm("정말로 '" + appName + "' 앱을 삭제하시겠습니까?")) {
            window.location.href = deleteUrl;
        }
    }
</script>

</body>
</html>