package de.wackernagel.essbar.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import de.wackernagel.essbar.R;

public class FullscreenDialogFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        applyFullscreenLayout();
    }

    private void applyFullscreenLayout() {
        final Dialog dialog = getDialog();
        if( dialog != null && dialog.getWindow() != null ) {
            dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
        }
    }

}
