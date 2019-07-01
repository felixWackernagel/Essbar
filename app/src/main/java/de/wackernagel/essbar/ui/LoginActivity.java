package de.wackernagel.essbar.ui;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.ActivityLoginBinding;
import de.wackernagel.essbar.ui.viewModels.LoginViewModel;
import de.wackernagel.essbar.utils.ConnectivityLifecycleObserver;
import de.wackernagel.essbar.utils.ViewUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    ConnectivityLifecycleObserver connectivityLifecycleObserver;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView( this, R.layout.activity_login );

        final KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE );

        final int spaceInPixel = ViewUtils.dpToPx( 16, this );
        binding.viewPager.setAdapter( new LoginPagerAdapter( getSupportFragmentManager(), keyguardManager.isDeviceSecure() ? 2 : 1 ) );
        binding.viewPager.setPageMargin( spaceInPixel );
        binding.viewPager.setPadding( spaceInPixel, 0 , spaceInPixel, 0 );
        binding.viewPager.setClipToPadding( false );

        final LoginViewModel viewModel = new ViewModelProvider( this, viewModelFactory).get( LoginViewModel.class );
        viewModel.getCustomersCount().observe( this, (count) -> binding.viewPager.setCurrentItem( count > 0 ? 1 : 0 ));
        viewModel.getHome().observe(this, ready -> Log.e("LoginActivity", "login ready? " + ready ));

        getLifecycle().addObserver( connectivityLifecycleObserver );
        connectivityLifecycleObserver.getConnectedStatus().observe( this, this::showOfflineState );
    }

    private void showOfflineState( boolean hasInternet ) {
        binding.offlineContainer.setVisibility( hasInternet ? GONE : VISIBLE );
        binding.onlineContainer.setVisibility( hasInternet ? VISIBLE : GONE );
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    static class LoginPagerAdapter extends FragmentPagerAdapter {
        private final int count;

        LoginPagerAdapter(@NonNull FragmentManager fm, final int count ) {
            super(fm);
            this.count = count;
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
    }
}
