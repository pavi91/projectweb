<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Create Reservation</title>
</head>
<body>
  <h2>Create Reservation</h2>
  <%
    String error = (String) request.getAttribute("error");
    if (error != null && !error.trim().isEmpty()) {
  %>
    <p style="color: red;"><%= error %></p>
  <% } %>
  <form method="post" action="/projectweb/reservation/create">
    <label>Room ID: <input type="number" name="roomId" required></label><br>
    <label>Check-in: <input type="date" name="checkIn" required></label><br>
    <label>Check-out: <input type="date" name="checkOut" required></label><br>
    <label>Name: <input type="text" name="name" required></label><br>
    <label>NIC: <input type="text" name="nic" required></label><br>
    <label>Phone: <input type="text" name="phone" required></label><br>
    <label>Email: <input type="email" name="email" required></label><br>
    <button type="submit">Create Reservation</button>
  </form>
  <p><a href="/projectweb/reservation/search">Back to Search</a></p>
</body>
</html>

