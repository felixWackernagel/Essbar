package de.wackernagel.essbar.utils;

import android.widget.EditText;

import androidx.annotation.Nullable;

public final class ViewUtils {

    private ViewUtils() {
        // no instance needed
    }

    @Nullable
    public static String getString( @Nullable  final EditText view ) {
        if( view == null ) {
            return null;
        }
        if( view.getText() == null ) {
            return null;
        }
        return view.getText().toString();
    }
}
