package de.wackernagel.essbar.dagger;

import android.app.Application;
import android.util.Log;

import javax.inject.Singleton;

import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Provides;
import de.wackernagel.essbar.BuildConfig;
import de.wackernagel.essbar.ui.viewModels.ViewModelFactory;
import de.wackernagel.essbar.web.JSoupConverter;
import de.wackernagel.essbar.web.LiveDataResourceCallAdapterFactory;
import de.wackernagel.essbar.web.PreferencesCookieJar;
import de.wackernagel.essbar.web.WebService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

@Module
public class RetrofitModule {

    private final Application application;

    public RetrofitModule( final Application application ) {
        this.application = application;
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory providerViewModelFactory( final WebService webService ) {
        return new ViewModelFactory( webService );
    }

    @Provides
    @Singleton
    WebService provideWebservice() {
        final OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cookieJar( new PreferencesCookieJar( application ) );

        addRequestLogging(client);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "https://www.bestellung-pipapo-catering.de" )
                .addCallAdapterFactory( new LiveDataResourceCallAdapterFactory() )
                .addConverterFactory( JSoupConverter.FACTORY )
                .client( client.build() )
                .build();

        return retrofit.create(WebService.class);
    }

    private void addRequestLogging(OkHttpClient.Builder client) {
        if (BuildConfig.DEBUG) {
            final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor( new HttpLoggingInterceptor.Logger() {
                @Override
                public void log( final String message ) {
                    Log.i("Essbar", message);
                }
            } );
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(interceptor);
        }
    }
}