<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Room Search</title>
</head>
<body>
  <h2>Search Available Rooms</h2>
  <%
    String error = (String) request.getAttribute("error");
    if (error != null && !error.trim().isEmpty()) {
  %>
    <p style="color: red;"><%= error %></p>
  <% } %>
  <form method="post" action="/projectweb/reservation/search">
    <label>Check-in Date: <input type="date" name="checkIn" required></label><br>
    <label>Check-out Date: <input type="date" name="checkOut" required></label><br>
    <button type="submit">Search</button>
  </form>
  <p><a href="/projectweb/reservation/list">My Reservations</a></p>
  <p><a href="/projectweb/">Home</a> | <a href="${pageContext.request.contextPath}/logout">Logout</a></p>
</body>
</html>

