package de.wackernagel.essbar.ui.viewModels;

import javax.inject.Singleton;

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
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if( modelClass.isAssignableFrom( MainViewModel.class ) ) {
            return (T) new MainViewModel( repository );
        }
        throw new IllegalArgumentException( "Unsupported ViewModel class." );
    }
}
