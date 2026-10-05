package models;

public class Seat {

    private final String rowLabel;
    private final int number;
    private boolean booked;

    public Seat(String rowLabel, int number) {
        this.rowLabel = rowLabel;
        this.number = number;
        this.booked = false;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public int getNumber() {
        return number;
    }

    public String getSeatName() {
        return rowLabel + number;
    }

    public boolean isBooked() {
        return booked;
    }

    public boolean book() {

        if (booked) {
            return false;
        }

        booked = true;
        return true;
    }

    public void release() {
        booked = false;
    }
}