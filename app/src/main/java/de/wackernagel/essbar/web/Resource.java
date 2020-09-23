package de.wackernagel.essbar.web;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.wackernagel.essbar.utils.Status;

import static de.wackernagel.essbar.utils.Status.ERROR;
import static de.wackernagel.essbar.utils.Status.LOADING;
import static de.wackernagel.essbar.utils.Status.SUCCESS;

//public class Resource<T> {
//    private int statusCode;
//    private String url;
//    private T resource = null;
//    private Throwable error = null;
//
//    private Resource() {
//    }
//
//
//
//    @Nullable
//    public T getResource() {
//        return resource;
//    }
//
//    @Nullable
//    public Throwable getError() {
//        return error;
//    }
//
//    static <T> Resource<T> success( final int statusCode, final String url, @Nullable final T body ) {
//        final Resource<T> resource = new Resource<>();
//        resource.statusCode = statusCode;
//        resource.url = url;
//        resource.resource = body;
//        return resource;
//    }
//
//    static <T> Resource error( final String url, @Nullable final Throwable error ) {
//        final Resource<T> resource = new Resource<>();
//        resource.statusCode = 500;
//        resource.url = url;
//        resource.error = error;
//        return resource;
//    }
//
//    static <T> Resource<T> loading( @Nullable T data ) {
//        final Resource<T> resource = new Resource<>();
//        resource.statusCode = 200;
//        resource.resource = data;
//        resource.
//        return resource;
//    }
//
//    @NonNull
//    @Override
//    public String toString() {
//        return "Resource [statusCode=" + statusCode + ", url=" + url + ", success=" + isAvailable() + "]";
//    }
//}

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
public class Resource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final String message;

    @Nullable
    public final T data;

    private String url;
    private int code;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public T getResource() {
        return data;
    }

    @Nullable
    public String getError() {
        return message;
    }

    public boolean isStatusOk() {
        return code >= 200 && code < 300;
    }

    public boolean isAvailable() {
        return data != null && status == SUCCESS;
    }

    public String getUrl() {
        return url;
    }

    public Resource setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Resource setCode(int code) {
        this.code = code;
        return this;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(ERROR, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(LOADING, data, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Resource<?> resource = (Resource<?>) o;

        if (status != resource.status) {
            return false;
        }
        if (message != null ? !message.equals(resource.message) : resource.message != null) {
            return false;
        }
        return data != null ? data.equals(resource.data) : resource.data == null;
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}