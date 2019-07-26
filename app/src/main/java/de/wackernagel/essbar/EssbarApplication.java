package de.wackernagel.essbar;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraDialog;
import org.acra.annotation.AcraMailSender;
import org.acra.data.StringFormat;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import de.wackernagel.essbar.dagger.DaggerApplicationComponent;
import de.wackernagel.essbar.dagger.RetrofitModule;
import de.wackernagel.essbar.dagger.RoomModule;

@AcraCore(
        buildConfigClass = BuildConfig.class,
        reportFormat = StringFormat.KEY_VALUE_LIST )
@AcraMailSender(
        mailTo = EssbarConstants.ACRA_EMAIL )
@AcraDialog(
        resIcon = 0,
        resNegativeButtonText = R.string.acra_dialog_negativ,
        resPositiveButtonText = R.string.acra_dialog_positiv,
        resText = R.string.acra_dialog_text,
        resTitle = R.string.acra_dialog_title )
public class EssbarApplication extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerApplicationComponent
                .builder()
                .roomModule( new RoomModule( this ) )
                .retrofitModule( new RetrofitModule( this ) )
                .build()
                .inject( this );
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }
}