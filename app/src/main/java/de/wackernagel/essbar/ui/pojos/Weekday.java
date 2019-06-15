package de.wackernagel.essbar.ui.pojos;

import java.util.Set;

public enum Weekday {

    MONDAY("weekday-1", 0),
    TUESDAY("weekday-2", 1),
    WEDNESDAY("weekday-3", 2),
    THURSDAY("weekday-4", 3),
    FRIDAY("weekday-5", 4),
    UNKNOWN("", 0);

    private final String selector;
    private final int number;

    Weekday( final String selector, final int number ) {
        this.selector = selector;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public static Weekday from(final Set<String> cssClasses ) {
        for( Weekday weekday : Weekday.values() ) {
            if( cssClasses.contains( weekday.selector ) ) {
                return weekday;
            }
        }
        return UNKNOWN;
    }
}
