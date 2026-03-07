<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hotelreservation.dto.ReservationDTO" %>
<html>
<head>
    <title>My Reservations</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; }
        .header h2 { margin: 0; }
        .header-links a { color: white; text-decoration: none; margin-left: 15px; }
        .container { max-width: 900px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 20px; margin-bottom: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        a.btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin-right: 8px; }
        a.btn.red { background: #e74c3c; }
        a.btn.green { background: #27ae60; }
        .info { color: #7f8c8d; }
        .error { color: #e74c3c; font-weight: bold; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { padding: 10px 12px; text-align: left; border-bottom: 1px solid #ecf0f1; }
        th { background: #2c3e50; color: white; }
        tr:hover { background: #f5f6fa; }
        .status-pending { color: #f39c12; font-weight: bold; }
        .status-confirmed { color: #27ae60; font-weight: bold; }
        .status-checked_in { color: #2980b9; font-weight: bold; }
        .status-checked_out { color: #7f8c8d; font-weight: bold; }
        .status-cancelled { color: #e74c3c; font-weight: bold; }
        .actions a { margin-right: 5px; font-size: 0.9em; }
    </style>
</head>
<body>
    <div class="header">
        <h2>My Reservations</h2>
        <div class="header-links">
            <a href="${pageContext.request.contextPath}/reservation/search">Search Rooms</a>
            <a href="${pageContext.request.contextPath}/reservation/list">Refresh</a>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>
    <div class="container">
        <%
            String error = (String) request.getAttribute("error");
            if (error != null && !error.trim().isEmpty()) {
        %>
            <div class="card"><p class="error"><%= error %></p></div>
        <% } %>

        <%
            List<ReservationDTO> reservations = (List<ReservationDTO>) request.getAttribute("reservations");
        %>

        <% if (reservations != null && !reservations.isEmpty()) { %>
        <div class="card">
            <h3>All Reservations</h3>
            <table>
                <thead>
                    <tr>
                        <th>Reservation ID</th>
                        <th>Room ID</th>
                        <th>Type</th>
                        <th>Check-in</th>
                        <th>Check-out</th>
                        <th>Amount</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                <% for (ReservationDTO res : reservations) {
                    String statusClass = "status-" + (res.getStatus() != null ? res.getStatus().toLowerCase() : "pending");
                %>
                    <tr>
                        <td><%= res.getId() %></td>
                        <td><%= res.getRoomId() %></td>
                        <td><%= res.getReservationType() != null ? res.getReservationType() : "N/A" %></td>
                        <td><%= res.getCheckInDate() %></td>
                        <td><%= res.getCheckOutDate() %></td>
                        <td>$<%= String.format("%.2f", res.getTotalAmount()) %></td>
                        <td><span class="<%= statusClass %>"><%= res.getStatus() %></span></td>
                        <td class="actions">
                            <% if (!"CANCELLED".equals(res.getStatus()) && !"CHECKED_OUT".equals(res.getStatus())) { %>
                                <a href="${pageContext.request.contextPath}/reservation/cancel?reservationId=<%= res.getId() %>">Cancel</a>
                            <% } %>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <% } else { %>
        <div class="card">
            <p class="info">No reservations found. Start by searching for available rooms.</p>
        </div>
        <% } %>

        <div class="card">
            <h3>Cancel a Reservation</h3>
            <form method="get" action="${pageContext.request.contextPath}/reservation/cancel" style="display: flex; gap: 10px; align-items: center;">
                <label>Reservation ID: <input type="text" name="reservationId" required placeholder="e.g. ONL_abc12345" style="padding: 6px; border: 1px solid #ccc; border-radius: 4px;"></label>
                <button type="submit" style="padding: 8px 16px; background: #e74c3c; color: white; border: none; border-radius: 4px; cursor: pointer;">Look Up</button>
            </form>
        </div>

        <div style="margin-top: 15px;">
            <a class="btn" href="${pageContext.request.contextPath}/reservation/search">Search Rooms</a>
            <a class="btn" href="${pageContext.request.contextPath}/">Home</a>
        </div>
    </div>
</body>
</html>

