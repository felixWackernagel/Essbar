package de.wackernagel.essbar.utils;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * @param calendarWeekWithYear (yyyy,cw) like (2020,1), (2019,26)
     * @return int array with year at [0] and week of year at [1]
     */
    public static int[] splitCalendarWeekWithYear( final String calendarWeekWithYear )
    {
        final String[] dateParts = calendarWeekWithYear.substring( 1, calendarWeekWithYear.length() - 1 ).split(",");
        int year = Integer.valueOf( dateParts[0] );
        int weekOfYear = Integer.valueOf( dateParts[1] );
        return new int[]{ year, weekOfYear };
    }

    /**
     * @param dayOfWeek like MONDAY
     * @param weekOfYear like 26
     * @param year like 2019
     * @return 2019-07-14
     */
    public static String getDay( int dayOfWeek, int weekOfYear, int year ) {
        final Calendar calendar = getCalendarForDay( dayOfWeek, weekOfYear, year );
        return calendar.get(Calendar.YEAR) + "-" + twoDigitFormat( calendar.get( Calendar.MONTH ) + 1 ) + "-" + twoDigitFormat( calendar.get( Calendar.DATE ) );
    }

    /**
     * @param dayOfWeek like MONDAY
     * @param calendarWeekWithYear (yyyy,cw) like (2020,1), (2019,26)
     * @return Date with given year, weekOfYear and dayOfWeek at 00:00:00 o'clock.
     */
    public static Date getDayAsDate( int dayOfWeek, final String calendarWeekWithYear ) {
        final int[] yearWithWeekOfYear = splitCalendarWeekWithYear( calendarWeekWithYear );
        return getCalendarForDay( dayOfWeek, yearWithWeekOfYear[1], yearWithWeekOfYear[0] ).getTime();
    }

    private static Calendar getCalendarForDay( int dayOfWeek, int weekOfYear, int year ) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek( Calendar.MONDAY );
        calendar.set( Calendar.YEAR, year );
        calendar.set( Calendar.WEEK_OF_YEAR, weekOfYear );
        calendar.set( Calendar.DAY_OF_WEEK, dayOfWeek );
        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        return calendar;
    }

    private static String twoDigitFormat( int digit ) {
        if( digit <= 9 ) {
            return "0" + digit;
        }
        return String.valueOf( digit );
    }

    /**
     * @return calendar week like 26
     */
    public static int calculateCurrentCalendarWeek() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        return calendar.get( Calendar.WEEK_OF_YEAR );
    }

    /**
     *
     * @param calendarWeekWithYear (yyyy,cw) like (2019,26), (2020,1)
     * @return current week of year if calendarWeekWithYear is null or week of year from given string
     */
    public static int calculateCalendarWeek( @Nullable final String calendarWeekWithYear ) {
        if( calendarWeekWithYear == null ) {
            return calculateCurrentCalendarWeek();
        } else {
            return splitCalendarWeekWithYear( calendarWeekWithYear )[1];
        }
    }
}
