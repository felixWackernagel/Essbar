package de.wackernagel.essbar.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.android.AndroidInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.ActivityMenuBinding;
import de.wackernagel.essbar.ui.viewModels.MenuViewModel;
import de.wackernagel.essbar.utils.SectionItemDecoration;

public class MenuActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ToolbarSpinnerAdapter<KW> kwAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        final ActivityMenuBinding binding = DataBindingUtil.setContentView( this, R.layout.activity_menu);
        final MenuViewModel viewModel = new ViewModelProvider( this, viewModelFactory ).get( MenuViewModel.class );

        final String[] localizedWeekdays = getResources().getStringArray( R.array.weekdays );
        final String todayAsWord = getString( R.string.today );
        // Calendar starts with sunday which is index 1
        final int currentDayOfWeek = GregorianCalendar.getInstance().get( Calendar.DAY_OF_WEEK ) - 2;

        setSupportActionBar(binding.toolbar);

        final ActionBar actionBar = getSupportActionBar();
        Context themedContext = getApplicationContext();
        if( actionBar != null ) {
            actionBar.setDisplayShowTitleEnabled(false);
            themedContext = actionBar.getThemedContext();
        }

        kwAdapter = new ToolbarSpinnerAdapter<KW>( themedContext ) {
            @Override
            String getTitle(int position) {
                return getItem( position ).getName().split("\\|\\|")[0];
            }
        };
        binding.toolbarSpinner.setAdapter( kwAdapter );
        binding.toolbarSpinner.setOnItemSelectedListener(this );

        final MenuListAdapter adapter = new MenuListAdapter();
        binding.recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        binding.recyclerView.setHasFixedSize( true );
        binding.recyclerView.setAdapter( adapter );
        binding.recyclerView.addItemDecoration( new SectionItemDecoration( this, false, new SectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection( int position ) {
                // first item or when current and previous position have different weekdays
                return position <= 0 || adapter.getListItem( position - 1 ).getWeekday() != adapter.getListItem( position ).getWeekday();
            }

            @Override
            public CharSequence getSectionHeader( int position ) {
                final Menu item = adapter.getListItem( position );
                final String weekdayAsWord = localizedWeekdays[ item.getWeekday() ];
                if( item.getWeekday() == currentDayOfWeek ) {
                    return weekdayAsWord.concat( todayAsWord );
                }
                return weekdayAsWord;
            }
        }) );
        viewModel.getKwItems().observe(this, kws -> {
            kwAdapter.setItems( kws );
            binding.toolbarSpinner.setSelection( findIndexOfSelectedItem( kws ), false );
        });
        viewModel.getMenuItems().observe(this, adapter::submitList);
    }

    private int findIndexOfSelectedItem( final List<KW> kws) {
        if( kws != null ) {
            final int size = kws.size();
            for( int index = 0; index < size; index++ ) {
                if( kws.get( index ).isSelected() ) {
                    return index;
                }
            }
        }
        return 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.e("MenuActivity", "Spinner position=" + position );
        if( position != ArrayAdapter.NO_SELECTION ) {
            Toast.makeText(this, kwAdapter.getItem( position ).getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
