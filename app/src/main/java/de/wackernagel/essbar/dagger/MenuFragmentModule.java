package de.wackernagel.essbar.dagger;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.wackernagel.essbar.ui.CalendarWeekSelectorFragment;
import de.wackernagel.essbar.ui.MenuConfirmationFragment;
import de.wackernagel.essbar.ui.MenuListFragment;

@Module
abstract class MenuFragmentModule {

    @ContributesAndroidInjector
    abstract MenuListFragment contributeMenuListFragment();

    @ContributesAndroidInjector
    abstract MenuConfirmationFragment contributeFullscreenFragment();

    @ContributesAndroidInjector
    abstract CalendarWeekSelectorFragment contributeCalendarWeekSelectorFragment();

}