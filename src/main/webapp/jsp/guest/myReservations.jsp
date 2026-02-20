<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>My Reservations</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; }
        .header h2 { margin: 0; }
        .container { max-width: 800px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 20px; margin-bottom: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        a.btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; }
        .info { color: #7f8c8d; }
    </style>
</head>
<body>
    <div class="header"><h2>My Reservations</h2></div>
    <div class="container">
        <div class="card">
            <p class="info">Your reservations will appear here once they are retrieved from the database.</p>
            <p class="info">Use the reservation ID provided at booking time to look up or cancel a reservation.</p>
        </div>
        <div class="card">
            <h3>Cancel a Reservation</h3>
            <form method="get" action="${pageContext.request.contextPath}/reservation/cancel">
                <label>Reservation ID: <input type="text" name="reservationId" required></label><br><br>
                <button type="submit" class="btn" style="border:none;cursor:pointer;">Look Up</button>
            </form>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/reservation/search">Search Rooms</a>
        <a class="btn" href="${pageContext.request.contextPath}/">Home</a>
    </div>
</body>
</html>

