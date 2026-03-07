<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Admin Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; }
        .header h2 { margin: 0; }
        .container { max-width: 900px; margin: 30px auto; padding: 20px; }
        .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
        .card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        .card h3 { margin-top: 0; color: #2c3e50; }
        a.btn { display: inline-block; padding: 10px 18px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin: 4px 0; }
        a.btn.green { background: #27ae60; }
        a.btn.orange { background: #e67e22; }
        a.btn.purple { background: #8e44ad; }
        a.btn.red { background: #e74c3c; }
        a.btn:hover { opacity: 0.85; }
    </style>
</head>
<body>
    <div class="header">
        <h2>Admin Dashboard</h2>
        <span>Welcome, <%= session.getAttribute("username") %></span>
    </div>
    <div class="container">
        <div class="grid">
            <div class="card">
                <h3>Staff Management</h3>
                <p>Create and manage staff accounts.</p>
                <a class="btn green" href="${pageContext.request.contextPath}/admin/staff">Manage Staff</a>
            </div>
            <div class="card">
                <h3>Reports</h3>
                <p>Generate revenue, occupancy and other reports.</p>
                <a class="btn" href="${pageContext.request.contextPath}/admin/reports">View Reports</a>
            </div>
            <div class="card">
                <h3>Payment Info</h3>
                <p>Payment adapters are auto-selected:<br>
                   <strong>Online bookings</strong> → Online Gateway<br>
                   <strong>Walk-in bookings</strong> → POS Terminal</p>
            </div>
            <div class="card">
                <h3>Maintenance</h3>
                <p>Manage room maintenance status.</p>
                <a class="btn purple" href="${pageContext.request.contextPath}/admin/maintenance">Manage</a>
            </div>
            <div class="card">
                <h3>Seasonal Pricing</h3>
                <p>Configure seasonal rates and multipliers for peak/off-peak periods.</p>
                <a class="btn orange" href="${pageContext.request.contextPath}/admin/seasonal-pricing">Manage Seasons</a>
            </div>
        </div>
        <br>
        <a class="btn red" href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</body>
</html>

