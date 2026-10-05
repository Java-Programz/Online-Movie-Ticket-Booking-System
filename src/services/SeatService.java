package services;

import models.Seat;
import models.Show;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeatService {

    public Seat findSeat(
            Show show,
            String seatName
    ) {

        if (
                show == null
                        || seatName == null
        ) {

            return null;
        }

        String normalized =
                normalizeSeatName(
                        seatName
                );

        for (Seat seat : show.getSeats()) {

            if (
                    seat.getSeatName()
                            .equalsIgnoreCase(
                                    normalized
                            )
            ) {

                return seat;
            }
        }

        return null;
    }

    public boolean isSeatAvailable(
            Show show,
            String seatName
    ) {

        Seat seat =
                findSeat(
                        show,
                        seatName
                );

        return seat != null
                && !seat.isBooked();
    }

    public boolean areSeatsAvailable(
            Show show,
            List<String> seatNames
    ) {

        if (
                seatNames == null
                        || seatNames.isEmpty()
        ) {

            return false;
        }

        Set<String> uniqueNames =
                new HashSet<>();

        for (String name : seatNames) {

            String normalized =
                    normalizeSeatName(name);

            if (
                    !uniqueNames.add(normalized)

                            ||

                    !isSeatAvailable(
                            show,
                            normalized
                    )
            ) {

                return false;
            }
        }

        return true;
    }

    public boolean reserveSeats(
            Show show,
            List<String> seatNames
    ) {

        if (
                !areSeatsAvailable(
                        show,
                        seatNames
                )
        ) {

            return false;
        }

        for (String name : seatNames) {

            Seat seat =
                    findSeat(
                            show,
                            name
                    );

            seat.book();
        }

        return true;
    }

    public void releaseSeats(
            Show show,
            List<String> seatNames
    ) {

        if (
                show == null
                        || seatNames == null
        ) {

            return;
        }

        for (String name : seatNames) {

            Seat seat =
                    findSeat(
                            show,
                            name
                    );

            if (seat != null) {
                seat.release();
            }
        }
    }

    public String normalizeSeatName(
            String seatName
    ) {

        return seatName == null
                ? ""
                : seatName
                    .trim()
                    .toUpperCase();
    }
}