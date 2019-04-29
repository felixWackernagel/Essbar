package de.wackernagel.essbar.ui;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Objects;

import javax.annotation.Nonnull;

import androidx.annotation.Nullable;

public class Menu {
    private int id;
    private String menuName;
    private boolean editable;
    private boolean ordered;
    private int weekday;
    private int menuTyp;
    private boolean paused;
    private String inputName;
    private String price;

    public Menu(final Element element ) {
        weekday = getWeekdayIndex( element );
        menuTyp = getMenuTypIndex( element );
        id = menuTyp + ( weekday * 4 );
        editable = element.classNames().contains( "pointer" );
        ordered = element.classNames().contains( "gruen" );
        if( editable ) {
            final Elements hiddenFields = element.select( "input[type='hidden']" );
            if( hiddenFields != null && hiddenFields.size() == 1 ) {
                inputName = hiddenFields.get( 0 ).attr( "name" );
            }
        }

        final Element priceCell = element.siblingElements().get( 0 );
        priceCell.select( "span" ).remove();
        priceCell.select( "br" ).remove();
        price = priceCell.text();

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

    @Nullable
    public String getInputName() {
        return inputName;
    }

    public String getPrice() {
        return price;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return id == menu.id &&
                editable == menu.editable &&
                ordered == menu.ordered &&
                weekday == menu.weekday &&
                menuTyp == menu.menuTyp &&
                paused == menu.paused &&
                Objects.equals(menuName, menu.menuName) &&
                Objects.equals(inputName, menu.inputName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, menuName, editable, ordered, weekday, menuTyp, paused, inputName);
    }

    @Nonnull
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
                ", inputName='" + inputName + '\'' +
                '}';
    }
}