package ui;

import models.Seat;
import models.Show;

import java.util.Collections;
import java.util.Set;

public class SeatMapUI {

    public void display(
            Show show
    ) {

        display(
                show,
                Collections.emptySet()
        );
    }

    public void display(
            Show show,
            Set<String> selectedSeats
    ) {

        System.out.println();

        System.out.println(
                "===================================================================="
        );

        System.out.println(
                "                               SCREEN"
        );

        System.out.println(
                "===================================================================="
        );

        System.out.println();

        String currentRow = "";

        for (Seat seat : show.getSeats()) {

            if (
                    !seat.getRowLabel()
                            .equals(currentRow)
            ) {

                if (!currentRow.isEmpty()) {
                    System.out.println();
                }

                currentRow =
                        seat.getRowLabel();

                System.out.printf(
                        "%-3s  ",
                        currentRow
                );
            }

            String seatName =
                    seat.getSeatName();

            String content;

            if (
                    selectedSeats.contains(
                            seatName.toUpperCase()
                    )
            ) {

                content = "**";

            } else if (
                    seat.isBooked()
            ) {

                content = "XX";

            } else {

                content = seatName;
            }

            System.out.printf(
                    "[%-3s] ",
                    content
            );
        }

        System.out.println();
        System.out.println();

        System.out.println(
                "[A1] = Available   [XX] = Booked   [**] = Your selection"
        );

        System.out.printf(
                "Available seats: %d / %d%n",
                show.getAvailableSeatCount(),
                show.getTotalSeats()
        );

        System.out.println(
                "===================================================================="
        );
    }
}