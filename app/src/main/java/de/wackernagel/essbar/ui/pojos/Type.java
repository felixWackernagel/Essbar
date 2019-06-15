package de.wackernagel.essbar.ui.pojos;

import java.util.Set;

public enum Type {

    BREAKFAST("menue-Fruehstueck", 1),
    FRUIT_BREAKFAST("menue-Obstfruehstueck", 2),
    LUNCH("menue-Mittag", 3),
    SNACK("menue-Vesper", 4),
    UNKNOWN("", 0);

    private final String selector;
    private final int number;

    Type( final String selector, final int number ) {
        this.selector = selector;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public static Type from(final Set<String> cssClasses ) {
        for( Type type : Type.values() ) {
            if( cssClasses.contains( type.selector ) ) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
