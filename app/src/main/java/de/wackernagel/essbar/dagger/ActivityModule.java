package de.wackernagel.essbar.dagger;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.wackernagel.essbar.ui.LoginActivity;
import de.wackernagel.essbar.ui.MenuActivity;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MenuActivity contributeMainActivityInjector();

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivityInjector();

}
