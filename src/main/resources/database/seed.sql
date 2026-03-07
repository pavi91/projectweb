-- ================================================================
-- Ocean View Resort - Hotel Reservation System
-- Seed Data - Run this AFTER schema.sql
-- ================================================================

-- ========================
-- Default Users
-- ========================
-- Password for all users shown below (BCrypt hashed)

-- admin / admin123
INSERT INTO users (username, password_hash, role) VALUES ('admin', '$2a$12$kJ92U4YsUWwocBBtd8UeF.D3L/tKvremb0w2knqdDwabjAQ7uJOly', 'ADMIN');

-- receptionist / recep123
INSERT INTO users (username, password_hash, role) VALUES ('receptionist', '$2a$12$V0R6ImYTyf./2pdwpB4wxeBEOhsRswi8EzPkDG8GLB6NjPv7kdGyu', 'RECEPTIONIST');

-- guest / guest123
INSERT INTO users (username, password_hash, role) VALUES ('guest', '$2a$12$qrOTn9o4waMhqaZGJNrIZ.34cu38IyXjprtSyOddCiJ3Hq5GpxoOa', 'GUEST');


-- ========================
-- Sample Rooms
-- ========================
INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('101', 'SINGLE', 100.00, 'AVAILABLE', TRUE);
INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('102', 'SINGLE', 100.00, 'AVAILABLE', TRUE);
INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('103', 'SINGLE', 100.00, 'AVAILABLE', TRUE);
INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('201', 'DOUBLE', 175.00, 'AVAILABLE', TRUE);
INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('202', 'DOUBLE', 175.00, 'AVAILABLE', TRUE);
INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('203', 'DOUBLE', 175.00, 'AVAILABLE', TRUE);
INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('301', 'SUITE', 300.00, 'AVAILABLE', TRUE);
INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('302', 'SUITE', 300.00, 'AVAILABLE', TRUE);

-- ========================
-- Sample Guest Record (linked to 'guest' user)
-- ========================
INSERT INTO guests (user_id, name, nic, phone, email, address)
VALUES (
    (SELECT id FROM users WHERE username = 'guest'),
    'John Doe',
    '901234567V',
    '+94771234567',
    'john.doe@example.com',
    '123 Main Street, Colombo'
);

-- ========================
-- Sample Seasonal Pricing
-- ========================
INSERT INTO seasonal_pricing (season_name, start_date, end_date, multiplier, is_active)
VALUES ('Christmas & New Year', '2026-12-15', '2027-01-05', 1.50, TRUE);

INSERT INTO seasonal_pricing (season_name, start_date, end_date, multiplier, is_active)
VALUES ('Summer Peak', '2026-07-01', '2026-08-31', 1.30, TRUE);

INSERT INTO seasonal_pricing (season_name, start_date, end_date, multiplier, is_active)
VALUES ('Off-Season Discount', '2026-02-01', '2026-03-31', 0.85, TRUE);

