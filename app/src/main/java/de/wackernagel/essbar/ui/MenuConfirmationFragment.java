package de.wackernagel.essbar.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.FragmentMenuConfirmationBinding;
import de.wackernagel.essbar.ui.lists.DataBindingListAdapter;
import de.wackernagel.essbar.ui.pojos.ChangedMenu;
import de.wackernagel.essbar.ui.viewModels.MenuViewModel;
import de.wackernagel.essbar.utils.SectionItemDecoration;

public class MenuConfirmationFragment extends FullscreenDialogFragment {

    static String TAG = "MenuConfirmationFragment";

    static MenuConfirmationFragment newInstance() {
        final MenuConfirmationFragment fragment = new MenuConfirmationFragment();
        fragment.setArguments(Bundle.EMPTY);
        return fragment;
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentMenuConfirmationBinding binding;
    private MenuViewModel viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this );
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        binding = FragmentMenuConfirmationBinding.inflate( inflater, container, false );
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider( requireActivity(), viewModelFactory ).get( MenuViewModel.class );
        viewModel.getSuccessfulOrder().observe( getViewLifecycleOwner(), successEvent -> {
        {
            if( Boolean.TRUE.equals( successEvent.getContentIfNotHandled() ) ) {
                viewModel.resetChangedOrders();
                dismiss();
            }
        }
        });

        setupToolbar();
        setupRecyclerView();

        if( savedInstanceState == null ) {
            viewModel.loadChangedMenusToConfirm();
        }
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationIcon( R.drawable.ic_close_black_24dp );
        binding.toolbar.setNavigationOnClickListener(v -> dismiss());
        binding.toolbar.setTitle( R.string.menu_confirmation_fragment_title );
        binding.toolbar.inflateMenu( R.menu.menu_confirmation_menu );
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if( item.getItemId() == R.id.action_buy ) {
                viewModel.postChangedAndConfirmedMenus();
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        final DataBindingListAdapter<ChangedMenu> adapter = new DataBindingListAdapter<>();
        binding.recyclerView.setLayoutManager( new LinearLayoutManager( null ) );
        binding.recyclerView.setHasFixedSize( true );
        binding.recyclerView.setAdapter( adapter );
        binding.recyclerView.addItemDecoration( new SectionItemDecoration( requireContext(), false, new SectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection( int position ) {
                final int itemPosition = Math.max( 0, position );
                // first item or when current and previous position have different weekdays
                return itemPosition == 0 || adapter.getListItem( Math.max( 0, itemPosition - 1 ) ).getWeekday() != adapter.getListItem( itemPosition ).getWeekday();
            }

            @Override
            public CharSequence getSectionHeader( int position ) {
                final int itemPosition = Math.max( 0, position );
                final ChangedMenu item = adapter.getListItem( itemPosition );
                return getResources().getStringArray( R.array.weekdays )[ item.getWeekday().getNumber() ];
            }
        }) );
        viewModel.getChangedMenusToConfirm().observe( getViewLifecycleOwner(), adapter::submitList );
    }
}
