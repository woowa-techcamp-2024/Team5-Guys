<%@ include file="/WEB-INF/views/common_head.jsp" %>
<body>
<h1>Find Project</h1>
<form action="projects/search" method="get">
    <label for="projectName">Enter Project Name:</label>
    <input type="text" id="projectName" name="name" required>
    <button type="submit">Search Project</button>
</form>
</body>
</html>