package de.wackernagel.essbar.ui.lists;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import de.wackernagel.essbar.R;
import de.wackernagel.essbar.ui.pojos.ChangedMenu;

public class DataBindingListAdapter<T extends Listable> extends ListAdapter<T, DataBindingViewHolder<T>> {

    public DataBindingListAdapter() {
        this( new ListableItemCallback<>() );
    }

    private DataBindingListAdapter( @NonNull final DiffUtil.ItemCallback<T> diffCallback ) {
        super(diffCallback);
        setHasStableIds( true );
    }

    @Override
    public long getItemId(int position) {
        return getItem( position ).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewType( getItem( position ) );
    }

    public T getListItem( int position ) {
        return getItem( position );
    }

    @LayoutRes
    private int getItemViewType( T item ) {
        if( item instanceof ChangedMenu ) {
            return R.layout.item_changed_menu;
        }
        throw new IllegalStateException( "Can resolve viewType for class '" + item.getClass().getSimpleName() + "'!" );
    }

    @NonNull
    @Override
    public DataBindingViewHolder<T> onCreateViewHolder( @NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from( parent.getContext() );
        final ViewDataBinding binding = DataBindingUtil.inflate( layoutInflater, viewType, parent, false );
        return new DataBindingViewHolder<>( binding );
    }

    @Override
    public void onBindViewHolder(@NonNull DataBindingViewHolder<T> holder, int position) {
        final T data = getItem( position );
        holder.bind( data );
    }

}
