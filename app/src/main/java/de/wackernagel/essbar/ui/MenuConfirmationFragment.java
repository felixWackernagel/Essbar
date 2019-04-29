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
import dagger.android.support.AndroidSupportInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.databinding.FragmentMenuConfirmationBinding;
import de.wackernagel.essbar.ui.viewModels.MenuViewModel;

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

        binding.toolbar.setNavigationIcon( R.drawable.ic_close_black_24dp );
        binding.toolbar.setNavigationOnClickListener(v -> dismiss());
        binding.toolbar.setTitle( "Bestelländerungen" );

        viewModel.getMenuReviewDocument().observe(getViewLifecycleOwner(), resource -> {
            if( resource != null && resource.isSuccess() ) {
                binding.textView.setText( resource.getResource().toString() );
            } else if( resource != null ) {
                binding.textView.setText( resource.getError().toString() );
            } else {
                binding.textView.setText( "no resource error" );
            }
        });
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
