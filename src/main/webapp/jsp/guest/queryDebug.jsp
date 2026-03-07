    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hotelreservation.util.QueryLogger" %>
<%@ page import="java.util.List" %>
<%
    // DEV ONLY - SQL Query Debug Page
    // ⚠️ REMOVE BEFORE PRODUCTION (see DEV_FEATURES_TO_REMOVE.md)

    QueryLogger ql = QueryLogger.getInstance();

    // Handle actions
    String action = request.getParameter("action");
    if ("clear".equals(action)) {
        ql.clear();
        response.sendRedirect(request.getContextPath() + "/debug/queries");
        return;
    }
    if ("toggle".equals(action)) {
        ql.setEnabled(!ql.isEnabled());
        response.sendRedirect(request.getContextPath() + "/debug/queries");
        return;
    }

    // Filter support
    String filterDao = request.getParameter("dao");
    String filterStatus = request.getParameter("status"); // "success", "error", or null
    String filterSql = request.getParameter("sql");

    List<QueryLogger.QueryEntry> allEntries = ql.getEntries();

    // Count stats
    long totalQueries = allEntries.size();
    long errorCount = allEntries.stream().filter(e -> !e.isSuccess()).count();
    long successCount = totalQueries - errorCount;
    double avgMs = allEntries.stream().mapToLong(e -> e.durationMs).average().orElse(0);
    long maxMs = allEntries.stream().mapToLong(e -> e.durationMs).max().orElse(0);
