package de.wackernagel.essbar.dagger;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.wackernagel.essbar.ui.CustomerFormFragment;
import de.wackernagel.essbar.ui.CustomersListFragment;

@Module
abstract class LoginFragmentModule {

    @ContributesAndroidInjector
    abstract CustomersListFragment contributeCustomerListFragment();

    @ContributesAndroidInjector
    abstract CustomerFormFragment contributeCustomerFormFragment();

}