package de.wackernagel.essbar.ui.viewModels;

import android.util.SparseBooleanArray;

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
import de.wackernagel.essbar.ui.Menu;
import de.wackernagel.essbar.web.Resource;

public class MenuViewModel extends ViewModel {

    private static String[] menuTypeSelectors = new String[] {
            "tr.menue-line-Fruehstueck > td.menue-Fruehstueck",
            "tr.menue-line-Obstfruehstueck > td.menue-Obstfruehstueck",
            "tr.menue-line-Mittag > td.menue-Mittag",
            "tr.menue-line-Vesper > td.menue-Vesper"
    };

    private final EssbarRepository repository;
    private final MutableLiveData<List<Menu>> itemsLiveData;
    private final MutableLiveData<SparseBooleanArray> checkedItemsLiveData;

    MenuViewModel(final EssbarRepository repository ) {
        this.repository = repository;
        itemsLiveData = new MutableLiveData<>();
        itemsLiveData.setValue( Collections.emptyList() );
        checkedItemsLiveData = new MutableLiveData<>();
        checkedItemsLiveData.setValue( new SparseBooleanArray( 0 ) );
    }

    public LiveData<List<Menu>> getMenuItems() {
        return Transformations.switchMap( repository.getMenusDocument(), this::getMenusList );
    }

    private LiveData<List<Menu>> getMenusList(final Resource<Document> resource) {
        if( resource != null && resource.getResource() != null ) {
            final Document menuPage = resource.getResource();
            final List<Menu> allMenuItems = new ArrayList<>();
            for(int menuTypeIndex = 0; menuTypeIndex < menuTypeSelectors.length; menuTypeIndex++ ) {
                final String menuTypeSelector = menuTypeSelectors[ menuTypeIndex ];
                final Elements menuTypeElements = menuPage.select( menuTypeSelector );
                for( int menuIndex = 0; menuIndex < menuTypeElements.size(); menuIndex++ ) {
                    final int menuItemIndex = menuTypeIndex + ( ( menuTypeIndex + 1 ) * menuIndex );
                    allMenuItems.add( menuItemIndex, new Menu( menuTypeElements.get( menuIndex ) ) );
                }
            }
            itemsLiveData.setValue( allMenuItems );
        }
        return itemsLiveData;
    }

    public LiveData<SparseBooleanArray> getCheckedMenuItems() {
        return Transformations.switchMap( itemsLiveData, this::getOrderedMenus );
    }

    private LiveData<SparseBooleanArray> getOrderedMenus( final List<Menu> menuList ) {
        if( menuList != null && !menuList.isEmpty() ) {
            final SparseBooleanArray checkedItems = new SparseBooleanArray( menuList.size() );
            for (Menu item : menuList ) {
                if( item.isOrdered() ) {
                    checkedItems.put( item.getId(), true );
                }
            }
            checkedItemsLiveData.setValue( checkedItems );
        }
        return checkedItemsLiveData;
    }
}
