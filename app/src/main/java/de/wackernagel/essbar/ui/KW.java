package de.wackernagel.essbar.ui;

import org.jsoup.nodes.Element;

public class KW {
    private final String value;
    private final boolean selected;
    private final String name;

    public KW( final Element element ) {
        this.value = element.attr("value" );
        this.selected = element.classNames().contains( "selected" );
        this.name = formatName( element.text() );
    }

    // FROM: KW: 09 || 25.02.2019 - 03.03.2019
    // TO: 25.02. - 03.03.
    private String formatName( String name ) {
        return name.substring(10, 16).concat( name.substring(20, 29) );
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }
}
