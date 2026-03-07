<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.hotelreservation.dto.ReservationDTO" %>
<html>
<head>
    <title>Reservation Confirmed</title>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #27ae60; color: white; padding: 15px 20px; }
        .container { max-width: 600px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 25px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .success-icon { text-align: center; font-size: 60px; margin-bottom: 10px; }
        .success-title { text-align: center; color: #27ae60; font-size: 22px; margin-bottom: 20px; }
        .detail-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #f0f0f0; }
        .detail-row .label { color: #888; }
        .detail-row .value { font-weight: bold; color: #2c3e50; }
        .total-row { display: flex; justify-content: space-between; padding: 12px 0; font-size: 18px; margin-top: 10px; border-top: 2px solid #27ae60; }
        .total-row .value { color: #27ae60; font-weight: bold; }
        .payment-badge { background: #d4edda; color: #155724; padding: 10px 15px; border-radius: 6px; text-align: center; margin-top: 15px; font-size: 14px; }
        a.btn { display: inline-block; padding: 10px 18px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin: 4px; }
        a.btn:hover { opacity: 0.85; }
        .links { margin-top: 15px; }
    </style>
</head>
<body>
    <div class="header"><h2>✅ Booking Confirmed</h2></div>
    <div class="container">
        <%
            ReservationDTO reservation = (ReservationDTO) request.getAttribute("reservation");
            if (reservation == null) {
        %>
            <div class="card"><p>No reservation details found.</p></div>
        <% } else { %>
        <div class="card">
            <div class="success-icon">✅</div>
            <div class="success-title">Reservation Confirmed!</div>

            <div class="detail-row">
                <span class="label">Reservation ID</span>
                <span class="value"><%= reservation.getId() %></span>
            </div>
            <div class="detail-row">
                <span class="label">Room</span>
                <span class="value"><%= reservation.getRoom() != null ? reservation.getRoom().getNumber() + " (" + reservation.getRoom().getType() + ")" : "Room " + reservation.getRoomId() %></span>
            </div>
            <div class="detail-row">
                <span class="label">Check-in</span>
                <span class="value"><%= reservation.getCheckInDate() %></span>
            </div>
            <div class="detail-row">
                <span class="label">Check-out</span>
                <span class="value"><%= reservation.getCheckOutDate() %></span>
            </div>
            <div class="total-row">
                <span class="label">Total Paid</span>
                <span class="value">$<%= String.format("%.2f", reservation.getTotalAmount()) %></span>
            </div>

            <div class="payment-badge">
                💳 Payment processed successfully via Online Gateway
            </div>
        </div>
        <% } %>
        <div class="links">
            <a class="btn" href="${pageContext.request.contextPath}/reservation/list">My Reservations</a>
            <a class="btn" href="${pageContext.request.contextPath}/reservation/search">Book Another Room</a>
        </div>
    </div>
</body>
</html>
