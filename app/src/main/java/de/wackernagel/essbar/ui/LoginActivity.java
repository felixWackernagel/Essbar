package de.wackernagel.essbar.ui;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.ActivityLoginBinding;
import de.wackernagel.essbar.ui.viewModels.LoginViewModel;
import de.wackernagel.essbar.utils.ConnectivityLifecycleObserver;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LoginActivity extends DaggerAppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    ConnectivityLifecycleObserver connectivityLifecycleObserver;

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView( this, R.layout.activity_login );
        viewModel = ViewModelProviders.of( this, viewModelFactory ).get( LoginViewModel.class );

        final LoginPagerAdapter adapter = new LoginPagerAdapter( getSupportFragmentManager() );
        binding.viewPager.setAdapter( adapter );

        final KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE );
        if( keyguardManager.isDeviceSecure() ) {
            viewModel.hasCustomers().observe( this, (hasCustomers) -> {
                if( hasCustomers ) {
                    adapter.setCount( 2 );
                    binding.viewPager.setCurrentItem( 1 );
                } else {
                    adapter.setCount( 1 );
                    binding.viewPager.setCurrentItem( 0 );
                }
            });
        }

        getLifecycle().addObserver( connectivityLifecycleObserver );
        connectivityLifecycleObserver.getConnectedStatus().observe( this, this::showOfflineState );
    }

    private void showOfflineState( boolean hasInternet ) {
        if( hasInternet ) {
            showLoadingState();
        } else {
            binding.offlineContainer.setVisibility( VISIBLE );
            binding.onlineContainer.setVisibility( GONE );
        }
    }

    private void showLoadingState() {
        binding.loadingContainer.setVisibility( VISIBLE );
        binding.onlineContainer.setVisibility( GONE );

        viewModel.isWebsiteReady().observe(this, ready -> {
            binding.loadingContainer.setVisibility( ready ? GONE : VISIBLE );
            binding.onlineContainer.setVisibility( ready ? VISIBLE : GONE );
        });
    }

    static class LoginPagerAdapter extends FragmentPagerAdapter {
        private int count = 1;

        LoginPagerAdapter(@NonNull FragmentManager fm ) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if( position == 1 ) {
                return CustomersListFragment.newInstance();
            }
            return CustomerFormFragment.newInstance();
        }

        @Override
        public int getCount() {
            return count;
        }

        void setCount( final int count ) {
            this.count = Math.max( count, 1 );
            notifyDataSetChanged();
        }
    }
}
