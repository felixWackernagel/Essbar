package de.wackernagel.essbar.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.android.support.AndroidSupportInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.FragmentMenuListBinding;
import de.wackernagel.essbar.ui.pojos.CalendarWeek;
import de.wackernagel.essbar.ui.pojos.Menu;
import de.wackernagel.essbar.ui.viewModels.MenuViewModel;
import de.wackernagel.essbar.utils.SectionItemDecoration;

import static de.wackernagel.essbar.utils.ViewUtils.spToPx;

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
    private ToolbarSpinnerAdapter<CalendarWeek> calendarWeeksAdapter;
    private ActionMode actionMode;
    private int statusBarColor;

    private TextView calendarWeekNumberView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        viewModel.getCalendarWeek().observe( getViewLifecycleOwner(), calendarWeek -> {
            if( calendarWeekNumberView != null )
                calendarWeekNumberView.setText( String.valueOf( viewModel.calculateCalendarWeek( calendarWeek ) ) );
        } );

        setupToolbar();
        setupRecyclerView();
        setupContextualActionBar();

        if( savedInstanceState == null ) {
            viewModel.loadCurrentCalendarWeek();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull android.view.Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate( R.menu.menu_list, menu );
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull android.view.Menu menu) {
        super.onPrepareOptionsMenu(menu);
        setSelectedCalendarWeek( menu );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch( item.getItemId() ) {
            case R.id.action_calendar_week:
                CalendarWeekSelectorFragment.newInstance().show( requireFragmentManager(), "calendar_week_selector" );
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSelectedCalendarWeek(@NonNull final android.view.Menu menu ) {
        final MenuItem item = menu.findItem(R.id.action_calendar_week);
        if( item != null ) {
            final View actionView = item.getActionView();
            actionView.setOnClickListener( v -> menu.performIdentifierAction( item.getItemId(), 0 ) );
            calendarWeekNumberView = actionView.findViewById( android.R.id.text1 );
            calendarWeekNumberView.setText( String.valueOf( viewModel.getSelectedCalendarWeek() ) );
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

        calendarWeeksAdapter = new ToolbarSpinnerAdapter<CalendarWeek>( themedContext ) {
            @Override
            CharSequence getTitle( final int position, boolean forDropDown ) {
                final CalendarWeek item = getItem( position );
                if( forDropDown ) {
                    return item.getDateRange();
                }

                // style like Title and Subtitle
                final String selectedTitle = getString( R.string.menu_fragment_title, item.getDateRange() );
                final int spanStart = selectedTitle.length() - item.getDateRange().length();
                final int spanEnd = selectedTitle.length();
                final SpannableStringBuilder ssb = new SpannableStringBuilder( selectedTitle );
                ssb.setSpan( new AbsoluteSizeSpan( spToPx( 16f, requireContext() ) ), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan( new TypefaceSpan( "sans-serif" /* roboto regular */ ), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return ssb;
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

    private int findIndexOfSelectedItem( final List<CalendarWeek> calendarWeeks) {
        if( calendarWeeks != null ) {
            final int size = calendarWeeks.size();
            for( int index = 0; index < size; index++ ) {
                if( calendarWeeks.get( index ).isSelected() ) {
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
                if( position < 0 ) {
                    return false;
                }
                // first item or when current and previous position have different weekdays
                return position == 0 || adapter.getListItem( position - 1 ).getWeekday() != adapter.getListItem( position ).getWeekday();
            }

            @Override
            public CharSequence getSectionHeader( int position ) {
                final Menu item = adapter.getListItem( position );
                final CalendarWeek calendarWeek = calendarWeeksAdapter.getItem( binding.toolbarSpinner.getSelectedItemPosition() );
                final Calendar calendar = Calendar.getInstance();
                // calendarWeek start date is monday
                calendar.setTime( calendarWeek.getStartDate() );
                // weekday is a integer i.e. monday is 0
                calendar.add( Calendar.DAY_OF_WEEK, item.getWeekday().getNumber() );
                final String weekdayAsWord = localizedWeekdays[ item.getWeekday().getNumber() ];
                if( DateUtils.isToday( calendar.getTimeInMillis() ) ) {
                    return weekdayAsWord.concat( todayAsWord );
                }
                return weekdayAsWord;
            }
        }) );

        viewModel.getMenus().observe( getViewLifecycleOwner(), adapter::submitList);
    }

    private void updateNumberOfChangedMenus(@Nonnull final Menu menu, final boolean isOrdered ) {
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

        final Window window = requireActivity().getWindow();
        statusBarColor = window.getStatusBarColor();
        window.setStatusBarColor(ContextCompat.getColor( requireContext(), R.color.actionModeStatusBar));

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
                MenuConfirmationFragment.newInstance().show( requireFragmentManager().beginTransaction(), MenuConfirmationFragment.TAG );
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        requireActivity().getWindow().setStatusBarColor(statusBarColor);
        actionMode = null;
        viewModel.resetChangedOrders();
    }
}
