package de.wackernagel.essbar.repository;

import org.jsoup.nodes.Document;

import java.util.List;

import androidx.lifecycle.LiveData;
import de.wackernagel.essbar.room.Customer;
import de.wackernagel.essbar.room.CustomerDao;
import de.wackernagel.essbar.web.Resource;
import de.wackernagel.essbar.web.WebService;
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

    public LiveData<Resource<Document>> getMenusDocument() {
        return webService.getMenus();
    }

    public LiveData<Resource<Document>> getMenusDocumentByDate( final String date ) {
        return webService.postMenusStartDate( date );
    }

    public LiveData<Resource<Document>> getLoginDocument( final String username, final String password ) {
        return webService.postLoginData( username, password );
    }

    public LiveData<Resource<Document>> getMenuConfirmationDocument(final String formPayload ) {
        return webService.postChangedMenus( RequestBody.create( MediaType.parse( "text/plain" ), formPayload ) );
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
}