%>
<html>
<head>
<title>SQL Query Debug</title>
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body { font-family: 'JetBrains Mono', 'Consolas', monospace; padding: 20px; background: #1e1e1e; color: #d4d4d4; font-size: 13px; }
  h1 { color: #569cd6; margin-bottom: 5px; }
  .subtitle { color: #888; margin-bottom: 20px; }

  /* Stats bar */
  .stats { display: flex; gap: 15px; margin-bottom: 20px; flex-wrap: wrap; }
  .stat-card { background: #2d2d2d; border: 1px solid #444; border-radius: 8px; padding: 12px 20px; min-width: 140px; }
  .stat-card .label { color: #888; font-size: 11px; text-transform: uppercase; }
  .stat-card .value { font-size: 24px; font-weight: bold; margin-top: 4px; }
  .stat-card .value.success { color: #4ec9b0; }
  .stat-card .value.error { color: #f44747; }
  .stat-card .value.info { color: #569cd6; }
  .stat-card .value.warn { color: #dcdcaa; }

  /* Controls */
  .controls { display: flex; gap: 10px; margin-bottom: 20px; align-items: center; flex-wrap: wrap; }
  .btn { display: inline-block; padding: 8px 16px; border-radius: 4px; text-decoration: none; font-family: inherit; font-size: 12px; cursor: pointer; border: 1px solid #555; }
  .btn-blue { background: #0e639c; color: white; border-color: #0e639c; }
  .btn-red { background: #8b0000; color: white; border-color: #8b0000; }
  .btn-green { background: #2e7d32; color: white; border-color: #2e7d32; }
  .btn-gray { background: #3c3c3c; color: #d4d4d4; }
  .btn:hover { opacity: 0.85; }
  .btn.active { box-shadow: 0 0 0 2px #569cd6; }

  /* Filter bar */
  .filter-bar { display: flex; gap: 8px; align-items: center; }
  .filter-bar select, .filter-bar input {
    background: #2d2d2d; color: #d4d4d4; border: 1px solid #555; padding: 6px 10px;
    border-radius: 4px; font-family: inherit; font-size: 12px;
  }

  /* Status indicator */
  .status-badge { display: inline-block; padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: bold; }
  .status-on { background: #2e7d32; color: #fff; }
  .status-off { background: #8b0000; color: #fff; }

  /* Table */
  table { border-collapse: collapse; width: 100%; margin-top: 10px; }
  th { background: #0e639c; color: white; padding: 10px 8px; text-align: left; font-size: 11px; text-transform: uppercase;
       position: sticky; top: 0; z-index: 10; }
  td { border: 1px solid #333; padding: 8px; vertical-align: top; }
  tr:nth-child(even) { background: #252525; }
  tr:nth-child(odd) { background: #1e1e1e; }
  tr:hover { background: #2a2d2e; }
  tr.error-row { border-left: 3px solid #f44747; }
  tr.success-row { border-left: 3px solid #4ec9b0; }

  /* SQL styling */
  .sql-cell { font-family: 'JetBrains Mono', monospace; font-size: 12px; color: #ce9178; word-break: break-all; max-width: 500px; }
  .sql-cell .keyword { color: #569cd6; font-weight: bold; }
  .params-cell { color: #dcdcaa; font-size: 12px; }
  .error-msg { color: #f44747; font-size: 11px; font-style: italic; max-width: 300px; word-break: break-word; }
  .time-cell { text-align: right; }
  .time-fast { color: #4ec9b0; }
  .time-medium { color: #dcdcaa; }
  .time-slow { color: #f44747; font-weight: bold; }
  .count-cell { text-align: center; font-weight: bold; }
  .dao-cell { color: #c586c0; font-size: 11px; }
  .thread-cell { color: #888; font-size: 10px; }
  .timestamp-cell { color: #888; font-size: 11px; white-space: nowrap; }

  /* Sequence number */
  .seq { color: #555; font-size: 11px; }

  /* Empty state */
  .empty-state { text-align: center; padding: 60px 20px; color: #666; }
  .empty-state .icon { font-size: 48px; margin-bottom: 10px; }

  /* Scroll container */
  .table-wrap { max-height: 70vh; overflow-y: auto; border: 1px solid #333; border-radius: 4px; }

  /* Navigation */
  .nav-links { margin-bottom: 15px; }
  .nav-links a { color: #569cd6; text-decoration: none; margin-right: 15px; }
  .nav-links a:hover { text-decoration: underline; }
</style>
</head>
<body>

<div class="nav-links">
  <a href="<%= request.getContextPath() %>/debug/session">🔍 Session Debug</a>
  <a href="<%= request.getContextPath() %>/debug/queries">🗄️ Query Debug</a>
  <a href="<%= request.getContextPath() %>/">🏠 Home</a>
</div>

<h1>🗄️ SQL Query Debug</h1>
<p class="subtitle">Real-time capture of every database query from all DAOs</p>

<!-- Stats -->
<div class="stats">
  <div class="stat-card">
    <div class="label">Logging</div>
    <div class="value info">
      <span class="status-badge <%= ql.isEnabled() ? "status-on" : "status-off" %>"><%= ql.isEnabled() ? "ON" : "OFF" %></span>
    </div>
  </div>
  <div class="stat-card">
    <div class="label">Total Queries</div>
    <div class="value info"><%= totalQueries %></div>
  </div>
  <div class="stat-card">
    <div class="label">Successful</div>
    <div class="value success"><%= successCount %></div>
  </div>
  <div class="stat-card">
    <div class="label">Errors</div>
    <div class="value error"><%= errorCount %></div>
  </div>
  <div class="stat-card">
    <div class="label">Avg Time</div>
    <div class="value warn"><%= String.format("%.1f", avgMs) %> ms</div>
  </div>
  <div class="stat-card">
    <div class="label">Max Time</div>
    <div class="value <%= maxMs > 100 ? "error" : maxMs > 30 ? "warn" : "success" %>"><%= maxMs %> ms</div>
  </div>
</div>

<!-- Controls -->
<div class="controls">
  <a href="?action=toggle" class="btn <%= ql.isEnabled() ? "btn-red" : "btn-green" %>">
    <%= ql.isEnabled() ? "⏸ Pause Logging" : "▶ Resume Logging" %>
  </a>
  <a href="?action=clear" class="btn btn-red" onclick="return confirm('Clear all captured queries?')">🗑 Clear All</a>
  <a href="?" class="btn btn-blue">🔄 Refresh</a>

  <span style="color:#555; margin: 0 10px;">|</span>

  <!-- Filters -->
  <form class="filter-bar" method="get">
    <select name="dao">
      <option value="">All DAOs</option>
      <option value="UserDAOImpl" <%= "UserDAOImpl".equals(filterDao) ? "selected" : "" %>>UserDAOImpl</option>
      <option value="GuestRepositoryImpl" <%= "GuestRepositoryImpl".equals(filterDao) ? "selected" : "" %>>GuestRepositoryImpl</option>
      <option value="ReservationDAOImpl" <%= "ReservationDAOImpl".equals(filterDao) ? "selected" : "" %>>ReservationDAOImpl</option>
      <option value="RoomDAOImpl" <%= "RoomDAOImpl".equals(filterDao) ? "selected" : "" %>>RoomDAOImpl</option>
    </select>
    <select name="status">
      <option value="">All Status</option>
      <option value="success" <%= "success".equals(filterStatus) ? "selected" : "" %>>✅ Success</option>
      <option value="error" <%= "error".equals(filterStatus) ? "selected" : "" %>>❌ Errors</option>
    </select>
    <input type="text" name="sql" placeholder="Search SQL..." value="<%= filterSql != null ? filterSql : "" %>" style="width:200px"/>
    <button type="submit" class="btn btn-blue">🔎 Filter</button>
    <a href="?" class="btn btn-gray">✕ Reset</a>
  </form>
</div>

<!-- Query Table -->
<div class="table-wrap">
<table>
  <tr>
    <th>#</th>
    <th>Time</th>
    <th>DAO</th>
    <th>SQL Query</th>
    <th>Parameters</th>
    <th>Rows</th>
    <th>Duration</th>
    <th>Status</th>
    <th>Thread</th>
  </tr>
<%
    int seq = 0;
    boolean hasRows = false;
    for (QueryLogger.QueryEntry entry : allEntries) {
        // Apply filters
        if (filterDao != null && !filterDao.isEmpty() && !filterDao.equals(entry.callerClass)) continue;
        if ("success".equals(filterStatus) && !entry.isSuccess()) continue;
        if ("error".equals(filterStatus) && entry.isSuccess()) continue;
        if (filterSql != null && !filterSql.isEmpty() && !entry.sql.toLowerCase().contains(filterSql.toLowerCase())) continue;

        seq++;
        hasRows = true;

        // Highlight SQL keywords
        String displaySql = entry.sql
            .replace("SELECT", "<span class='keyword'>SELECT</span>")
            .replace("INSERT INTO", "<span class='keyword'>INSERT INTO</span>")
            .replace("UPDATE", "<span class='keyword'>UPDATE</span>")
            .replace("DELETE", "<span class='keyword'>DELETE</span>")
            .replace("FROM", "<span class='keyword'>FROM</span>")
            .replace("WHERE", "<span class='keyword'>WHERE</span>")
            .replace("AND", "<span class='keyword'>AND</span>")
            .replace("SET", "<span class='keyword'>SET</span>")
            .replace("VALUES", "<span class='keyword'>VALUES</span>")
            .replace("COUNT", "<span class='keyword'>COUNT</span>")
            .replace("COALESCE", "<span class='keyword'>COALESCE</span>")
            .replace("SUM", "<span class='keyword'>SUM</span>")
            .replace("NOT IN", "<span class='keyword'>NOT IN</span>")
            .replace("ORDER BY", "<span class='keyword'>ORDER BY</span>");

        String timeClass = entry.durationMs < 10 ? "time-fast" : entry.durationMs < 50 ? "time-medium" : "time-slow";
%>
  <tr class="<%= entry.isSuccess() ? "success-row" : "error-row" %>">
    <td class="seq"><%= seq %></td>
    <td class="timestamp-cell"><%= entry.timestamp %></td>
    <td class="dao-cell"><%= entry.callerClass %></td>
    <td class="sql-cell"><%= displaySql %></td>
    <td class="params-cell"><%= entry.params != null ? entry.params : "-" %></td>
    <td class="count-cell"><%= entry.isSuccess() ? entry.resultCount : "-" %></td>
    <td class="time-cell <%= timeClass %>"><%= entry.durationMs %> ms</td>
    <td>
      <% if (entry.isSuccess()) { %>
        <span style="color:#4ec9b0">✅ OK</span>
      <% } else { %>
        <span style="color:#f44747">❌ ERROR</span>
        <div class="error-msg"><%= entry.error %></div>
      <% } %>
    </td>
    <td class="thread-cell"><%= entry.threadName %></td>
  </tr>
<%
    }
    if (!hasRows) {
%>
  <tr>
    <td colspan="9">
      <div class="empty-state">
        <div class="icon">📭</div>
        <p>No queries captured yet.</p>
        <p style="margin-top:8px; font-size:12px; color:#555">
          Use the application (login, view reservations, etc.) and queries will appear here in real-time.
        </p>
      </div>
    </td>
  </tr>
<% } %>
</table>
</div>

<hr style="border-color:#333; margin-top:30px">
<p style="color:#666; font-size:11px; margin-top:10px">
  ⚠️ <strong>DEV ONLY</strong> — Remove this page and <code>QueryLogger.java</code> before production deployment.
  See <code>DEV_FEATURES_TO_REMOVE.md</code> for full list.
</p>

</body>
</html>

