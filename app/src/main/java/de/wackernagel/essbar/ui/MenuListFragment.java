package de.wackernagel.essbar.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import javax.inject.Inject;

import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.FragmentMenuListBinding;
import de.wackernagel.essbar.ui.lists.DataBindingListAdapter;
import de.wackernagel.essbar.ui.lists.Listable;
import de.wackernagel.essbar.ui.viewModels.MenuViewModel;
import de.wackernagel.essbar.utils.DateUtils;

public class MenuListFragment extends EssbarFragment implements ActionMode.Callback {

    static MenuListFragment newInstance() {
        final MenuListFragment fragment = new MenuListFragment();
        fragment.setArguments( Bundle.EMPTY );
        return fragment;
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentMenuListBinding binding;
    private MenuViewModel viewModel;
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
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider( requireActivity(), viewModelFactory ).get( MenuViewModel.class );
        viewModel.getCalendarWeek().observe( getViewLifecycleOwner(), calendarWeek -> {
            if( calendarWeekNumberView != null )
                calendarWeekNumberView.setText( String.valueOf( DateUtils.calculateCalendarWeek( calendarWeek ) ) );
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
        if( item.getItemId() == R.id.action_calendar_week ) {
            CalendarWeekSelectorFragment.newInstance().show( requireFragmentManager(), "calendar_week_selector" );
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        if( actionBar != null ) {
            actionBar.setTitle( R.string.menu_fragment_title );
        }
    }

    private void setupRecyclerView() {
        final DataBindingListAdapter<Listable> adapter = new DataBindingListAdapter<>( viewModel );
        binding.recyclerView.setLayoutManager( new LinearLayoutManager( null ) );
        binding.recyclerView.setHasFixedSize( true );
        binding.recyclerView.setAdapter( adapter );
        viewModel.getMenus().observe( getViewLifecycleOwner(), adapter::submitList);
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
        if( item.getItemId() == R.id.action_change_menus_order_status ) {
            MenuConfirmationFragment.newInstance().show(requireFragmentManager().beginTransaction(), MenuConfirmationFragment.TAG);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        requireActivity().getWindow().setStatusBarColor(statusBarColor);
        actionMode = null;
        viewModel.resetChangedOrders();
    }
}
