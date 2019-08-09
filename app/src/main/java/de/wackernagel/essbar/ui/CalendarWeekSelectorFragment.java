package de.wackernagel.essbar.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.wackernagel.essbar.databinding.FragmentCalendarWeekSelectorBinding;
import de.wackernagel.essbar.ui.lists.DataBindingClickListener;
import de.wackernagel.essbar.ui.lists.DataBindingListAdapter;
import de.wackernagel.essbar.ui.pojos.CalendarWeek;
import de.wackernagel.essbar.ui.viewModels.MenuViewModel;

public class CalendarWeekSelectorFragment extends BottomSheetDialogFragment implements DataBindingClickListener<CalendarWeek> {

    static CalendarWeekSelectorFragment newInstance() {
        final CalendarWeekSelectorFragment fragment = new CalendarWeekSelectorFragment();
        fragment.setArguments( Bundle.EMPTY );
        return fragment;
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MenuViewModel viewModel;
    private FragmentCalendarWeekSelectorBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCalendarWeekSelectorBinding.inflate( inflater, container, false );
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this );
        super.onActivityCreated(savedInstanceState);

        final DataBindingListAdapter<CalendarWeek> adapter = new DataBindingListAdapter<>();
        adapter.setClickListener( this );
        binding.recyclerView.setLayoutManager( new LinearLayoutManager( null ) );
        binding.recyclerView.setHasFixedSize( true );
        binding.recyclerView.setAdapter( adapter );

        viewModel = new ViewModelProvider( requireActivity(), viewModelFactory ).get( MenuViewModel.class );
        viewModel.getCalendarWeeks().observe( getViewLifecycleOwner(), adapter::submitList );
    }

    @Override
    public void onBindingClicked( final CalendarWeek item ) {
        viewModel.loadCalendarWeek( item.getValue() );
        dismiss();
    }
}
