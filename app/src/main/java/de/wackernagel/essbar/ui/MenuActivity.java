package de.wackernagel.essbar.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.android.AndroidInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.ActivityMenuBinding;
import de.wackernagel.essbar.ui.viewModels.MenuViewModel;
import de.wackernagel.essbar.utils.SectionItemDecoration;

public class MenuActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ActionMode.Callback {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ToolbarSpinnerAdapter<KW> kwAdapter;

    private MenuViewModel viewModel;

    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        final ActivityMenuBinding binding = DataBindingUtil.setContentView( this, R.layout.activity_menu);
        viewModel = new ViewModelProvider( this, viewModelFactory ).get( MenuViewModel.class );

        final String[] localizedWeekdays = getResources().getStringArray( R.array.weekdays );
        final String todayAsWord = getString( R.string.today );

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
                return getItem( position ).getName();
            }

            @Override
            int getIconVisibility(int position) {
                return getItem( position ).isSelected() ? View.VISIBLE : View.GONE;
            }
        };
        binding.toolbarSpinner.setAdapter( kwAdapter );
        binding.toolbarSpinner.setOnItemSelectedListener(this );

        final MenuListAdapter adapter = new MenuListAdapter();
        adapter.setOnMenuStatusChangedListener( ( (menu, isOrdered) -> {
            if( menu.isOrdered() != isOrdered ) {
                viewModel.incrementNumberOfChangedOrders();
            } else {
                viewModel.decrementNumberOfChangedOrders();
            }
        } ) );
        viewModel.getMenusOrderStatus().observe( this, adapter::setCheckedItems );

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
                final KW kw = kwAdapter.getItem( binding.toolbarSpinner.getSelectedItemPosition() );
                final Calendar calendar = Calendar.getInstance();
                // kw start date is monday
                calendar.setTime( kw.getStartDate() );
                // weekday is a integer i.e. monday is 0
                calendar.add( Calendar.DAY_OF_WEEK, item.getWeekday() );
                final String weekdayAsWord = localizedWeekdays[ item.getWeekday() ];
                if( DateUtils.isToday( calendar.getTimeInMillis() ) ) {
                    return weekdayAsWord.concat( todayAsWord );
                }
                return weekdayAsWord;
            }
        }) );
        viewModel.getCalendarWeeks().observe(this, kws -> {
            binding.toolbarSpinner.setOnItemSelectedListener( null );
            kwAdapter.setItems( kws );
            binding.toolbarSpinner.setSelection( findIndexOfSelectedItem( kws ), false );
            binding.toolbarSpinner.setOnItemSelectedListener( MenuActivity.this );
        });
        viewModel.getMenus().observe(this, adapter::submitList);

        viewModel.getNumberOfChangedOrders().observe( this, (changedOrdersCount) -> {
            if( changedOrdersCount > 0 && actionMode == null ) {
                actionMode = startSupportActionMode(this );
            } else if( actionMode != null ) {
                if( changedOrdersCount == 0 ) {
                    actionMode.finish();
                } else {
                    actionMode.setTitle( getResources().getQuantityString( R.plurals.changed_orders, changedOrdersCount, changedOrdersCount ) );
                }
            }
        });

        if( savedInstanceState == null ) {
            viewModel.loadCalendarWeek( calculateStartOfCalendarWeekInSeconds() );
        }
    }

    private String calculateStartOfCalendarWeekInSeconds() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
        return String.valueOf( calendar.getTimeInMillis() / 1000 );
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
        viewModel.loadCalendarWeek( kwAdapter.getItem( position ).getValue() );
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
        mode.getMenuInflater().inflate( R.menu.menu_list_cab_menu, menu );
        final Integer quantity = viewModel.getNumberOfChangedOrders().getValue();
        mode.setTitle( getResources().getQuantityString( R.plurals.changed_orders, quantity, quantity ) );
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, android.view.Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch( item.getItemId() ) {
            case R.id.action_change:
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        viewModel.resetChangedOrders();
    }
}
