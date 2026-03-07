<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String error = (String) request.getAttribute("error");
    String username = (String) request.getAttribute("username");
    String name = (String) request.getAttribute("name");
    String nic = (String) request.getAttribute("nic");
    String phone = (String) request.getAttribute("phone");
    String email = (String) request.getAttribute("email");
%>
<html>
<head>
    <title>Sign Up - Ocean View Resort</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', Arial, sans-serif; background: #f0f4f8; min-height: 100vh; display: flex; flex-direction: column; }
        .header { background: #2c3e50; color: white; padding: 20px 30px; text-align: center; }
        .header h1 { font-size: 24px; margin-bottom: 4px; }
        .header p { opacity: 0.7; font-size: 14px; }

        .main { flex: 1; display: flex; justify-content: center; align-items: flex-start; padding: 40px 20px; }
        .signup-card { background: white; border-radius: 12px; padding: 35px 30px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); width: 100%; max-width: 480px; }
        .signup-card h2 { color: #2c3e50; text-align: center; margin-bottom: 6px; font-size: 22px; }
        .signup-card .subtitle { color: #888; text-align: center; font-size: 14px; margin-bottom: 25px; }

        .section-title { font-size: 13px; text-transform: uppercase; letter-spacing: 1px; color: #3498db; font-weight: 700; margin-bottom: 12px; margin-top: 20px; padding-bottom: 6px; border-bottom: 2px solid #f0f0f0; }
        .section-title:first-of-type { margin-top: 0; }

        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: 600; color: #2c3e50; font-size: 13px; }
        .form-group input { width: 100%; padding: 11px 14px; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 14px; transition: border-color 0.2s; }
        .form-group input:focus { border-color: #3498db; outline: none; }
        .form-group input::placeholder { color: #bbb; }
        .form-group .hint { font-size: 11px; color: #aaa; margin-top: 3px; }

        .row { display: flex; gap: 12px; }
        .row .form-group { flex: 1; }

        .signup-btn { width: 100%; padding: 13px; background: #27ae60; color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: 600; cursor: pointer; transition: background 0.2s; margin-top: 20px; }
        .signup-btn:hover { background: #219a52; }

        .error-msg { background: #fdecea; color: #e74c3c; padding: 10px 14px; border-radius: 6px; margin-bottom: 18px; font-size: 14px; text-align: center; }

        .links { text-align: center; margin-top: 20px; font-size: 14px; color: #888; }
        .links a { color: #3498db; text-decoration: none; font-weight: 600; }
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
        <div class="signup-card">
            <div class="icon">📝</div>
            <h2>Create Your Account</h2>
            <p class="subtitle">Sign up to book rooms and manage your reservations</p>

            <% if (error != null && !error.trim().isEmpty()) { %>
                <div class="error-msg">❌ <%= error %></div>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/signup">

                <div class="section-title">Account Details</div>
                <div class="form-group">
                    <label>Username</label>
                    <input type="text" name="username" placeholder="Choose a username" required
                           value="<%= username != null ? username : "" %>">
                </div>
                <div class="row">
                    <div class="form-group">
                        <label>Password</label>
                        <input type="password" name="password" placeholder="Min. 6 characters" required minlength="6">
                    </div>
                    <div class="form-group">
                        <label>Confirm Password</label>
                        <input type="password" name="confirmPassword" placeholder="Re-enter password" required minlength="6">
                    </div>
                </div>

                <div class="section-title">Personal Information</div>
                <div class="form-group">
                    <label>Full Name</label>
                    <input type="text" name="name" placeholder="e.g. John Doe" required
                           value="<%= name != null ? name : "" %>">
                </div>
                <div class="row">
                    <div class="form-group">
                        <label>NIC Number</label>
                        <input type="text" name="nic" placeholder="e.g. 200312345678" required
                               value="<%= nic != null ? nic : "" %>">
                    </div>
                    <div class="form-group">
                        <label>Phone</label>
                        <input type="text" name="phone" placeholder="e.g. 0771234567" required
                               value="<%= phone != null ? phone : "" %>">
                    </div>
                </div>
                <div class="form-group">
                    <label>Email <span style="color:#aaa; font-weight:normal;">(optional)</span></label>
                    <input type="email" name="email" placeholder="you@example.com"
                           value="<%= email != null ? email : "" %>">
                </div>

                <button type="submit" class="signup-btn">Create Account</button>
            </form>

            <div class="links">
                Already have an account? <a href="${pageContext.request.contextPath}/login">Sign In</a>
            </div>
        </div>
    </div>
</body>
</html>

