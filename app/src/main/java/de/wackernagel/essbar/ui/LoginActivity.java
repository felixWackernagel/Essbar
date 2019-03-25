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
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import dagger.android.AndroidInjection;
import de.wackernagel.essbar.EssbarPreferences;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.utils.EncryptionUtils;
import de.wackernagel.essbar.web.WebService;

/**
 * FIRST LOGIN:
 * - enter username and passwordView
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

    private EditText usernameView;
    private EditText passwordView;
    private Button loginButton;

    private KeyguardManager keyguardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameView = findViewById(R.id.usernameView);
        passwordView = findViewById(R.id.passwordView);
        loginButton = findViewById(R.id.loginButton);

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE );

        if( !keyguardManager.isKeyguardSecure() ) {
            Toast.makeText( this, R.string.no_secure_lock_setup, Toast.LENGTH_LONG ).show();
        }

        if( EssbarPreferences.getUsername( this ) != null && EssbarPreferences.getEncryptionIV( this ) != null && EssbarPreferences.getEncryptedPassword( this ) != null ) {
            doLoginWithSecureLock();
        }
    }

    private void doLoginWithSecureLock() {
        final String username = EssbarPreferences.getUsername( this );
        final String encryptedPassword = EssbarPreferences.getEncryptedPassword( this );
        final String encryptionIV = EssbarPreferences.getEncryptionIV( this );

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
                Toast.makeText( LoginActivity.this, R.string.error_occurred, Toast.LENGTH_LONG ).show();
            }
        });
    }

    public void doLoginWithCredentials(final View view ) {
        final String username = usernameView.getText().toString();
        if( TextUtils.isEmpty( username ) ) {
            return;
        }

        final String password = passwordView.getText().toString();
        if( TextUtils.isEmpty( password ) ) {
            return;
        }

        EncryptionUtils.encrypt( password, ALIAS, new EncryptionUtils.EncryptionCallback()
        {
            @Override
            public void onEncryptionSuccess(String encryptedPassword, String encryptionIV) {
                Log.i( "Essbar", "Encryption success " + encryptedPassword );
                EssbarPreferences.setEncryptedPassword(LoginActivity.this, encryptedPassword);
                EssbarPreferences.setEncryptionIV(LoginActivity.this, encryptionIV);
                EssbarPreferences.setUsername( LoginActivity.this, username );
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
                Toast.makeText( LoginActivity.this, R.string.error_occurred, Toast.LENGTH_LONG ).show();
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
                doLoginWithSecureLock();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginAtWebsite(String username, String password ) {
        webService.login( username, password ).observe(this, resource -> {
            Log.i( "Essbar", "Login at website sucess? " + resource.isSuccess() );
            if( resource.isSuccess() ) {
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        final Intent intent = new Intent( this, MainActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( intent );
        finish();
    }
}
