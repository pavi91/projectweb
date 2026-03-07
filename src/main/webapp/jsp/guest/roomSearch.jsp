<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Search Rooms - Ocean View Resort</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', Arial, sans-serif; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; }
        .header h2 { margin: 0; font-size: 20px; }
        .header-links a { color: white; text-decoration: none; margin-left: 15px; font-size: 14px; opacity: 0.85; }
        .header-links a:hover { opacity: 1; text-decoration: underline; }

        .container { max-width: 600px; margin: 40px auto; padding: 0 20px; }
        .card { background: white; border-radius: 12px; padding: 30px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
        .card-icon { text-align: center; font-size: 50px; margin-bottom: 10px; }
        .card h3 { text-align: center; color: #2c3e50; margin-bottom: 6px; font-size: 22px; }
        .card .subtitle { text-align: center; color: #888; font-size: 14px; margin-bottom: 25px; }

        .form-group { margin-bottom: 18px; }
        .form-group label { display: block; margin-bottom: 6px; font-weight: 600; color: #2c3e50; font-size: 14px; }
        .form-group input { width: 100%; padding: 12px 14px; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 15px; transition: border-color 0.2s; }
        .form-group input:focus { border-color: #3498db; outline: none; }

        .row { display: flex; gap: 15px; }
        .row .form-group { flex: 1; }

        .search-btn { width: 100%; padding: 13px; background: #3498db; color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: 600; cursor: pointer; transition: background 0.2s; margin-top: 5px; }
        .search-btn:hover { background: #2980b9; }

        .error-msg { background: #fdecea; color: #e74c3c; padding: 10px 14px; border-radius: 6px; margin-bottom: 18px; font-size: 14px; }

        .quick-links { display: flex; gap: 10px; margin-top: 20px; justify-content: center; }
        .quick-links a { display: inline-block; padding: 10px 18px; background: white; color: #2c3e50; text-decoration: none; border-radius: 8px; font-size: 14px; font-weight: 600; box-shadow: 0 2px 6px rgba(0,0,0,0.08); transition: box-shadow 0.2s; }
        .quick-links a:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
        .quick-links a .icon { margin-right: 5px; }
    </style>
</head>
<body>
    <div class="header">
        <h2>🏨 Ocean View Resort</h2>
        <div class="header-links">
            <a href="${pageContext.request.contextPath}/reservation/list">📋 My Reservations</a>
            <a href="${pageContext.request.contextPath}/logout">🚪 Logout</a>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <div class="card-icon">🔍</div>
            <h3>Search Available Rooms</h3>
            <p class="subtitle">Select your dates to find the perfect room</p>

            <%
                String error = (String) request.getAttribute("error");
                if (error != null && !error.trim().isEmpty()) {
            %>
                <div class="error-msg">❌ <%= error %></div>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/reservation/search">
                <div class="row">
                    <div class="form-group">
                        <label>Check-in Date</label>
                        <input type="date" name="checkIn" required>
                    </div>
                    <div class="form-group">
                        <label>Check-out Date</label>
                        <input type="date" name="checkOut" required>
                    </div>
                </div>
                <button type="submit" class="search-btn">🔍 Search Rooms</button>
            </form>
        </div>

        <div class="quick-links">
            <a href="${pageContext.request.contextPath}/reservation/list"><span class="icon">📋</span>My Reservations</a>
            <a href="${pageContext.request.contextPath}/"><span class="icon">🏠</span>Home</a>
        </div>
    </div>
</body>
</html>

