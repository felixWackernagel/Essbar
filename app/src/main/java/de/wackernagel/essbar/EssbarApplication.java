package de.wackernagel.essbar;

import android.content.Context;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraDialog;
import org.acra.annotation.AcraMailSender;
import org.acra.data.StringFormat;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import de.wackernagel.essbar.dagger.DaggerApplicationComponent;

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
public class EssbarApplication extends DaggerApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerApplicationComponent
                .builder()
                .application( this )
                .build();
    }
}