package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Show {

    public static final int SEATS_PER_ROW = 10;

    private final String date;
    private final String time;
    private final int totalSeats;
    private final double ticketPrice;

    private final List<Seat> seats;

    public Show(
            String date,
            String time,
            int totalSeats,
            int availableSeats,
            double ticketPrice
    ) {

        this.date = date;
        this.time = time;
        this.totalSeats = totalSeats;
        this.ticketPrice = ticketPrice;

        this.seats = new ArrayList<>();

        generateSeats();
        applyInitialAvailability(availableSeats);
    }

    private void generateSeats() {

        for (int i = 0; i < totalSeats; i++) {

            int rowIndex = i / SEATS_PER_ROW;
            int seatNumber = (i % SEATS_PER_ROW) + 1;

            seats.add(
                    new Seat(
                            toRowLabel(rowIndex),
                            seatNumber
                    )
            );
        }
    }

    private void applyInitialAvailability(int availableSeats) {

        int safeAvailable =
                Math.max(
                        0,
                        Math.min(availableSeats, totalSeats)
                );

        int alreadyBooked =
                totalSeats - safeAvailable;

        /*
         * The CSV contains only:
         *
         * Total Seats
         * Available Seats
         *
         * It does NOT tell us which exact seats
         * have already been booked.
         *
         * Therefore, we mark the last N seats
         * as already booked.
         */

        for (
                int i = totalSeats - 1;
                i >= totalSeats - alreadyBooked;
                i--
        ) {

            if (i >= 0) {
                seats.get(i).book();
            }
        }
    }

    private String toRowLabel(int index) {

        StringBuilder label =
                new StringBuilder();

        int value = index + 1;

        while (value > 0) {

            value--;

            label.insert(
                    0,
                    (char) ('A' + (value % 26))
            );

            value /= 26;
        }

        return label.toString();
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public List<Seat> getSeats() {
        return Collections.unmodifiableList(seats);
    }

    public int getAvailableSeatCount() {

        int count = 0;

        for (Seat seat : seats) {

            if (!seat.isBooked()) {
                count++;
            }
        }

        return count;
    }
}