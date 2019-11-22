package de.wackernagel.essbar.ui.viewModels;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import de.wackernagel.essbar.repository.EssbarRepository;
import de.wackernagel.essbar.room.Customer;
import de.wackernagel.essbar.web.DocumentParser;
import de.wackernagel.essbar.web.InMemoryCookieJar;
import de.wackernagel.essbar.web.Resource;
import de.wackernagel.essbar.web.forms.LoginForm;

public class LoginViewModel extends ViewModel {

    private final EssbarRepository repository;

    private String username = "";
    private String password = "";
    private String customerName = null;
    private Customer customer;

    @Inject
    LoginViewModel(EssbarRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<Document>> getLoginDocument() {
        final HashMap<String, String> formFields = new HashMap<>(3 );
        formFields.put( "csrfmiddlewaretoken", InMemoryCookieJar.get().getCSRFToken() );
        formFields.put( "login", username );
        formFields.put( "password", password );
        return repository.getLoginDocument( new LoginForm( formFields ) );
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

    public void deleteCustomer( final Customer customer ) {
        repository.deleteCustomer( customer );
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

    public LiveData<Boolean> hasCustomers() {
        return Transformations.map( repository.hasCustomers(), (hasCustomers) -> hasCustomers > 0 ? Boolean.TRUE : Boolean.FALSE);
    }

    public LiveData<Boolean> isWebsiteReady() {
        InMemoryCookieJar.get().clear();
        return Transformations.switchMap( repository.getHomeDocument(), this::checkToken);
    }

    private LiveData<Boolean> checkToken(final Resource<Document> resource ) {
        final MutableLiveData<Boolean> result = new MutableLiveData<>();
        if( resource != null && resource.isStatusOk() && resource.isAvailable() ) {
            final String token = InMemoryCookieJar.get().getCSRFToken();
            result.setValue( !TextUtils.isEmpty( token ) );
        } else {
            result.setValue( Boolean.FALSE );
        }
        return result;
    }
}
