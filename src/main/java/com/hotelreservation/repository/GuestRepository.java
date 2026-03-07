package com.hotelreservation.repository;

import com.hotelreservation.entity.Guest;

import java.util.Optional;

/**
 * GuestRepository - data access abstraction for Guest entity
 */
public interface GuestRepository {

    /**
     * Find guest by their primary key ID
     * @param id the guests.id value
     * @return Optional guest
     */
    Optional<Guest> findById(int id);

    /**
     * Find guest by linked user ID
     * @param userId the users.id value
     * @return Optional guest
     */
    Optional<Guest> findByUserId(int userId);

    /**
     * Find guest by NIC (unique identifier)
     * @param nic the NIC value
     * @return Optional guest
     */
    Optional<Guest> findByNic(String nic);

    /**
     * Save a new guest record
     * @param guest the guest to save
     * @return the saved guest with generated ID
     */
    Guest save(Guest guest);

    /**
     * Update the user_id on an existing guest record (link guest to a user account)
     * @param guestId the guest ID
     * @param userId the user ID to link
     * @return true if updated successfully
     */
    boolean updateUserId(int guestId, int userId);
}

