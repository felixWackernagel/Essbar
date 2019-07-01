package de.wackernagel.essbar.web;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;

public class Resource<T> {
    private int statusCode;
    private String url;
    private T resource = null;
    private Throwable error = null;

    private Resource() {
    }

    public boolean isStatusOk() {
        return statusCode >= 200 && statusCode < 300;
    }

    public boolean isAvailable() {
        return resource != null && error == null;
    }

    public String getUrl() {
        return url;
    }

    @Nullable
    public T getResource() {
        return resource;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

    static <T> Resource<T> success( final int statusCode, final String url, @Nullable final T body ) {
        final Resource<T> resource = new Resource<>();
        resource.statusCode = statusCode;
        resource.url = url;
        resource.resource = body;
        return resource;
    }

    static <T> Resource error( final String url, @Nullable final Throwable error ) {
        final Resource<T> resource = new Resource<>();
        resource.statusCode = 500;
        resource.url = url;
        resource.error = error;
        return resource;
    }

    @NonNull
    @Override
    public String toString() {
        return "Resource [statusCode=" + statusCode + ", url=" + url + ", success=" + isAvailable() + "]";
    }
}