package de.wackernagel.essbar.ui.pojos;

public enum Weekday {

    MONDAY(0 ),
    TUESDAY(1 ),
    WEDNESDAY(2 ),
    THURSDAY(3 ),
    FRIDAY(4 ),
    UNKNOWN(0 );

    private final int number;

    Weekday( final int number ) {
        this.number = number;
    }

    /**
     * @return a number just for calculation of the menu id
     */
    public int getNumber() {
        return number;
    }

}
