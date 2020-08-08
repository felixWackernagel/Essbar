package de.wackernagel.essbar.ui.pojos;

import android.util.Log;

import org.jsoup.nodes.Element;

import java.util.Calendar;

import de.wackernagel.essbar.ui.lists.Listable;
import de.wackernagel.essbar.utils.DateUtils;

public class CalendarWeek implements Listable {
    private final String value;
    private final String dateRange;
    private final String number;
    private final long id;
    private final String url;

    public CalendarWeek( final Element element ) {
        final String text = element.text(); // KW 26 || 24.06. - 30.06.2019
        this.dateRange = sliceOutDateRange( text );
        this.number = sliceOutNumber( text );
        this.value = "(2020," + number + ")"; // (yyyy,cw) like (2019,26)
        this.id = DateUtils.getDayAsDate( Calendar.MONDAY, value ).getTime();
        this.url = element.parent().attr("href");
        Log.e("CalendarWeek", "url=" + url);
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

    public boolean isSelected( final String currentCalendarWeek ) {
        Log.e("CalendarWeek", "currentCW = " + currentCalendarWeek + ", thisCW = " + value);
        return value.equals( currentCalendarWeek );
    }

    public String getNumber() {
        return number;
    }

    @Override
    public long getId() {
        return id;
    }
}
