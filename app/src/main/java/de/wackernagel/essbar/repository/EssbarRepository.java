package de.wackernagel.essbar.repository;

import org.jsoup.nodes.Document;

import java.util.List;

import androidx.lifecycle.LiveData;
import de.wackernagel.essbar.room.Customer;
import de.wackernagel.essbar.room.CustomerDao;
import de.wackernagel.essbar.web.Resource;
import de.wackernagel.essbar.web.WebService;

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
        return webService.requestMenusDocument();
    }

    public LiveData<Resource<Document>> getMenusDocumentByDate( final String date ) {
        return webService.requestMenuDocumentByDate( date );
    }

    public LiveData<Resource<Document>> getLoginDocument( final String username, final String password ) {
        return webService.requestLoginDocument( username, password );
    }

    public void insertCustomer( final Customer customer ) {
        executors.diskIO().execute( () -> customerDao.insertCustomer( customer ) );
    }
}