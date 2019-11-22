package de.wackernagel.essbar.dagger;

import android.app.Application;
import android.util.Log;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import de.wackernagel.essbar.BuildConfig;
import de.wackernagel.essbar.EssbarConstants;
import de.wackernagel.essbar.web.JSoupConverter;
import de.wackernagel.essbar.web.LiveDataResourceCallAdapterFactory;
import de.wackernagel.essbar.web.NetworkConnectionInterceptor;
import de.wackernagel.essbar.web.InMemoryCookieJar;
import de.wackernagel.essbar.web.WebService;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

@Module
public class RetrofitModule {

    private static final String TAG = "RetrofitModule";

    @Provides
    @Singleton
    Cache provideCache( final Application application ) {
        // 10 MB response cache
        final long cacheSize = 10 * 1024 * 1024;
        return new Cache( application.getCacheDir(), cacheSize );
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient( final Application application, final Cache cache ) {
        final OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cookieJar( InMemoryCookieJar.get() );
        client.addInterceptor( new NetworkConnectionInterceptor( application ) );
        client.cache( cache );

        addRequestLogging(client);

        return client.build();
    }

    @Provides
    @Singleton
    WebService provideWebservice( final Lazy<OkHttpClient> okHttpClient ) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( EssbarConstants.Urls.BASE )
                .addCallAdapterFactory( new LiveDataResourceCallAdapterFactory() )
                .addConverterFactory( JSoupConverter.FACTORY )
                .callFactory( (request) -> okHttpClient.get().newCall( request ) )
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