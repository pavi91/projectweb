package com.hotelreservation.service.impl;

import com.hotelreservation.entity.*;
import com.hotelreservation.repository.ReservationRepository;
import com.hotelreservation.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.UUID;

/**
 * WalkInResService - Concrete implementation of ReservationService for walk-in reservations
 * Implements Factory Method pattern to create WalkInReservation instances
 * Special behavior: prints physical receipts at POS terminal
 */
public class WalkInResService extends ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(WalkInResService.class);
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public WalkInResService(ReservationRepository reservationRepository) {
        super(reservationRepository);
    }

    /**
     * Factory Method implementation - creates WalkInReservation
     * @param guest the guest making the reservation
     * @param room the room being reserved
     * @param totalAmount the total reservation amount
     * @return WalkInReservation instance
     */
    @Override
    protected Reservation createReservation(Guest guest, Room room, double totalAmount) {
        String reservationId = "WLK_" + UUID.randomUUID().toString().substring(0, 8);

        WalkInReservation reservation = new WalkInReservation(
            reservationId,
            guest.getId(),
            room.getId(),
            checkInDate,
            checkOutDate,
            totalAmount
        );

        logger.info("Created walk-in reservation: {}", reservationId);
        return reservation;
    }

    /**
     * Set check-in and check-out dates for the reservation
     * @param checkIn check-in date
     * @param checkOut check-out date
     */
    public void setReservationDates(LocalDate checkIn, LocalDate checkOut) {
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
    }

    /**
     * Print reservation receipt at POS terminal
     * @param guest the guest
     * @param reservation the reservation
     * @param room the room
     * @return true if receipt printed successfully
     */
    public boolean printReservationReceipt(Guest guest, Reservation reservation, Room room) {
        try {
            if (!(reservation instanceof WalkInReservation)) {
                logger.warn("Not a walk-in reservation: {}", reservation.getId());
                return false;
            }

            WalkInReservation walkinRes = (WalkInReservation) reservation;

            // In production, this would send to actual POS printer
            logger.info("Printing reservation receipt at POS terminal");
            printReceiptData(guest, reservation, room);

            // Mark receipt as printed
            walkinRes.markReceiptPrinted();
            reservationRepository.update(reservation);

            return true;
        } catch (Exception e) {
            logger.error("Failed to print reservation receipt", e);
            return false;
        }
    }

    /**
     * Print checkout bill/invoice
     * @param guest the guest
     * @param reservation the reservation
     * @param room the room
     * @param additionalCharges any additional charges (room service, etc.)
     * @return true if bill printed successfully
     */
    public boolean printCheckoutBill(Guest guest, Reservation reservation, Room room, double additionalCharges) {
        try {
            logger.info("Printing checkout bill at POS terminal");

            double roomCharges = reservation.getTotalAmount();
            double totalBill = roomCharges + additionalCharges;

            printBillData(guest, reservation, room, roomCharges, additionalCharges, totalBill);

            return true;
        } catch (Exception e) {
            logger.error("Failed to print checkout bill", e);
            return false;
        }
    }

    /**
     * Print refund receipt (for cancellations)
     * @param guest the guest
     * @param reservation the cancelled reservation
     * @param refundAmount the refund amount
     * @return true if receipt printed successfully
     */
    public boolean printRefundReceipt(Guest guest, Reservation reservation, double refundAmount) {
        try {
            logger.info("Printing refund receipt at POS terminal");
            logger.info("Guest: {}, Refund: {}", guest.getName(), refundAmount);

            return true;
        } catch (Exception e) {
            logger.error("Failed to print refund receipt", e);
            return false;
        }
    }

    /**
     * Internal method to format and print receipt data
     */
    private void printReceiptData(Guest guest, Reservation reservation, Room room) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("================================\n");
        receipt.append("   OCEAN VIEW RESORT\n");
        receipt.append("   Reservation Receipt\n");
        receipt.append("================================\n");
        receipt.append("Reservation ID: ").append(reservation.getId()).append("\n");
        receipt.append("Guest: ").append(guest.getName()).append("\n");
        receipt.append("NIC: ").append(guest.getNic()).append("\n");
        receipt.append("Phone: ").append(guest.getPhone()).append("\n");
        receipt.append("Room: ").append(room.getNumber()).append(" (").append(room.getType()).append(")\n");
        receipt.append("Check-In: ").append(reservation.getCheckInDate()).append("\n");
        receipt.append("Check-Out: ").append(reservation.getCheckOutDate()).append("\n");
        receipt.append("Nights: ").append(reservation.getNumberOfNights()).append("\n");
        receipt.append("Rate per Night: ").append(room.getBasePrice()).append("\n");
        receipt.append("Total Amount: ").append(reservation.getTotalAmount()).append("\n");
        receipt.append("================================\n");
        receipt.append("Thank you for choosing us!\n");
        receipt.append("================================\n");

        logger.debug("Receipt:\n{}", receipt.toString());
    }

    /**
     * Internal method to format and print bill data
     */
    private void printBillData(Guest guest, Reservation reservation, Room room,
                               double roomCharges, double additionalCharges, double totalBill) {
        StringBuilder bill = new StringBuilder();
        bill.append("================================\n");
        bill.append("   OCEAN VIEW RESORT\n");
        bill.append("   Final Invoice\n");
        bill.append("================================\n");
        bill.append("Reservation ID: ").append(reservation.getId()).append("\n");
        bill.append("Guest: ").append(guest.getName()).append("\n");
        bill.append("Room: ").append(room.getNumber()).append("\n");
        bill.append("Check-In: ").append(reservation.getCheckInDate()).append("\n");
        bill.append("Check-Out: ").append(reservation.getCheckOutDate()).append("\n");
        bill.append("Nights: ").append(reservation.getNumberOfNights()).append("\n");
        bill.append("--------------------------------\n");
        bill.append("Room Charges: ").append(roomCharges).append("\n");
        if (additionalCharges > 0) {
            bill.append("Additional Charges: ").append(additionalCharges).append("\n");
        }
        bill.append("================================\n");
        bill.append("Total Bill: ").append(totalBill).append("\n");
        bill.append("Payment Method: POS\n");
        bill.append("================================\n");
        bill.append("We hope you enjoyed your stay!\n");
        bill.append("================================\n");

        logger.debug("Bill:\n{}", bill.toString());
    }

    @Override
    protected int calculateNights(Guest guest) {
        if (checkInDate != null && checkOutDate != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 1;
    }

    @Override
    public String getReservationType() {
        return "WALK_IN";
    }

    @Override
    public String toString() {
        return "WalkInResService{" +
                "type=WALK_IN" +
                ", pricingStrategy=" + (pricingStrategy != null ? pricingStrategy.getStrategyName() : "NONE") +
                '}';
    }
}

