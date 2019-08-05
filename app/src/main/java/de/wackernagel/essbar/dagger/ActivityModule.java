package de.wackernagel.essbar.dagger;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.wackernagel.essbar.ui.LoginActivity;
import de.wackernagel.essbar.ui.MenuActivity;

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector( modules = MenuFragmentModule.class )
    abstract MenuActivity contributeMenuActivityInjector();

    @ContributesAndroidInjector( modules = LoginFragmentModule.class )
    abstract LoginActivity contributeLoginActivityInjector();

}
