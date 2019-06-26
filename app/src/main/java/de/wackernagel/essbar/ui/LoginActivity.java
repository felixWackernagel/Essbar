package de.wackernagel.essbar.ui;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.wackernagel.essbar.EssbarPreferences;
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
        if( !keyguardManager.isKeyguardSecure() ) {
            Snackbar.make( binding.coordinatorLayout, R.string.no_secure_lock_error, Snackbar.LENGTH_LONG ).show();
        }

        final int spaceInPixel = ViewUtils.dpToPx( 16, this );
        binding.viewPager.setAdapter( new LoginPagerAdapter( getSupportFragmentManager() ) );
        binding.viewPager.setPageMargin( spaceInPixel );
        binding.viewPager.setPadding( spaceInPixel, 0 , spaceInPixel, 0 );
        binding.viewPager.setClipToPadding( false );


        final LoginViewModel viewModel = new ViewModelProvider( this, viewModelFactory).get( LoginViewModel.class );
        viewModel.getCustomersCount().observe( this, (count) -> binding.viewPager.setCurrentItem( count > 0 ? 1 : 0 ));

        EssbarPreferences.setCookie( this, null );

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

        LoginPagerAdapter(@NonNull FragmentManager fm) {
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
            return 2;
        }
    }
}
