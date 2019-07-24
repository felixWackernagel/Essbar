package de.wackernagel.essbar.ui.viewModels;

import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import de.wackernagel.essbar.repository.EssbarRepository;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final EssbarRepository repository;

    public ViewModelFactory( final EssbarRepository repository ) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NonNull
    public <T extends ViewModel> T create( @NonNull final Class<T> modelClass) {
        if( modelClass.isAssignableFrom( MenuViewModel.class ) ) {
            return (T) new MenuViewModel( repository );
        } else if( modelClass.isAssignableFrom( LoginViewModel.class ) ) {
            return (T) new LoginViewModel( repository );
        }
        throw new IllegalArgumentException( "Unsupported ViewModel class." );
    }
}
