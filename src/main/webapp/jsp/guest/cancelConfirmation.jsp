<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Cancellation Confirmed</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; }
        .container { max-width: 600px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        .success { color: #27ae60; font-weight: bold; }
        a.btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin-top: 12px; }
    </style>
</head>
<body>
    <div class="header"><h2>Cancellation Confirmed</h2></div>
    <div class="container">
        <div class="card">
            <p class="success"><%= request.getAttribute("message") != null ? request.getAttribute("message") : "Reservation cancelled successfully." %></p>
            <div style="background: #fff3cd; color: #856404; border: 1px solid #ffc107; padding: 15px; border-radius: 6px; margin-top: 15px;">
                <strong>💰 Refund Notice</strong><br>
                Refunds are processed <strong>offline</strong> by our accounts department.
                Please allow <strong>5–7 business days</strong> for the refund to appear on your original payment method.
                If you have any questions, contact the front desk.
            </div>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/reservation/search">Search Rooms</a>
        <a class="btn" href="${pageContext.request.contextPath}/">Home</a>
    </div>
</body>
</html>

