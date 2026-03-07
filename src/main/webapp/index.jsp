<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Ocean View Resort - Hotel Reservation System</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', Arial, sans-serif; background: #f0f4f8; min-height: 100vh; display: flex; flex-direction: column; }

        /* Hero */
        .hero { background: linear-gradient(135deg, #2c3e50 0%, #34495e 50%, #3498db 100%); color: white; padding: 70px 20px 60px; text-align: center; }
        .hero h1 { font-size: 40px; margin-bottom: 10px; letter-spacing: -0.5px; }
        .hero .tagline { font-size: 17px; opacity: 0.8; margin-bottom: 35px; }
        .hero-actions { display: flex; gap: 14px; justify-content: center; flex-wrap: wrap; }
        .hero-actions .btn-primary { padding: 14px 36px; background: white; color: #2c3e50; text-decoration: none; border-radius: 8px; font-size: 16px; font-weight: 700; transition: transform 0.2s, box-shadow 0.2s; }
        .hero-actions .btn-primary:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(0,0,0,0.25); }
        .hero-actions .btn-outline { padding: 14px 36px; background: transparent; color: white; text-decoration: none; border-radius: 8px; font-size: 16px; font-weight: 600; border: 2px solid rgba(255,255,255,0.5); transition: all 0.2s; }
        .hero-actions .btn-outline:hover { background: rgba(255,255,255,0.15); border-color: white; }

        /* Content */
        .container { max-width: 900px; margin: -25px auto 40px; padding: 0 20px; flex: 1; }

        /* Features */
        .features { display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; gap: 16px; margin-bottom: 30px; }
        .feature-card { background: white; border-radius: 12px; padding: 25px 20px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); text-align: center; transition: transform 0.2s; }
        .feature-card:hover { transform: translateY(-3px); }
        .feature-card .icon { font-size: 40px; margin-bottom: 12px; }
        .feature-card h3 { color: #2c3e50; font-size: 16px; margin-bottom: 8px; }
        .feature-card p { color: #888; font-size: 13px; line-height: 1.5; }

        /* About */
        .about-card { background: white; border-radius: 12px; padding: 30px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); margin-bottom: 20px; }
        .about-card h2 { color: #2c3e50; font-size: 20px; margin-bottom: 12px; }
        .about-card p { color: #666; font-size: 14px; line-height: 1.8; margin-bottom: 10px; }
        .about-details { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-top: 15px; }
        .detail-item { display: flex; align-items: center; gap: 10px; }
        .detail-item .icon { font-size: 22px; flex-shrink: 0; }
        .detail-item .text { font-size: 14px; color: #555; }
        .detail-item .text strong { color: #2c3e50; }

        /* Footer */
        .footer { text-align: center; padding: 25px; color: #aaa; font-size: 13px; background: #2c3e50; color: rgba(255,255,255,0.6); }
    </style>
</head>
<body>

    <!-- Hero Section -->
    <div class="hero">
        <h1>🏨 Ocean View Resort</h1>
        <p class="tagline">Experience luxury and comfort with breathtaking ocean views</p>
        <div class="hero-actions">
            <a class="btn-primary" href="${pageContext.request.contextPath}/login">Sign In</a>
            <a class="btn-outline" href="${pageContext.request.contextPath}/signup">Create Account</a>
            <a class="btn-outline" href="${pageContext.request.contextPath}/help">Help & FAQs</a>
        </div>
    </div>

    <div class="container">

        <!-- Features -->
        <div class="features">
            <div class="feature-card">
                <div class="icon">🛏</div>
                <h3>Luxury Rooms</h3>
                <p>Choose from Single, Double, and Suite rooms — all with ocean views and modern amenities.</p>
            </div>
            <div class="feature-card">
                <div class="icon">💳</div>
                <h3>Easy Booking</h3>
                <p>Book your room online in minutes with our secure payment system.</p>
            </div>
            <div class="feature-card">
                <div class="icon">📋</div>
                <h3>Manage Reservations</h3>
                <p>View, modify, or cancel your bookings anytime from your account.</p>
            </div>
            <a href="${pageContext.request.contextPath}/help" style="text-decoration:none;">
                <div class="feature-card">
                    <div class="icon">❓</div>
                    <h3>Help & FAQs</h3>
                    <p>Find answers to common questions about bookings, payments, and more.</p>
                </div>
            </a>
        </div>

        <!-- About the Hotel -->
        <div class="about-card">
            <h2>About Ocean View Resort</h2>
            <p>
                Nestled along the pristine coastline, Ocean View Resort offers an unforgettable stay
                with world-class hospitality. Whether you're here for business or leisure, our
                dedicated staff ensures every moment of your stay is perfect.
            </p>
            <div class="about-details">
                <div class="detail-item">
                    <span class="icon">📍</span>
                    <span class="text"><strong>Location</strong><br>Coastal Road, Colombo, Sri Lanka</span>
                </div>
                <div class="detail-item">
                    <span class="icon">📞</span>
                    <span class="text"><strong>Contact</strong><br>+94 11 234 5678</span>
                </div>
                <div class="detail-item">
                    <span class="icon">🕐</span>
                    <span class="text"><strong>Check-in / Check-out</strong><br>2:00 PM / 12:00 PM</span>
                </div>
                <div class="detail-item">
                    <span class="icon">🌐</span>
                    <span class="text"><strong>Email</strong><br>info@oceanviewresort.lk</span>
                </div>
            </div>
        </div>

    </div>

    <div class="footer">&copy; 2026 Ocean View Resort. All rights reserved.</div>
</body>
</html>
