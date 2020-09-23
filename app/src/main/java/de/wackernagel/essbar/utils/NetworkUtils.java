package de.wackernagel.essbar.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetworkUtils {

    private NetworkUtils() {
        // no instance needed
    }

    public static boolean hasNetworkConnection( final Context context ) {
        return hasNetworkConnection( ( ConnectivityManager ) context.getSystemService( Context.CONNECTIVITY_SERVICE ) );
    }

    public static boolean hasNetworkConnection( final ConnectivityManager connectivityManager ) {
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
