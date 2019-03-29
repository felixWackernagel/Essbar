package de.wackernagel.essbar.dagger;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import de.wackernagel.essbar.EssbarApplication;

@Singleton
@Component(modules = {AndroidInjectionModule.class, RetrofitModule.class, ActivityModule.class, RoomModule.class} )
public interface ApplicationComponent extends AndroidInjector<EssbarApplication> {
}