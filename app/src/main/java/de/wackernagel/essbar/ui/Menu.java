package de.wackernagel.essbar.ui;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Objects;

public class Menu {
    private int id;
    private String menuName;
    private boolean editable;
    private boolean ordered;
    private int weekday;
    private int menuTyp;
    private boolean paused;

    public Menu(final Element element ) {
        weekday = getWeekdayIndex( element );
        menuTyp = getMenuTypIndex( element );
        id = menuTyp + ( weekday * 4 );
        editable = element.classNames().contains( "pointer" );
        ordered = element.classNames().contains( "gruen" );

        final Elements deliveryPause = element.select(".zustellpause");
        paused = !deliveryPause.isEmpty();
        if( paused ) {
            menuName = deliveryPause.get( 0 ).text().replaceAll("\\(.*?\\) ?", "");
        } else {
            // remove all meta data
            element.select("div").remove();

            // remove braces
            menuName = element.text().replaceAll("\\(.*?\\) ?", "");
        }

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

    int getWeekday() {
        return weekday;
    }

    int getMenuTyp() {
        return menuTyp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu item = (Menu) o;
        return id == item.id &&
                editable == item.editable &&
                ordered == item.ordered &&
                weekday == item.weekday &&
                menuTyp == item.menuTyp &&
                Objects.equals(menuName, item.menuName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, menuName, editable, ordered, weekday, menuTyp);
    }

    private static int getMenuTypIndex(final Element element ) {
        if( element.classNames().contains("menue-Fruehstueck") ) {
            return 1;
        } else if( element.classNames().contains("menue-Obstfruehstueck") ) {
            return 2;
        } else if( element.classNames().contains("menue-Mittag") ) {
            return 3;
        } else if( element.classNames().contains("menue-Vesper") ) {
            return 4;
        } else {
            throw new IllegalStateException("No known menu typ found.");
        }
    }

    private static int getWeekdayIndex( final Element element ) {
        if( element.classNames().contains("weekday-1") ) {
            return 0;
        } else if( element.classNames().contains("weekday-2") ) {
            return 1;
        } else if( element.classNames().contains("weekday-3") ) {
            return 2;
        } else if( element.classNames().contains("weekday-4") ) {
            return 3;
        } else if( element.classNames().contains("weekday-5") ) {
            return 4;
        } else {
            throw new IllegalStateException("No known weekday found.");
        }
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", menuName='" + menuName + '\'' +
                ", editable=" + editable +
                ", ordered=" + ordered +
                ", weekday=" + weekday +
                ", menuTyp=" + menuTyp +
                ", paused=" + paused +
                '}';
    }
}