package de.wackernagel.essbar.ui.viewModels;

import javax.inject.Singleton;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import de.wackernagel.essbar.web.WebService;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final WebService webService;

    public ViewModelFactory( final WebService webService ) {
        this.webService = webService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if( modelClass.isAssignableFrom( MainViewModel.class ) ) {
            return (T) new MainViewModel( webService );
        }
        throw new IllegalArgumentException( "Unsupported ViewModel class." );
    }
}
