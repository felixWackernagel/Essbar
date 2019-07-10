package de.wackernagel.essbar.repository;

import androidx.lifecycle.LiveData;

import org.jsoup.nodes.Document;

import java.util.List;

import de.wackernagel.essbar.room.Customer;
import de.wackernagel.essbar.room.CustomerDao;
import de.wackernagel.essbar.web.Resource;
import de.wackernagel.essbar.web.WebService;
import de.wackernagel.essbar.web.forms.ChangedMenusForm;
import okhttp3.MediaType;
import okhttp3.RequestBody;

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

    public LiveData<Resource<Document>> getMenusDocumentByDate( final String startDate, final String endDate, final String csrfToken, final String calendarWeekWithYear ) {
        return webService.postMenusStartDate( startDate, endDate, csrfToken, calendarWeekWithYear );
    }

    public LiveData<Resource<Document>> getLoginDocument( final String csrfToken, final String username, final String password ) {
        return webService.postLoginData( csrfToken, username, password );
    }

    public LiveData<Resource<Document>> getMenuConfirmationDocument(final ChangedMenusForm changedMenusForm ) {
        return webService.postChangedMenus( changedMenusForm.getStartDate(), changedMenusForm.getEndDate(), changedMenusForm.getFields() );
    }

    public LiveData<Resource<Document>> postConfirmedMenus( final String formPayload ) {
        return webService.postConfirmedMenus( RequestBody.create( MediaType.parse( "text/plain" ), formPayload ) );
    }

    public void insertCustomer( final Customer customer ) {
        executors.diskIO().execute( () -> customerDao.insertCustomer( customer ) );
    }

    public LiveData<Integer> getCustomersCount() {
        return customerDao.customersCount();
    }

    public LiveData<Resource<Document>> getHomeDocument() {
        return webService.getHome();
    }
}
