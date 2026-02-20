<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Ocean View Resort</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 20px; text-align: center; }
        .container { max-width: 800px; margin: 40px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 24px; margin-bottom: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        .card h3 { margin-top: 0; color: #2c3e50; }
        a.btn { display: inline-block; padding: 10px 20px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin: 4px 0; }
        a.btn:hover { background: #2980b9; }
        .footer { text-align: center; padding: 20px; color: #7f8c8d; font-size: 0.9em; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Ocean View Resort</h1>
        <p>Hotel Reservation System</p>
    </div>
    <div class="container">
        <div class="card">
            <h3>Welcome</h3>
            <p>Please log in to access the system.</p>
            <a class="btn" href="${pageContext.request.contextPath}/login">Login</a>
        </div>
        <div class="card">
            <h3>Quick Links</h3>
            <a class="btn" href="${pageContext.request.contextPath}/reservation/search">Search Rooms</a>
            <a class="btn" href="${pageContext.request.contextPath}/frontdesk/dashboard">Front Desk</a>
            <a class="btn" href="${pageContext.request.contextPath}/admin/dashboard">Admin Panel</a>
        </div>
    </div>
    <div class="footer">&copy; 2026 Ocean View Resort</div>
</body>
</html>
