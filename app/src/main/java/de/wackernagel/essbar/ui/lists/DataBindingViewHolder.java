package de.wackernagel.essbar.ui.lists;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import de.wackernagel.essbar.BR;

public class DataBindingViewHolder<T> extends RecyclerView.ViewHolder {
    private final ViewDataBinding binding;

    DataBindingViewHolder( @NonNull final ViewDataBinding viewDataBinding ) {
        super( viewDataBinding.getRoot() );
        this.binding = viewDataBinding;
    }

    public void bind( T data ) {
        binding.setVariable( BR.item, data );
        binding.executePendingBindings();
    }
}
