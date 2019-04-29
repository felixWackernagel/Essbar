package de.wackernagel.essbar.dagger;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.wackernagel.essbar.ui.MenuConfirmationFragment;
import de.wackernagel.essbar.ui.MenuListFragment;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract MenuListFragment contributeMenuListFragment();

    @ContributesAndroidInjector
    abstract MenuConfirmationFragment contributeFullscreenFragment();

}