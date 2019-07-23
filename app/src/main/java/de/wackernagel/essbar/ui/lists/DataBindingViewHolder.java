package de.wackernagel.essbar.ui.lists;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import de.wackernagel.essbar.BR;

public class DataBindingViewHolder<T> extends RecyclerView.ViewHolder {
    private final ViewDataBinding binding;

    DataBindingViewHolder( @NonNull final ViewDataBinding viewDataBinding ) {
        super( viewDataBinding.getRoot() );
        this.binding = viewDataBinding;
    }

    public void bind( T data, @Nullable final ViewModel viewModel ) {
        binding.setVariable( BR.item, data );
        binding.setVariable( BR.viewModel, viewModel );
        binding.executePendingBindings();
    }
}
