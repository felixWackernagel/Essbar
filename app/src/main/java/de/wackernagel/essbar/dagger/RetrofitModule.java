package de.wackernagel.essbar.dagger;

import android.app.Application;
import android.util.Log;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.wackernagel.essbar.BuildConfig;
import de.wackernagel.essbar.EssbarConstants;
import de.wackernagel.essbar.web.JSoupConverter;
import de.wackernagel.essbar.web.LiveDataResourceCallAdapterFactory;
import de.wackernagel.essbar.web.NetworkConnectionInterceptor;
import de.wackernagel.essbar.web.InMemoryCookieJar;
import de.wackernagel.essbar.web.WebService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

@Module
public class RetrofitModule {

    private static final String TAG = "RetrofitModule";

    private final Application application;

    public RetrofitModule( final Application application ) {
        this.application = application;
    }

    @Provides
    @Singleton
    WebService provideWebservice() {
        final OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cookieJar( InMemoryCookieJar.get() );
        client.addInterceptor( new NetworkConnectionInterceptor( application ) );

        addRequestLogging(client);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( EssbarConstants.Urls.BASE )
                .addCallAdapterFactory( new LiveDataResourceCallAdapterFactory() )
                .addConverterFactory( JSoupConverter.FACTORY )
                .client( client.build() )
                .build();

        return retrofit.create(WebService.class);
    }

    private void addRequestLogging(OkHttpClient.Builder client) {
        if (BuildConfig.DEBUG) {
            final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> Log.i(TAG, message));
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addNetworkInterceptor(interceptor);
        }
    }
}