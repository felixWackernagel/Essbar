package de.wackernagel.essbar.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import dagger.android.support.AndroidSupportInjection;

class EssbarFragment extends Fragment {

    @Override
    public void onAttach( @NonNull Context context) {
        // Host check
        if( !( context instanceof AppCompatActivity) ) {
            throw new IllegalStateException( "A EssbarFragment can only be attached to a AppCompatActivity." );
        }

        // DI
        AndroidSupportInjection.inject(this );

        super.onAttach(context);
    }

    @Nullable
    private AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity) getActivity();
    }

    @Nullable
    ActionBar getSupportActionBar() {
        final AppCompatActivity activity = getAppCompatActivity();
        if( activity != null )
            return activity.getSupportActionBar();
        return null;
    }

    void setSupportActionBar( @Nullable final Toolbar toolbar ) {
        final AppCompatActivity activity = getAppCompatActivity();
        if( activity != null )
            activity.setSupportActionBar( toolbar );
    }

    @Nullable
    ActionMode startSupportActionMode( @NonNull final ActionMode.Callback callback ) {
        final AppCompatActivity activity = getAppCompatActivity();
        if( activity != null )
            return activity.startSupportActionMode( callback );
        return null;
    }

}
