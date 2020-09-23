package de.wackernagel.essbar.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

import de.wackernagel.essbar.ui.lists.Listable;
import de.wackernagel.essbar.ui.pojos.Type;

@Entity( tableName = "meals" )
public class Meal implements Listable {
    @PrimaryKey( autoGenerate = true)
    private long id;
    @NonNull
    private String name;
    @NonNull
    @ColumnInfo( name = "meal_date" )
    private String date;
    @NonNull
    private Type type;

    @Ignore
    public Meal( @NonNull final String name, @NonNull final String date, @NonNull final Type type ) {
        this.name = name;
        this.date = date;
        this.type = type;
    }

    public Meal( final long id, @NonNull final String name, @NonNull final String date, @NonNull final Type type ) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.type = type;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meal meal = (Meal) o;
        return id == meal.id &&
                name.equals(meal.name) &&
                date.equals(meal.date) &&
                type == meal.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, date, type);
    }

    @NonNull
    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", type=" + type +
                '}';
    }
}
