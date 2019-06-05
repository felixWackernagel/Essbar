package de.wackernagel.essbar.ui;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.jsoup.nodes.Document;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import dagger.android.support.AndroidSupportInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.FragmentCustomerFormBinding;
import de.wackernagel.essbar.ui.viewModels.LoginViewModel;
import de.wackernagel.essbar.utils.EncryptionUtils;
import de.wackernagel.essbar.utils.ViewUtils;

public class CustomerFormFragment extends Fragment {

    private static final String KEYSTORE_ALIAS = "Essbar";

    private static final int REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION = 1;

    private FragmentCustomerFormBinding binding;
    private LoginViewModel viewModel;
    private KeyguardManager keyguardManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    static CustomerFormFragment newInstance() {
        Bundle args = new Bundle();
        
        CustomerFormFragment fragment = new CustomerFormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerFormBinding.inflate( inflater, container, false );
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this );
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider( requireActivity(), viewModelFactory).get( LoginViewModel.class );

        keyguardManager = (KeyguardManager) requireActivity().getSystemService(Context.KEYGUARD_SERVICE );

        binding.usernameField.setText( viewModel.getUsername() );
        binding.passwordField.setText( viewModel.getPassword() );
        ViewUtils.addRequiredValidationOnBlur( binding.usernameContainer, viewModel::getUsername, viewModel::setUsername, R.string.username_required_error );
        ViewUtils.addRequiredValidationOnBlur( binding.passwordContainer, viewModel::getPassword, viewModel::setPassword, R.string.password_required_error );
        binding.loginButton.setOnClickListener(v -> {
            showError( null );
            if( ViewUtils.validateRequiredValue( binding.usernameContainer, viewModel::setUsername, R.string.username_required_error ) &&
                    ViewUtils.validateRequiredValue( binding.passwordContainer, viewModel::setPassword, R.string.password_required_error ) ) {
                loginAtWebsite();
            }
        });
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

    private boolean wasWebLoginSuccessful( final Document document ) {
        return document != null && document.select( "#login-info .fehler" ).size() == 0;
    }

    private void startMainActivity() {
        final Intent intent = new Intent( requireContext(), MenuActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( intent );
        requireActivity().finish();
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
                Toast.makeText( requireContext(), R.string.unknown_error, Toast.LENGTH_LONG ).show();
            }
        });
    }

    private void startAuthenticationActivity( final int requestCode ) {
        final Intent intent = keyguardManager.createConfirmDeviceCredentialIntent( null, null );
        if( intent != null ) {
            startActivityForResult( intent, requestCode);
        }
    }

    private void setFormEnabled( boolean isEnabled ) {
        binding.usernameField.setEnabled( isEnabled );
        binding.passwordField.setEnabled( isEnabled );
        binding.saveCredentials.setEnabled( isEnabled );
        binding.loginButton.setEnabled( isEnabled );
    }

    private void showError( @Nullable final String message ) {
        if( message != null && !TextUtils.isEmpty( message ) ) {
            Snackbar snackbar = Snackbar.make( binding.getRoot(), message, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor( ContextCompat.getColor( requireContext(), R.color.colorErrorLight ) );
            TextView textView = snackbarView.findViewById( com.google.android.material.R.id.snackbar_text);
            textView.setLineSpacing(0f ,1.2f);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setCompoundDrawablesWithIntrinsicBounds( AppCompatResources.getDrawable( requireContext(), R.drawable.ic_error_outline_black_24dp ), null, null, null );
            textView.setCompoundDrawableTintList( AppCompatResources.getColorStateList( requireContext(), R.color.colorError ) );
            textView.setCompoundDrawablePadding( getResources().getDimensionPixelSize( R.dimen.view_space ) );
            textView.setTextColor( ContextCompat.getColor( requireContext(), R.color.colorError ) );
            snackbar.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if( resultCode == Activity.RESULT_CANCELED ) {
            if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION ) {
                startMainActivity();
            }
        }
        if( resultCode == Activity.RESULT_OK ) {
            if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION ) {
                doEncryption();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
