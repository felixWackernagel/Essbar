package de.wackernagel.essbar.web;

import android.content.Context;

import java.util.Collections;
import java.util.List;

import de.wackernagel.essbar.EssbarPreferences;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class PreferencesCookieJar implements CookieJar {

    private final Context context;

    public PreferencesCookieJar( final Context context ) {
        this.context = context;
    }

    @Override
    public void saveFromResponse( HttpUrl url, List<Cookie> cookies ) {
        if( !cookies.isEmpty() ) {
            final String cookie = cookies.get(0).toString();
            EssbarPreferences.setCookie( context, cookie );
        }
    }

    @Override
    public List<Cookie> loadForRequest( HttpUrl url ) {
        final String cookie = EssbarPreferences.getCookie( context );
        if( cookie != null ) {
            return Collections.singletonList( Cookie.parse( url, cookie ) );
        }
        return Collections.emptyList();
    }
}
