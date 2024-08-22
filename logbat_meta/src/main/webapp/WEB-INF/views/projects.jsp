<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common_head.jsp" %>
<body>
<main class="container">
    <header>
        <h1>Find Project</h1>
    </header>

    <section>
        <form action="projects/search" method="get">
            <label for="projectName">Enter Project Name:</label>
            <input type="text" id="projectName" name="name" required>
            <button type="submit">Search Project</button>
        </form>
    </section>

    <nav>
        <a href="projects/create" role="button">Create New Project</a>
    </nav>
</main>
</body>
</html>