package de.wackernagel.essbar.web;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;

import de.wackernagel.essbar.R;
import de.wackernagel.essbar.utils.NetworkUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkConnectionInterceptor implements Interceptor {

    private Context context;

    public NetworkConnectionInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept( @NonNull final Chain chain ) throws IOException {
        if ( !NetworkUtils.hasNetworkConnection( context ) ) {
            throw new NoNetworkConnectionException( context.getString( R.string.no_network_connection_error ) );
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

    public static class NoNetworkConnectionException extends IOException {
        NoNetworkConnectionException( final String localizedMessage ) {
            super( localizedMessage );
        }
    }
}