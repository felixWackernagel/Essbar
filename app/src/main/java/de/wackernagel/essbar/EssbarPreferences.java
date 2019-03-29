package de.wackernagel.essbar;

import android.content.Context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.wackernagel.essbar.utils.PreferenceUtils;

public final class EssbarPreferences {

    private static final String COOKIE = "cookie";

    @Nullable
    public static String getCookie( final Context context ) {
        return PreferenceUtils.getString( context, COOKIE, null );
    }

    public static void setCookie( final Context context, @Nonnull final String cookie ) {
        PreferenceUtils.setString( context, COOKIE, cookie );
    }
}
