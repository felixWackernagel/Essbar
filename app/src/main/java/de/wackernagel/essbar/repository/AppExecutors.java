package de.wackernagel.essbar.repository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class AppExecutors {

    private final Executor mDiskIO;

    AppExecutors() {
        this(Executors.newSingleThreadExecutor());
    }

    private AppExecutors( final Executor diskIO ) {
        this.mDiskIO = diskIO;
    }

    Executor diskIO() {
        return mDiskIO;
    }
}