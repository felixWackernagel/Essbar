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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.jsoup.nodes.Document;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.android.AndroidInjection;
import de.wackernagel.essbar.EssbarPreferences;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.ActivityLoginBinding;
import de.wackernagel.essbar.ui.viewModels.LoginViewModel;
import de.wackernagel.essbar.utils.ConnectivityLifecycleObserver;
import de.wackernagel.essbar.utils.EncryptionUtils;
import de.wackernagel.essbar.utils.SectionItemDecoration;
import de.wackernagel.essbar.utils.ViewUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity {

    private static final String KEYSTORE_ALIAS = "Essbar";

    private static final int REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION = 1;
    private static final int REQUEST_CODE_FOR_CREDENTIAL_DECRYPTION = 2;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    ConnectivityLifecycleObserver connectivityLifecycleObserver;

    private LoginViewModel viewModel;

    private ActivityLoginBinding binding;

    private KeyguardManager keyguardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView( this, R.layout.activity_login );
        viewModel = new ViewModelProvider( this, viewModelFactory).get( LoginViewModel.class );

        binding.usernameField.setText( viewModel.getUsername() );
        binding.passwordField.setText( viewModel.getPassword() );
        ViewUtils.addRequiredValidationOnBlur( binding.usernameContainer, viewModel::getUsername, viewModel::setUsername, R.string.username_required_error );
        ViewUtils.addRequiredValidationOnBlur( binding.passwordContainer, viewModel::getPassword, viewModel::setPassword, R.string.password_required_error );

        final CustomerListAdapter adapter = new CustomerListAdapter();
        adapter.setOnCustomerClickListener(customer -> {
            viewModel.setCustomer( customer );
            doDecryption();
        });

        binding.recyclerView.setLayoutManager( new LinearLayoutManager( this ));
        binding.recyclerView.setHasFixedSize( true );
        binding.recyclerView.setAdapter( adapter );
        binding.recyclerView.addItemDecoration( new SectionItemDecoration( this, false, new SectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection( int position ) {
                return position <= 0;
            }

            @Override
            public CharSequence getSectionHeader( int position ) {
                return getString(R.string.profile_section);
            }
        }) );
        viewModel.getAllCustomers().observe( this, adapter::submitList);

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE );

        if( !keyguardManager.isKeyguardSecure() ) {
            Snackbar.make( binding.coordinatorLayout, R.string.no_secure_lock_error, Snackbar.LENGTH_LONG ).show();
        }

        EssbarPreferences.setCookie( this, null );

        getLifecycle().addObserver( connectivityLifecycleObserver );
        connectivityLifecycleObserver.getConnectedStatus().observe( this, this::showOfflineState );
    }

    private void showOfflineState( boolean hasInternet ) {
        binding.offlineContainer.setVisibility( hasInternet ? GONE : VISIBLE );
        binding.formContainer.setVisibility( hasInternet ? VISIBLE : GONE );
    }

    private void doDecryption() {
        EncryptionUtils.decrypt( viewModel.getCustomer().getEncryptedPassword(), KEYSTORE_ALIAS, viewModel.getCustomer().getEncryptionIv(), new EncryptionUtils.DecryptionCallback() {
            @Override
            public void onDecryptionSuccess(String decryptedPassword) {
                viewModel.setUsername( viewModel.getCustomer().getNumber() );
                viewModel.setPassword( decryptedPassword );
                loginAtWebsite();
            }

            @Override
            public void onUserNotAuthenticatedForDecryption() {
                startAuthenticationActivity(REQUEST_CODE_FOR_CREDENTIAL_DECRYPTION);
            }

            @Override
            public void onDecryptionError(Exception e) {
                showError( getString( R.string.unknown_error ) );
            }
        });
    }

    public void doLoginWithCredentials(final View view ) {
        showError( null );
        if( ViewUtils.validateRequiredValue( binding.usernameContainer, viewModel::setUsername, R.string.username_required_error ) &&
            ViewUtils.validateRequiredValue( binding.passwordContainer, viewModel::setPassword, R.string.password_required_error ) ) {
            loginAtWebsite();
        }
    }

    private void doEncryption() {
        EncryptionUtils.encrypt( viewModel.getPassword(), KEYSTORE_ALIAS, new EncryptionUtils.EncryptionCallback() {
            @Override
            public void onEncryptionSuccess(String encryptedPassword, String encryptionIV) {
                viewModel.insertCustomer( encryptedPassword, encryptionIV );
                startMainActivity();
            }

            @Override
            public void onUserNotAuthenticatedForEncryption() {
                startAuthenticationActivity(REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION);
            }

            @Override
            public void onEncryptionError(Exception e) {
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
        if( resultCode == Activity.RESULT_CANCELED ) {
            if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION ) {
                startMainActivity();
            } else if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_DECRYPTION) {
                showError( getString( R.string.decryption_canceled_error ) );
            }
        }
        if( resultCode == Activity.RESULT_OK ) {
            if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_DECRYPTION) {
                doDecryption();
            } else if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION ) {
                doEncryption();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginAtWebsite() {
        setFormEnabled( false );

        viewModel.getLoginDocument().observe(this, resource -> {
            if( resource.isSuccess() ) {
                if( wasWebLoginSuccessful( resource.getResource() ) ) {
                    if( binding.saveCredentials.isChecked() ) {
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

            setFormEnabled( true );
        } );
    }

    private void setFormEnabled( boolean isEnabled ) {
        binding.usernameField.setEnabled( isEnabled );
        binding.passwordField.setEnabled( isEnabled );
        binding.saveCredentials.setEnabled( isEnabled );
        binding.loginButton.setEnabled( isEnabled );
    }

    private void showError( @Nullable final String message ) {
        if( message != null && !TextUtils.isEmpty( message ) ) {
            Snackbar snackbar = Snackbar.make( binding.coordinatorLayout, message, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor( getColor( R.color.colorErrorLight ) );
            TextView textView = snackbarView.findViewById( com.google.android.material.R.id.snackbar_text);
            textView.setLineSpacing(0f ,1.2f);
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
        final Intent intent = new Intent( this, MenuActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( intent );
        finish();
    }
}
