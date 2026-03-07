<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Error - Ocean View Resort</title>
  <style>
    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; background: #f5f5f5; }
    .container { max-width: 600px; margin: 0 auto; background: white; padding: 40px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
    h2 { color: #dc3545; }
    .error-msg { color: #721c24; background: #f8d7da; border: 1px solid #f5c6cb; padding: 15px; border-radius: 4px; text-align: left; }
    .error-code { display: inline-block; padding: 2px 8px; background: #e2e3e5; color: #383d41; border-radius: 3px; font-family: monospace; font-size: 12px; margin-bottom: 10px; }
    a { color: #007bff; text-decoration: none; }
    a:hover { text-decoration: underline; }
    .actions { margin-top: 20px; }
    .actions a { display: inline-block; margin: 5px 10px; padding: 10px 20px; background: #007bff; color: white; border-radius: 4px; }
    .actions a:hover { background: #0056b3; text-decoration: none; }
  </style>
</head>
<body>
  <div class="container">
    <h2>Something Went Wrong</h2>
    <%
      String error = (String) request.getAttribute("error");
      String errorCode = (String) request.getAttribute("errorCode");
      Integer statusCode = (Integer) request.getAttribute("statusCode");
    %>
    <% if (errorCode != null) { %>
      <span class="error-code"><%= errorCode %></span>
    <% } %>
    <% if (error != null && !error.trim().isEmpty()) { %>
      <div class="error-msg"><%= error %></div>
    <% } else { %>
      <p>An unexpected error occurred. Please try again or contact support.</p>
    <% } %>
    <div class="actions">
      <a href="${pageContext.request.contextPath}/">Go to Home</a>
      <a href="javascript:history.back()">Go Back</a>
    </div>
  </div>
</body>
</html>

