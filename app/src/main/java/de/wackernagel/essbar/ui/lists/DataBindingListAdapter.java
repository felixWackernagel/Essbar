package de.wackernagel.essbar.ui.lists;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import de.wackernagel.essbar.R;
import de.wackernagel.essbar.room.Customer;
import de.wackernagel.essbar.ui.pojos.CalendarWeek;
import de.wackernagel.essbar.ui.pojos.ChangedMenu;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

public class DataBindingListAdapter<T extends Listable> extends ListAdapter<T, DataBindingViewHolder<T>> {

    private DataBindingClickListener<T> clickListener;

    public DataBindingListAdapter() {
        this( new ListableItemCallback<>() );
    }

    private DataBindingListAdapter( @NonNull final DiffUtil.ItemCallback<T> diffCallback ) {
        super(diffCallback);
        setHasStableIds( true );
    }

    public void setClickListener( @Nullable final DataBindingClickListener<T> clickListener) {
        this.clickListener = clickListener;
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
        if( item instanceof CalendarWeek ) {
            return R.layout.item_calendar_week;
        }
        if( item instanceof Customer ) {
            return R.layout.item_customer;
        }
        throw new IllegalStateException( "Incomplete implementation to resolve viewType for class '" + item.getClass().getSimpleName() + "'!" );
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
        holder.itemView.setOnClickListener( v -> {
            final int adapterPosition = holder.getAdapterPosition();
            if( adapterPosition != NO_POSITION ) {
                clickListener.onBindingClicked( getItem( adapterPosition ) );
            }
        } );
    }

}
