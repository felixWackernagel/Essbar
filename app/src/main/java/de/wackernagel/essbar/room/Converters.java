package de.wackernagel.essbar.room;

import androidx.room.TypeConverter;

import de.wackernagel.essbar.ui.pojos.Type;

public class Converters {

    @TypeConverter
    public static Type ordinalToMealType( int value ) {
        return value < 0 || value >= Type.values().length ? null : Type.values()[ value ];
    }

    @TypeConverter
    public static Integer mealTypeToOrdinal( Type type ) {
        return type == null ? null : type.ordinal();
    }

}
