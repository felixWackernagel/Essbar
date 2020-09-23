package de.wackernagel.essbar.dagger;

import android.app.Application;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.wackernagel.essbar.repository.EssbarRepository;
import de.wackernagel.essbar.room.AppDatabase;
import de.wackernagel.essbar.room.CustomerDao;
import de.wackernagel.essbar.room.MealDao;
import de.wackernagel.essbar.utils.ConnectivityLifecycleObserver;
import de.wackernagel.essbar.web.WebService;

@Module
public class RoomModule {

    @Provides
    @Singleton
    AppDatabase providerAppDatabase( final Application application ) {
        return Room.databaseBuilder( application, AppDatabase.class, "essbar.db" )
            .fallbackToDestructiveMigration()
            .build();
    }

    @Provides
    @Singleton
    ConnectivityLifecycleObserver providerConnectivityLifecycleObserver( final Application application ) {
        return new ConnectivityLifecycleObserver( application );
    }

    @Provides
    @Singleton
    EssbarRepository provideEssbarRepository(final WebService webService, final CustomerDao customerDao, final MealDao mealDao ) {
        return new EssbarRepository( webService, customerDao, mealDao );
    }

    @Provides
    @Singleton
    CustomerDao provideCustomerDao( final AppDatabase database ) {
        return database.customerDao();
    }

    @Provides
    @Singleton
    MealDao provideMealDao(final AppDatabase database ) {
        return database.mealDao();
    }

}
