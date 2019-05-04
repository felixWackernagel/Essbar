package de.wackernagel.essbar.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import de.wackernagel.essbar.BR;
import de.wackernagel.essbar.databinding.ItemChangedMenuBinding;

public class ChangedMenuListAdapter extends ListAdapter<ChangedMenu, ChangedMenuListAdapter.ChangedMenuViewHolder> {

    ChangedMenuListAdapter() {
        super( new ChangedMenuItemCallback() );
        setHasStableIds( true );
    }

    ChangedMenu getListItem( final int position ) {
        return getItem( position );
    }

    @Override
    public long getItemId( final int position) {
        return getItem( position ).getId();
    }

    @NonNull
    @Override
    public ChangedMenuViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from( parent.getContext() );
        return new ChangedMenuViewHolder( ItemChangedMenuBinding.inflate( layoutInflater, parent, false ) );
    }

    @Override
    public void onBindViewHolder(@NonNull final ChangedMenuViewHolder holder, final int position) {
        holder.bind( getItem( position) );
    }

    public static class ChangedMenuItemCallback extends DiffUtil.ItemCallback<ChangedMenu> {
        @Override
        public boolean areItemsTheSame(@NonNull final ChangedMenu oldItem, @NonNull final ChangedMenu newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull final ChangedMenu oldItem, @NonNull final ChangedMenu newItem) {
            return TextUtils.equals( oldItem.getDate(), newItem.getDate() ) &&
                    TextUtils.equals( oldItem.getName(), newItem.getName() ) &&
                    TextUtils.equals( oldItem.getNewQuantity(), newItem.getNewQuantity() ) &&
                    TextUtils.equals( oldItem.getOldQuantity(), newItem.getOldQuantity() );
        }
    }

    static class ChangedMenuViewHolder extends RecyclerView.ViewHolder {
        private final ItemChangedMenuBinding binding;

        ChangedMenuViewHolder( @NonNull final ItemChangedMenuBinding binding ) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind( @Nonnull final ChangedMenu menu ) {
            binding.setVariable( BR.menu, menu );
            binding.executePendingBindings(); // run binding immediately
        }
    }
}
