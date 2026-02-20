<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Login</title>
</head>
<body>
  <h2>Login</h2>
  <%
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage != null && !errorMessage.trim().isEmpty()) {
  %>
    <p style="color: red;"><%= errorMessage %></p>
  <% } %>
  <form method="post" action="/projectweb/login">
    <label>Username: <input type="text" name="username" required></label><br>
    <label>Password: <input type="password" name="password" required></label><br>
    <button type="submit">Sign In</button>
  </form>
  <p><a href="/projectweb/">Back to Home</a></p>
</body>
</html>

