package de.wackernagel.essbar.ui.pojos;

import org.jsoup.nodes.Element;

import java.util.Date;

public class CalendarWeek {
    // private final SimpleDateFormat formatter = new SimpleDateFormat( "ww dd.MM.yyyy EEE", Locale.getDefault());

    private final String value;
    private final Date startDate;
    private final boolean selected;
    private final String dateRange;
    private final String number;

    public CalendarWeek( final Element element ) {
        this.value = element.attr("value" );
        this.startDate = new Date( Long.valueOf( value ) * 1000 ); // value is date in seconds
        this.selected = element.classNames().contains( "selected" );
        this.dateRange = sliceOutDateRange( element.text() );
        this.number = sliceOutNumber( element.text() );
    }

    // FROM: CW: 09 || 25.02.2019 - 03.03.2019
    // TO: 25.02. - 03.03.
    private String sliceOutDateRange( String name ) {
        return name.substring(10, 16).concat( name.substring(20, 29) );
    }

    private String sliceOutNumber( String name ) {
        return name.substring(4, 6);
    }

    public String getValue() {
        return value;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getDateRange() {
        return dateRange;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getNumber() {
        return number;
    }
}
