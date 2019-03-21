package de.wackernagel.essbar.web;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.lifecycle.LiveData;
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

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<Resource<R>> adapt( final Call<R> call ) {
        return new LiveData<Resource<R>>() {
            AtomicBoolean started = new AtomicBoolean(false );
            @Override
            protected void onActive() {
                super.onActive();
                if( started.compareAndSet(false, true ) ) {
                    call.enqueue(new Callback<R>() {
                        @Override
                        public void onResponse(Call<R> call, Response<R> response) {
                            if( call.isCanceled() )
                                return;
                            postValue( Resource.success( response.body() ) );
                        }

                        @Override
                        public void onFailure(Call<R> call, Throwable throwable) {
                            if( call.isCanceled() )
                                return;
                            postValue( Resource.error( throwable ) );
                        }
                    });
                }
            }
        };
    }
}