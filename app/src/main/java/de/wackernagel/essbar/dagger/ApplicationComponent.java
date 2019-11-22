package de.wackernagel.essbar.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import de.wackernagel.essbar.EssbarApplication;

@Singleton
@Component(modules = {AndroidInjectionModule.class, RetrofitModule.class, ActivityModule.class, RoomModule.class, ViewModelModule.class} )
public interface ApplicationComponent extends AndroidInjector<EssbarApplication> {

    @Component.Builder
    interface Builder {

        ApplicationComponent build();

        @BindsInstance
        Builder application(Application application);
    }
}