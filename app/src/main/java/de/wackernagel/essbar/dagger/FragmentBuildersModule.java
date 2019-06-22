package de.wackernagel.essbar.dagger;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.wackernagel.essbar.ui.CalendarWeekSelectorFragment;
import de.wackernagel.essbar.ui.CustomerFormFragment;
import de.wackernagel.essbar.ui.CustomersListFragment;
import de.wackernagel.essbar.ui.MenuConfirmationFragment;
import de.wackernagel.essbar.ui.MenuListFragment;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract CustomersListFragment contributeCustomerListFragment();

    @ContributesAndroidInjector
    abstract CustomerFormFragment contributeCustomerFormFragment();

    @ContributesAndroidInjector
    abstract MenuListFragment contributeMenuListFragment();

    @ContributesAndroidInjector
    abstract MenuConfirmationFragment contributeFullscreenFragment();

    @ContributesAndroidInjector
    abstract CalendarWeekSelectorFragment contributeCalendarWeekSelectorFragment();

}