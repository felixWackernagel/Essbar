package de.wackernagel.essbar.ui.pojos;

import org.jsoup.nodes.Element;

import java.util.Calendar;

import de.wackernagel.essbar.ui.lists.Listable;
import de.wackernagel.essbar.utils.DateUtils;

public class CalendarWeek implements Listable {
    private final String value;
    private final boolean selected;
    private final String dateRange;
    private final String number;
    private final long id;

    public CalendarWeek( final Element element ) {
        this.value = element.attr("value" ); // (yyyy,cw) like (2019,26)
        this.selected = "selected".equals( element.attr("selected") );
        this.dateRange = sliceOutDateRange( element.text() );
        this.number = sliceOutNumber( element.text() );
        this.id = DateUtils.getDayAsDate( Calendar.MONDAY, value ).getTime();
    }


    // KW 26 || 24.06. - 30.06.2019 or KW 6 || 24.06. - 30.06.2019
    // 24.06. - 30.06.2019
    private String sliceOutDateRange( String name ) {
        return name.substring( name.lastIndexOf("|" ) + 2 );
    }

    // KW 26 || 24.06. - 30.06.2019 or KW 6 || 24.06. - 30.06.2019
    // 26
    private String sliceOutNumber( String name ) {
        return name.substring(3, name.indexOf( "|" ) - 1 );
    }

    public String getValue() {
        return value;
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

    @Override
    public long getId() {
        return id;
    }
}
