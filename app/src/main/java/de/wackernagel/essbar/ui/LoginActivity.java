package de.wackernagel.essbar.ui;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jsoup.nodes.Document;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.AndroidInjection;
import de.wackernagel.essbar.EssbarPreferences;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.ui.viewModels.LoginViewModel;
import de.wackernagel.essbar.utils.EncryptionUtils;
import de.wackernagel.essbar.utils.NetworkUtils;
import de.wackernagel.essbar.utils.SectionItemDecoration;
import de.wackernagel.essbar.utils.ViewUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity {

    private static final String ALIAS = "Essbar";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private LoginViewModel viewModel;

    private final int REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION = 1;
    private final int REQUEST_CODE_FOR_SECURE_LOCK_LOGIN = 2;

    private CoordinatorLayout coordinatorLayout;
    private TextView offlineContainer;
    private LinearLayout formContainer;
    private TextInputLayout usernameContainer;
    private TextInputEditText usernameField;
    private TextInputLayout passwordContainer;
    private TextInputEditText passwordField;
    private CheckBox saveCredentials;
    private Button loginButton;

    private KeyguardManager keyguardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        offlineContainer = findViewById(R.id.offlineContainer);
        formContainer = findViewById(R.id.formContainer);
        usernameContainer = findViewById(R.id.usernameContainer);
        usernameField = findViewById(R.id.usernameField);
        passwordContainer = findViewById(R.id.passwordContainer);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        saveCredentials = findViewById(R.id.saveCredentials);

        viewModel = new ViewModelProvider( this, viewModelFactory).get( LoginViewModel.class );
        usernameField.setText( viewModel.getUsername() );
        usernameField.setOnFocusChangeListener((v, hasFocus) -> {
            if( !hasFocus ) {
                final String oldValue = viewModel.getUsername();
                final String newValue = ViewUtils.getString( usernameField );
                if( !TextUtils.equals( oldValue, newValue ) ) {
                    if( TextUtils.isEmpty( newValue ) ) {
                        usernameContainer.setError( getString( R.string.username_required_error ) );
                    } else {
                        usernameContainer.setError( null );
                        viewModel.setUsername( newValue );
                    }
                }
            }
        });
        passwordField.setText( viewModel.getPassword() );
        passwordField.setOnFocusChangeListener((v, hasFocus) -> {
            if( !hasFocus ) {
                final String oldValue = viewModel.getPassword();
                final String newValue = ViewUtils.getString( passwordField );
                if( !TextUtils.equals( oldValue, newValue ) ) {
                    if( TextUtils.isEmpty( newValue ) ) {
                        passwordContainer.setError( getString( R.string.password_required_error ) );
                    } else {
                        passwordContainer.setError( null );
                        viewModel.setPassword( newValue );
                    }
                }
            }
        });

        final CustomerListAdapter adapter = new CustomerListAdapter();
        adapter.setOnCustomerClickListener(customer -> {
            viewModel.setCustomer( customer );
            doDecryption();
        });
        final RecyclerView recyclerView = findViewById( R.id.recyclerView );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ));
        recyclerView.setHasFixedSize( true );
        recyclerView.setAdapter( adapter );
        recyclerView.addItemDecoration( new SectionItemDecoration( this, false, new SectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection( int position ) {
                return position <= 0;
            }

            @Override
            public CharSequence getSectionHeader( int position ) {
                return getString(R.string.profile_section);
            }
        }) );
        viewModel.getAllCustomers().observe( this, (list) -> {
            Log.e("Essbar", "Customer result " + (list != null ? list.size() : "null")  );
            adapter.submitList(list);
        } );

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE );

        if( !keyguardManager.isKeyguardSecure() ) {
            Snackbar.make( coordinatorLayout, R.string.no_secure_lock_error, Snackbar.LENGTH_LONG ).show();
        }

        EssbarPreferences.setCookie( this, null );
    }

    @Override
    protected void onResume() {
        super.onResume();
        showOfflineState( NetworkUtils.hasNetworkConnection( this ) );
    }

    private void showOfflineState( boolean hasInternet ) {
        offlineContainer.setVisibility( hasInternet ? GONE : VISIBLE );
        formContainer.setVisibility( hasInternet ? VISIBLE : GONE );
    }

    private void doDecryption() {
        EncryptionUtils.decrypt( viewModel.getCustomer().getEncryptedPassword(), ALIAS, viewModel.getCustomer().getEncryptionIv(), new EncryptionUtils.DecryptionCallback()
        {
            @Override
            public void onDecryptionSuccess(String decryptedPassword) {
                Log.i( "Essbar", "Decryption success " + decryptedPassword );
                viewModel.setUsername( viewModel.getCustomer().getNumber() );
                viewModel.setPassword( decryptedPassword );
                loginAtWebsite();
            }

            @Override
            public void onUserNotAuthenticatedForDecryption() {
                Log.i( "Essbar", "User not authenticated during decryption." );
                startAuthenticationActivity(REQUEST_CODE_FOR_SECURE_LOCK_LOGIN);
            }

            @Override
            public void onDecryptionError(Exception e) {
                Log.e( "Essbar", "Error during decryption.", e );
                showError( getString( R.string.unknown_error ) );
            }
        });
    }

    public void doLoginWithCredentials(final View view ) {
        showError( null );

        final String username = ViewUtils.getString( usernameField );
        if( TextUtils.isEmpty( username ) ) {
            usernameContainer.setError( getString( R.string.username_required_error ) );
            return;
        } else {
            usernameContainer.setError( null );
        }
        viewModel.setUsername( username );

        final String password = ViewUtils.getString( passwordField );
        if( TextUtils.isEmpty( password ) ) {
            passwordContainer.setError( getString( R.string.password_required_error ) );
            return;
        } else {
            passwordContainer.setError( null );
        }
        viewModel.setPassword( password );

        loginAtWebsite();
    }

    private void doEncryption() {
        EncryptionUtils.encrypt( viewModel.getPassword(), ALIAS, new EncryptionUtils.EncryptionCallback()
        {
            @Override
            public void onEncryptionSuccess(String encryptedPassword, String encryptionIV) {
                Log.i( "Essbar", "Encryption success " + encryptedPassword );
                viewModel.insertCustomer( encryptedPassword, encryptionIV );
                startMainActivity();
            }

            @Override
            public void onUserNotAuthenticatedForEncryption() {
                Log.i( "Essbar", "User not authenticated during encryption." );
                startAuthenticationActivity(REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION);
            }

            @Override
            public void onEncryptionError(Exception e) {
                Log.e( "Essbar", "Error during encryption.", e );
                Toast.makeText( LoginActivity.this, R.string.unknown_error, Toast.LENGTH_LONG ).show();
            }
        });
    }

    private void startAuthenticationActivity( final int requestCode ) {
        final Intent intent = keyguardManager.createConfirmDeviceCredentialIntent( null, null );
        if( intent != null ) {
            startActivityForResult( intent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i("Essbar", "requestCode=" + requestCode + ", resultCode=" + resultCode );
        if( resultCode == Activity.RESULT_CANCELED ) {
            if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION ) {
                startMainActivity();
            } else if( requestCode == REQUEST_CODE_FOR_SECURE_LOCK_LOGIN) {
                showError( getString( R.string.decryption_canceled_error ) );
            }
        }
        if( resultCode == Activity.RESULT_OK ) {
            if( requestCode == REQUEST_CODE_FOR_SECURE_LOCK_LOGIN) {
                doDecryption();
            } else if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION ) {
                doEncryption();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginAtWebsite() {
        usernameField.setEnabled( false );
        passwordField.setEnabled( false );
        saveCredentials.setEnabled( false );
        loginButton.setEnabled( false );

        viewModel.getLoginDocument().observe(this, resource -> {
            if( resource.isSuccess() ) {
                if( wasWebLoginSuccessful( resource.getResource() ) ) {
                    if( saveCredentials.isChecked() ) {
                        viewModel.findCustomerName( resource.getResource() );
                        doEncryption();
                    } else {
                        startMainActivity();
                    }
                } else {
                    showError( getString( R.string.username_password_error) );
                }
            } else {
                final String message = ( resource.getError() != null ? resource.getError().getMessage() : getString( R.string.unknown_error ) );
                Log.e( "Essbar", message );
                showError( message );
            }

            usernameField.setEnabled( true );
            passwordField.setEnabled( true );
            saveCredentials.setEnabled( true );
            loginButton.setEnabled( true );
        } );
    }

    private void showError( @Nullable final String message ) {
        if( message != null && !TextUtils.isEmpty( message ) ) {
            Snackbar snackbar = Snackbar.make( coordinatorLayout, message, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor( getColor( R.color.colorErrorLight ) );
            TextView textView = snackbarView.findViewById( com.google.android.material.R.id.snackbar_text);
            textView.setLineSpacing(0f ,1.4f);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setCompoundDrawablesWithIntrinsicBounds( AppCompatResources.getDrawable( this, R.drawable.ic_error_outline_black_24dp ), null, null, null );
            textView.setCompoundDrawableTintList( AppCompatResources.getColorStateList( this, R.color.colorError ) );
            textView.setCompoundDrawablePadding( getResources().getDimensionPixelSize( R.dimen.view_space ) );
            textView.setTextColor( ContextCompat.getColor( this, R.color.colorError ) );
            snackbar.show();
        }
    }

    private boolean wasWebLoginSuccessful( final Document document ) {
        return document != null && document.select( "#login-info .fehler" ).size() == 0;
    }

    private void startMainActivity() {
        final Intent intent = new Intent( this, MainActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( intent );
        finish();
    }
}
