package de.wackernagel.essbar.ui.viewModels;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.wackernagel.essbar.repository.EssbarRepository;
import de.wackernagel.essbar.ui.pojos.CalendarWeek;
import de.wackernagel.essbar.ui.pojos.ChangedMenu;
import de.wackernagel.essbar.ui.pojos.Menu;
import de.wackernagel.essbar.ui.pojos.Weekday;
import de.wackernagel.essbar.utils.Event;
import de.wackernagel.essbar.web.DocumentParser;
import de.wackernagel.essbar.web.InMemoryCookieJar;
import de.wackernagel.essbar.web.Resource;
import de.wackernagel.essbar.web.forms.ChangedMenusForm;

public class MenuViewModel extends ViewModel {

    private final EssbarRepository repository;

    private final MutableLiveData<String> calendarWeek;

    private final LiveData<Resource<Document>> menusDocument;
    private final LiveData<List<Menu>> menus;
    private final LiveData<SparseBooleanArray> menusOrderStatus;
    private final LiveData<List<CalendarWeek>> calendarWeeks;
    private final MutableLiveData<Integer> numberOfChangedOrders;

    private final MutableLiveData<ChangedMenusForm> menusToChange;
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
        menusOrderStatus = Transformations.switchMap(menus, this::transformMenusToOrderStatus);
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

    /**
     * @param calendarWeekWithYear (yyyy,cw)
     */
    private LiveData<Resource<Document>> getMenuDocument( final String calendarWeekWithYear ) {
        final String[] dateParts = calendarWeekWithYear.substring( 1, calendarWeekWithYear.length() - 1 ).split(",");
        int year = Integer.valueOf( dateParts[0] );
        int weekOfYear = Integer.valueOf( dateParts[1] );
        return repository.getMenusDocumentByDate( getDay( Calendar.MONDAY, weekOfYear, year ), getDay( Calendar.SUNDAY, weekOfYear, year ) ,InMemoryCookieJar.get().getCSRFToken(), calendarWeekWithYear );
    }

