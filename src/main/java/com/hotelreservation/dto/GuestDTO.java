package com.hotelreservation.dto;

/**
 * GuestDTO - Data Transfer Object for Guest
 */
public class GuestDTO {
    private int id;
    private String name;
    private String nic;
    private String phone;
    private String email;
    private String address;

    public GuestDTO() {
    }

    public GuestDTO(String name, String nic, String phone) {
        this.name = name;
        this.nic = nic;
        this.phone = phone;
    }

    public GuestDTO(int id, String name, String nic, String phone, String email, String address) {
        this.id = id;
        this.name = name;
        this.nic = nic;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "GuestDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nic='" + nic + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

