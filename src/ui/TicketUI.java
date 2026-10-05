package ui;

import models.Booking;

public class TicketUI {

    private static final int VALUE_WIDTH = 44;

    public void printTicket(
            Booking booking
    ) {

        System.out.println();

        System.out.println(
                "+------------------------------------------------------+"
        );

        System.out.println(
                "|                    MOVIE TICKET                      |"
        );

        System.out.println(
                "+------------------------------------------------------+"
        );

        printLine(
                "Booking ID",
                booking.getBookingId()
        );

        printLine(
                "Customer",
                booking.getCustomerName()
        );

        printLine(
                "Movie",
                booking.getMovieName()
        );

        printLine(
                "Date",
                booking.getShowDate()
        );

        printLine(
                "Show",
                booking.getShowTime()
        );

        printLine(
                "Seats",
                String.join(
                        ", ",
                        booking.getSelectedSeats()
                )
        );

        printLine(
                "Tickets",
                String.valueOf(
                        booking
                                .getSelectedSeats()
                                .size()
                )
        );

        printLine(
                "Ticket Price",
                String.format(
                        "Rs. %.2f",
                        booking.getTicketPrice()
                )
        );

        printLine(
                "Total",
                String.format(
                        "Rs. %.2f",
                        booking.getTotalPrice()
                )
        );

        printLine(
                "Status",
                booking.getStatus()
        );

        System.out.println(
                "+------------------------------------------------------+"
        );
    }

    private void printLine(
            String label,
            String value
    ) {

        String combined =
                String.format(
                        "%-12s: %s",
                        label,
                        value
                );

        if (
                combined.length()
                        > VALUE_WIDTH
        ) {

            combined =
                    combined.substring(
                            0,
                            VALUE_WIDTH - 3
                    )
                            + "...";
        }

        System.out.printf(
                "| %-52s |%n",
                combined
        );
    }
}