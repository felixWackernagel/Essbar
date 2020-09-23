package de.wackernagel.essbar.repository;

import androidx.lifecycle.LiveData;

import org.jsoup.nodes.Document;

import java.util.List;

import de.wackernagel.essbar.room.Customer;
import de.wackernagel.essbar.room.CustomerDao;
import de.wackernagel.essbar.room.Meal;
import de.wackernagel.essbar.room.MealDao;
import de.wackernagel.essbar.ui.pojos.MealListItem;
import de.wackernagel.essbar.ui.pojos.Type;
import de.wackernagel.essbar.web.Resource;
import de.wackernagel.essbar.web.WebService;
import de.wackernagel.essbar.web.forms.CalendarWeekForm;
import de.wackernagel.essbar.web.forms.ChangedMenusForm;
import de.wackernagel.essbar.web.forms.ConfirmedMenusForm;
import de.wackernagel.essbar.web.forms.LoginForm;

public class EssbarRepository {

    private final AppExecutors executors;
    private final WebService webService;
    private final CustomerDao customerDao;
    private final MealDao mealDao;

    public EssbarRepository( final WebService webService, final CustomerDao customerDao, final MealDao mealDao) {
        this.webService = webService;
        this.customerDao = customerDao;
        this.mealDao = mealDao;
        this.executors = new AppExecutors();
    }

    public LiveData<List<Customer>> getAllCustomers() {
        return customerDao.queryAllCustomers();
    }

    public LiveData<Resource<Document>> getMenusDocumentByCalendarWeek(final CalendarWeekForm changeCalendarWeekForm ) {
        return webService.getCalendarWeekMenusPage( changeCalendarWeekForm.getSecret(), changeCalendarWeekForm.getStartDate(), changeCalendarWeekForm.getEndDate() );
    }

    public LiveData<Resource<Document>> getLoginDocument( final LoginForm loginForm ) {
        return webService.postLoginData( loginForm.getFields() );
    }

    public LiveData<Resource<Document>> getMenuConfirmationDocument(final ChangedMenusForm changedMenusForm ) {
        return webService.postChangedMenusData( changedMenusForm.getReferer(), changedMenusForm.getStartDate(), changedMenusForm.getEndDate(), changedMenusForm.getFields() );
    }

    public LiveData<Resource<Document>> postConfirmedMenus( final ConfirmedMenusForm confirmedMenusForm ) {
        return webService.postChangedAndConfirmedMenusData( confirmedMenusForm.getFields() );
    }

    public void insertCustomer( final Customer customer ) {
        executors.diskIO().execute( () -> customerDao.insertCustomer( customer ) );
    }

    public void upsertMeal(final Meal meal ) {
        executors.diskIO().execute( () -> {
            final Meal existingMeal = mealDao.queryMeal( meal.getType(), meal.getDate() );
            if( existingMeal == null ) {
                mealDao.insertMeal( meal );
            } else {
                meal.setId( existingMeal.getId() );
                mealDao.updateMeal( meal );
            }
        } );
    }

    public void deleteCustomer( final Customer customer ) {
        executors.diskIO().execute( () -> customerDao.deleteCustomer( customer ) );
    }

    public LiveData<Integer> hasCustomers() {
        return customerDao.hasCustomers();
    }

    public LiveData<Resource<Document>> getHomeDocument() {
        return webService.getHomePage();
    }

    public LiveData<List<MealListItem>> getMealsOfTypeFromWeekOfYear( final Type type, final int weekOfYear, final int year ) {
        // sqlite starts weekOfYear with 0 but java with 1
        final String sqliteWeekOfYear = "" + ( weekOfYear - 1 );
        return mealDao.queryMealsOfTypeFromWeekOfYear( type, sqliteWeekOfYear, "" + year );
    }
}
