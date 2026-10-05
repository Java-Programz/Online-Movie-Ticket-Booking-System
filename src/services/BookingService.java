package services;

import models.Booking;
import models.Movie;
import models.Show;
import storage.FileManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingService {

    private final MovieService movieService;
    private final SeatService seatService;
    private final FileManager fileManager;

    private final List<Booking> bookings;

    public BookingService(
            MovieService movieService,
            SeatService seatService,
            FileManager fileManager
    ) {

        this.movieService =
                movieService;

        this.seatService =
                seatService;

        this.fileManager =
                fileManager;

        this.bookings =
                new ArrayList<>(
                        fileManager.loadBookings()
                );

        restoreSavedBookingsToSeatMaps();
    }

    private void restoreSavedBookingsToSeatMaps() {

        for (Booking booking : bookings) {

            if (!booking.isConfirmed()) {
                continue;
            }

            Show show =
                    movieService.findShow(
                            booking.getMovieCode(),
                            booking.getShowDate(),
                            booking.getShowTime()
                    );

            if (show == null) {
                continue;
            }

            boolean restored =
                    seatService.reserveSeats(
                            show,
                            booking.getSelectedSeats()
                    );

            if (!restored) {

                System.out.println(
                        "Warning: could not restore every seat for booking "
                                + booking.getBookingId()
                );
            }
        }
    }

    public Booking createBooking(
            String customerName,
            Movie movie,
            Show show,
            List<String> selectedSeats
    ) {

        List<String> normalizedSeats =
                new ArrayList<>();

        for (String seat : selectedSeats) {

            normalizedSeats.add(
                    seatService
                            .normalizeSeatName(
                                    seat
                            )
            );
        }

        if (
                !seatService.reserveSeats(
                        show,
                        normalizedSeats
                )
        ) {

            return null;
        }

        Booking booking =
                new Booking(
                        generateBookingId(),
                        customerName.trim(),
                        movie,
                        show,
                        normalizedSeats
                );

        bookings.add(booking);

        if (
                !fileManager.saveBookings(
                        bookings
                )
        ) {

            bookings.remove(booking);

            seatService.releaseSeats(
                    show,
                    normalizedSeats
            );

            throw new IllegalStateException(
                    "The booking could not be saved."
            );
        }

        return booking;
    }

    public Booking findBookingById(
            String bookingId
    ) {

        if (bookingId == null) {
            return null;
        }

        for (Booking booking : bookings) {

            if (
                    booking
                            .getBookingId()
                            .equalsIgnoreCase(
                                    bookingId.trim()
                            )
            ) {

                return booking;
            }
        }

        return null;
    }

    public boolean cancelBooking(
            String bookingId
    ) {

        Booking booking =
                findBookingById(
                        bookingId
                );

        if (
                booking == null
                        || !booking.isConfirmed()
        ) {

            return false;
        }

        Show show =
                movieService.findShow(
                        booking.getMovieCode(),
                        booking.getShowDate(),
                        booking.getShowTime()
                );

        if (show == null) {
            return false;
        }

        seatService.releaseSeats(
                show,
                booking.getSelectedSeats()
        );

        booking.markCancelled();

        if (
                !fileManager.saveBookings(
                        bookings
                )
        ) {

            booking.markConfirmed();

            seatService.reserveSeats(
                    show,
                    booking.getSelectedSeats()
            );

            return false;
        }

        return true;
    }

    public List<Booking> getBookings() {

        return Collections.unmodifiableList(
                bookings
        );
    }

    private String generateBookingId() {

        int maxNumber = 0;

        for (Booking booking : bookings) {

            String id =
                    booking.getBookingId();

            if (
                    id != null
                            && id.matches(
                                    "BK\\d+"
                            )
            ) {

                try {

                    int number =
                            Integer.parseInt(
                                    id.substring(2)
                            );

                    maxNumber =
                            Math.max(
                                    maxNumber,
                                    number
                            );

                } catch (
                        NumberFormatException ignored
                ) {

                    // Ignore malformed IDs
                }
            }
        }

        return String.format(
                "BK%04d",
                maxNumber + 1
        );
    }
}