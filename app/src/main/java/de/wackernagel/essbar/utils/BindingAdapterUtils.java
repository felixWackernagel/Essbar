package de.wackernagel.essbar.utils;

import android.view.View;

import androidx.databinding.BindingAdapter;

public class BindingAdapterUtils {

    @BindingAdapter("android:visibility")
    public static void setVisibility( final View view, final Boolean value ) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("check")
    public static void setChecked( final CheckableFrameLayout layout, final Boolean isChecked ) {
        layout.setChecked(isChecked);
    }

    @BindingAdapter("singleTag")
    public static void setSingleTag( final View view, final Object tag ) {
        view.setTag( tag );
    }
}
