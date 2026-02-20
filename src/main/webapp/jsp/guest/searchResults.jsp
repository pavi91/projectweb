<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.hotelreservation.dto.RoomDTO" %>
<html>
<head>
  <title>Search Results</title>
</head>
<body>
  <h2>Available Rooms</h2>
  <%
    String error = (String) request.getAttribute("error");
    if (error != null && !error.trim().isEmpty()) {
  %>
    <p style="color: red;"><%= error %></p>
  <% }
    List<RoomDTO> rooms = (List<RoomDTO>) request.getAttribute("rooms");
    String checkIn = (String) request.getAttribute("checkIn");
    String checkOut = (String) request.getAttribute("checkOut");
    if (rooms == null || rooms.isEmpty()) {
  %>
    <p>No rooms available for the selected dates.</p>
  <% } else { %>
    <table border="1" cellpadding="4" cellspacing="0">
      <tr>
        <th>ID</th>
        <th>Number</th>
        <th>Type</th>
        <th>Price</th>
        <th>Status</th>
      </tr>
      <% for (RoomDTO room : rooms) { %>
      <tr>
        <td><%= room.getId() %></td>
        <td><%= room.getNumber() %></td>
        <td><%= room.getType() %></td>
        <td><%= room.getBasePrice() %></td>
        <td><%= room.getStatus() %></td>
      </tr>
      <% } %>
    </table>

    <h3>Reserve a Room</h3>
    <form method="post" action="/projectweb/reservation/create">
      <label>Room ID: <input type="number" name="roomId" required></label><br>
      <label>Check-in: <input type="date" name="checkIn" value="<%= checkIn != null ? checkIn : "" %>" required></label><br>
      <label>Check-out: <input type="date" name="checkOut" value="<%= checkOut != null ? checkOut : "" %>" required></label><br>
      <label>Name: <input type="text" name="name" required></label><br>
      <label>NIC: <input type="text" name="nic" required></label><br>
      <label>Phone: <input type="text" name="phone" required></label><br>
      <label>Email: <input type="email" name="email" required></label><br>
      <button type="submit">Create Reservation</button>
    </form>
  <% } %>
  <p><a href="/projectweb/reservation/search">New Search</a></p>
</body>
</html>

