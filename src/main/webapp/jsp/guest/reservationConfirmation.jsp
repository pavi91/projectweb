<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.hotelreservation.dto.ReservationDTO" %>
<html>
<head>
  <title>Reservation Confirmation</title>
</head>
<body>
  <h2>Reservation Confirmed</h2>
  <%
    ReservationDTO reservation = (ReservationDTO) request.getAttribute("reservation");
    if (reservation == null) {
  %>
    <p>No reservation details found.</p>
  <% } else { %>
    <p>Reservation ID: <%= reservation.getId() %></p>
    <p>Room ID: <%= reservation.getRoomId() %></p>
    <p>Check-in: <%= reservation.getCheckInDate() %></p>
    <p>Check-out: <%= reservation.getCheckOutDate() %></p>
    <p>Total Amount: <%= reservation.getTotalAmount() %></p>
  <% } %>
  <p><a href="/projectweb/reservation/search">Book Another</a></p>
</body>
</html>
