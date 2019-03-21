package de.wackernagel.essbar;

import android.content.Context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.wackernagel.essbar.utils.PreferenceUtils;

public final class EssbarPreferences {

    private static final String USERNAME = "username";
    private static final String ENCRYPTED_PASSWORD = "encrypted:password";
    private static final String ENCRYPTION_IV = "encryption:iv";
    private static final String COOKIE = "cookie";

    @Nullable
    public static String getUsername( final Context context ) {
        return PreferenceUtils.getString( context, USERNAME, null );
    }

    public static void setUsername( final Context context, @Nonnull final String username ) {
        PreferenceUtils.setString( context, USERNAME, username );
    }

    @Nullable
    public static String getEncryptedPassword( final Context context ) {
        return PreferenceUtils.getString( context, ENCRYPTED_PASSWORD, null );
    }

    public static void setEncryptedPassword( final Context context, @Nonnull final String password ) {
        PreferenceUtils.setString( context, ENCRYPTED_PASSWORD, password );
    }

    @Nullable
    public static String getEncryptionIV( final Context context ) {
        return PreferenceUtils.getString( context, ENCRYPTION_IV, null );
    }

    public static void setEncryptionIV( final Context context, @Nonnull final String encryptionIV ) {
        PreferenceUtils.setString( context, ENCRYPTION_IV, encryptionIV );
    }

    @Nullable
    public static String getCookie( final Context context ) {
        return PreferenceUtils.getString( context, COOKIE, null );
    }

    public static void setCookie( final Context context, @Nonnull final String cookie ) {
        PreferenceUtils.setString( context, COOKIE, cookie );
    }
}
