<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common_head.jsp" %>
<body>
<main class="container">
    <header>
        <h1>Create New Project</h1>
    </header>

    <section>
        <form action="${pageContext.request.contextPath}/projects/create" method="post">
            <label for="projectName">Project Name:</label>
            <input type="text" id="projectName" name="name" required>

<%--            <label for="projectDescription">Project Description:</label>--%>
<%--            <textarea id="projectDescription" name="description" rows="4"></textarea>--%>

<%--            <label for="projectOwner">Project Owner:</label>--%>
<%--            <input type="text" id="projectOwner" name="owner" required>--%>

            <button type="submit">Create Project</button>
        </form>
    </section>

    <nav>
        <a href="projects/search" role="button" class="secondary">Back to Search</a>
    </nav>
</main>
</body>
</html>