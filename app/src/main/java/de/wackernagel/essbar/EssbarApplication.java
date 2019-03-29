package de.wackernagel.essbar;

import android.app.Activity;
import android.app.Application;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import de.wackernagel.essbar.dagger.DaggerApplicationComponent;
import de.wackernagel.essbar.dagger.RetrofitModule;
import de.wackernagel.essbar.dagger.RoomModule;

public class EssbarApplication extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerApplicationComponent
                .builder()
                .roomModule( new RoomModule( this ) )
                .retrofitModule( new RetrofitModule( this ) )
                .build()
                .inject( this );
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }
}