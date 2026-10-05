package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELLED = "CANCELLED";

    private final String bookingId;
    private final String customerName;

    private final String movieCode;
    private final String movieName;

    private final String showDate;
    private final String showTime;

    private final List<String> selectedSeats;

    private final double ticketPrice;
    private final double totalPrice;

    private String status;

    public Booking(
            String bookingId,
            String customerName,
            Movie movie,
            Show show,
            List<String> selectedSeats
    ) {

        this.bookingId = bookingId;
        this.customerName = customerName;

        this.movieCode = movie.getCode();
        this.movieName = movie.getName();

        this.showDate = show.getDate();
        this.showTime = show.getTime();

        this.selectedSeats =
                new ArrayList<>(selectedSeats);

        this.ticketPrice =
                show.getTicketPrice();

        this.totalPrice =
                ticketPrice * selectedSeats.size();

        this.status = CONFIRMED;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getMovieCode() {
        return movieCode;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getShowDate() {
        return showDate;
    }

    public String getShowTime() {
        return showTime;
    }

    public List<String> getSelectedSeats() {
        return Collections.unmodifiableList(
                selectedSeats
        );
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public boolean isConfirmed() {
        return CONFIRMED.equals(status);
    }

    public void markCancelled() {
        status = CANCELLED;
    }

    public void markConfirmed() {
        status = CONFIRMED;
    }
}