package de.wackernagel.essbar.ui;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import dagger.android.support.DaggerAppCompatActivity;
import de.wackernagel.essbar.R;

public class LunchActivity extends DaggerAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBindingUtil.setContentView( this, R.layout.activity_lunch);

        if( savedInstanceState == null ) {
            getSupportFragmentManager().beginTransaction().replace( R.id.container, LunchListFragment.newInstance(), "lunch-list").commit();
        }
    }

}
