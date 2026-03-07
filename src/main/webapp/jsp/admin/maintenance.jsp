<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.hotelreservation.dto.RoomDTO" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Room Maintenance</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; }
        .container { max-width: 900px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .error { color: #e74c3c; background: #fdecea; padding: 10px 15px; border-radius: 4px; margin-bottom: 15px; }
        .success { color: #27ae60; background: #eafaf1; padding: 10px 15px; border-radius: 4px; margin-bottom: 15px; }
        a.btn { display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; margin-top: 12px; }

        /* Table */
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th { background: #2c3e50; color: white; padding: 10px 12px; text-align: left; font-size: 13px; }
        td { padding: 10px 12px; border-bottom: 1px solid #e0e0e0; font-size: 13px; }
        tr:hover { background: #f8f9fa; }

        /* Badges */
        .badge { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 11px; font-weight: bold; color: white; }
        .badge-dirty { background: #e74c3c; }
        .badge-clean { background: #27ae60; }
        .badge-maintenance { background: #f39c12; }
        .badge-available { background: #27ae60; }
        .badge-occupied { background: #3498db; }
        .badge-reserved { background: #8e44ad; }

        /* Action buttons */
        .action-btn { padding: 6px 14px; border: none; border-radius: 4px; cursor: pointer; font-size: 12px; font-weight: bold; color: white; }
        .btn-clean { background: #27ae60; }
        .btn-clean:hover { background: #219a52; }
        .btn-maint { background: #f39c12; }
        .btn-maint:hover { background: #e67e22; }

        .alert-box { padding: 15px; border-radius: 8px; margin-bottom: 15px; }
        .alert-warning { background: #fff3cd; color: #856404; border: 1px solid #ffc107; }
        .count { font-size: 28px; font-weight: bold; }
        .stats { display: flex; gap: 15px; margin-bottom: 20px; }
        .stat-card { flex: 1; background: white; border-radius: 8px; padding: 15px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); text-align: center; }
        .stat-label { color: #888; font-size: 12px; text-transform: uppercase; }
    </style>
</head>
<body>
    <div class="header"><h2>🧹 Room Maintenance Management</h2></div>
    <div class="container">

        <%-- Messages --%>
        <%
            String error = (String) request.getAttribute("error");
            String message = (String) request.getAttribute("message");
            if (error != null && !error.trim().isEmpty()) {
        %>
            <div class="error">❌ <%= error %></div>
        <% } %>
        <% if (message != null && !message.trim().isEmpty()) { %>
            <div class="success">✅ <%= message %></div>
        <% } %>

        <%
            List<RoomDTO> dirtyRooms = (List<RoomDTO>) request.getAttribute("dirtyRooms");
            List<RoomDTO> allRooms = (List<RoomDTO>) request.getAttribute("allRooms");
            int dirtyCount = dirtyRooms != null ? dirtyRooms.size() : 0;
            int totalCount = allRooms != null ? allRooms.size() : 0;
            int cleanCount = totalCount - dirtyCount;
        %>

        <!-- Stats -->
        <div class="stats">
            <div class="stat-card">
                <div class="stat-label">Needs Cleaning</div>
                <div class="count" style="color: #e74c3c;"><%= dirtyCount %></div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Clean & Ready</div>
                <div class="count" style="color: #27ae60;"><%= cleanCount %></div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Total Rooms</div>
                <div class="count" style="color: #3498db;"><%= totalCount %></div>
            </div>
        </div>

        <% if (dirtyCount > 0) { %>
        <!-- Dirty Rooms needing attention -->
        <div class="alert-box alert-warning">
            ⚠️ <strong><%= dirtyCount %> room(s)</strong> need cleaning before they can be booked by guests.
        </div>
        <% } %>

        <!-- Rooms Needing Attention -->
        <div class="card">
            <h3>🔴 Rooms Needing Cleaning / Under Maintenance</h3>
            <% if (dirtyRooms != null && !dirtyRooms.isEmpty()) { %>
            <table>
                <tr>
                    <th>Room ID</th>
                    <th>Room No.</th>
                    <th>Type</th>
                    <th>Status</th>
                    <th>Clean?</th>
                    <th>Actions</th>
                </tr>
                <% for (RoomDTO room : dirtyRooms) { %>
                <tr>
                    <td><%= room.getId() %></td>
                    <td><strong><%= room.getNumber() %></strong></td>
                    <td><%= room.getType() %></td>
                    <td>
                        <span class="badge <%= "UNDER_MAINTENANCE".equals(room.getStatus()) ? "badge-maintenance" :
                            "AVAILABLE".equals(room.getStatus()) ? "badge-available" :
                            "OCCUPIED".equals(room.getStatus()) ? "badge-occupied" : "badge-reserved" %>">
                            <%= room.getStatus() %>
                        </span>
                    </td>
                    <td>
                        <span class="badge <%= room.isClean() ? "badge-clean" : "badge-dirty" %>">
                            <%= room.isClean() ? "Clean" : "Dirty" %>
                        </span>
                    </td>
                    <td>
                        <% if (!room.isClean()) { %>
                        <form method="post" action="${pageContext.request.contextPath}/admin/maintenance" style="display:inline">
                            <input type="hidden" name="roomId" value="<%= room.getId() %>">
                            <input type="hidden" name="action" value="markClean">
                            <button type="submit" class="action-btn btn-clean">✅ Mark Clean</button>
                        </form>
                        <% } %>
                        <% if (!"UNDER_MAINTENANCE".equals(room.getStatus())) { %>
                        <form method="post" action="${pageContext.request.contextPath}/admin/maintenance" style="display:inline; margin-left:5px;">
                            <input type="hidden" name="roomId" value="<%= room.getId() %>">
                            <input type="hidden" name="action" value="markMaintenance">
                            <button type="submit" class="action-btn btn-maint">🔧 Maintenance</button>
                        </form>
                        <% } %>
                        <% if ("UNDER_MAINTENANCE".equals(room.getStatus())) { %>
                        <form method="post" action="${pageContext.request.contextPath}/admin/maintenance" style="display:inline">
                            <input type="hidden" name="roomId" value="<%= room.getId() %>">
                            <input type="hidden" name="action" value="markClean">
                            <button type="submit" class="action-btn btn-clean">✅ Complete & Mark Clean</button>
                        </form>
                        <% } %>
                    </td>
                </tr>
                <% } %>
            </table>
            <% } else { %>
            <p style="color: #27ae60; text-align: center; padding: 20px;">🎉 All rooms are clean! Nothing needs attention.</p>
            <% } %>
        </div>

        <!-- All Rooms Overview -->
        <div class="card">
            <h3>📋 All Rooms Overview</h3>
            <% if (allRooms != null && !allRooms.isEmpty()) { %>
            <table>
                <tr>
                    <th>Room ID</th>
                    <th>Room No.</th>
                    <th>Type</th>
                    <th>Price</th>
                    <th>Status</th>
                    <th>Clean?</th>
                    <th>Bookable?</th>
                </tr>
                <% for (RoomDTO room : allRooms) {
                    boolean bookable = "AVAILABLE".equals(room.getStatus()) && room.isClean();
                %>
                <tr>
                    <td><%= room.getId() %></td>
                    <td><%= room.getNumber() %></td>
                    <td><%= room.getType() %></td>
                    <td>$<%= String.format("%.2f", room.getBasePrice()) %></td>
                    <td>
                        <span class="badge <%= "AVAILABLE".equals(room.getStatus()) ? "badge-available" :
                            "UNDER_MAINTENANCE".equals(room.getStatus()) ? "badge-maintenance" :
                            "OCCUPIED".equals(room.getStatus()) ? "badge-occupied" : "badge-reserved" %>">
                            <%= room.getStatus() %>
                        </span>
                    </td>
                    <td>
                        <span class="badge <%= room.isClean() ? "badge-clean" : "badge-dirty" %>">
                            <%= room.isClean() ? "Clean" : "Dirty" %>
                        </span>
                    </td>
                    <td style="text-align:center; font-weight:bold; color:<%= bookable ? "#27ae60" : "#e74c3c" %>">
                        <%= bookable ? "✅ Yes" : "❌ No" %>
                    </td>
                </tr>
                <% } %>
            </table>
            <% } %>
        </div>

        <a class="btn" href="${pageContext.request.contextPath}/admin/dashboard">Back to Dashboard</a>
    </div>
</body>
</html>

