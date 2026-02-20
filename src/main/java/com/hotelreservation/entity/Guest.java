package com.hotelreservation.entity;

/**
 * Guest entity - represents a guest of the hotel
 */
public class Guest {
    private int id;
    private int userId;
    private String name;
    private String nic;
    private String phone;
    private String email;
    private String address;
    private long createdAt;

    public Guest() {
    }

    public Guest(String name, String nic, String phone) {
        this.name = name;
        this.nic = nic;
        this.phone = phone;
        this.createdAt = System.currentTimeMillis();
    }

    public Guest(int id, String name, String nic, String phone, String email, String address) {
        this.id = id;
        this.name = name;
        this.nic = nic;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    /**
     * Update contact information
     * @param phone new phone number
     * @param email new email address
     */
    public void updateContact(String phone, String email) {
        this.phone = phone;
        this.email = email;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nic='" + nic + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

