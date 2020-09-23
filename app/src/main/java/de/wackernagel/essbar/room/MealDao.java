package de.wackernagel.essbar.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Update;

import java.util.List;

import de.wackernagel.essbar.ui.pojos.MealListItem;
import de.wackernagel.essbar.ui.pojos.Type;

@Dao
public interface MealDao {

    @Insert( onConflict = OnConflictStrategy.IGNORE )
    void insertMeal( Meal meal );

    @Update
    void updateMeal( Meal meal );

    @Query( "SELECT id, name, meal_date AS date " +
            "FROM meals " +
            "WHERE type = :type " +
            "AND strftime('%W',meal_date) = :weekOfYear " +
            "AND strftime('%Y',meal_date) = :year " +
            "ORDER BY date ASC" )
    LiveData<List<MealListItem>> queryMealsOfTypeFromWeekOfYear( Type type, String weekOfYear, String year );

    @Query( "SELECT * " +
            "FROM meals " +
            "WHERE type = :type " +
            "AND meal_date = :mealDate " +
            "LIMIT 1" )
    Meal queryMeal( Type type, String mealDate );

}
