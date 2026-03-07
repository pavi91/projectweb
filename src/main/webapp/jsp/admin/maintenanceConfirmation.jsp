<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Maintenance Updated</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; }
        .container { max-width: 600px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        .success { color: #27ae60; font-weight: bold; font-size: 1.1em; }
        a.btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin-top: 12px; }
    </style>
</head>
<body>
    <div class="header"><h2>Maintenance Status Updated</h2></div>
    <div class="container">
        <div class="card">
            <p class="success"><%= request.getAttribute("message") != null ? request.getAttribute("message") : "Maintenance status updated successfully." %></p>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/admin/maintenance">Update Another</a>
        <a class="btn" href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
    </div>
</body>
</html>

