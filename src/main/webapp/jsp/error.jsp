<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Error</title>
</head>
<body>
  <h2>Something went wrong</h2>
  <%
    String error = (String) request.getAttribute("error");
    if (error != null && !error.trim().isEmpty()) {
  %>
    <p style="color: red;"><%= error %></p>
  <% } else { %>
    <p>Please try again or contact support.</p>
  <% } %>
  <p><a href="/projectweb/">Go to Home</a></p>
</body>
</html>

