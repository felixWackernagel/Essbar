package de.wackernagel.essbar.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * Lifecycle aware connectivity checker that exposes the network connected status via a LiveData.
 *
 * The loss of connectivity while the user scrolls through the feed should NOT be a blocker for the
 * user.
 *
 * The loss of connectivity when the activity is resumed should be a blocker for the user
 * (since we can't get feed items) - in onResume, we should get the connectivity status. If we
 * are NOT connected then we register a listener and wait to be notified. Only once we are
 * connected, we stop listening to connectivity.¬
 */
public class ConnectivityLifecycleObserver implements LifecycleObserver {

    private final ConnectivityManager connectivityManager;
    private final MutableLiveData<Boolean> connectedStatus;

    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            connectedStatus.postValue(true);
        }

        @Override
        public void onLost(Network network) {
            connectedStatus.postValue(false);
        }
    };

    public ConnectivityLifecycleObserver(final Application application) {
        connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectedStatus = new MutableLiveData<>();
    }

    public LiveData<Boolean> getConnectedStatus() {
        return connectedStatus;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void startMonitoringConnectivity() {
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        final boolean connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        connectedStatus.postValue( connected );
        connectivityManager.registerNetworkCallback(
                new NetworkRequest.Builder().addCapability( NetworkCapabilities.NET_CAPABILITY_INTERNET ).build(),
                networkCallback
        );
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void stopMonitoringConnectivity() {
        connectivityManager.unregisterNetworkCallback( networkCallback );
    }
}