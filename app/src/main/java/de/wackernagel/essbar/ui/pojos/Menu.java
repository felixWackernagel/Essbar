package de.wackernagel.essbar.ui.pojos;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jsoup.nodes.Element;

import java.util.Calendar;
import java.util.Objects;
import java.util.Set;

public class Menu {
    private int id;
    private String menuName;
    private boolean editable;
    private boolean ordered;
    private Weekday weekday;
    private Type typ;
    private boolean paused;
    private String inputName;
    private String price;

    public Menu(final Element element ) {
        menuName = element.select("meal > mealtxt").text().replaceAll("\\(.*?\\) ?", ""); // remove braces
        editable = element.select( "input" ).size() == 1;
        ordered = element.select( ".ordered-label" ).size() == 1 || element.select( "input[checked]" ).size() == 1 || element.classNames().contains("in-order");
        if( editable ) {
            inputName = element.select( "input" ).get( 0 ).attr( "name" );
        }
        price = element.siblingElements().select( ".mealtitel > price" ).text();
        weekday = calculateWeekday( element.attr( "day" ) );
        typ = calculateType( element.classNames(), menuName );
        id = typ.getNumber() + ( weekday.getNumber() * 4 );

        // TODO
        paused = false;
        if( paused ) {
            menuName = "<Grund>";
        }
    }

    private Type calculateType( final Set<String> cssClasses, final String menuName ) {
        if( cssClasses.contains( "menuGroup_1" ) ) {
            return Type.LUNCH;
        }

        if( TextUtils.isEmpty( menuName ) ) {
            return Type.UNKNOWN;
        }

        final String firstLetter = String.valueOf( menuName.charAt( 0 ) );
        if( "F".equalsIgnoreCase( firstLetter ) ) {
            return Type.BREAKFAST;
        } else if( "O".equalsIgnoreCase( firstLetter ) ) {
            return Type.FRUIT_BREAKFAST;
        } else if( "V".equalsIgnoreCase( firstLetter ) ) {
            return Type.SNACK;
        }
        return typ = Type.UNKNOWN;
    }

    /**
     * @param day 2019-07-05
     */
    private Weekday calculateWeekday( final String day ) {
        if( TextUtils.isEmpty( day ) ) {
            return Weekday.UNKNOWN;
        }
        final String[] dayParts = day.split("-");
        if( dayParts.length != 3 ) {
            return Weekday.UNKNOWN;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, Integer.valueOf( dayParts[0] ) );
        calendar.set( Calendar.MONTH, Integer.valueOf( dayParts[1] ) - 1 );
        calendar.set( Calendar.DATE, Integer.valueOf( dayParts[2] ) );
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

    public String getMenuName() {
        return menuName;
    }

    public int getId() {
        return id;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public boolean isPaused() {
        return paused;
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public Type getTyp() {
        return typ;
    }

    @Nullable
    public String getInputName() {
        return inputName;
    }

    public String getPrice() {
        return price;
    }

    @NonNull
    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", menuName='" + menuName + '\'' +
                ", editable=" + editable +
                ", ordered=" + ordered +
                ", weekday=" + weekday +
                ", typ=" + typ +
                ", paused=" + paused +
                ", inputName='" + inputName + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return id == menu.id &&
                editable == menu.editable &&
                ordered == menu.ordered &&
                paused == menu.paused &&
                Objects.equals(menuName, menu.menuName) &&
                weekday == menu.weekday &&
                typ == menu.typ &&
                Objects.equals(inputName, menu.inputName) &&
                Objects.equals(price, menu.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, menuName, editable, ordered, weekday, typ, paused, inputName, price);
    }
}