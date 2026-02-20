<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Occupancy Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; }
        .container { max-width: 700px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        pre { background: #ecf0f1; padding: 16px; border-radius: 4px; white-space: pre-wrap; word-wrap: break-word; font-size: 0.95em; }
        a.btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin-top: 12px; }
    </style>
</head>
<body>
    <div class="header"><h2>Occupancy Report</h2></div>
    <div class="container">
        <div class="card">
            <pre><%= request.getAttribute("report") != null ? request.getAttribute("report") : "No report data available." %></pre>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/admin/reports">Back to Reports</a>
        <a class="btn" href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
    </div>
</body>
</html>

