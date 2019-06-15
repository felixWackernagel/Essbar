package de.wackernagel.essbar.ui.pojos;

import org.jsoup.nodes.Element;

import java.util.Objects;

public class ChangedMenu {

    private final long id;
    private final String date;
    private final String name;
    private final String oldQuantity;
    private final String newQuantity;
    private final boolean nowOrdered;
    private final int weekday;

    public ChangedMenu( Element element ) {
        date = element.selectFirst(".best_datum").text();
        name = element.selectFirst(".best_bez").text();
        weekday = getWeekdayIndex( date );
        id = calculateId( weekday, name );

        oldQuantity = element.selectFirst(".best_anz_alt").text();
        newQuantity = element.selectFirst(".best_anz_neu").text();
        nowOrdered = "1".equals( newQuantity );
    }

    private static long calculateId( final int weekday, String name ) {
        return getMenuTypIndex( name ) + ( weekday * 4 );
    }

    private static int getWeekdayIndex( final String date ) {
        if( date.contains("Montag") ) {
            return 0;
        } else if( date.contains("Dienstag") ) {
            return 1;
        } else if( date.contains("Mittwoch") ) {
            return 2;
        } else if( date.contains("Donnerstag") ) {
            return 3;
        } else if( date.contains("Freitag") ) {
            return 4;
        } else {
            throw new IllegalStateException("No known weekday found.");
        }
    }

    private static int getMenuTypIndex( final String name ) {
        if( "Frühstück".equals( name ) ) {
            return 1;
        } else if( "Obstfrühstück".equals( name ) ) {
            return 2;
        } else if( "Mittag".equals( name ) ) {
            return 3;
        } else if( "Vesper".equals( name ) ) {
            return 4;
        } else {
            throw new IllegalStateException("No known menu typ found.");
        }
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

    public int getWeekday() {
        return weekday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangedMenu that = (ChangedMenu) o;
        return id == that.id &&
                Objects.equals(date, that.date) &&
                Objects.equals(name, that.name) &&
                Objects.equals(oldQuantity, that.oldQuantity) &&
                Objects.equals(newQuantity, that.newQuantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, name, oldQuantity, newQuantity);
    }

    @Override
    public String toString() {
        return "ChangedMenu{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", oldQuantity='" + oldQuantity + '\'' +
                ", newQuantity='" + newQuantity + '\'' +
                '}';
    }
}
