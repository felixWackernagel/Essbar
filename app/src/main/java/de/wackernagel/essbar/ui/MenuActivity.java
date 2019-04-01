package de.wackernagel.essbar.ui;

import android.os.Bundle;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.android.AndroidInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.ActivityMenuBinding;
import de.wackernagel.essbar.ui.viewModels.MenuViewModel;
import de.wackernagel.essbar.utils.GridGutterDecoration;
import de.wackernagel.essbar.utils.SectionItemDecoration;

public class MenuActivity extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

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

        final MenuListAdapter adapter = new MenuListAdapter();

        binding.recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        binding.recyclerView.setHasFixedSize( false );
        binding.recyclerView.setAdapter( adapter );
        binding.recyclerView.addItemDecoration( new GridGutterDecoration( getResources().getDimensionPixelSize( R.dimen.view_space ), 1, false, true ) );
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

        viewModel.getMenuItems().observe(this, adapter::submitList);
        viewModel.getCheckedMenuItems().observe( this, adapter::setCheckedItems );
    }
}
