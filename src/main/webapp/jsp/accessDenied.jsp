<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Access Denied - Ocean View Resort</title>
  <style>
    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; background: #f5f5f5; }
    .container { max-width: 500px; margin: 0 auto; background: white; padding: 40px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
    h2 { color: #dc3545; }
    .error-code { font-size: 64px; color: #dc3545; margin: 0; }
    p { color: #555; }
    .role-badge { display: inline-block; padding: 4px 12px; background: #ffc107; color: #333; border-radius: 4px; font-weight: bold; }
    a { color: #007bff; text-decoration: none; }
    a:hover { text-decoration: underline; }
    .actions { margin-top: 20px; }
    .actions a { display: inline-block; margin: 5px 10px; padding: 10px 20px; background: #007bff; color: white; border-radius: 4px; }
    .actions a:hover { background: #0056b3; text-decoration: none; }
  </style>
</head>
<body>
  <div class="container">
    <p class="error-code">403</p>
    <h2>Access Denied</h2>
    <%
      String error = (String) request.getAttribute("error");
      String userRole = (String) request.getAttribute("userRole");
    %>
    <% if (error != null) { %>
      <p><%= error %></p>
    <% } else { %>
      <p>You do not have permission to access this page.</p>
    <% } %>
    <% if (userRole != null) { %>
      <p>Your role: <span class="role-badge"><%= userRole %></span></p>
    <% } %>
    <div class="actions">
      <a href="${pageContext.request.contextPath}/">Go to Home</a>
      <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
  </div>
</body>
</html>

