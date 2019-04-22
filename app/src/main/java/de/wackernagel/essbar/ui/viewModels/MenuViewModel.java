package de.wackernagel.essbar.ui.viewModels;

import android.text.TextUtils;
import android.util.SparseBooleanArray;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import de.wackernagel.essbar.repository.EssbarRepository;
import de.wackernagel.essbar.ui.KW;
import de.wackernagel.essbar.ui.Menu;
import de.wackernagel.essbar.web.Resource;

public class MenuViewModel extends ViewModel {

    private static String[] MENU_TYPE_SELECTORS = new String[] {
            "tr.menue-line-Fruehstueck > td.menue-Fruehstueck",
            "tr.menue-line-Obstfruehstueck > td.menue-Obstfruehstueck",
            "tr.menue-line-Mittag > td.menue-Mittag",
            "tr.menue-line-Vesper > td.menue-Vesper"
    };

    private static String CALENDAR_WEEK_SELECTOR = "#select_woche > select > option";

    private final EssbarRepository repository;

    private final MutableLiveData<String> calendarWeek;

    private final LiveData<List<Menu>> menus;
    private final LiveData<SparseBooleanArray> menusOrderStatus;
    private final LiveData<List<KW>> calendarWeeks;
    private final MutableLiveData<Integer> numberOfChangedOrders;

    MenuViewModel( final EssbarRepository repository ) {
        this.repository = repository;
        calendarWeek = new MutableLiveData<>();
        numberOfChangedOrders = new MutableLiveData<>();
        numberOfChangedOrders.setValue( 0 );

        final LiveData<Resource<Document>> documentLiveData = Transformations.switchMap(calendarWeek, this::getMenuDocument );
        menus = Transformations.switchMap( documentLiveData, this::getMenusList );
        menusOrderStatus = Transformations.switchMap(menus, this::getCheckedMenus );
        calendarWeeks = Transformations.switchMap( documentLiveData, this::getCalendarWeekList);
    }

    private LiveData<Resource<Document>> getMenuDocument( final String calendarWeek ) {
        if(TextUtils.isEmpty( calendarWeek ) ) {
            return repository.getMenusDocument();
        } else {
            return repository.getMenusDocumentByDate( calendarWeek );
        }
    }

    private LiveData<List<Menu>> getMenusList( final Resource<Document> resource) {
        final MutableLiveData<List<Menu>> result = new MutableLiveData<>();
        if( resource != null && resource.isSuccess() ) {
            final Document menuPage = resource.getResource();
            final List<Menu> allMenuItems = new ArrayList<>();
            for(int menuTypeIndex = 0; menuTypeIndex < MENU_TYPE_SELECTORS.length; menuTypeIndex++ ) {
                final String menuTypeSelector = MENU_TYPE_SELECTORS[ menuTypeIndex ];
                final Elements menuTypeElements = menuPage.select( menuTypeSelector );
                for( int menuIndex = 0; menuIndex < menuTypeElements.size(); menuIndex++ ) {
                    final int menuItemIndex = menuTypeIndex + ( ( menuTypeIndex + 1 ) * menuIndex );
                    allMenuItems.add( menuItemIndex, new Menu( menuTypeElements.get( menuIndex ) ) );
                }
            }
            result.setValue( allMenuItems );
        } else {
            result.setValue( Collections.emptyList() );
        }
        return result;
    }

    private LiveData<SparseBooleanArray> getCheckedMenus( final List<Menu> menus ) {
        final MutableLiveData<SparseBooleanArray> result = new MutableLiveData<>();
        final SparseBooleanArray checkedMenus = new SparseBooleanArray();
        if( menus != null && !menus.isEmpty() ) {
            for( Menu menu : menus ) {
                checkedMenus.put( menu.getId(), menu.isOrdered() );
            }
        }
        result.setValue( checkedMenus );
        return result;
    }

    private LiveData<List<KW>> getCalendarWeekList( final Resource<Document> resource) {
        final MutableLiveData<List<KW>> result = new MutableLiveData<>();
        if( resource != null && resource.isSuccess() ) {
            final Document menuPage = resource.getResource();
            final List<KW> kwItems = new ArrayList<>();
            for( Element option : menuPage.select(CALENDAR_WEEK_SELECTOR) ) {
                kwItems.add( new KW( option ) );
            }
            result.setValue( kwItems );
        } else {
            result.setValue( Collections.emptyList() );
        }
        return result;
    }

    public LiveData<List<Menu>> getMenus() {
        return menus;
    }

    public LiveData<SparseBooleanArray> getMenusOrderStatus() {
        return menusOrderStatus;
    }

    public LiveData<List<KW>> getCalendarWeeks() {
        return calendarWeeks;
    }

    public LiveData<Integer> getNumberOfChangedOrders() {
        return numberOfChangedOrders;
    }

    public void loadCalendarWeek( @Nullable final String dateOfCalendarWeekInSeconds ) {
        calendarWeek.setValue( dateOfCalendarWeekInSeconds );
    }

    public void incrementNumberOfChangedOrders() {
        numberOfChangedOrders.setValue( numberOfChangedOrders.getValue() + 1 );
    }

    public void decrementNumberOfChangedOrders() {
        numberOfChangedOrders.setValue( numberOfChangedOrders.getValue() - 1 );
    }

    public void resetChangedOrders() {
        if( numberOfChangedOrders.getValue() > 0 ) {
            numberOfChangedOrders.setValue( 0 );
            loadCalendarWeek( calendarWeek.getValue() );
        }
    }
}
