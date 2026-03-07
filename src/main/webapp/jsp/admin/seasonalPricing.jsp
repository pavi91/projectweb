<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.hotelreservation.entity.SeasonalPricing" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Seasonal Pricing</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; }
        .header h2 { margin: 0; }
        .container { max-width: 960px; margin: 30px auto; padding: 20px; }
        .card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .card h3 { margin-top: 0; color: #2c3e50; }
        .error { color: #e74c3c; background: #fde8e8; padding: 10px; border-radius: 4px; margin-bottom: 12px; }
        .success { color: #27ae60; background: #e8fde8; padding: 10px; border-radius: 4px; margin-bottom: 12px; }
        label { display: block; margin: 10px 0 4px; font-weight: bold; }
        input[type="text"], input[type="date"], input[type="number"] {
            padding: 8px; width: 100%; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px;
        }
        .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
        button { padding: 10px 20px; color: white; border: none; border-radius: 4px; cursor: pointer; margin-top: 12px; }
        button.primary { background: #27ae60; }
        button.primary:hover { background: #219a52; }
        a.btn { display: inline-block; padding: 8px 16px; color: white; text-decoration: none; border-radius: 4px; margin-top: 12px; font-size: 0.9em; }
        a.btn.blue { background: #3498db; }
        a.btn.blue:hover { opacity: 0.85; }

        table { width: 100%; border-collapse: collapse; margin-top: 12px; }
        th, td { padding: 10px 12px; text-align: left; border-bottom: 1px solid #e0e0e0; }
        th { background: #f8f9fa; color: #2c3e50; font-weight: bold; }
        tr:hover { background: #f5f7fa; }
        .badge { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 0.85em; font-weight: bold; }
        .badge.active { background: #d4edda; color: #155724; }
        .badge.inactive { background: #f8d7da; color: #721c24; }
        .badge.multiplier { background: #fff3cd; color: #856404; }
        .action-form { display: inline; }
        .action-form button { padding: 5px 12px; font-size: 0.85em; margin: 2px; }
        .btn-toggle { background: #e67e22; }
        .btn-toggle:hover { background: #d35400; }
        .btn-delete { background: #e74c3c; }
        .btn-delete:hover { background: #c0392b; }
        .empty-state { text-align: center; padding: 30px; color: #888; }
        .info-box { background: #eaf4fe; border-left: 4px solid #3498db; padding: 12px 16px; margin-bottom: 16px; border-radius: 0 4px 4px 0; }
        .info-box strong { color: #2c3e50; }
    </style>
</head>
<body>
    <div class="header">
        <h2>🗓️ Seasonal Pricing Management</h2>
        <span>Welcome, <%= session.getAttribute("username") %></span>
    </div>
    <div class="container">

        <%-- Messages --%>
        <%
            String error = (String) request.getAttribute("error");
            String message = (String) request.getAttribute("message");
            if (error != null && !error.trim().isEmpty()) {
        %>
            <div class="error"><%= error %></div>
        <% } %>
        <% if (message != null && !message.trim().isEmpty()) { %>
            <div class="success"><%= message %></div>
        <% } %>

        <%-- Info Box --%>
        <div class="info-box">
            <strong>How it works:</strong> When a guest makes a reservation, the system checks if the check-in date
            falls within any active season below. If it does, the <strong>seasonal multiplier</strong> is applied to the
            room's base price. Otherwise, the standard rate (1.0×) is used.
            <br><em>Example: A room at $100/night with a 1.5× Christmas multiplier = $150/night.</em>
        </div>

        <%-- Existing Seasons Table --%>
        <div class="card">
            <h3>Active & Configured Seasons</h3>
            <%
                List<SeasonalPricing> seasons = (List<SeasonalPricing>) request.getAttribute("seasons");
                if (seasons == null || seasons.isEmpty()) {
            %>
                <div class="empty-state">
                    <p>No seasons configured yet. All reservations use the standard rate (1.0×).</p>
                    <p>Add a season below to activate seasonal pricing.</p>
                </div>
            <% } else { %>
                <table>
                    <thead>
                        <tr>
                            <th>Season Name</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Multiplier</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (SeasonalPricing season : seasons) { %>
                        <tr>
                            <td><%= season.getSeasonName() %></td>
                            <td><%= season.getStartDate() %></td>
                            <td><%= season.getEndDate() %></td>
                            <td><span class="badge multiplier"><%= season.getMultiplier() %>×</span></td>
                            <td>
                                <% if (season.isActive()) { %>
                                    <span class="badge active">Active</span>
                                <% } else { %>
                                    <span class="badge inactive">Inactive</span>
                                <% } %>
                            </td>
                            <td>
                                <%-- Toggle active/inactive --%>
                                <form class="action-form" method="post" action="${pageContext.request.contextPath}/admin/seasonal-pricing/toggle">
                                    <input type="hidden" name="seasonId" value="<%= season.getId() %>">
                                    <% if (season.isActive()) { %>
                                        <input type="hidden" name="active" value="false">
                                        <button type="submit" class="btn-toggle">Deactivate</button>
                                    <% } else { %>
                                        <input type="hidden" name="active" value="true">
                                        <button type="submit" class="btn-toggle" style="background:#27ae60;">Activate</button>
                                    <% } %>
                                </form>
                                <%-- Delete --%>
                                <form class="action-form" method="post" action="${pageContext.request.contextPath}/admin/seasonal-pricing/delete"
                                      onsubmit="return confirm('Delete season \'<%= season.getSeasonName() %>\'?');">
                                    <input type="hidden" name="seasonId" value="<%= season.getId() %>">
                                    <button type="submit" class="btn-delete">Delete</button>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>

        <%-- Add New Season Form --%>
        <div class="card">
            <h3>Add New Season</h3>
            <form method="post" action="${pageContext.request.contextPath}/admin/seasonal-pricing/create">
                <div class="form-grid">
                    <div>
                        <label>Season Name:</label>
                        <input type="text" name="seasonName" placeholder="e.g. Christmas Peak" required>
                    </div>
                    <div>
                        <label>Multiplier:</label>
                        <input type="number" name="multiplier" step="0.01" min="0.01" max="5.00" placeholder="e.g. 1.50" required>
                    </div>
                    <div>
                        <label>Start Date:</label>
                        <input type="date" name="startDate" required>
                    </div>
                    <div>
                        <label>End Date:</label>
                        <input type="date" name="endDate" required>
                    </div>
                </div>
                <button type="submit" class="primary">Add Season</button>
            </form>
        </div>

        <a class="btn blue" href="${pageContext.request.contextPath}/admin/dashboard">← Back to Dashboard</a>
    </div>
</body>
</html>

