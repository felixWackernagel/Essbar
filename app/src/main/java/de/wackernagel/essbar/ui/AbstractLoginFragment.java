package de.wackernagel.essbar.ui;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import de.wackernagel.essbar.R;

public class AbstractLoginFragment extends Fragment {

    static final String KEYSTORE_ALIAS = "Essbar";

    private KeyguardManager keyguardManager;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        keyguardManager = (KeyguardManager) requireContext().getSystemService(Context.KEYGUARD_SERVICE );
    }

    void startAuthenticationActivity(final int requestCode ) {
        final Intent intent = keyguardManager.createConfirmDeviceCredentialIntent( null, null );
        if( intent != null ) {
            startActivityForResult( intent, requestCode);
        }
    }

    void startMainActivity() {
        final Intent intent = new Intent( requireContext(), MenuActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( intent );
        requireActivity().finish();
    }

    boolean wasWebLoginSuccessful( final Document document ) {
        return document != null && document.select( "#login-info .fehler" ).size() == 0;
    }

    void showError(@Nullable final String message ) {
        if( message != null && !TextUtils.isEmpty( message ) ) {
            final Snackbar snackbar = Snackbar.make( requireActivity().findViewById(R.id.coordinatorLayout), message, Snackbar.LENGTH_LONG);
            final View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor( ContextCompat.getColor( requireContext(), R.color.colorErrorLight ) );
            final TextView textView = snackbarView.findViewById( com.google.android.material.R.id.snackbar_text);
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
