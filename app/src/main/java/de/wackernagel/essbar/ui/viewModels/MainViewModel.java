package de.wackernagel.essbar.ui.viewModels;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import de.wackernagel.essbar.repository.EssbarRepository;
import de.wackernagel.essbar.ui.MainActivity;
import de.wackernagel.essbar.web.Resource;

public class MainViewModel extends ViewModel {

    private static String[] menuTypeSelectors = new String[] {
            "tr.menue-line-Fruehstueck > td.menue-Fruehstueck",
            "tr.menue-line-Obstfruehstueck > td.menue-Obstfruehstueck",
            "tr.menue-line-Mittag > td.menue-Mittag",
            "tr.menue-line-Vesper > td.menue-Vesper"
    };

    private final EssbarRepository repository;
    private final MutableLiveData<List<MainActivity.MenuItem>> itemsLiveData;

    MainViewModel( final EssbarRepository repository ) {
        this.repository = repository;
        itemsLiveData = new MutableLiveData<>();
        itemsLiveData.setValue( Collections.emptyList() );
    }

    public LiveData<List<MainActivity.MenuItem>> getMenuItems() {
        return Transformations.switchMap( repository.getMenusDocument(), this::getMenusList);
    }

    private LiveData<List<MainActivity.MenuItem>> getMenusList(final Resource<Document> resource) {
        if( resource != null && resource.getResource() != null ) {
            final Document menuPage = resource.getResource();
            final List<MainActivity.MenuItem> allMenuItems = new ArrayList<>();
            for(int menuTypeIndex = 0; menuTypeIndex < menuTypeSelectors.length; menuTypeIndex++ ) {
                final String menuTypeSelector = menuTypeSelectors[ menuTypeIndex ];
                final Elements menuTypeElements = menuPage.select( menuTypeSelector );
                for( int menuIndex = 0; menuIndex < menuTypeElements.size(); menuIndex++ ) {
                    final int menuItemIndex = menuTypeIndex + ( ( menuTypeIndex + 1 ) * menuIndex );
                    allMenuItems.add( menuItemIndex, new MainActivity.MenuItem( menuTypeElements.get( menuIndex ) ) );
                }
            }
            itemsLiveData.setValue( allMenuItems );
        }
        return itemsLiveData;
    }
}
