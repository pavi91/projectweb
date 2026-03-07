<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Enumeration" %>
<html>
<head><title>Session Debug</title>
<style>
  body { font-family: monospace; padding: 20px; background: #1e1e1e; color: #d4d4d4; }
  table { border-collapse: collapse; width: 100%; }
  th { background: #0e639c; color: white; padding: 10px; text-align: left; }
  td { border: 1px solid #444; padding: 8px; }
  tr:nth-child(even) { background: #2d2d2d; }
  .section { margin-bottom: 30px; }
  h2 { color: #569cd6; }
  .null { color: #f44747; }
  .value { color: #4ec9b0; }
</style>
</head>
<body>
<h1>🔍 Session Debug Page</h1>

<%
    HttpSession sess = request.getSession(false);
    if (sess == null) {
%>
    <p class="null">❌ No active session found. <a href="/projectweb/login" style="color:#569cd6">Go to Login</a></p>
<%
    } else {
%>

<div class="section">
  <h2>📋 Session Info</h2>
  <table>
    <tr><th>Property</th><th>Value</th></tr>
    <tr><td>Session ID</td><td class="value"><%= sess.getId() %></td></tr>
    <tr><td>Creation Time</td><td class="value"><%= new java.util.Date(sess.getCreationTime()) %></td></tr>
    <tr><td>Last Accessed</td><td class="value"><%= new java.util.Date(sess.getLastAccessedTime()) %></td></tr>
    <tr><td>Max Inactive Interval</td><td class="value"><%= sess.getMaxInactiveInterval() %> seconds</td></tr>
    <tr><td>Is New?</td><td class="value"><%= sess.isNew() %></td></tr>
  </table>
</div>

<div class="section">
  <h2>🗂️ All Session Attributes</h2>
  <table>
    <tr><th>Key</th><th>Type</th><th>Value</th></tr>
    <%
        Enumeration<String> names = sess.getAttributeNames();
        if (!names.hasMoreElements()) {
    %>
    <tr><td colspan="3" class="null">No attributes in session</td></tr>
    <%
        } else {
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                Object value = sess.getAttribute(name);
    %>
    <tr>
      <td><strong><%= name %></strong></td>
      <td style="color:#ce9178"><%= value != null ? value.getClass().getSimpleName() : "null" %></td>
      <td class="value"><%= value != null ? value.toString() : "<span class='null'>null</span>" %></td>
    </tr>
    <%
            }
        }
    %>
  </table>
</div>

<div class="section">
  <h2>🎯 Key Attributes (Used by ReservationServlet)</h2>
  <table>
    <tr><th>Attribute</th><th>Value</th><th>Status</th></tr>
    <tr>
      <td>userId</td>
      <td class="value"><%= sess.getAttribute("userId") %></td>
      <td><%= sess.getAttribute("userId") != null ? "✅ Present" : "<span class='null'>❌ Missing</span>" %></td>
    </tr>
    <tr>
      <td>username</td>
      <td class="value"><%= sess.getAttribute("username") %></td>
      <td><%= sess.getAttribute("username") != null ? "✅ Present" : "<span class='null'>❌ Missing</span>" %></td>
    </tr>
    <tr>
      <td>role</td>
      <td class="value"><%= sess.getAttribute("role") %></td>
      <td><%= sess.getAttribute("role") != null ? "✅ Present" : "<span class='null'>❌ Missing</span>" %></td>
    </tr>
    <tr>
      <td>guestId</td>
      <td class="value"><%= sess.getAttribute("guestId") %></td>
      <td><%= sess.getAttribute("guestId") != null ? "✅ Present (GUEST user)" : "<span class='null'>❌ Missing - reservations will NOT load!</span>" %></td>
    </tr>
  </table>
</div>

<% } %>

<hr style="border-color:#444; margin-top:30px">
<p style="color:#888">⚠️ Remove this file before production deployment</p>
</body>
</html>

