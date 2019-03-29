package de.wackernagel.essbar.ui;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import dagger.android.AndroidInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.utils.EncryptionUtils;
import de.wackernagel.essbar.utils.NetworkUtils;
import de.wackernagel.essbar.web.WebService;

/**
 * FIRST LOGIN:
 * - enter username and passwordField
 * - authenticate to android to store password encrypted
 *
 * LOGIN AFTER FIRST SUCCESS
 * - authenticate to android to get password decrypted
 */
public class LoginActivity extends AppCompatActivity {

    private static final String ALIAS = "Essbar";

    @Inject
    WebService webService;

    private final int REQUEST_CODE_FOR_CREDENTIAL_LOGIN = 1;
    private final int REQUEST_CODE_FOR_SECURE_LOCK_LOGIN = 2;

    private CoordinatorLayout coordinatorLayout;
    private TextView offlineContainer;
    private LinearLayout formContainer;
    private TextInputLayout usernameContainer;
    private TextInputEditText usernameField;
    private TextInputLayout passwordContainer;
    private TextInputEditText passwordField;
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

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE );

        if( !keyguardManager.isKeyguardSecure() ) {
            Snackbar.make( coordinatorLayout, R.string.no_secure_lock_error, Snackbar.LENGTH_LONG ).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showOfflineState( NetworkUtils.hasNetworkConnection( this ) );
    }

    private void showOfflineState(boolean hasInternet ) {
        offlineContainer.setVisibility( hasInternet ? View.GONE : View.VISIBLE );
        formContainer.setVisibility( hasInternet ? View.VISIBLE : View.GONE );
    }

    // triggered by click on customer
    private void doLoginWithSecureLock( final String username, final String encryptedPassword, final String encryptionIV ) {
        EncryptionUtils.decrypt(encryptedPassword, ALIAS, encryptionIV, new EncryptionUtils.DecryptionCallback()
        {
            @Override
            public void onDecryptionSuccess(String decryptedPassword) {
                Log.i( "Essbar", "Decryption success " + decryptedPassword );
                loginAtWebsite( username, decryptedPassword );
            }

            @Override
            public void onUserNotAuthenticatedForDecryption() {
                Log.i( "Essbar", "User not authenticated during decryption." );
                startAuthenticationActivity(REQUEST_CODE_FOR_SECURE_LOCK_LOGIN);
            }

            @Override
            public void onDecryptionError(Exception e) {
                Log.e( "Essbar", "Error during decryption.", e );
                Toast.makeText( LoginActivity.this, R.string.unknown_error, Toast.LENGTH_LONG ).show();
            }
        });
    }

    public void doLoginWithCredentials(final View view ) {
        final String username = usernameField.getText().toString();
        if( TextUtils.isEmpty( username ) ) {
            usernameContainer.setError( getString( R.string.username_required_error ) );
            return;
        } else {
            usernameContainer.setError( null );
        }

        final String password = passwordField.getText().toString();
        if( TextUtils.isEmpty( password ) ) {
            passwordContainer.setError( getString( R.string.password_required_error ) );
            return;
        } else {
            passwordContainer.setError( null );
        }

        EncryptionUtils.encrypt( password, ALIAS, new EncryptionUtils.EncryptionCallback()
        {
            @Override
            public void onEncryptionSuccess(String encryptedPassword, String encryptionIV) {
                Log.i( "Essbar", "Encryption success " + encryptedPassword );
                // TODO insert customer
                loginAtWebsite( username, password );
            }

            @Override
            public void onUserNotAuthenticatedForEncryption() {
                Log.i( "Essbar", "User not authenticated during encryption." );
                startAuthenticationActivity(REQUEST_CODE_FOR_CREDENTIAL_LOGIN);
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
        if( resultCode == Activity.RESULT_OK ) {
            if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_LOGIN) {
                doLoginWithCredentials( loginButton );
            } else if( requestCode == REQUEST_CODE_FOR_SECURE_LOCK_LOGIN) {
                // TODO handle result
                doLoginWithSecureLock( "", "", "" );
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginAtWebsite(String username, String password ) {
        webService.login( username, password ).observe(this, resource -> {
            Log.i( "Essbar", "Login at website sucess? " + resource.isSuccess() );
            if( resource.isSuccess() ) {
                if( wasWebLoginSuccessful( resource.getResource() ) ) {
                    startMainActivity();
                } else {
                    Snackbar.make( coordinatorLayout, getString( R.string.username_password_error), Snackbar.LENGTH_LONG ).show();
                }
            } else {
                Snackbar.make( coordinatorLayout, resource.getError().getMessage(), Snackbar.LENGTH_LONG ).show();
            }
        });
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
