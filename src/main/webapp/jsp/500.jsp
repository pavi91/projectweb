<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<html>
<head>
  <title>500 - Server Error - Ocean View Resort</title>
  <style>
    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; background: #f5f5f5; }
    .container { max-width: 500px; margin: 0 auto; background: white; padding: 40px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
    .error-code { font-size: 64px; color: #dc3545; margin: 0; }
    h2 { color: #333; }
    p { color: #555; }
    a { display: inline-block; margin-top: 15px; padding: 10px 20px; background: #007bff; color: white; border-radius: 4px; text-decoration: none; }
    a:hover { background: #0056b3; }
  </style>
</head>
<body>
  <div class="container">
    <p class="error-code">500</p>
    <h2>Internal Server Error</h2>
    <p>An unexpected error occurred on the server. Our team has been notified.</p>
    <a href="${pageContext.request.contextPath}/">Go to Home</a>
  </div>
</body>
</html>

