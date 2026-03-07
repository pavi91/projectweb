<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Login - Ocean View Resort</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', Arial, sans-serif; background: #f0f4f8; min-height: 100vh; display: flex; flex-direction: column; }
        .header { background: #2c3e50; color: white; padding: 20px 30px; text-align: center; }
        .header h1 { font-size: 24px; margin-bottom: 4px; }
        .header p { opacity: 0.7; font-size: 14px; }

        .main { flex: 1; display: flex; justify-content: center; align-items: center; padding: 40px 20px; }
        .login-card { background: white; border-radius: 12px; padding: 35px 30px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); width: 100%; max-width: 420px; }
        .login-card h2 { color: #2c3e50; text-align: center; margin-bottom: 8px; font-size: 22px; }
        .login-card .subtitle { color: #888; text-align: center; font-size: 14px; margin-bottom: 25px; }

        .form-group { margin-bottom: 18px; }
        .form-group label { display: block; margin-bottom: 6px; font-weight: 600; color: #2c3e50; font-size: 14px; }
        .form-group input { width: 100%; padding: 12px 14px; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 15px; transition: border-color 0.2s; }
        .form-group input:focus { border-color: #3498db; outline: none; }
        .form-group input::placeholder { color: #bbb; }

        .login-btn { width: 100%; padding: 13px; background: #3498db; color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: 600; cursor: pointer; transition: background 0.2s; margin-top: 5px; }
        .login-btn:hover { background: #2980b9; }
        .login-btn:active { transform: scale(0.99); }

        .error-msg { background: #fdecea; color: #e74c3c; padding: 10px 14px; border-radius: 6px; margin-bottom: 18px; font-size: 14px; text-align: center; }

        .links { text-align: center; margin-top: 20px; }
        .links a { color: #3498db; text-decoration: none; font-size: 14px; }
        .links a:hover { text-decoration: underline; }

        .icon { text-align: center; margin-bottom: 15px; font-size: 50px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🏨 Ocean View Resort</h1>
        <p>Hotel Reservation System</p>
    </div>

    <div class="main">
        <div class="login-card">
            <div class="icon">🔐</div>
            <h2>Welcome Back</h2>
            <p class="subtitle">Sign in to your account</p>

            <%
                String errorMessage = (String) request.getAttribute("errorMessage");
                String registered = request.getParameter("registered");
                if ("true".equals(registered)) {
            %>
                <div style="background: #d4edda; color: #155724; padding: 10px 14px; border-radius: 6px; margin-bottom: 18px; font-size: 14px; text-align: center;">
                    ✅ Account created successfully! Please sign in.
                </div>
            <% }
                if (errorMessage != null && !errorMessage.trim().isEmpty()) {
            %>
                <div class="error-msg">❌ <%= errorMessage %></div>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/login">
                <div class="form-group">
                    <label>Username</label>
                    <input type="text" name="username" placeholder="Enter your username" required autofocus>
                </div>
                <div class="form-group">
                    <label>Password</label>
                    <input type="password" name="password" placeholder="Enter your password" required>
                </div>
                <button type="submit" class="login-btn">Sign In</button>
            </form>

            <div class="links">
                <a href="${pageContext.request.contextPath}/">← Back to Home</a>
                <span style="margin: 0 8px; color: #ddd;">|</span>
                Don't have an account? <a href="${pageContext.request.contextPath}/signup">Sign Up</a>
            </div>
        </div>
    </div>
</body>
</html>

