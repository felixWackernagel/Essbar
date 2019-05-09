package de.wackernagel.essbar.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.android.support.AndroidSupportInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.FragmentMenuConfirmationBinding;
import de.wackernagel.essbar.ui.viewModels.MenuViewModel;
import de.wackernagel.essbar.utils.SectionItemDecoration;

public class MenuConfirmationFragment extends DialogFragment {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
    }

    @Override
    public View onCreateView( @Nonnull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        binding = FragmentMenuConfirmationBinding.inflate( inflater, container, false );
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this );
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider( requireActivity(), viewModelFactory ).get( MenuViewModel.class );
        viewModel.getSuccessfulOrder().observe( getViewLifecycleOwner(), success -> {
        {
            // FIXME on second change this closes the fragment immediat
            if( Boolean.TRUE.equals( success ) ) {
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
            switch( item.getItemId() ) {
                case R.id.action_buy:
                    viewModel.postChangedAndConfirmedMenus();
                    return true;
                default:
                    return false;
            }
        });
    }

    private void setupRecyclerView() {
        final ChangedMenuListAdapter adapter = new ChangedMenuListAdapter();
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
                final String[] localizedWeekdays = getResources().getStringArray( R.array.weekdays );
                return localizedWeekdays[ item.getWeekday() ];
            }
        }) );
        viewModel.getChangedMenusToConfirm().observe( getViewLifecycleOwner(), adapter::submitList );
    }

    @Override
    public void onStart() {
        super.onStart();
        applyFullscreenLayout();
    }

    private void applyFullscreenLayout() {
        final Dialog dialog = getDialog();
        if( dialog != null && dialog.getWindow() != null ) {
            dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
        }
    }
}
