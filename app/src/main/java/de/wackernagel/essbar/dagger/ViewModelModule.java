package de.wackernagel.essbar.dagger;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import de.wackernagel.essbar.ui.viewModels.LoginViewModel;
import de.wackernagel.essbar.ui.viewModels.MenuViewModel;

@SuppressWarnings("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel( LoginViewModel viewModel );

    @Binds
    @IntoMap
    @ViewModelKey(MenuViewModel.class)
    abstract ViewModel bindMenuViewModel( MenuViewModel viewModel );

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory( ViewModelFactory factory );

}
