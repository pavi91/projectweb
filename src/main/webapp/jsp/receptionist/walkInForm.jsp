<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.hotelreservation.dto.RoomDTO" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Walk-In Reservation</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; }
        .container { max-width: 800px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .error { color: red; margin-bottom: 10px; }
        label { display: block; margin: 10px 0 4px; font-weight: bold; }
        input, select { padding: 8px; width: 100%; box-sizing: border-box; }
        button { padding: 10px 20px; background: #27ae60; color: white; border: none; border-radius: 4px; cursor: pointer; margin-top: 12px; }
        button:hover { background: #219a52; }
        a.btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin-top: 12px; }

        /* Available Rooms Table */
        .rooms-table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        .rooms-table th { background: #2c3e50; color: white; padding: 10px 12px; text-align: left; font-size: 13px; }
        .rooms-table td { padding: 10px 12px; border-bottom: 1px solid #e0e0e0; font-size: 13px; }
        .rooms-table tr:hover { background: #e8f4fd; cursor: pointer; }
        .rooms-table tr.selected { background: #d4edda; border-left: 4px solid #27ae60; }
        .rooms-table .badge { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 11px; font-weight: bold; color: white; }
        .badge-single { background: #3498db; }
        .badge-double { background: #9b59b6; }
        .badge-suite { background: #e67e22; }
        .badge-clean { background: #27ae60; }
        .badge-dirty { background: #e74c3c; }
        .rooms-count { color: #888; font-size: 13px; margin-top: 5px; }
        .select-btn { padding: 4px 12px; background: #27ae60; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 12px; }
        .select-btn:hover { background: #219a52; }
        .no-rooms { color: #e74c3c; text-align: center; padding: 20px; }
    </style>
</head>
<body>
    <div class="header"><h2>Walk-In Reservation</h2></div>
    <div class="container">

        <!-- Available Rooms Section -->
        <div class="card">
            <h3>📋 Available Rooms</h3>
            <p class="rooms-count">Select a room from the table below — it will auto-fill the Room ID field.</p>
            <%
                List<RoomDTO> availableRooms = (List<RoomDTO>) request.getAttribute("availableRooms");
                if (availableRooms != null && !availableRooms.isEmpty()) {
            %>
            <table class="rooms-table">
                <tr>
                    <th>Room ID</th>
                    <th>Room No.</th>
                    <th>Type</th>
                    <th>Price/Night</th>
                    <th>Clean</th>
                    <th></th>
                </tr>
                <% for (RoomDTO room : availableRooms) {
                    String badgeClass = "badge-single";
                    if ("DOUBLE".equals(room.getType())) badgeClass = "badge-double";
                    else if ("SUITE".equals(room.getType())) badgeClass = "badge-suite";
                %>
                <tr id="room-row-<%= room.getId() %>" onclick="selectRoom(<%= room.getId() %>, '<%= room.getNumber() %>')">
                    <td><strong><%= room.getId() %></strong></td>
                    <td><%= room.getNumber() %></td>
                    <td><span class="badge <%= badgeClass %>"><%= room.getType() %></span></td>
                    <td>$<%= String.format("%.2f", room.getBasePrice()) %></td>
                    <td><span class="badge <%= room.isClean() ? "badge-clean" : "badge-dirty" %>"><%= room.isClean() ? "Clean" : "Dirty" %></span></td>
                    <td><button type="button" class="select-btn" onclick="selectRoom(<%= room.getId() %>, '<%= room.getNumber() %>')">Select</button></td>
                </tr>
                <% } %>
            </table>
            <p class="rooms-count"><%= availableRooms.size() %> room(s) available</p>
            <% } else { %>
            <p class="no-rooms">⚠ No rooms are currently available.</p>
            <% } %>
        </div>

        <!-- Reservation Form -->
        <div class="card">
            <%
                String error = (String) request.getAttribute("error");
                if (error != null && !error.trim().isEmpty()) {
            %>
                <p class="error"><%= error %></p>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/frontdesk/walkin">
                <h3>Guest Details</h3>
                <label>Name:</label>
                <input type="text" name="name" required>
                <label>NIC:</label>
                <input type="text" name="nic" required>
                <label>Phone:</label>
                <input type="text" name="phone" required>

                <h3>Room Details</h3>
                <label>Room ID: <span id="selected-room-info" style="font-weight:normal; color:#27ae60;"></span></label>
                <input type="number" name="roomId" id="roomIdInput" required>
                <label>Check-in Date:</label>
                <input type="date" name="checkIn" required>
                <label>Check-out Date:</label>
                <input type="date" name="checkOut" required>

                <button type="submit">Create Walk-In Reservation</button>
            </form>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/frontdesk/dashboard">Back to Dashboard</a>
    </div>

    <script>
        function selectRoom(roomId, roomNumber) {
            // Set the room ID input
            document.getElementById('roomIdInput').value = roomId;
            document.getElementById('selected-room-info').textContent = '(Room ' + roomNumber + ' selected)';

            // Highlight selected row
            document.querySelectorAll('.rooms-table tr').forEach(function(r) { r.classList.remove('selected'); });
            var row = document.getElementById('room-row-' + roomId);
            if (row) row.classList.add('selected');

            // Scroll to the form
            document.getElementById('roomIdInput').focus();
        }
    </script>
</body>
</html>

