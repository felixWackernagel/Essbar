package de.wackernagel.essbar.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.android.support.AndroidSupportInjection;
import de.wackernagel.essbar.databinding.FragmentCalendarWeekSelectorBinding;
import de.wackernagel.essbar.ui.pojos.CalendarWeek;
import de.wackernagel.essbar.ui.viewModels.MenuViewModel;

public class CalendarWeekSelectorFragment extends BottomSheetDialogFragment implements CalendarWeekListAdapter.OnCalendarWeekClickListener {

    static CalendarWeekSelectorFragment newInstance() {
        final Bundle args = new Bundle();
        final CalendarWeekSelectorFragment fragment = new CalendarWeekSelectorFragment();
        fragment.setArguments(args);
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

        final CalendarWeekListAdapter adapter = new CalendarWeekListAdapter( this );
        binding.recyclerView.setLayoutManager( new LinearLayoutManager( null ) );
        binding.recyclerView.setHasFixedSize( true );
        binding.recyclerView.setAdapter( adapter );

        viewModel = new ViewModelProvider( requireActivity(), viewModelFactory ).get( MenuViewModel.class );
        viewModel.getCalendarWeeks().observe( getViewLifecycleOwner(), adapter::submitList );
    }

    @Override
    public void onCalendarWeekClick( @NonNull final CalendarWeek calendarWeek ) {
        viewModel.loadCalendarWeek( calendarWeek.getValue() );
        dismiss();
    }
}
