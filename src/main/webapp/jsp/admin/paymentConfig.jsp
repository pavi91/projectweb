<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Payment Configuration</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; }
        .container { max-width: 600px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        .error { color: red; margin-bottom: 10px; }
        label { display: block; margin: 10px 0 4px; font-weight: bold; }
        select { padding: 8px; width: 100%; box-sizing: border-box; }
        button { padding: 10px 20px; background: #e67e22; color: white; border: none; border-radius: 4px; cursor: pointer; margin-top: 12px; }
        button:hover { background: #d35400; }
        a.btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin-top: 12px; }
    </style>
</head>
<body>
    <div class="header"><h2>Payment Adapter Configuration</h2></div>
    <div class="container">
        <div class="card">
            <%
                String error = (String) request.getAttribute("error");
                if (error != null && !error.trim().isEmpty()) {
            %>
                <p class="error"><%= error %></p>
            <% } %>

            <p>Select the payment adapter to use for processing transactions.</p>
            <form method="post" action="${pageContext.request.contextPath}/admin/payment-config">
                <label>Payment Adapter:</label>
                <select name="adapter" required>
                    <option value="">-- Select Adapter --</option>
                    <option value="POS">POS Terminal (Walk-in)</option>
                    <option value="ONLINE_GATEWAY">Online Payment Gateway</option>
                </select>
                <button type="submit">Apply Configuration</button>
            </form>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/admin/dashboard">Back to Dashboard</a>
    </div>
</body>
</html>

