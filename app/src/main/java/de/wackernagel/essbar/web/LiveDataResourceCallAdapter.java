package de.wackernagel.essbar.web;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A Retrofit adapter that converts the Call into a LiveData of Resource.
 * @param <R>
 */
public class LiveDataResourceCallAdapter<R> implements CallAdapter<R, LiveData<Resource<R>>> {
    private final Type responseType;

    LiveDataResourceCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @NonNull
    @Override
    public Type responseType() {
        return responseType;
    }

    @NonNull
    @Override
    public LiveData<Resource<R>> adapt( @NonNull final Call<R> call ) {
        return new LiveData<Resource<R>>() {
            AtomicBoolean started = new AtomicBoolean(false );
            @Override
            protected void onActive() {
                super.onActive();
                if( started.compareAndSet(false, true ) ) {
                    call.enqueue(new Callback<R>() {
                        @Override
                        public void onResponse( @NonNull Call<R> call, @NonNull Response<R> response) {
                            if( call.isCanceled() )
                                return;
                            postValue( Resource.success( response.code(), response.raw().request().url().toString(), response.body() ) );
                        }

                        @Override
                        public void onFailure( @NonNull Call<R> call, @NonNull Throwable throwable) {
                            if( call.isCanceled() )
                                return;
                            postValue( Resource.error( call.request().url().toString(), throwable ) );
                        }
                    });
                }
            }
        };
    }
}