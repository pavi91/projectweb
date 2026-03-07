package com.hotelreservation.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility to generate BCrypt password hashes for seed data.
 * Run this once to get the SQL INSERT statements.
 */
public class GenerateSeedData {
    public static void main(String[] args) {
        String[][] users = {
            {"admin", "admin123", "ADMIN"},
            {"receptionist", "recep123", "RECEPTIONIST"},
            {"guest", "guest123", "GUEST"}
        };

        System.out.println("-- Seed data for hotel_reservation database");
        System.out.println("-- Run this after schema.sql\n");

        for (String[] user : users) {
            String hash = BCrypt.hashpw(user[1], BCrypt.gensalt(12));
            System.out.printf("INSERT INTO users (username, password_hash, role) VALUES ('%s', '%s', '%s');%n",
                    user[0], hash, user[2]);
        }

        System.out.println("\n-- Sample rooms");
        System.out.println("INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('101', 'SINGLE', 100.00, 'AVAILABLE', TRUE);");
        System.out.println("INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('102', 'SINGLE', 100.00, 'AVAILABLE', TRUE);");
        System.out.println("INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('201', 'DOUBLE', 175.00, 'AVAILABLE', TRUE);");
        System.out.println("INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('202', 'DOUBLE', 175.00, 'AVAILABLE', TRUE);");
        System.out.println("INSERT INTO rooms (number, type, base_price, status, is_clean) VALUES ('301', 'SUITE', 300.00, 'AVAILABLE', TRUE);");
    }
}

