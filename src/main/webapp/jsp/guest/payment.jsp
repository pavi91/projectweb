<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.hotelreservation.dto.RoomDTO" %>
<%
    String roomId = request.getParameter("roomId") != null ? request.getParameter("roomId") : (String) request.getAttribute("roomId");
    String checkIn = request.getParameter("checkIn") != null ? request.getParameter("checkIn") : (String) request.getAttribute("checkIn");
    String checkOut = request.getParameter("checkOut") != null ? request.getParameter("checkOut") : (String) request.getAttribute("checkOut");
    String name = request.getParameter("name") != null ? request.getParameter("name") : (String) request.getAttribute("name");
    String nic = request.getParameter("nic") != null ? request.getParameter("nic") : (String) request.getAttribute("nic");
    String phone = request.getParameter("phone") != null ? request.getParameter("phone") : (String) request.getAttribute("phone");
    String email = request.getParameter("email") != null ? request.getParameter("email") : (String) request.getAttribute("email");

    RoomDTO room = (RoomDTO) request.getAttribute("room");
    String roomNumber = room != null ? room.getNumber() : roomId;
    String roomType = room != null ? room.getType() : "";
    double basePrice = room != null ? room.getBasePrice() : 0;
    double totalAmount = request.getAttribute("totalAmount") != null ? (Double) request.getAttribute("totalAmount") : 0;

    String error = (String) request.getAttribute("error");
