<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Staff Management</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; }
        .container { max-width: 600px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        .error { color: red; margin-bottom: 10px; }
        label { display: block; margin: 10px 0 4px; font-weight: bold; }
        input, select { padding: 8px; width: 100%; box-sizing: border-box; }
        button { padding: 10px 20px; background: #27ae60; color: white; border: none; border-radius: 4px; cursor: pointer; margin-top: 12px; }
        button:hover { background: #219a52; }
        a.btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin-top: 12px; }
    </style>
</head>
<body>
    <div class="header"><h2>Create Staff Account</h2></div>
    <div class="container">
        <div class="card">
            <%
                String error = (String) request.getAttribute("error");
                if (error != null && !error.trim().isEmpty()) {
            %>
                <p class="error"><%= error %></p>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/admin/staff/create">
                <label>Username:</label>
                <input type="text" name="username" required>
                <label>Password:</label>
                <input type="password" name="password" required>
                <label>Confirm Password:</label>
                <input type="password" name="confirmPassword" required>
                <label>Role:</label>
                <select name="role" required>
                    <option value="">-- Select Role --</option>
                    <option value="RECEPTIONIST">Receptionist</option>
                    <option value="MAINTENANCE">Maintenance Crew</option>
                </select>
                <button type="submit">Create Account</button>
            </form>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/admin/dashboard">Back to Dashboard</a>
    </div>
</body>
</html>

