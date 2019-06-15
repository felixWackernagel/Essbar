package de.wackernagel.essbar.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.android.support.AndroidSupportInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.FragmentCustomerListBinding;
import de.wackernagel.essbar.ui.viewModels.LoginViewModel;
import de.wackernagel.essbar.utils.EncryptionUtils;
import de.wackernagel.essbar.utils.SectionItemDecoration;
import de.wackernagel.essbar.web.DocumentParser;

public class CustomersListFragment extends AbstractLoginFragment {

    private static final int REQUEST_CODE_FOR_CREDENTIAL_DECRYPTION = 2;

    private FragmentCustomerListBinding binding;
    private LoginViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    static CustomersListFragment newInstance() {
        final Bundle args = new Bundle();
        final CustomersListFragment fragment = new CustomersListFragment();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if( requestCode == REQUEST_CODE_FOR_CREDENTIAL_DECRYPTION) {
            if( resultCode == Activity.RESULT_CANCELED ) {
                showError( getString( R.string.decryption_canceled_error ) );
            } else if( resultCode == Activity.RESULT_OK ) {
                doDecryption();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginAtWebsite() {
        viewModel.getLoginDocument().observe(this, resource -> {
            if( resource.isSuccess() ) {
                if( DocumentParser.isLoginSuccessful( resource.getResource() ) ) {
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
}
