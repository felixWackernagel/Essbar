package de.wackernagel.essbar.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@TypeConverters( { Converters.class } )
@Database( entities = { Customer.class, Meal.class }, version = 2 )
public abstract class AppDatabase extends RoomDatabase {
    public abstract CustomerDao customerDao();
    public abstract MealDao mealDao();
}
