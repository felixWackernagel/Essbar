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

import com.google.android.material.snackbar.Snackbar;

import org.jsoup.nodes.Document;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.android.support.AndroidSupportInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.FragmentCustomerListBinding;
import de.wackernagel.essbar.ui.viewModels.LoginViewModel;
import de.wackernagel.essbar.utils.EncryptionUtils;
import de.wackernagel.essbar.utils.SectionItemDecoration;

public class CustomersListFragment extends Fragment {

    private static final String KEYSTORE_ALIAS = "Essbar";

    private static final int REQUEST_CODE_FOR_CREDENTIAL_DECRYPTION = 2;

    private FragmentCustomerListBinding binding;
    private LoginViewModel viewModel;
    private KeyguardManager keyguardManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    static CustomersListFragment newInstance() {
        Bundle args = new Bundle();

        CustomersListFragment fragment = new CustomersListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerListBinding.inflate( inflater, container, false );
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this );
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider( requireActivity(), viewModelFactory).get( LoginViewModel.class );

        keyguardManager = (KeyguardManager) requireActivity().getSystemService(Context.KEYGUARD_SERVICE );

        final CustomerListAdapter adapter = new CustomerListAdapter();
        adapter.setOnCustomerClickListener(customer -> {
            viewModel.setCustomer( customer );
            doDecryption();
        });

        binding.recyclerView.setLayoutManager( new LinearLayoutManager( requireContext() ));
        binding.recyclerView.setHasFixedSize( true );
        binding.recyclerView.setAdapter( adapter );
        binding.recyclerView.addItemDecoration( new SectionItemDecoration( requireContext(), false, new SectionItemDecoration.SectionCallback() {
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
                startAuthenticationActivity( REQUEST_CODE_FOR_CREDENTIAL_DECRYPTION );
            }

            @Override
            public void onDecryptionError(Exception e) {
                showError( getString( R.string.unknown_error ) );
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if( resultCode == Activity.RESULT_CANCELED ) {
            if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_DECRYPTION) {
                showError( getString( R.string.decryption_canceled_error ) );
            }
        }
        if( resultCode == Activity.RESULT_OK ) {
            if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_DECRYPTION) {
                doDecryption();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginAtWebsite() {
        viewModel.getLoginDocument().observe(this, resource -> {
            if( resource.isSuccess() ) {
                if( wasWebLoginSuccessful( resource.getResource() ) ) {
                    startMainActivity();
                } else {
                    showError( getString( R.string.username_password_error) );
                }
            } else {
                final String message = ( resource.getError() != null ? resource.getError().getMessage() : getString( R.string.unknown_error ) );
                Log.e( "Essbar", message );
                showError( message );
            }
        } );
    }

    private void startMainActivity() {
        final Intent intent = new Intent( requireContext(), MenuActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( intent );
        requireActivity().finish();
    }

    private boolean wasWebLoginSuccessful( final Document document ) {
        return document != null && document.select( "#login-info .fehler" ).size() == 0;
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
}
