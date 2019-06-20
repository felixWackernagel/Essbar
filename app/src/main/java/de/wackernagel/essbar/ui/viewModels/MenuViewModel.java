package de.wackernagel.essbar.ui.viewModels;

import android.text.TextUtils;
import android.util.SparseBooleanArray;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import de.wackernagel.essbar.repository.EssbarRepository;
import de.wackernagel.essbar.ui.pojos.CalendarWeek;
import de.wackernagel.essbar.ui.pojos.ChangedMenu;
import de.wackernagel.essbar.ui.pojos.Menu;
import de.wackernagel.essbar.utils.Event;
import de.wackernagel.essbar.web.DocumentParser;
import de.wackernagel.essbar.web.Resource;

public class MenuViewModel extends ViewModel {

    private final EssbarRepository repository;

    private final MutableLiveData<String> calendarWeek;

    private final LiveData<Resource<Document>> menusDocument;
    private final LiveData<List<Menu>> menus;
    private final LiveData<SparseBooleanArray> menusOrderStatus;
    private final LiveData<List<CalendarWeek>> calendarWeeks;
    private final MutableLiveData<Integer> numberOfChangedOrders;

    private final MutableLiveData<String> menusToChange;
    private final LiveData<Resource<Document>> menuConfirmationDocument;
    private final LiveData<List<ChangedMenu>> changedMenusToConfirm;

    private final MutableLiveData<String> confirmedMenus;
    private final LiveData<Event<Boolean>> successfulOrder;

    MenuViewModel( final EssbarRepository repository ) {
        this.repository = repository;

        // menus
        calendarWeek = new MutableLiveData<>();
        numberOfChangedOrders = new MutableLiveData<>();
        numberOfChangedOrders.setValue( 0 );
        menusDocument = Transformations.switchMap(calendarWeek, this::getMenuDocument );
        menus = Transformations.switchMap(menusDocument, this::getMenusList );
        menusOrderStatus = Transformations.switchMap(menus, this::getCheckedMenus );
        calendarWeeks = Transformations.switchMap(menusDocument, this::getCalendarWeekList);

        // menus > order confirmation
        menusToChange = new MutableLiveData<>();
        menuConfirmationDocument = Transformations.switchMap( menusToChange, repository::getMenuConfirmationDocument );
        changedMenusToConfirm = Transformations.switchMap( menuConfirmationDocument, this::getChangedMenuList );

        // order confirmation > thank you
        confirmedMenus = new MutableLiveData<>();
        final LiveData<Resource<Document>> thankYouDocument = Transformations.switchMap( confirmedMenus, repository::postConfirmedMenus );
        successfulOrder = Transformations.switchMap( thankYouDocument, this::getOrderStatusFromThankYouDocument );
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
            result.setValue( DocumentParser.getMenuList( resource.getResource() ) );
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

    private LiveData<List<CalendarWeek>> getCalendarWeekList(final Resource<Document> resource) {
        final MutableLiveData<List<CalendarWeek>> result = new MutableLiveData<>();
        if( resource != null && resource.isSuccess() ) {
            result.setValue( DocumentParser.getCalendarWeekList( resource.getResource() ) );
        } else {
            result.setValue( Collections.emptyList() );
        }
        return result;
    }

    private LiveData<List<ChangedMenu>> getChangedMenuList( final Resource<Document> resource ) {
        final MutableLiveData<List<ChangedMenu>> result = new MutableLiveData<>();
        if( resource != null && resource.isSuccess() ) {
            result.setValue( DocumentParser.getChangedMenuList( resource.getResource() ) );
        } else {
            result.setValue( Collections.emptyList() );
        }
        return result;
    }

    private LiveData<Event<Boolean>> getOrderStatusFromThankYouDocument( final Resource<Document> resource ) {
        final MutableLiveData<Event<Boolean>> result = new MutableLiveData<>();
        if( resource != null && resource.isSuccess() ) {
            result.setValue( new Event<>( DocumentParser.isOrderSuccessful( resource.getResource() ) ) );
        } else {
            result.setValue( new Event<>( Boolean.FALSE ) );
        }
        return result;
    }

    public LiveData<List<Menu>> getMenus() {
        return menus;
    }

    public LiveData<SparseBooleanArray> getMenusOrderStatus() {
        return menusOrderStatus;
    }

    public LiveData<List<CalendarWeek>> getCalendarWeeks() {
        return calendarWeeks;
    }

    public LiveData<Integer> getNumberOfChangedOrders() {
        return numberOfChangedOrders;
    }

    public LiveData<List<ChangedMenu>> getChangedMenusToConfirm() {
        return changedMenusToConfirm;
    }

    public LiveData<Event<Boolean>> getSuccessfulOrder() {
        return successfulOrder;
    }

    public int getSelectedCalendarWeek() {
        final String calendarWeekSeconds = calendarWeek.getValue();
        Date calendarWeekDate;
        if( calendarWeekSeconds == null ) {
            calendarWeekDate = calculateCurrentCalendarWeek();
        } else {
            calendarWeekDate = new Date( Long.valueOf( calendarWeekSeconds ) * 1000L );
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime( calendarWeekDate );
        return calendar.get( Calendar.WEEK_OF_YEAR );
    }

    public void loadCurrentCalendarWeek() {
        final String dateInSeconds = String.valueOf( calculateCurrentCalendarWeek().getTime() / 1000 );
        loadCalendarWeek( dateInSeconds );
    }

    private Date calculateCurrentCalendarWeek() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        return calendar.getTime();
    }

    public void loadCalendarWeek( @Nullable final String startDateOfCalendarWeekInSeconds ) {
        calendarWeek.setValue( startDateOfCalendarWeekInSeconds );
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

    public void loadChangedMenusToConfirm() {
        final StringBuilder formPayloadBuilder = new StringBuilder();
        final List<Menu> allMenus = menus.getValue();
        if( allMenus != null && !allMenus.isEmpty() ) {
            formPayloadBuilder.append("m_alt=");
            formPayloadBuilder.append("&starttag=").append( menusDocument.getValue().getResource().selectFirst("input[name=starttag]").attr("value"));
            formPayloadBuilder.append("&endtag=").append( menusDocument.getValue().getResource().selectFirst("input[name=endtag]").attr("value"));
            for( Menu menu : allMenus ) {
                if( menu.getInputName() == null ) {
                    continue;
                }
                formPayloadBuilder.append("&").append( menu.getInputName() ).append("=").append( 0 );
                // add second input only if menu is ordered or was already ordered
                if( menusOrderStatus.getValue().get( menu.getId(), menu.isOrdered() ) ) {
                    formPayloadBuilder.append("&").append( menu.getInputName() ).append("=").append( 1 );
                }
            }
            formPayloadBuilder.append("&btn_bestellen=Weiter");
        }
        menusToChange.setValue( formPayloadBuilder.toString() );
    }

    public void postChangedAndConfirmedMenus() {
        final Resource<Document> menuConfirmationDocument = this.menuConfirmationDocument.getValue();
        if( menuConfirmationDocument != null && menuConfirmationDocument.isSuccess() ) {
            final Document page = menuConfirmationDocument.getResource();
            final StringBuilder sb = new StringBuilder();
            for( Element input : page.select( "form .block input" ) ) {
                // add each input name and value
                if( sb.length() > 0 ) {
                    sb.append( "&" );
                }
                sb.append( input.attr("name") );
                sb.append( "=" );
                sb.append( input.attr("value") );
            }
            confirmedMenus.setValue( sb.toString() );
        }
    }
}
