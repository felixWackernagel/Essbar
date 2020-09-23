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
        Log.e("CalendarWeek", "start=" + getStartDate() + ", end=" + getEndDate() + ", secret=" + getSecret());
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

    public String getUrl() {
        return url;
    }

    // like 2020-08-17
    public String getStartDate()
    {
        final String[] paths = getUrlPaths();
        final int lastPath = paths.length - 1;
        return paths[ lastPath - 1 ];
    }

    // like 2020-08-23
    public String getEndDate()
    {
        final String[] paths = getUrlPaths();
        final int lastPath = paths.length - 1;
        return paths[ lastPath ];
    }

    // like 107182-pipapof34e3d641d41f39ae62b899f339bb806
    public String getSecret()
    {
        final String[] paths = getUrlPaths();
        final int lastPath = paths.length - 1;
        return paths[ lastPath - 2 ];
    }

    private String[] getUrlPaths()
    {
        String urlPaths = url.split("#")[0];
        if( urlPaths.endsWith("/") ) {
            urlPaths = urlPaths.substring( 0, urlPaths.length() - 1 );
        }
        return urlPaths.split( "/" );
    }
}
