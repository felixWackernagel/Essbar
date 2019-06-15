package de.wackernagel.essbar.ui.pojos;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        weekday = Weekday.from( element.classNames() );
        typ = Type.from( element.classNames() );
        id = typ.getNumber() + ( weekday.getNumber() * 4 );
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