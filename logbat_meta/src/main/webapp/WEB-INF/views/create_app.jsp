<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common_head.jsp" %>
<body>
<main class="container">
    <header>
        <h1>Create New App for Project: ${project.name}</h1>
    </header>

    <section>
        <form action="${pageContext.request.contextPath}/projects/apps/create" method="post">
            <input type="hidden" name="projectId" value="${project.id}">

            <label for="appName">App Name:</label>
            <input type="text" id="appName" name="name" required>

            <label for="appType">App Type:</label>
            <select id="appType" name="appType" required>
                <c:forEach items="${appTypes}" var="type">
                    <option value="${type}">${type}</option>
                </c:forEach>
            </select>

<%--            <label for="appDescription">App Description:</label>--%>
<%--            <textarea id="appDescription" name="description" rows="4"></textarea>--%>

            <button type="submit">Create App</button>
        </form>
    </section>

    <nav>
        <a href="${pageContext.request.contextPath}/projects/search?name=${project.name}" role="button" class="secondary">Back to Project Details</a>
    </nav>
</main>
</body>
</html>