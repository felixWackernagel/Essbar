package de.wackernagel.essbar.dagger;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.wackernagel.essbar.ui.CalendarWeekSelectorFragment;
import de.wackernagel.essbar.ui.LunchListFragment;
import de.wackernagel.essbar.ui.MenuConfirmationFragment;
import de.wackernagel.essbar.ui.MenuListFragment;

@Module
abstract class LunchListFragmentModule {

    @ContributesAndroidInjector
    abstract LunchListFragment contributeLunchListFragment();

}