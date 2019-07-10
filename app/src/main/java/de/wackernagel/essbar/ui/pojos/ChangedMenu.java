package de.wackernagel.essbar.ui.pojos;

import android.text.TextUtils;

import org.jsoup.nodes.Element;

import java.util.Calendar;
import java.util.Objects;

public class ChangedMenu {

    private final long id;
    private final String date;
    private final String name;
    private final String oldQuantity;
    private final String newQuantity;
    private final boolean nowOrdered;
    private final Weekday weekday;
    private final Type type;

    public ChangedMenu( Element element ) {
        date = element.selectFirst(".date").text();
        name = element.selectFirst(".mealtxt").text().replaceAll("\\(.*?\\) ?", ""); // remove braces
        weekday = calculateWeekday( date );
        type = calculateType( element.selectFirst( ".mealtitle" ).text() );
        id = type.getNumber() + ( weekday.getNumber() * 4 );
        oldQuantity = element.selectFirst(".befor").text();
        newQuantity = element.selectFirst(".after").text();
        nowOrdered = "1".equals( newQuantity );
    }

    /**
     * @param day 05.07.2019
     */
    private Weekday calculateWeekday( final String day ) {
        if( TextUtils.isEmpty( day ) ) {
            return Weekday.UNKNOWN;
        }
        final String[] dayParts = day.split("\\.");
        if( dayParts.length != 3 ) {
            return Weekday.UNKNOWN;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, Integer.valueOf( dayParts[2] ) );
        calendar.set( Calendar.MONTH, Integer.valueOf( withoutNull( dayParts[1] ) ) - 1 );
        calendar.set( Calendar.DATE, Integer.valueOf( withoutNull( dayParts[0] ) ) );
        final int dayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );
        switch ( dayOfWeek ) {
            case Calendar.SUNDAY: return Weekday.UNKNOWN;
            case Calendar.MONDAY: return Weekday.MONDAY;
            case Calendar.TUESDAY: return Weekday.TUESDAY;
            case Calendar.WEDNESDAY: return Weekday.WEDNESDAY;
            case Calendar.THURSDAY: return Weekday.THURSDAY;
            case Calendar.FRIDAY: return Weekday.FRIDAY;
            case Calendar.SATURDAY: return Weekday.UNKNOWN;
        }
        return Weekday.UNKNOWN;
    }

    private String withoutNull( final String digit ) {
        if( TextUtils.isEmpty( digit ) ) {
            return digit;
        }
        if( digit.startsWith("0") ) {
            return digit.substring( 1 );
        }
        return digit;
    }

    private Type calculateType( final String menuTitle ) {
        if( TextUtils.isEmpty( menuTitle ) ) {
            return Type.UNKNOWN;
        }

        final String firstLetter = String.valueOf( menuTitle.charAt( 0 ) );
        if( "F".equalsIgnoreCase( firstLetter ) ) {
            return Type.BREAKFAST;
        } else if( "M".equalsIgnoreCase( firstLetter ) ) {
            return Type.LUNCH;
        } else if( "O".equalsIgnoreCase( firstLetter ) ) {
            return Type.FRUIT_BREAKFAST;
        } else if( "V".equalsIgnoreCase( firstLetter ) ) {
            return Type.SNACK;
        }
        return Type.UNKNOWN;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getOldQuantity() {
        return oldQuantity;
    }

    public String getNewQuantity() {
        return newQuantity;
    }

    public boolean isNowOrdered() {
        return nowOrdered;
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangedMenu that = (ChangedMenu) o;
        return id == that.id &&
                nowOrdered == that.nowOrdered &&
                Objects.equals(date, that.date) &&
                Objects.equals(name, that.name) &&
                Objects.equals(oldQuantity, that.oldQuantity) &&
                Objects.equals(newQuantity, that.newQuantity) &&
                weekday == that.weekday &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, name, oldQuantity, newQuantity, nowOrdered, weekday, type);
    }

    @Override
    public String toString() {
        return "ChangedMenu{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", oldQuantity='" + oldQuantity + '\'' +
                ", newQuantity='" + newQuantity + '\'' +
                ", nowOrdered=" + nowOrdered +
                ", weekday=" + weekday +
                ", type=" + type +
                '}';
    }
}
