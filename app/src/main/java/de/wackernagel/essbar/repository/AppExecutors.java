package de.wackernagel.essbar.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private final Executor mDiskIO;

    private final Executor mainThread;

    AppExecutors() {
        this( Executors.newSingleThreadExecutor(), new MainThreadExecutor() );
    }

    private AppExecutors( final Executor diskIO, final Executor mainThread ) {
        this.mDiskIO = diskIO;
        this.mainThread = mainThread;
    }

    public Executor diskIO() {
        return mDiskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}