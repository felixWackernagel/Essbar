package de.wackernagel.essbar.web;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class InMemoryCookieJar implements CookieJar {

    private static InMemoryCookieJar instance = new InMemoryCookieJar();

    private HashMap<String, List<Cookie>> cookieStore;

    public static InMemoryCookieJar get() {
        return instance;
    }

    private InMemoryCookieJar() {
        cookieStore = new HashMap<>();
    }

    @Override
    public void saveFromResponse( @NonNull final HttpUrl url, @NonNull final List<Cookie> cookies ) {
        cookieStore.put( url.host(), cookies );
    }

    @Override
    public List<Cookie> loadForRequest( @NonNull final HttpUrl url ) {
        final List<Cookie> cookies = cookieStore.get( url.host() );
        return cookies == null ? Collections.emptyList() : cookies;
    }
}
