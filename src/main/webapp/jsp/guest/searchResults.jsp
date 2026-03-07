<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.hotelreservation.dto.RoomDTO" %>
<%
    String error = (String) request.getAttribute("error");
    List<RoomDTO> rooms = (List<RoomDTO>) request.getAttribute("rooms");
    String checkIn = (String) request.getAttribute("checkIn");
    String checkOut = (String) request.getAttribute("checkOut");
%>
<html>
<head>
    <title>Available Rooms - Ocean View Resort</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', Arial, sans-serif; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; }
        .header h2 { margin: 0; font-size: 20px; }
        .header-links a { color: white; text-decoration: none; margin-left: 15px; font-size: 14px; opacity: 0.85; }
        .header-links a:hover { opacity: 1; text-decoration: underline; }

        .container { max-width: 900px; margin: 30px auto; padding: 0 20px; }
        .card { background: white; border-radius: 10px; padding: 25px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); margin-bottom: 20px; }
        .card h3 { color: #2c3e50; margin-bottom: 15px; font-size: 18px; }

        .error-msg { background: #fdecea; color: #e74c3c; padding: 10px 14px; border-radius: 6px; margin-bottom: 18px; font-size: 14px; }
        .info-bar { background: #e8f4fd; color: #2980b9; padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; font-size: 14px; display: flex; justify-content: space-between; align-items: center; }
        .info-bar .dates { font-weight: 600; }

        /* Rooms Table */
        .rooms-table { width: 100%; border-collapse: collapse; }
        .rooms-table th { background: #2c3e50; color: white; padding: 12px 14px; text-align: left; font-size: 13px; text-transform: uppercase; letter-spacing: 0.5px; }
        .rooms-table td { padding: 12px 14px; border-bottom: 1px solid #f0f0f0; font-size: 14px; }
        .rooms-table tr:hover { background: #e8f4fd; cursor: pointer; }
        .rooms-table tr.selected { background: #d4edda; }

        .badge { display: inline-block; padding: 4px 12px; border-radius: 20px; font-size: 11px; font-weight: 700; color: white; }
        .badge-single { background: #3498db; }
        .badge-double { background: #9b59b6; }
        .badge-suite { background: #e67e22; }
        .price { font-weight: 700; color: #27ae60; font-size: 15px; }

        .select-btn { padding: 6px 14px; background: #27ae60; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 12px; font-weight: 600; transition: background 0.2s; }
        .select-btn:hover { background: #219a52; }

        .no-rooms { text-align: center; padding: 40px 20px; color: #888; }
        .no-rooms .icon { font-size: 48px; margin-bottom: 10px; }

        /* Booking Form */
        .form-section { border-top: 2px solid #f0f0f0; padding-top: 20px; margin-top: 20px; }
        .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
        .form-group { margin-bottom: 0; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: 600; color: #2c3e50; font-size: 13px; }
        .form-group input { width: 100%; padding: 10px 12px; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 14px; transition: border-color 0.2s; }
        .form-group input:focus { border-color: #3498db; outline: none; }
        .form-group input::placeholder { color: #bbb; }
        .form-group.full { grid-column: 1 / -1; }
        .selected-room-info { color: #27ae60; font-weight: normal; font-size: 13px; }

        .pay-btn { width: 100%; padding: 13px; background: #27ae60; color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: 600; cursor: pointer; transition: background 0.2s; margin-top: 15px; }
        .pay-btn:hover { background: #219a52; }

        .back-link { display: inline-block; margin-top: 15px; color: #3498db; text-decoration: none; font-size: 14px; font-weight: 600; }
        .back-link:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="header">
        <h2>🏨 Ocean View Resort</h2>
        <div class="header-links">
            <a href="${pageContext.request.contextPath}/reservation/search">🔍 New Search</a>
            <a href="${pageContext.request.contextPath}/reservation/list">📋 My Reservations</a>
            <a href="${pageContext.request.contextPath}/logout">🚪 Logout</a>
        </div>
    </div>

    <div class="container">

        <% if (error != null && !error.trim().isEmpty()) { %>
            <div class="error-msg">❌ <%= error %></div>
        <% } %>

        <div class="info-bar">
            <span>🗓 Showing available rooms for:</span>
            <span class="dates"><%= checkIn %> → <%= checkOut %></span>
        </div>

        <% if (rooms == null || rooms.isEmpty()) { %>
        <!-- No Rooms -->
        <div class="card">
            <div class="no-rooms">
                <div class="icon">😔</div>
                <h3>No Rooms Available</h3>
                <p>Sorry, no rooms are available for the selected dates. Please try different dates.</p>
            </div>
        </div>

        <% } else { %>
        <!-- Available Rooms -->
        <div class="card">
            <h3>🛏 Available Rooms (<%= rooms.size() %>)</h3>
            <p style="color: #888; font-size: 13px; margin-bottom: 12px;">Click a room to auto-fill the booking form below.</p>
            <table class="rooms-table">
                <thead>
                    <tr>
                        <th>Room</th>
                        <th>Type</th>
                        <th>Price / Night</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                <% for (RoomDTO room : rooms) {
                    String badgeClass = "badge-single";
                    if ("DOUBLE".equals(room.getType())) badgeClass = "badge-double";
                    else if ("SUITE".equals(room.getType())) badgeClass = "badge-suite";
                %>
                    <tr id="row-<%= room.getId() %>" onclick="selectRoom(<%= room.getId() %>, '<%= room.getNumber() %>', '<%= room.getType() %>', '<%= room.getBasePrice() %>')">
                        <td><strong>Room <%= room.getNumber() %></strong></td>
                        <td><span class="badge <%= badgeClass %>"><%= room.getType() %></span></td>
                        <td><span class="price">$<%= String.format("%.2f", room.getBasePrice()) %></span></td>
                        <td><button type="button" class="select-btn" onclick="selectRoom(<%= room.getId() %>, '<%= room.getNumber() %>', '<%= room.getType() %>', '<%= room.getBasePrice() %>')">Select</button></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <!-- Booking Form -->
        <div class="card">
            <h3>📝 Complete Your Booking <span id="selectedRoomInfo" class="selected-room-info"></span></h3>

            <form method="post" action="${pageContext.request.contextPath}/reservation/pay" id="bookingForm">
                <input type="hidden" name="checkIn" value="<%= checkIn != null ? checkIn : "" %>">
                <input type="hidden" name="checkOut" value="<%= checkOut != null ? checkOut : "" %>">

                <div class="form-grid">
                    <div class="form-group">
                        <label>Room ID</label>
                        <input type="number" name="roomId" id="roomIdInput" placeholder="Select a room above" required>
                    </div>
                    <div class="form-group">
                        <label>Full Name</label>
                        <input type="text" name="name" placeholder="John Doe" required>
                    </div>
                    <div class="form-group">
                        <label>NIC Number</label>
                        <input type="text" name="nic" placeholder="e.g. 200312345678" required>
                    </div>
                    <div class="form-group">
                        <label>Phone</label>
                        <input type="text" name="phone" placeholder="e.g. 0771234567" required>
                    </div>
                    <div class="form-group full">
                        <label>Email</label>
                        <input type="email" name="email" placeholder="you@example.com" required>
                    </div>
                </div>

                <button type="submit" class="pay-btn">💳 Proceed to Payment</button>
            </form>
        </div>
        <% } %>

        <a class="back-link" href="${pageContext.request.contextPath}/reservation/search">← New Search</a>
    </div>

    <script>
        function selectRoom(roomId, roomNumber, roomType, price) {
            document.getElementById('roomIdInput').value = roomId;
            document.getElementById('selectedRoomInfo').textContent = '— Room ' + roomNumber + ' (' + roomType + ') · $' + parseFloat(price).toFixed(2) + '/night';

            // Highlight row
            document.querySelectorAll('.rooms-table tbody tr').forEach(function(r) { r.classList.remove('selected'); });
            var row = document.getElementById('row-' + roomId);
            if (row) row.classList.add('selected');

            // Scroll to form
            document.getElementById('bookingForm').scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    </script>
</body>
</html>

