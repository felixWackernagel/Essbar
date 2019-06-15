package de.wackernagel.essbar.ui.viewModels;

import org.jsoup.nodes.Document;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.wackernagel.essbar.repository.EssbarRepository;
import de.wackernagel.essbar.room.Customer;
import de.wackernagel.essbar.web.DocumentParser;
import de.wackernagel.essbar.web.Resource;

public class LoginViewModel extends ViewModel {

    private  final EssbarRepository repository;

    private String username = "";
    private String password = "";
    private String customerName = null;
    private Customer customer;

    LoginViewModel(EssbarRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<Document>> getLoginDocument() {
        return repository.getLoginDocument( username, password );
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void insertCustomer( String encryptedPassword, String encryptionIV ) {
        repository.insertCustomer( new Customer( username, encryptionIV, encryptedPassword, customerName ) );
    }

    /**
     * Searches the username inside the document and
     * save it inside the LoginViewModel.
     *
     * @param document which contain the logged in username
     */
    public void findCustomerName( final Document document ) {
        customerName = DocumentParser.getLoginUsername( document );
    }

    public LiveData<List<Customer>> getAllCustomers() {
        return repository.getAllCustomers();
    }

    public LiveData<Integer> getCustomersCount() {
        return repository.getCustomersCount();
    }
}
