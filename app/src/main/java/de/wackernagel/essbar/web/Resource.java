package de.wackernagel.essbar.web;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;

public class Resource<T> {
    private T resource;
    private Throwable error;

    private Resource() {
    }

    public boolean isSuccess() {
        return resource != null && error == null;
    }

    @Nullable
    public T getResource() {
        return resource;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

    static <T> Resource<T> success( @Nullable T body ) {
        final Resource<T> resource = new Resource<>();
        resource.resource = body;
        return resource;
    }

    static <T> Resource error( @Nullable Throwable error ) {
        final Resource<T> resource = new Resource<>();
        resource.error = error;
        return resource;
    }

    @NonNull
    @Override
    public String toString() {
        return "Resource [success=" + isSuccess() + "]";
    }
}