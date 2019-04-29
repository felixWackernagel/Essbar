package de.wackernagel.essbar.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.android.support.AndroidSupportInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.FragmentMenuListBinding;
import de.wackernagel.essbar.ui.viewModels.MenuViewModel;
import de.wackernagel.essbar.utils.SectionItemDecoration;

public class MenuListFragment extends ToolbarFragment implements AdapterView.OnItemSelectedListener, ActionMode.Callback {

    static MenuListFragment newInstance() {
        final MenuListFragment fragment = new MenuListFragment();
        fragment.setArguments( Bundle.EMPTY );
        return fragment;
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentMenuListBinding binding;
    private MenuViewModel viewModel;
    private ToolbarSpinnerAdapter<KW> calendarWeeksAdapter;
    private ActionMode actionMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMenuListBinding.inflate( inflater, container, false );
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this );
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider( requireActivity(), viewModelFactory ).get( MenuViewModel.class );

        setupToolbar();
        setupRecyclerView();
        setupContextualActionBar();

        if( savedInstanceState == null ) {
            viewModel.loadCurrentCalendarWeek();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);

        final ActionBar actionBar = getSupportActionBar();
        Context themedContext = getContext();
        if( actionBar != null ) {
            actionBar.setDisplayShowTitleEnabled(false);
            themedContext = actionBar.getThemedContext();
        }

        calendarWeeksAdapter = new ToolbarSpinnerAdapter<KW>( themedContext ) {
            @Override
            String getTitle(int position) {
                return getItem( position ).getName();
            }

            @Override
            int getIconVisibility(int position) {
                return getItem( position ).isSelected() ? View.VISIBLE : View.GONE;
            }
        };
        binding.toolbarSpinner.setAdapter(calendarWeeksAdapter);
        binding.toolbarSpinner.setOnItemSelectedListener(this );

        viewModel.getCalendarWeeks().observe( getViewLifecycleOwner(), calendarWeeks -> {
            binding.toolbarSpinner.setOnItemSelectedListener( null );
            calendarWeeksAdapter.setItems( calendarWeeks );
            binding.toolbarSpinner.setSelection( findIndexOfSelectedItem( calendarWeeks ), false );
            binding.toolbarSpinner.setOnItemSelectedListener( MenuListFragment.this );
        });
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

    private void setupRecyclerView() {
        final String[] localizedWeekdays = getResources().getStringArray( R.array.weekdays );
        final String todayAsWord = getString( R.string.today );

        final MenuListAdapter adapter = new MenuListAdapter();
        adapter.setOnMenuStatusChangedListener( this::updateNumberOfChangedMenus );
        viewModel.getMenusOrderStatus().observe( getViewLifecycleOwner(), adapter::setCheckedItems );

        binding.recyclerView.setLayoutManager( new LinearLayoutManager( null ) );
        binding.recyclerView.setHasFixedSize( true );
        binding.recyclerView.setAdapter( adapter );
        binding.recyclerView.addItemDecoration( new SectionItemDecoration( requireContext(), false, new SectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection( int position ) {
                // first item or when current and previous position have different weekdays
                return position <= 0 || adapter.getListItem( position - 1 ).getWeekday() != adapter.getListItem( position ).getWeekday();
            }

            @Override
            public CharSequence getSectionHeader( int position ) {
                final Menu item = adapter.getListItem( position );
                final KW kw = calendarWeeksAdapter.getItem( binding.toolbarSpinner.getSelectedItemPosition() );
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

        viewModel.getMenus().observe( getViewLifecycleOwner(), adapter::submitList);
    }

    private void updateNumberOfChangedMenus( @Nonnull final Menu menu, final boolean isOrdered ) {
        if( menu.isOrdered() != isOrdered ) {
            viewModel.incrementNumberOfChangedOrders();
        } else {
            viewModel.decrementNumberOfChangedOrders();
        }
    }

    private void setupContextualActionBar() {
        viewModel.getNumberOfChangedOrders().observe( getViewLifecycleOwner(), (changedOrdersCount) -> {
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        viewModel.loadCalendarWeek( calendarWeeksAdapter.getItem( position ).getValue() );
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
        mode.getMenuInflater().inflate( R.menu.menu_list_cab_menu, menu );
        final Integer quantity = viewModel.getNumberOfChangedOrders().getValue();
        final int quantityValue = quantity == null ? 0 : quantity;
        mode.setTitle( getResources().getQuantityString( R.plurals.changed_orders, quantityValue, quantityValue ) );
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, android.view.Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch( item.getItemId() ) {
            case R.id.action_change_menus_order_status:
                MenuConfirmationFragment.newInstance().show( requireFragmentManager(), MenuConfirmationFragment.TAG );
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
