package de.wackernagel.essbar.dagger;

import android.app.Application;

import javax.inject.Singleton;

import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import de.wackernagel.essbar.repository.EssbarRepository;
import de.wackernagel.essbar.room.AppDatabase;
import de.wackernagel.essbar.room.CustomerDao;
import de.wackernagel.essbar.ui.viewModels.ViewModelFactory;
import de.wackernagel.essbar.web.WebService;

@Module
public class RoomModule {

    private final AppDatabase database;

    public RoomModule( final Application application ) {
        this.database = Room.databaseBuilder( application, AppDatabase.class, "essbar.db" ).build();
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory providerViewModelFactory(final EssbarRepository repository ) {
        return new ViewModelFactory( repository );
    }

    @Provides
    @Singleton
    EssbarRepository provideEssbarRepository(final WebService webService, final CustomerDao customerDao ) {
        return new EssbarRepository( webService, customerDao );
    }

    @Provides
    @Singleton
    CustomerDao provideCustomerDao( final AppDatabase database ) {
        return database.customerDao();
    }

    @Provides
    @Singleton
    AppDatabase provideAppDatabase() {
        return database;
    }

}
