package de.wackernagel.essbar.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.core.util.Supplier;

import com.google.android.material.textfield.TextInputLayout;

public final class ViewUtils {

    private ViewUtils() {
        // no instance needed
    }

    @Nullable
    private static String getString( @Nullable final EditText view ) {
        if( view == null ) {
            return null;
        }
        if( view.getText() == null ) {
            return null;
        }
        return view.getText().toString();
    }

    public static void addRequiredValidationOnBlur(final TextInputLayout layout, final Supplier<String> previousValue, final Consumer<String> nextValue, final int errorResId ) {
        if( layout != null && layout.getEditText() != null ) {
            layout.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
                if( !hasFocus ) {
                    final String oldValue = previousValue.get();
                    final String newValue = ViewUtils.getString( layout.getEditText() );
                    if( !TextUtils.equals( oldValue, newValue ) ) {
                        validateRequiredValue( layout, nextValue, errorResId );
                    }
                }
            });
        }
    }

    public static boolean validateRequiredValue(final TextInputLayout layout, final Consumer<String> nextValue, final int errorResId ) {
        if( layout != null && layout.getEditText() != null ) {
            final String value = ViewUtils.getString( layout.getEditText() );
            if( TextUtils.isEmpty( value ) ) {
                layout.setError( layout.getResources().getString( errorResId ) );
                return false;
            } else {
                layout.setError( null );
                nextValue.accept( value );
                return true;
            }
        }
        return false;
    }

    public static int spToPx( float sp, @NonNull final Context context ) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPx( float dp, @NonNull final Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