%>
<html>
<head>
    <title>Payment - Ocean View Resort</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', Arial, sans-serif; background: #f0f4f8; }
        .header { background: #2c3e50; color: white; padding: 15px 20px; }
        .container { max-width: 700px; margin: 30px auto; padding: 0 20px; }

        /* Order summary */
        .summary-card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .summary-card h3 { color: #2c3e50; margin-bottom: 15px; border-bottom: 2px solid #3498db; padding-bottom: 8px; }
        .summary-row { display: flex; justify-content: space-between; padding: 6px 0; font-size: 14px; }
        .summary-row .label { color: #666; }
        .summary-row .value { font-weight: bold; color: #2c3e50; }
        .summary-total { display: flex; justify-content: space-between; padding: 12px 0; font-size: 18px; border-top: 2px solid #eee; margin-top: 10px; }
        .summary-total .value { color: #27ae60; font-weight: bold; font-size: 22px; }

        /* Payment card */
        .payment-card { background: white; border-radius: 8px; padding: 25px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .payment-card h3 { color: #2c3e50; margin-bottom: 15px; }
        .card-icons { display: flex; gap: 8px; margin-bottom: 15px; }
        .card-icon { background: #f0f0f0; padding: 5px 10px; border-radius: 4px; font-size: 12px; font-weight: bold; color: #555; }

        /* Form fields */
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: 600; color: #333; font-size: 13px; }
        .form-group input { width: 100%; padding: 12px; border: 2px solid #ddd; border-radius: 6px; font-size: 15px; font-family: 'Courier New', monospace; transition: border-color 0.2s; }
        .form-group input:focus { border-color: #3498db; outline: none; }
        .form-group input::placeholder { color: #bbb; }
        .row { display: flex; gap: 15px; }
        .row .form-group { flex: 1; }

        /* Card visual */
        .card-preview { background: linear-gradient(135deg, #2c3e50, #3498db); border-radius: 12px; padding: 20px; color: white; margin-bottom: 20px; min-height: 160px; position: relative; }
        .card-preview .chip { width: 40px; height: 30px; background: linear-gradient(135deg, #f1c40f, #e67e22); border-radius: 5px; margin-bottom: 15px; }
        .card-preview .card-number { font-size: 20px; letter-spacing: 3px; font-family: 'Courier New', monospace; margin-bottom: 15px; }
        .card-preview .card-info { display: flex; justify-content: space-between; font-size: 12px; }
        .card-preview .card-label { opacity: 0.7; font-size: 10px; text-transform: uppercase; }

        /* Buttons */
        .pay-btn { width: 100%; padding: 14px; background: #27ae60; color: white; border: none; border-radius: 6px; font-size: 16px; font-weight: bold; cursor: pointer; transition: background 0.2s; }
        .pay-btn:hover { background: #219a52; }
        .pay-btn:active { transform: scale(0.99); }
        .back-link { display: inline-block; margin-top: 12px; color: #3498db; text-decoration: none; font-size: 14px; }
        .back-link:hover { text-decoration: underline; }

        .error { color: #e74c3c; background: #fdecea; padding: 10px 15px; border-radius: 4px; margin-bottom: 15px; }
        .secure-note { text-align: center; color: #888; font-size: 12px; margin-top: 10px; }
        .demo-badge { background: #fff3cd; color: #856404; border: 1px solid #ffc107; padding: 8px 15px; border-radius: 4px; font-size: 12px; text-align: center; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="header"><h2>💳 Payment</h2></div>
    <div class="container">

        <% if (error != null && !error.trim().isEmpty()) { %>
            <div class="error">❌ <%= error %></div>
        <% } %>

        <div class="demo-badge">
            🔒 This is a <strong>demo payment gateway</strong>. No real charges will be made. Enter any card details.
        </div>

        <!-- Order Summary -->
        <div class="summary-card">
            <h3>📋 Booking Summary</h3>
            <div class="summary-row">
                <span class="label">Guest</span>
                <span class="value"><%= name != null ? name : "-" %></span>
            </div>
            <div class="summary-row">
                <span class="label">Room</span>
                <span class="value"><%= roomNumber %> <% if (!roomType.isEmpty()) { %>(<%= roomType %>)<% } %></span>
            </div>
            <div class="summary-row">
                <span class="label">Check-in</span>
                <span class="value"><%= checkIn %></span>
            </div>
            <div class="summary-row">
                <span class="label">Check-out</span>
                <span class="value"><%= checkOut %></span>
            </div>
            <% if (basePrice > 0) { %>
            <div class="summary-row">
                <span class="label">Rate per Night</span>
                <span class="value">$<%= String.format("%.2f", basePrice) %></span>
            </div>
            <% } %>
            <div class="summary-total">
                <span class="label">Total Amount</span>
                <span class="value">$<%= String.format("%.2f", totalAmount) %></span>
            </div>
        </div>

        <!-- Card Preview -->
        <div class="card-preview">
            <div class="chip"></div>
            <div class="card-number" id="previewNumber">•••• •••• •••• ••••</div>
            <div class="card-info">
                <div>
                    <div class="card-label">Card Holder</div>
                    <div id="previewName">YOUR NAME</div>
                </div>
                <div>
                    <div class="card-label">Expires</div>
                    <div id="previewExpiry">MM/YY</div>
                </div>
            </div>
        </div>

        <!-- Payment Form -->
        <div class="payment-card">
            <h3>Enter Card Details</h3>
            <div class="card-icons">
                <span class="card-icon">VISA</span>
                <span class="card-icon">MasterCard</span>
                <span class="card-icon">AMEX</span>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/reservation/create" id="paymentForm">
                <!-- Hidden fields to carry reservation data -->
                <input type="hidden" name="roomId" value="<%= roomId %>">
                <input type="hidden" name="checkIn" value="<%= checkIn %>">
                <input type="hidden" name="checkOut" value="<%= checkOut %>">
                <input type="hidden" name="name" value="<%= name %>">
                <input type="hidden" name="nic" value="<%= nic %>">
                <input type="hidden" name="phone" value="<%= phone %>">
                <input type="hidden" name="email" value="<%= email != null ? email : "" %>">
                <input type="hidden" name="paymentConfirmed" value="true">

                <div class="form-group">
                    <label>Card Number</label>
                    <input type="text" id="cardNumber" placeholder="1234 5678 9012 3456" maxlength="19" required
                           oninput="formatCardNumber(this); updatePreview();">
                </div>

                <div class="form-group">
                    <label>Cardholder Name</label>
                    <input type="text" id="cardName" placeholder="JOHN DOE" required
                           oninput="updatePreview();" style="text-transform: uppercase;">
                </div>

                <div class="row">
                    <div class="form-group">
                        <label>Expiry Date</label>
                        <input type="text" id="cardExpiry" placeholder="MM/YY" maxlength="5" required
                               oninput="formatExpiry(this); updatePreview();">
                    </div>
                    <div class="form-group">
                        <label>CVV</label>
                        <input type="password" id="cardCvv" placeholder="•••" maxlength="4" required>
                    </div>
                </div>

                <button type="submit" class="pay-btn">
                    🔒 Pay $<%= String.format("%.2f", totalAmount) %>
                </button>
            </form>

            <p class="secure-note">🔒 Your payment is secure. This is a demo — no real charges applied.</p>
        </div>

        <a class="back-link" href="${pageContext.request.contextPath}/reservation/search">← Back to Room Search</a>
    </div>

    <script>
        function formatCardNumber(input) {
            var v = input.value.replace(/\D/g, '').substring(0, 16);
            var parts = [];
            for (var i = 0; i < v.length; i += 4) {
                parts.push(v.substring(i, i + 4));
            }
            input.value = parts.join(' ');
        }

        function formatExpiry(input) {
            var v = input.value.replace(/\D/g, '').substring(0, 4);
            if (v.length >= 2) {
                input.value = v.substring(0, 2) + '/' + v.substring(2);
            } else {
                input.value = v;
            }
        }

        function updatePreview() {
            var num = document.getElementById('cardNumber').value || '•••• •••• •••• ••••';
            var name = document.getElementById('cardName').value || 'YOUR NAME';
            var exp = document.getElementById('cardExpiry').value || 'MM/YY';

            document.getElementById('previewNumber').textContent = num || '•••• •••• •••• ••••';
            document.getElementById('previewName').textContent = name.toUpperCase();
            document.getElementById('previewExpiry').textContent = exp;
        }
    </script>
</body>
</html>

