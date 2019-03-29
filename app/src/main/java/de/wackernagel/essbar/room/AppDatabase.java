package de.wackernagel.essbar.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database( entities = { Customer.class}, version = 1 )
public abstract class AppDatabase extends RoomDatabase {
    public abstract CustomerDao customerDao();
}
