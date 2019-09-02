package de.wackernagel.essbar.dagger;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferencesModule {

    private final Application application;

    public SharedPreferencesModule( @NonNull final Application application) {
        this.application = application;
    }

    @Provides
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences( application );
    }
}
