package de.wackernagel.essbar.web;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class PreferencesCookieJar implements CookieJar {

    private static final String TAG = "PreferencesCookieJar";
    //private final Context context;

    private HashMap<String, List<Cookie>> cookieStore;

    private static PreferencesCookieJar instance = new PreferencesCookieJar( null );

    public static PreferencesCookieJar get() {
        return instance;
    }

    private PreferencesCookieJar( final Context context ) {
       // this.context = context;
        Log.e(TAG, "new instance");
        cookieStore = new HashMap<>();
    }

    @Override
    public void saveFromResponse( @NonNull final HttpUrl url, @NonNull final List<Cookie> cookies ) {
        Log.e(TAG, "saveFromResponse: Url host = " + url.host() );

        for( Cookie c : cookies )
            Log.e(TAG, "saveFromResponse: name = " + c.name() + ", value = " + c.value() + ", domain = " + c.domain() + ", path=" + c.path() );

        cookieStore.put( url.host(), cookies );

//        if( !cookies.isEmpty() ) {
//            final StringBuilder sb = new StringBuilder();
//            for( Cookie c : cookies ) {
//                if( sb.length() > 0 ) {
//                    sb.append( "|" );
//                }
//                sb.append( c.toString() );
//            }
//            EssbarPreferences.setCookie( context, sb.toString() );
//        }
    }

    @Override
    public List<Cookie> loadForRequest( @NonNull final HttpUrl url ) {
        final List<Cookie> cookies = cookieStore.get( url.host() );
        Log.e(TAG, "loadForRequest: url host = " + url.host() );
        Log.e(TAG, "loadForRequest: list of coockies = " + cookies );
        if( cookies == null || cookies.isEmpty() ) {
            return Collections.emptyList();
        } else {
            final StringBuilder value = new StringBuilder();
            Cookie ref = null;
            for( Cookie cookie : cookies ) {
                if( value.length() > 0 ) {
                    value.append( ";" );
                }
                value.append( cookie.name() ).append( "=" ).append( cookie.value() );
                ref = cookie;
            }
            if( ref == null ) {
                return Collections.emptyList();
            }
            final Cookie result = new Cookie.Builder().name("Cookie" ).value( value.toString() ).domain( ref.domain() ).path( ref.path() ).build();
            Log.e(TAG, "loadForRequest: resul = " + result );
            return Collections.singletonList( result );
        }

//        final String cookies = EssbarPreferences.getCookie( context );
//        if( cookies != null ) {
//            final ArrayList<Cookie> result = new ArrayList<>();
//            for( String cookie : cookies.split( "\\|" ) ) {
//                Cookie c = Cookie.parse( url, cookie );
//                Log.e("PreferencesCookieJar", "load: " + c.toString() );
//                result.add( c );
//            }
//            return result;
//        }
//        return Collections.emptyList();
    }
}
