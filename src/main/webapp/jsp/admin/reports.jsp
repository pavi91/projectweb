<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Reports</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; }
        .container { max-width: 800px; margin: 30px auto; padding: 20px; }
        .error { color: red; margin-bottom: 10px; }
        .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
        .card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        .card h3 { margin-top: 0; color: #2c3e50; }
        form { display: inline; }
        button { padding: 10px 18px; color: white; border: none; border-radius: 4px; cursor: pointer; }
        .btn-blue { background: #3498db; }
        .btn-green { background: #27ae60; }
        .btn-orange { background: #e67e22; }
        .btn-purple { background: #8e44ad; }
        button:hover { opacity: 0.85; }
        a.btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin-top: 16px; }
    </style>
</head>
<body>
    <div class="header"><h2>Generate Reports</h2></div>
    <div class="container">
        <%
            String error = (String) request.getAttribute("error");
            if (error != null && !error.trim().isEmpty()) {
        %>
            <p class="error"><%= error %></p>
        <% } %>

        <div class="grid">
            <div class="card">
                <h3>Revenue Report</h3>
                <p>Total revenue and transaction summary.</p>
                <form method="post" action="${pageContext.request.contextPath}/admin/reports/revenue">
                    <button type="submit" class="btn-blue">Generate</button>
                </form>
            </div>
            <div class="card">
                <h3>Occupancy Report</h3>
                <p>Room occupancy and availability statistics.</p>
                <form method="post" action="${pageContext.request.contextPath}/admin/reports/occupancy">
                    <button type="submit" class="btn-green">Generate</button>
                </form>
            </div>
            <div class="card">
                <h3>Cancellation Report</h3>
                <p>Cancellation rates and breakdown.</p>
                <form method="post" action="${pageContext.request.contextPath}/admin/reports/cancellation">
                    <button type="submit" class="btn-orange">Generate</button>
                </form>
            </div>
            <div class="card">
                <h3>Comprehensive Report</h3>
                <p>Full hotel analytics report.</p>
                <form method="post" action="${pageContext.request.contextPath}/admin/reports/comprehensive">
                    <button type="submit" class="btn-purple">Generate</button>
                </form>
            </div>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/admin/dashboard">Back to Dashboard</a>
    </div>
</body>
</html>

