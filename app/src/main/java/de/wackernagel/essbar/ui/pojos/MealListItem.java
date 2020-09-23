package de.wackernagel.essbar.ui.pojos;

import androidx.annotation.NonNull;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.wackernagel.essbar.ui.lists.Listable;
import de.wackernagel.essbar.utils.DateUtils;

public class MealListItem implements Listable {
    private static final Format FORMATTER = new SimpleDateFormat("EEEE", Locale.getDefault() );

    public long id;
    public String name;
    public String date;

    @Override
    public long getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getFormattedDate() {
        final Date mealDate = DateUtils.parseDate( date );
        return FORMATTER.format( mealDate ) + ( android.text.format.DateUtils.isToday( mealDate.getTime() ) ? "- Heute" : "" );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealListItem meal = (MealListItem) o;
        return id == meal.id &&
                name.equals(meal.name) &&
                date.equals(meal.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, date);
    }

    @NonNull
    @Override
    public String toString() {
        return "MealListItem {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date='" + date +
                '}';
    }
}
