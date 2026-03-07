<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Help & FAQs - Ocean View Resort</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', Arial, sans-serif; background: #f0f4f8; min-height: 100vh; display: flex; flex-direction: column; }

        /* Header */
        .header { background: linear-gradient(135deg, #2c3e50 0%, #34495e 50%, #3498db 100%); color: white; padding: 40px 20px 35px; text-align: center; }
        .header h1 { font-size: 32px; margin-bottom: 6px; }
        .header p { font-size: 15px; opacity: 0.8; }
        .header a { color: rgba(255,255,255,0.85); text-decoration: none; font-size: 14px; }
        .header a:hover { color: white; text-decoration: underline; }
        .back-link { margin-bottom: 14px; display: inline-block; }

        /* Container */
        .container { max-width: 860px; margin: 30px auto 40px; padding: 0 20px; flex: 1; }

        /* Section title */
        .section-title { font-size: 22px; color: #2c3e50; margin-bottom: 18px; padding-bottom: 8px; border-bottom: 2px solid #3498db; }

        /* FAQ */
        .faq-list { margin-bottom: 35px; }
        .faq-item { background: white; border-radius: 10px; margin-bottom: 12px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); overflow: hidden; }
        .faq-question { padding: 18px 22px; cursor: pointer; font-weight: 600; font-size: 15px; color: #2c3e50; display: flex; justify-content: space-between; align-items: center; transition: background 0.2s; }
        .faq-question:hover { background: #f8f9fa; }
        .faq-question .arrow { font-size: 13px; color: #3498db; transition: transform 0.3s; }
        .faq-item.open .faq-question .arrow { transform: rotate(180deg); }
        .faq-answer { padding: 0 22px 0; max-height: 0; overflow: hidden; transition: max-height 0.3s ease, padding 0.3s ease; }
        .faq-item.open .faq-answer { max-height: 300px; padding: 0 22px 18px; }
        .faq-answer p { color: #666; font-size: 14px; line-height: 1.7; }

        /* Contact card */
        .contact-card { background: white; border-radius: 12px; padding: 28px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); margin-bottom: 25px; }
        .contact-card h2 { color: #2c3e50; font-size: 20px; margin-bottom: 15px; }
        .contact-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
        .contact-item { display: flex; align-items: center; gap: 10px; }
        .contact-item .icon { font-size: 22px; }
        .contact-item .text { font-size: 14px; color: #555; }
        .contact-item .text strong { color: #2c3e50; display: block; margin-bottom: 2px; }

        /* Quick links */
        .quick-links { display: flex; gap: 12px; flex-wrap: wrap; margin-top: 20px; }
        .quick-links a { padding: 10px 22px; background: #3498db; color: white; text-decoration: none; border-radius: 8px; font-size: 14px; font-weight: 600; transition: background 0.2s; }
        .quick-links a:hover { background: #2980b9; }

        /* Footer */
        .footer { text-align: center; padding: 25px; font-size: 13px; background: #2c3e50; color: rgba(255,255,255,0.6); }
    </style>
</head>
<body>

    <!-- Header -->
    <div class="header">
        <a class="back-link" href="${pageContext.request.contextPath}/">← Back to Home</a>
        <h1>❓ Help & FAQs</h1>
        <p>Find answers to common questions about Ocean View Resort</p>
    </div>

    <div class="container">

        <!-- Booking FAQs -->
        <h2 class="section-title">🛏 Booking & Reservations</h2>
        <div class="faq-list">
            <div class="faq-item">
                <div class="faq-question" onclick="toggleFaq(this)">
                    How do I make an online reservation?
                    <span class="arrow">▼</span>
                </div>
                <div class="faq-answer">
                    <p>Create a guest account by clicking "Create Account" on the home page. Once signed in, you'll be directed to the room search page where you can select your check-in and check-out dates. Browse available rooms, choose the one you prefer, and confirm your booking. You'll receive a reservation ID upon successful payment.</p>
                </div>
            </div>
            <div class="faq-item">
                <div class="faq-question" onclick="toggleFaq(this)">
                    What room types are available?
                    <span class="arrow">▼</span>
                </div>
                <div class="faq-answer">
                    <p>We offer three room types: <strong>Single Rooms</strong> ($100/night) — ideal for solo travellers with a comfortable single bed; <strong>Double Rooms</strong> ($175/night) — spacious rooms with a double bed for couples; and <strong>Suite Rooms</strong> ($300/night) — premium suites with separate living area, king bed, and panoramic ocean views.</p>
                </div>
            </div>
            <div class="faq-item">
                <div class="faq-question" onclick="toggleFaq(this)">
                    Can I cancel my reservation?
                    <span class="arrow">▼</span>
                </div>
                <div class="faq-answer">
                    <p>Yes. Sign in to your guest account and navigate to "My Reservations". You'll see all your bookings with their current status. Click the "Cancel" button next to any confirmed reservation. Once cancelled, the room is released and the reservation status changes to "CANCELLED". Refunds are processed offline by our accounts team.</p>
                </div>
            </div>
            <div class="faq-item">
                <div class="faq-question" onclick="toggleFaq(this)">
                    How do I view my existing reservations?
                    <span class="arrow">▼</span>
                </div>
                <div class="faq-answer">
                    <p>After signing in as a guest, click "My Reservations" in the navigation menu. You'll see a table listing all your reservations including the reservation ID, room details, dates, total amount, and current status (Confirmed, Checked-In, Checked-Out, or Cancelled).</p>
                </div>
            </div>
        </div>

        <!-- Check-in / Check-out FAQs -->
        <h2 class="section-title">🔑 Check-In & Check-Out</h2>
        <div class="faq-list">
            <div class="faq-item">
                <div class="faq-question" onclick="toggleFaq(this)">
                    What are the check-in and check-out times?
                    <span class="arrow">▼</span>
                </div>
                <div class="faq-answer">
                    <p>Standard check-in time is <strong>2:00 PM</strong> and check-out time is <strong>12:00 PM (noon)</strong>. Early check-in or late check-out may be available upon request at the front desk, subject to room availability.</p>
                </div>
            </div>
            <div class="faq-item">
                <div class="faq-question" onclick="toggleFaq(this)">
                    Can I make a walk-in reservation without an online account?
                    <span class="arrow">▼</span>
                </div>
                <div class="faq-answer">
                    <p>Yes! Simply visit our front desk and our receptionist will assist you. Walk-in reservations are processed on the spot — you'll provide your name, NIC, and phone number, select an available room, and payment is processed via our POS terminal. A printed receipt is provided immediately.</p>
                </div>
            </div>
            <div class="faq-item">
                <div class="faq-question" onclick="toggleFaq(this)">
                    How does check-out and billing work?
                    <span class="arrow">▼</span>
                </div>
                <div class="faq-answer">
                    <p>At check-out, the receptionist will process your departure and generate a detailed invoice showing your room number, dates of stay, number of nights, nightly rate, and total charges. The bill is displayed on screen and can be printed at the front desk.</p>
                </div>
            </div>
        </div>

        <!-- Payment FAQs -->
        <h2 class="section-title">💳 Payment</h2>
        <div class="faq-list">
            <div class="faq-item">
                <div class="faq-question" onclick="toggleFaq(this)">
                    What payment methods are accepted?
                    <span class="arrow">▼</span>
                </div>
                <div class="faq-answer">
                    <p>We accept two payment methods: <strong>Online Gateway</strong> (for online reservations) — payments are processed through our secure bank payment portal; and <strong>POS Terminal</strong> (for walk-in reservations) — card payments processed at the front desk terminal. The payment method is automatically selected based on your reservation type.</p>
                </div>
            </div>
            <div class="faq-item">
                <div class="faq-question" onclick="toggleFaq(this)">
                    Are there seasonal price changes?
                    <span class="arrow">▼</span>
                </div>
                <div class="faq-answer">
                    <p>Yes. During peak seasons (holidays, summer, festivals), room rates may include a seasonal multiplier set by the hotel administration. For example, a 1.5x peak-season multiplier means a $100/night room would cost $150/night. Seasonal pricing is automatically applied at the time of booking — the total shown on the payment page reflects any active seasonal rates.</p>
                </div>
            </div>
        </div>

        <!-- Account FAQs -->
        <h2 class="section-title">👤 Account & Security</h2>
        <div class="faq-list">
            <div class="faq-item">
                <div class="faq-question" onclick="toggleFaq(this)">
                    How do I create a guest account?
                    <span class="arrow">▼</span>
                </div>
                <div class="faq-answer">
                    <p>Click "Create Account" on the home page. Fill in your username, password (minimum 6 characters), full name, NIC number, and phone number. Once registered, you can sign in and start booking rooms immediately. If you previously stayed as a walk-in guest, your existing guest profile will be automatically linked to your new account.</p>
                </div>
            </div>
            <div class="faq-item">
                <div class="faq-question" onclick="toggleFaq(this)">
                    I forgot my password. What should I do?
                    <span class="arrow">▼</span>
                </div>
                <div class="faq-answer">
                    <p>Please contact our front desk at <strong>+94 11 234 5678</strong> or email <strong>info@oceanviewresort.lk</strong> with your registered username and NIC number. Our team will assist you with resetting your account credentials.</p>
                </div>
            </div>
            <div class="faq-item">
                <div class="faq-question" onclick="toggleFaq(this)">
                    Is my personal information secure?
                    <span class="arrow">▼</span>
                </div>
                <div class="faq-answer">
                    <p>Absolutely. All passwords are encrypted using <strong>BCrypt hashing</strong> — we never store plain-text passwords. User sessions are protected with automatic timeout (30 minutes of inactivity). Role-based access control ensures that guests, receptionists, and administrators can only access the areas permitted to their role.</p>
                </div>
            </div>
        </div>

        <!-- Contact Section -->
        <div class="contact-card">
            <h2>📞 Still Need Help?</h2>
            <div class="contact-grid">
                <div class="contact-item">
                    <span class="icon">📞</span>
                    <span class="text"><strong>Phone</strong>+94 11 234 5678</span>
                </div>
                <div class="contact-item">
                    <span class="icon">📧</span>
                    <span class="text"><strong>Email</strong>info@oceanviewresort.lk</span>
                </div>
                <div class="contact-item">
                    <span class="icon">📍</span>
                    <span class="text"><strong>Visit Us</strong>Coastal Road, Colombo, Sri Lanka</span>
                </div>
                <div class="contact-item">
                    <span class="icon">🕐</span>
                    <span class="text"><strong>Front Desk</strong>24/7 — Always available</span>
                </div>
            </div>
        </div>

        <!-- Quick Links -->
        <div style="text-align:center; margin-bottom: 20px;">
            <div class="quick-links" style="justify-content: center;">
                <a href="${pageContext.request.contextPath}/">🏠 Home</a>
                <a href="${pageContext.request.contextPath}/login">🔑 Sign In</a>
                <a href="${pageContext.request.contextPath}/signup">📝 Create Account</a>
            </div>
        </div>

    </div>

    <div class="footer">&copy; 2026 Ocean View Resort. All rights reserved.</div>

    <script>
        function toggleFaq(el) {
            var item = el.parentElement;
            item.classList.toggle('open');
        }
    </script>
</body>
</html>

