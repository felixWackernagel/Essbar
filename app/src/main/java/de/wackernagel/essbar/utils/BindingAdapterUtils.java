package de.wackernagel.essbar.utils;

import android.view.View;

import androidx.databinding.BindingAdapter;

public class BindingAdapterUtils {

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, Boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }

}
