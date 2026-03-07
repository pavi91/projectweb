<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Receptionist Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; }
        .header h2 { margin: 0; }
        .container { max-width: 900px; margin: 30px auto; padding: 20px; }
        .stats { display: flex; gap: 16px; margin-bottom: 20px; }
        .stat-card { flex: 1; background: white; border-radius: 8px; padding: 20px; text-align: center; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        .stat-card h4 { margin: 0 0 8px; color: #7f8c8d; }
        .stat-card .number { font-size: 2em; color: #2c3e50; font-weight: bold; }
        .card { background: white; border-radius: 8px; padding: 20px; margin-bottom: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        a.btn { display: inline-block; padding: 10px 20px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin: 4px; }
        a.btn.green { background: #27ae60; }
        a.btn.orange { background: #e67e22; }
        a.btn.red { background: #e74c3c; }
        a.btn:hover { opacity: 0.85; }
    </style>
</head>
<body>
    <div class="header">
        <h2>Front Desk Dashboard</h2>
        <span>Welcome, <%= session.getAttribute("username") %></span>
    </div>
    <div class="container">
        <div class="stats">
            <div class="stat-card">
                <h4>Available Rooms</h4>
                <div class="number"><%= request.getAttribute("availableRooms") != null ? request.getAttribute("availableRooms") : "N/A" %></div>
            </div>
        </div>

        <div class="card">
            <h3>Quick Actions</h3>
            <a class="btn green" href="${pageContext.request.contextPath}/frontdesk/walkin">Walk-In Reservation</a>
            <a class="btn" href="${pageContext.request.contextPath}/frontdesk/checkin">Check-In Guest</a>
            <a class="btn orange" href="${pageContext.request.contextPath}/frontdesk/checkout">Check-Out Guest</a>
        </div>

        <a class="btn red" href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</body>
</html>

