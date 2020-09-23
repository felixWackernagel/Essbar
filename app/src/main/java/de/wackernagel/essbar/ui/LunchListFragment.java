package de.wackernagel.essbar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import javax.inject.Inject;

import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.FragmentLunchListBinding;
import de.wackernagel.essbar.ui.lists.DataBindingListAdapter;
import de.wackernagel.essbar.ui.pojos.MealListItem;
import de.wackernagel.essbar.ui.viewModels.LunchListViewModel;
import de.wackernagel.essbar.utils.SectionItemDecoration;

public class LunchListFragment extends EssbarFragment {

    static LunchListFragment newInstance() {
        final LunchListFragment fragment = new LunchListFragment();
        fragment.setArguments( Bundle.EMPTY );
        return fragment;
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentLunchListBinding binding;
    private LunchListViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLunchListBinding.inflate( inflater, container, false );
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider( requireActivity(), viewModelFactory ).get( LunchListViewModel.class );
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull android.view.Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate( R.menu.menu_lunch_list, menu );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if( item.getItemId() == R.id.action_change_order ) {
            startMenuActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if( actionBar != null ) {
            actionBar.setTitle( R.string.app_name );
        }
    }

    private void setupRecyclerView() {
        final DataBindingListAdapter<MealListItem> adapter = new DataBindingListAdapter<>();
        binding.recyclerView.addItemDecoration( new SectionItemDecoration( requireContext(), false, new SectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection( int position ) {
                return position <= 0;
            }

            @Override
            public CharSequence getSectionHeader( int position ) {
                return getString(R.string.lunch_section);
            }
        }) );
        binding.recyclerView.setLayoutManager( new LinearLayoutManager( null ) );
        binding.recyclerView.setHasFixedSize( true );
        binding.recyclerView.setAdapter( adapter );
        viewModel.getLunchesFromCurrentWeekOfYear().observe( getViewLifecycleOwner(), meals -> {
            if( meals.isEmpty() ) {
                startMenuActivity();
            } else {
                adapter.submitList( meals );
            }
        } );
    }

    private void startMenuActivity() {
        startActivity( new Intent( getContext(), LoginActivity.class ) );
        requireActivity().finish();
    }
}
