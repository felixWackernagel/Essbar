package de.wackernagel.essbar.repository;

import androidx.lifecycle.LiveData;

import org.jsoup.nodes.Document;

import java.util.List;

import de.wackernagel.essbar.room.Customer;
import de.wackernagel.essbar.room.CustomerDao;
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

    public EssbarRepository( final WebService webService, final CustomerDao customerDao) {
        this.webService = webService;
        this.customerDao = customerDao;
        this.executors = new AppExecutors();
    }

    public LiveData<List<Customer>> getAllCustomers() {
        return customerDao.queryAllCustomers();
    }

    public LiveData<Resource<Document>> getMenusDocumentByCalendarWeek(final CalendarWeekForm changeCalendarWeekForm ) {
        return webService.getCalendarWeekMenusPage( changeCalendarWeekForm.getStartDate(), changeCalendarWeekForm.getEndDate() );
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

    public LiveData<Integer> getCustomersCount() {
        return customerDao.customersCount();
    }

    public LiveData<Resource<Document>> getHomeDocument() {
        return webService.getHomePage();
    }
}
