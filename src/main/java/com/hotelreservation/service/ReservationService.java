package com.hotelreservation.service;

import com.hotelreservation.dto.ReservationDTO;
import com.hotelreservation.entity.Guest;
import com.hotelreservation.entity.Reservation;
import com.hotelreservation.entity.Room;
import com.hotelreservation.strategy.IPricingStrategy;

/**
 * ReservationService abstract class - implements Factory Method pattern
 * Defines common reservation processing logic while allowing subclasses
 * to create different reservation types (Online, WalkIn)
 */
public abstract class ReservationService {
    protected ReservationRepository reservationRepository;
    protected IPricingStrategy pricingStrategy;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
        this.pricingStrategy = null;
    }

    /**
     * Factory Method - abstract method that subclasses must implement
     * Creates the appropriate type of reservation (OnlineReservation or WalkInReservation)
     * @param guest the guest making the reservation
     * @param room the room being reserved
     * @param totalAmount the total reservation amount
     * @return the created reservation
     */
    protected abstract Reservation createReservation(Guest guest, Room room, double totalAmount);

    /**
     * Template Method - orchestrates the reservation process
     * Calls the factory method to create appropriate reservation type
     * @param guest the guest
     * @param room the room
     * @return the processed and saved reservation
     */
    public Reservation processBooking(Guest guest, Room room) {
        // Calculate total using pricing strategy
        int nights = calculateNights(guest);
        double totalAmount = calculateTotal(nights, room.getBasePrice());

        // Create appropriate reservation type via factory method
        Reservation reservation = createReservation(guest, room, totalAmount);

        // Confirm the reservation
        reservation.confirm();

        // Save to repository
        reservationRepository.save(reservation);

        return reservation;
    }

    /**
     * Calculate number of nights (to be implemented by subclasses if needed)
     * @param guest the guest
     * @return number of nights
     */
    protected int calculateNights(Guest guest) {
        // Default implementation - override in subclasses if needed
        return 1;
    }

    /**
     * Calculate total price using the pricing strategy
     * @param nights number of nights
     * @param baseRate base rate per night
     * @return total price
     */
    protected double calculateTotal(int nights, double baseRate) {
        if (pricingStrategy != null) {
            return pricingStrategy.calculateTotal(nights, baseRate);
        }
        // Default: standard rate if no strategy set
        return nights * baseRate;
    }

    /**
     * Set the pricing strategy at runtime
     * @param strategy the pricing strategy to use
     */
    public void setPricingStrategy(IPricingStrategy strategy) {
        this.pricingStrategy = strategy;
    }

    /**
     * Get the current pricing strategy
     * @return the pricing strategy
     */
    public IPricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }

    /**
     * Get reservation type name
     * @return reservation type (ONLINE or WALK_IN)
     */
    public abstract String getReservationType();

    /**
     * Cancel a reservation
     * @param reservationId the reservation ID to cancel
     */
    public void cancelReservation(String reservationId) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            reservation.cancel();
            reservationRepository.update(reservation);
        });
    }

    /**
     * Get a reservation by ID
     * @param reservationId the reservation ID
     * @return ReservationDTO if found, null otherwise
     */
    public ReservationDTO getReservation(String reservationId) {
        return reservationRepository.findById(reservationId)
                .map(this::toDTO)
                .orElse(null);
    }

    /**
     * Convert Reservation entity to DTO
     * @param reservation the reservation entity
     * @return ReservationDTO
     */
    protected ReservationDTO toDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO(
            reservation.getId(),
            reservation.getGuestId(),
            reservation.getRoomId(),
            reservation.getCheckInDate(),
            reservation.getCheckOutDate(),
            reservation.getTotalAmount()
        );
        dto.setStatus(reservation.getStatus());
        dto.setReservationType(reservation.getReservationType());
        dto.setPaymentMethod(reservation.getPaymentMethod());
        return dto;
    }
}

/**
 * ReservationRepository interface - needed for this abstract class
 * Would normally be imported from service package
 */
interface ReservationRepository {
    java.util.Optional<Reservation> findById(String id);
    Reservation save(Reservation reservation);
    void update(Reservation reservation);
}

