package de.wackernagel.essbar.ui.pojos;

public enum Type {

    BREAKFAST(1 ),
    FRUIT_BREAKFAST(2 ),
    LUNCH(3 ),
    SNACK( 4 ),
    UNKNOWN(0 );

    private final int number;

    Type( final int number ) {
        this.number = number;
    }

    /**
     * @return a number just for calculation of the menu id
     */
    public int getNumber() {
        return number;
    }
}
