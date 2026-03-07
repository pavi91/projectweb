<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.hotelreservation.dto.ReservationDTO" %>
<%@ page import="com.hotelreservation.servlet.FrontDeskServlet" %>
<html>
<head>
    <title>Bill / Invoice</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; }
        .container { max-width: 600px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 24px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        .success { color: #27ae60; font-weight: bold; font-size: 1.1em; }
        table { width: 100%; border-collapse: collapse; margin-top: 12px; }
        td { padding: 8px; border-bottom: 1px solid #eee; }
        td:first-child { font-weight: bold; width: 45%; color: #555; }
        .total-row td { border-top: 2px solid #2c3e50; font-size: 1.15em; font-weight: bold; }
        a.btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin-top: 12px; }
        @media print { .no-print { display: none; } }
    </style>
</head>
<body>
    <div class="header"><h2>Check-Out Bill / Invoice</h2></div>
    <div class="container">
        <div class="card">
            <p class="success">Check-out completed successfully!</p>
            <%
                FrontDeskServlet.BillDetails bill = (FrontDeskServlet.BillDetails) request.getAttribute("bill");
                ReservationDTO reservation = (ReservationDTO) request.getAttribute("reservation");
                if (bill != null) {
            %>
            <h3 style="text-align:center;">Ocean View Resort</h3>
            <table>
                <tr><td>Reservation ID</td><td><%= bill.getReservationId() %></td></tr>
                <tr><td>Room</td><td><%= bill.getRoomNumber() %></td></tr>
                <tr><td>Check-in</td><td><%= bill.getCheckInDate() %></td></tr>
                <tr><td>Check-out</td><td><%= bill.getCheckOutDate() %></td></tr>
                <tr><td>Nights</td><td><%= bill.getNights() %></td></tr>
                <tr><td>Rate per Night</td><td>$<%= String.format("%.2f", bill.getRatePerNight()) %></td></tr>
                <tr><td>Room Charges</td><td>$<%= String.format("%.2f", bill.getRoomCharges()) %></td></tr>
                <tr><td>Additional Charges</td><td>$<%= String.format("%.2f", bill.getAdditionalCharges()) %></td></tr>
                <tr class="total-row"><td>Total</td><td>$<%= String.format("%.2f", bill.getTotal()) %></td></tr>
            </table>
            <% } else if (reservation != null) { %>
            <table>
                <tr><td>Reservation ID</td><td><%= reservation.getId() %></td></tr>
                <tr><td>Room</td><td><%= reservation.getRoomId() %></td></tr>
                <tr><td>Check-in</td><td><%= reservation.getCheckInDate() %></td></tr>
                <tr><td>Check-out</td><td><%= reservation.getCheckOutDate() %></td></tr>
                <tr class="total-row"><td>Total</td><td>$<%= String.format("%.2f", reservation.getTotalAmount()) %></td></tr>
            </table>
            <% } %>
        </div>
        <div class="no-print">
            <button onclick="window.print()" style="padding:8px 16px;background:#27ae60;color:white;border:none;border-radius:4px;cursor:pointer;margin-top:12px;">Print Bill</button>
            <a class="btn" href="${pageContext.request.contextPath}/frontdesk/checkout">Check Out Another</a>
            <a class="btn" href="${pageContext.request.contextPath}/frontdesk/dashboard">Dashboard</a>
        </div>
    </div>
</body>
</html>

