package de.wackernagel.essbar.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import de.wackernagel.essbar.EssbarConstants;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.FragmentCustomerFormBinding;
import de.wackernagel.essbar.ui.viewModels.LoginViewModel;
import de.wackernagel.essbar.utils.EncryptionUtils;
import de.wackernagel.essbar.utils.ViewUtils;
import de.wackernagel.essbar.web.DocumentParser;

public class CustomerFormFragment extends AbstractLoginFragment {

    private static final String TAG = "CustomerFormFragment";

    private static final int REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION = 1;

    private FragmentCustomerFormBinding binding;
    private LoginViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    static CustomerFormFragment newInstance() {
        final CustomerFormFragment fragment = new CustomerFormFragment();
        fragment.setArguments( Bundle.EMPTY );
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
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider( requireActivity(), viewModelFactory).get( LoginViewModel.class );

        if( !keyguardManager.isDeviceSecure() ) {
            binding.saveCredentialsContainer.setVisibility( View.GONE );
        }

        binding.usernameField.setText( viewModel.getUsername() );
        binding.passwordField.setText( viewModel.getPassword() );
        ViewUtils.addRequiredValidationOnBlur( binding.usernameContainer, viewModel::getUsername, viewModel::setUsername, R.string.username_required_error );
        ViewUtils.addRequiredValidationOnBlur( binding.passwordContainer, viewModel::getPassword, viewModel::setPassword, R.string.password_required_error );
        binding.loginButton.setOnClickListener(v -> {
            if( ViewUtils.validateRequiredValue( binding.usernameContainer, viewModel::setUsername, R.string.username_required_error ) &&
                    ViewUtils.validateRequiredValue( binding.passwordContainer, viewModel::setPassword, R.string.password_required_error ) ) {
                loginAtWebsite();
            }
        });
    }

    private void loginAtWebsite() {
        setFormEnabled( false );

        viewModel.getLoginDocument().observe(getViewLifecycleOwner(), resource -> {
            Log.i(TAG, resource.toString() );
            if( resource.isStatusOk() && resource.isAvailable() ) {
                if( !EssbarConstants.Urls.HOME.equals( resource.getUrl() ) && DocumentParser.isLoginSuccessful( resource.getResource() ) ) {
                    final String urlSecret = DocumentParser.getSecret( resource.getUrl() );
                    viewModel.setUrlSecret( urlSecret );
                    if (binding.saveCredentials.isChecked()) {
                        viewModel.findCustomerName(resource.getResource());
                        doEncryption( urlSecret );
                    } else {
                        startMenuActivity( urlSecret );
                    }
                } else {
                    showError( getString( R.string.username_password_error) );
                }
            } else {
                final String message = (TextUtils.isEmpty( resource.getError() )? getString( R.string.unknown_error ) : resource.getError() );
                showError( message );
            }

            setFormEnabled( true );
        } );
    }

    private void doEncryption( final String urlSecret ) {
        EncryptionUtils.encrypt( viewModel.getPassword(), KEYSTORE_ALIAS, new EncryptionUtils.EncryptionCallback() {
            @Override
            public void onEncryptionSuccess(String encryptedPassword, String encryptionIV) {
                viewModel.insertCustomer( encryptedPassword, encryptionIV );
                startMenuActivity( urlSecret );
            }

            @Override
            public void onUserNotAuthenticatedForEncryption() {
                startAuthenticationActivity(REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION);
            }

            @Override
            public void onEncryptionError(Exception e) {
                Log.e(TAG, "encryption failed", e );
                Toast.makeText( requireContext(), R.string.unknown_error, Toast.LENGTH_LONG ).show();
            }
        });
    }

    private void setFormEnabled( boolean isEnabled ) {
        binding.usernameField.setEnabled( isEnabled );
        binding.passwordField.setEnabled( isEnabled );
        binding.saveCredentials.setEnabled( isEnabled );
        binding.loginButton.setEnabled( isEnabled );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_ENCRYPTION ) {
            if( resultCode == Activity.RESULT_CANCELED ) {
                startMenuActivity( viewModel.getUrlSecret() );
            } else if( resultCode == Activity.RESULT_OK ) {
                doEncryption( viewModel.getUrlSecret() );
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
