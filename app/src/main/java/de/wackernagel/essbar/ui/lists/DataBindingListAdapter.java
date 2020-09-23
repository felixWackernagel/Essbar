package de.wackernagel.essbar.ui.lists;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import de.wackernagel.essbar.R;
import de.wackernagel.essbar.room.Customer;
import de.wackernagel.essbar.ui.pojos.CalendarWeek;
import de.wackernagel.essbar.ui.pojos.ChangedMenu;
import de.wackernagel.essbar.ui.pojos.MealListItem;
import de.wackernagel.essbar.ui.pojos.Menu;
import de.wackernagel.essbar.ui.pojos.Section;
import de.wackernagel.essbar.ui.pojos.Type;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

public class DataBindingListAdapter<ITEM extends Listable> extends ListAdapter<ITEM, DataBindingViewHolder<ITEM>> {

    private ViewModel viewModel;
    @Nullable
    private DataBindingClickListener<ITEM> clickListener;

    public DataBindingListAdapter() {
        this( null );
    }

    public DataBindingListAdapter( @Nullable final ViewModel viewModel ) {
        this( new ListableItemCallback<>(), viewModel );
    }

    private DataBindingListAdapter( @NonNull final DiffUtil.ItemCallback<ITEM> diffCallback, @Nullable final ViewModel viewModel ) {
        super(diffCallback);
        this.viewModel = viewModel;
        setHasStableIds( true );
    }

    public void setClickListener( @Nullable final DataBindingClickListener<ITEM> clickListener) {
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

    public ITEM getListItem(int position ) {
        return getItem( position );
    }

    @LayoutRes
    private int getItemViewType( ITEM item ) {
        if( item instanceof ChangedMenu ) {
            return R.layout.item_changed_menu;
        }
        if( item instanceof CalendarWeek ) {
            return R.layout.item_calendar_week;
        }
        if( item instanceof Customer ) {
            return R.layout.item_customer;
        }
        if( item instanceof Menu ) {
            final Menu menu = (Menu) item;
            return ( menu.getTyp() == Type.LUNCH && !menu.isPaused() ) ? R.layout.item_menu_lunch : R.layout.item_menu;
        }
        if( item instanceof Section ) {
            return R.layout.item_menu_section;
        }
        if( item instanceof MealListItem) {
            return R.layout.item_meal;
        }
        throw new IllegalStateException( "Incomplete implementation to resolve viewType for class '" + item.getClass().getSimpleName() + "'!" );
    }

    @NonNull
    @Override
    public DataBindingViewHolder<ITEM> onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from( parent.getContext() );
        final ViewDataBinding binding = DataBindingUtil.inflate( layoutInflater, viewType, parent, false );
        return new DataBindingViewHolder<>( binding );
    }

    @Override
    public void onBindViewHolder(@NonNull DataBindingViewHolder<ITEM> holder, int position) {
        final ITEM data = getItem( position );
        holder.bind( data, viewModel );
        if( clickListener != null ) {
            holder.itemView.setOnClickListener( v -> {
                final int adapterPosition = holder.getAdapterPosition();
                if( adapterPosition != NO_POSITION ) {
                    clickListener.onBindingClicked( getItem( adapterPosition ) );
                }
            } );
        }
    }

}
