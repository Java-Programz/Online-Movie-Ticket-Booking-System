package ui;

import models.Booking;
import models.Movie;
import models.Show;

import services.BookingService;
import services.MovieService;
import services.SeatService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ConsoleUI {

    private final MovieService movieService;
    private final BookingService bookingService;
    private final SeatService seatService;

    private final SeatMapUI seatMapUI;
    private final TicketUI ticketUI;

    private final Scanner scanner;

    public ConsoleUI(
            MovieService movieService,
            BookingService bookingService,
            SeatService seatService
    ) {

        this.movieService =
                movieService;

        this.bookingService =
                bookingService;

        this.seatService =
                seatService;

        this.seatMapUI =
                new SeatMapUI();

        this.ticketUI =
                new TicketUI();

        this.scanner =
                new Scanner(System.in);
    }

    public void start() {

        printHeader();

        while (true) {

            printMainMenu();

            int option =
                    readInt(
                            "Enter option > ",
                            1,
                            6
                    );

            switch (option) {

                case 1 ->
                        createBooking();

                case 2 ->
                        searchBooking();

                case 3 ->
                        cancelBooking();

                case 4 ->
                        viewMovieSchedule();

                case 5 ->
                        viewSeatAvailability();

                case 6 -> {

                    System.out.println(
                            "Thank you for using the Movie Reservation System."
                    );

                    return;
                }

                default ->
                        System.out.println(
                                "Invalid option."
                        );
            }
        }
    }

    private void printHeader() {

        System.out.println(
                "============================================================"
        );

        System.out.println(
                "             MOVIE TICKET RESERVATION SYSTEM"
        );

        System.out.println(
                "============================================================"
        );
    }

    private void printMainMenu() {

        System.out.println();

        System.out.println(
                "[1] Create Booking"
        );

        System.out.println(
                "[2] Search Booking"
        );

        System.out.println(
                "[3] Cancel Booking"
        );

        System.out.println(
                "[4] View Movie Schedule"
        );

        System.out.println(
                "[5] View Seat Availability"
        );

        System.out.println(
                "[6] Exit"
        );

        System.out.println(
                "------------------------------------------------------------"
        );
    }

    private void createBooking() {

        System.out.println();

        System.out.println(
                "---------------------- NEW BOOKING -------------------------"
        );

        String customerName =
                readNonEmpty(
                        "Customer name > "
                );

        Movie movie =
                chooseMovie();

        if (movie == null) {
            return;
        }

        Show show =
                chooseShow(movie);

        if (show == null) {
            return;
        }

        if (
                show.getAvailableSeatCount()
                        == 0
        ) {

            System.out.println(
                    "This show is sold out."
            );

            return;
        }

        seatMapUI.display(show);

        int seatCount =
                readInt(
                        "How many seats do you want? > ",
                        1,
                        show.getAvailableSeatCount()
                );

        List<String> selectedSeats =
                selectSeats(
                        show,
                        seatCount
                );

        Set<String> selectedSet =
                new HashSet<>(
                        selectedSeats
                );

        seatMapUI.display(
                show,
                selectedSet
        );

        double total =
                show.getTicketPrice()
                        * selectedSeats.size();

        System.out.println();

        System.out.println(
                "Booking summary"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        System.out.println(
                "Customer : "
                        + customerName
        );

        System.out.println(
                "Movie    : "
                        + movie.getName()
        );

        System.out.println(
                "Date     : "
                        + show.getDate()
        );

        System.out.println(
                "Show     : "
                        + show.getTime()
        );

        System.out.println(
                "Seats    : "
                        + String.join(
                        ", ",
                        selectedSeats
                )
        );

        System.out.printf(
                "Total    : Rs. %.2f%n",
                total
        );

        if (
                !readYesNo(
                        "Confirm booking? [Y/N] > "
                )
        ) {

            System.out.println(
                    "Booking cancelled before payment."
            );

            return;
        }

        double payment =
                readPayment(total);

        if (payment == 0) {

            System.out.println(
                    "Booking cancelled before payment."
            );

            return;
        }

        try {

            Booking booking =
                    bookingService.createBooking(
                            customerName,
                            movie,
                            show,
                            selectedSeats
                    );

            if (booking == null) {

                System.out.println(
                        "One or more selected seats are no longer available."
                );

                return;
            }

            System.out.printf(
                    "Change: Rs. %.2f%n",
                    payment
                            - booking.getTotalPrice()
            );

            System.out.println(
                    "Booking successful."
            );

            ticketUI.printTicket(
                    booking
            );

        } catch (
                IllegalStateException e
        ) {

            System.out.println(
                    "Booking failed: "
                            + e.getMessage()
            );
        }
    }

    private List<String> selectSeats(
            Show show,
            int seatCount
    ) {

        List<String> selected =
                new ArrayList<>();

        while (
                selected.size()
                        < seatCount
        ) {

            String input =
                    readNonEmpty(
                            "Enter seat "
                                    + (selected.size() + 1)
                                    + " > "
                    );

            String seatName =
                    seatService
                            .normalizeSeatName(
                                    input
                            );

            if (
                    selected.contains(
                            seatName
                    )
            ) {

                System.out.println(
                        "You already selected "
                                + seatName
                                + "."
                );

                continue;
            }

            if (
                    seatService.findSeat(
                            show,
                            seatName
                    ) == null
            ) {

                System.out.println(
                        "Seat "
                                + seatName
                                + " does not exist."
                );

                continue;
            }

            if (
                    !seatService.isSeatAvailable(
                            show,
                            seatName
                    )
            ) {

                System.out.println(
                        "Seat "
                                + seatName
                                + " is already booked."
                );

                continue;
            }

            selected.add(
                    seatName
            );
        }

        return selected;
    }

    private void searchBooking() {

        System.out.println();

        System.out.println(
                "--------------------- SEARCH BOOKING -----------------------"
        );

        String id =
                readNonEmpty(
                        "Booking ID > "
                );

        Booking booking =
                bookingService
                        .findBookingById(id);

        if (booking == null) {

            System.out.println(
                    "No booking found with ID "
                            + id
                            + "."
            );

            return;
        }

        ticketUI.printTicket(
                booking
        );
    }

    private void cancelBooking() {

        System.out.println();

        System.out.println(
                "--------------------- CANCEL BOOKING -----------------------"
        );

        String id =
                readNonEmpty(
                        "Booking ID > "
                );

        Booking booking =
                bookingService
                        .findBookingById(id);

        if (booking == null) {

            System.out.println(
                    "No booking found with ID "
                            + id
                            + "."
            );

            return;
        }

        ticketUI.printTicket(
                booking
        );

        if (!booking.isConfirmed()) {

            System.out.println(
                    "This booking is already cancelled."
            );

            return;
        }

        if (
                !readYesNo(
                        "Cancel this booking? [Y/N] > "
                )
        ) {

            System.out.println(
                    "Cancellation stopped."
            );

            return;
        }

        if (
                bookingService.cancelBooking(
                        id
                )
        ) {

            System.out.println(
                    "Booking "
                            + id
                            + " cancelled successfully."
            );

            System.out.println(
                    "Its seats are available again."
            );

        } else {

            System.out.println(
                    "The booking could not be cancelled."
            );
        }
    }

    private void viewMovieSchedule() {

        System.out.println();

        System.out.println(
                "--------------------- MOVIE SCHEDULE -----------------------"
        );

        List<Movie> movies =
                movieService.getMovies();

        for (Movie movie : movies) {

            System.out.printf(
                    "%s - %s | %s | %s%n",
                    movie.getCode(),
                    movie.getName(),
                    movie.getLanguage(),
                    movie.getGenre()
            );

            for (
                    Show show :
                    movie.getShows()
            ) {

                System.out.printf(
                        "    %s | %-10s | Rs. %-7.2f | Available: %d/%d%n",
                        show.getDate(),
                        show.getTime(),
                        show.getTicketPrice(),
                        show.getAvailableSeatCount(),
                        show.getTotalSeats()
                );
            }

            System.out.println();
        }
    }

    private void viewSeatAvailability() {

        System.out.println();

        System.out.println(
                "------------------- SEAT AVAILABILITY ----------------------"
        );

        Movie movie =
                chooseMovie();

        if (movie == null) {
            return;
        }

        Show show =
                chooseShow(movie);

        if (show == null) {
            return;
        }

        seatMapUI.display(show);
    }

    private Movie chooseMovie() {

        List<Movie> movies =
                movieService.getMovies();

        if (movies.isEmpty()) {

            System.out.println(
                    "No movies are loaded."
            );

            return null;
        }

        System.out.println();

        System.out.println(
                "Available movies"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        for (
                int i = 0;
                i < movies.size();
                i++
        ) {

            Movie movie =
                    movies.get(i);

            System.out.printf(
                    "[%d] %-25s | %-10s | %s%n",
                    i + 1,
                    movie.getName(),
                    movie.getLanguage(),
                    movie.getGenre()
            );
        }

        System.out.println(
                "[0] Back"
        );

        int choice =
                readInt(
                        "Select movie > ",
                        0,
                        movies.size()
                );

        if (choice == 0) {
            return null;
        }

        return movieService
                .getMovieByNumber(
                        choice
                );
    }

    private Show chooseShow(
            Movie movie
    ) {

        List<Show> shows =
                movie.getShows();

        System.out.println();

        System.out.println(
                "Shows for "
                        + movie.getName()
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        for (
                int i = 0;
                i < shows.size();
                i++
        ) {

            Show show =
                    shows.get(i);

            System.out.printf(
                    "[%d] %s | %-10s | Rs. %-7.2f | Available %d/%d%n",
                    i + 1,
                    show.getDate(),
                    show.getTime(),
                    show.getTicketPrice(),
                    show.getAvailableSeatCount(),
                    show.getTotalSeats()
            );
        }

        System.out.println(
                "[0] Back"
        );

        int choice =
                readInt(
                        "Select show > ",
                        0,
                        shows.size()
                );

        if (choice == 0) {
            return null;
        }

        return movieService
                .getShowByNumber(
                        movie,
                        choice
                );
    }

    private int readInt(
            String prompt,
            int min,
            int max
    ) {

        while (true) {

            System.out.print(
                    prompt
            );

            String input =
                    scanner
                            .nextLine()
                            .trim();

            try {

                int value =
                        Integer.parseInt(
                                input
                        );

                if (
                        value >= min
                                && value <= max
                ) {

                    return value;
                }

            } catch (
                    NumberFormatException ignored
            ) {
            }

            System.out.printf(
                    "Please enter a number from %d to %d.%n",
                    min,
                    max
            );
        }
    }

    private double readPayment(
            double total
    ) {

        while (true) {

            System.out.printf(
                    "Enter payment (Rs. %.2f required, 0 to cancel) > ",
                    total
            );

            String input =
                    scanner
                            .nextLine()
                            .trim();

            try {

                double payment =
                        Double.parseDouble(
                                input
                        );

                if (payment == 0) {
                    return 0;
                }

                if (payment >= total) {
                    return payment;
                }

                System.out.println(
                        "Payment is not enough."
                );

            } catch (
                    NumberFormatException e
            ) {

                System.out.println(
                        "Please enter a valid payment amount."
                );
            }
        }
    }

    private String readNonEmpty(
            String prompt
    ) {

        while (true) {

            System.out.print(
                    prompt
            );

            String value =
                    scanner
                            .nextLine()
                            .trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println(
                    "This value cannot be empty."
            );
        }
    }

    private boolean readYesNo(
            String prompt
    ) {

        while (true) {

            System.out.print(
                    prompt
            );

            String answer =
                    scanner
                            .nextLine()
                            .trim();

            if (
                    answer.equalsIgnoreCase("y")
                            || answer.equalsIgnoreCase("yes")
            ) {

                return true;
            }

            if (
                    answer.equalsIgnoreCase("n")
                            || answer.equalsIgnoreCase("no")
            ) {

                return false;
            }

            System.out.println(
                    "Please enter Y or N."
            );
        }
    }
}