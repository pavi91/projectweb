<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.hotelreservation.dto.ReservationDTO" %>
<html>
<head>
    <title>Walk-In Confirmation</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; }
        .container { max-width: 600px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        .success { color: #27ae60; font-weight: bold; font-size: 1.1em; }
        table { width: 100%; border-collapse: collapse; margin-top: 12px; }
        td { padding: 8px; border-bottom: 1px solid #eee; }
        td:first-child { font-weight: bold; width: 40%; color: #555; }
        a.btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin-top: 12px; }
    </style>
</head>
<body>
    <div class="header"><h2>Walk-In Reservation Confirmed</h2></div>
    <div class="container">
        <div class="card">
            <p class="success">Walk-in reservation created successfully!</p>
            <%
                ReservationDTO reservation = (ReservationDTO) request.getAttribute("reservation");
                if (reservation != null) {
            %>
            <table>
                <tr><td>Reservation ID</td><td><%= reservation.getId() %></td></tr>
                <tr><td>Room ID</td><td><%= reservation.getRoomId() %></td></tr>
                <tr><td>Check-in</td><td><%= reservation.getCheckInDate() %></td></tr>
                <tr><td>Check-out</td><td><%= reservation.getCheckOutDate() %></td></tr>
                <tr><td>Total Amount</td><td>$<%= String.format("%.2f", reservation.getTotalAmount()) %></td></tr>
                <tr><td>Status</td><td><%= reservation.getStatus() %></td></tr>
            </table>
            <% } %>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/frontdesk/walkin">New Walk-In</a>
        <a class="btn" href="${pageContext.request.contextPath}/frontdesk/dashboard">Dashboard</a>
    </div>
</body>
</html>