    /**
     * @param dayOfWeek like MONDAY
     * @param weekOfYear like 26
     * @param year like 2019
     * @return 2019-07-14
     */
    private String getDay( int dayOfWeek, int weekOfYear, int year ) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, year );
        // FIXME sunday is first day of week but we need monday so on sunday we increment the week of day by 1 but this creates a error on new year
        calendar.set( Calendar.WEEK_OF_YEAR, (dayOfWeek == Calendar.SUNDAY) ? (weekOfYear + 1) : weekOfYear );
        calendar.set( Calendar.DAY_OF_WEEK, dayOfWeek );
        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        Log.e( "MenuViewModel", "Get Day from [" + dayOfWeek + ", " + weekOfYear + ", " + year + "] to " + new SimpleDateFormat( "dd.MM.yyyy" ).format( calendar.getTime() ) );
        return calendar.get(Calendar.YEAR) + "-" + twoDigitFormat( calendar.get( Calendar.MONTH ) + 1 ) + "-" + twoDigitFormat( calendar.get( Calendar.DATE ) );
    }

    private String twoDigitFormat( int digit ) {
        if( digit <= 9 ) {
            return "0" + digit;
        }
        return String.valueOf( digit );
    }

    private LiveData<List<Menu>> getMenusList( final Resource<Document> resource) {
        final MutableLiveData<List<Menu>> result = new MutableLiveData<>();
        if( resource != null && resource.isAvailable() ) {
            final List<Menu> menuList = DocumentParser.getMenuList( resource.getResource() );
            filterEqualPausedMenus( menuList );
            result.setValue( menuList );
        } else {
            result.setValue( Collections.emptyList() );
        }
        return result;
    }

    private void filterEqualPausedMenus(final List<Menu> menuList ) {
        if( !menuList.isEmpty() ) {
            final Iterator<Menu> iterator = menuList.iterator();
            Weekday toExclude = null;
            while( iterator.hasNext() ) {
                final Menu menu = iterator.next();
                if( menu.getWeekday().equals( toExclude )  ) {
                    iterator.remove();
                } else if( menu.isPaused() ) {
                    toExclude = menu.getWeekday();
                }
            }
        }
    }

    private LiveData<SparseBooleanArray> transformMenusToOrderStatus( final List<Menu> menus ) {
        final MutableLiveData<SparseBooleanArray> result = new MutableLiveData<>();
        final SparseBooleanArray menuOrderStatus = new SparseBooleanArray();
        if( menus != null && !menus.isEmpty() ) {
            for( Menu menu : menus ) {
                menuOrderStatus.put( menu.getId(), menu.isOrdered() );
            }
        }
        result.setValue( menuOrderStatus );
        return result;
    }

    private LiveData<List<CalendarWeek>> getCalendarWeekList(final Resource<Document> resource) {
        final MutableLiveData<List<CalendarWeek>> result = new MutableLiveData<>();
        if( resource != null && resource.isStatusOk() && resource.isAvailable() ) {
            result.setValue( DocumentParser.getCalendarWeekList( resource.getResource() ) );
        } else {
            result.setValue( Collections.emptyList() );
        }
        return result;
    }

    private LiveData<List<ChangedMenu>> getChangedMenuList( final Resource<Document> resource ) {
        final MutableLiveData<List<ChangedMenu>> result = new MutableLiveData<>();
        if( resource != null && resource.isStatusOk() && resource.isAvailable() ) {
            result.setValue( DocumentParser.getChangedMenuList( resource.getResource() ) );
        } else {
            result.setValue( Collections.emptyList() );
        }
        return result;
    }

    private LiveData<Event<Boolean>> getOrderStatusFromThankYouDocument( final Resource<Document> resource ) {
        final MutableLiveData<Event<Boolean>> result = new MutableLiveData<>();
        if( resource != null && resource.isAvailable() ) {
            result.setValue( new Event<>( DocumentParser.isOrderSuccessful( resource.getResource() ) ) );
        } else {
            result.setValue( new Event<>( Boolean.FALSE ) );
        }
        return result;
    }

    public LiveData<List<Menu>> getMenus() {
        return menus;
    }

    /**
     * @return A map with menu id -> menu order status (true or false)
     */
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

    /**
     * @return (yyyy,cw) like (2019,26)
     */
    @NonNull
    public LiveData<String> getCalendarWeek() {
        return calendarWeek;
    }

    public int calculateCalendarWeek( @Nullable final String calendarWeekWithYear ) {
        if( calendarWeekWithYear == null ) {
            return calculateCurrentCalendarWeek();
        } else {
            // (2019,27) = length 9
            final String[] dateParts = calendarWeekWithYear.substring( 1, calendarWeekWithYear.length() - 1 ).split(",");
            return Integer.valueOf( dateParts[1] );
        }
    }

    public int getSelectedCalendarWeek() {
        return calculateCalendarWeek( calendarWeek.getValue() );
    }

    public void loadCurrentCalendarWeek() {
        loadCalendarWeek( "(" + Calendar.getInstance().get( Calendar.YEAR ) + "," + calculateCurrentCalendarWeek() + ")" );
    }

    /**
     * @return calendar week like 26
     */
    private int calculateCurrentCalendarWeek() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        return calendar.get( Calendar.WEEK_OF_YEAR );
    }

    /**
     * @param calendarWeekWithYear (yyyy,cw) like (2019,26)
     */
    public void loadCalendarWeek( @Nullable final String calendarWeekWithYear ) {
        calendarWeek.setValue( calendarWeekWithYear );
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
        final HashMap<String, String> formFields = new HashMap<>();
        formFields.put( "csrfmiddlewaretoken", InMemoryCookieJar.get().getCSRFToken() );

        final List<Menu> allMenus = menus.getValue();
        if( allMenus != null && !allMenus.isEmpty() ) {
            for( Menu menu : allMenus ) {
                if( TextUtils.isEmpty( menu.getInputName() ) || TextUtils.isEmpty( menu.getInputValue() ) ) {
                    Log.e("MenuViewModel", "(1) Skip menu for changed menu request. " + menu.toString() );
                    continue;
                }
                if( menusOrderStatus.getValue().get( menu.getId(), menu.isOrdered() ) ) {
                    formFields.put( menu.getInputName(), menu.getInputValue() );
                } else {
                    Log.e("MenuViewModel", "(2) Skip menu for changed menu request. " + menu.toString() );
                }
            }
        }

        formFields.put( "change_order", "Weiter" );

        final String calendarWeekWithYear = calendarWeek.getValue();
        final String[] dateParts = calendarWeekWithYear.substring( 1, calendarWeekWithYear.length() - 1 ).split(",");
        int year = Integer.valueOf( dateParts[0] );
        int weekOfYear = Integer.valueOf( dateParts[1] );
        menusToChange.setValue( new ChangedMenusForm( getDay( Calendar.MONDAY, weekOfYear, year), getDay( Calendar.SUNDAY, weekOfYear, year), formFields ) );
    }

    public void postChangedAndConfirmedMenus() {
        final Resource<Document> menuConfirmationDocument = this.menuConfirmationDocument.getValue();
        if( menuConfirmationDocument != null && menuConfirmationDocument.isAvailable() ) {
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
