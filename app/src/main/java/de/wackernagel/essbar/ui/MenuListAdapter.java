package de.wackernagel.essbar.ui;

import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import de.wackernagel.essbar.R;

import static de.wackernagel.essbar.BR.adapter;
import static de.wackernagel.essbar.BR.holder;
import static de.wackernagel.essbar.BR.obj;

public class MenuListAdapter extends ListAdapter<Menu, MenuListAdapter.MenuViewHolder> {
    private SparseBooleanArray checkedItems;

    MenuListAdapter() {
        super( new MenuItemCallback() );
        this.checkedItems = new SparseBooleanArray( 20 );
        setHasStableIds( true );
    }

    @Override
    public long getItemId(int position) {
        return Integer.valueOf( getItem( position ).getId() ).longValue();
    }

    @Override
    public void submitList(@Nullable List<Menu> list) {
        super.submitList(list);
        if( list != null ) {
            for( Menu item : list ) {
                checkedItems.put( item.getId(), item.isOrdered() );
            }
        }
    }

    Menu getListItem(int position ) {
        return getItem( position );
    }

    @Override
    public int getItemViewType(int position) {
        if( getItem( position ).getMenuTyp() == 3 && !getItem( position ).isPaused() )
            return R.layout.item_menu_lunch;
        return R.layout.item_menu;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from( parent.getContext() );
        final ViewDataBinding binding = DataBindingUtil.inflate( layoutInflater, viewType, parent, false );
        binding.setVariable( adapter, this );
        return new MenuViewHolder( binding, checkedItems);
    }

    public boolean isMenuChecked( int menuId ) {
        return checkedItems.get( menuId );
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        final Menu item = getItem( position );
        holder.bind( item );
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        final ViewDataBinding binding;
        final SparseBooleanArray checkedItems;
        Menu menu;

        MenuViewHolder(@NonNull ViewDataBinding binding, SparseBooleanArray checkedItems) {
            super( binding.getRoot() );
            this.binding = binding;
            this.checkedItems = checkedItems;
        }

        void bind( final Menu menu ) {
            this.menu = menu;
            binding.setVariable( obj, menu );
            binding.setVariable( holder, this );
            binding.executePendingBindings(); // run binding immediately
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // Check if change was by button press or setter based.
            if( buttonView.isPressed() ) {
                checkedItems.put( menu.getId(), !checkedItems.get( menu.getId() ) );
            }
        }
    }

    public static class MenuItemCallback extends DiffUtil.ItemCallback<Menu> {
        @Override
        public boolean areItemsTheSame(@NonNull Menu oldItem, @NonNull Menu newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Menu oldItem, @NonNull Menu newItem) {
            return oldItem.getWeekday() == newItem.getWeekday() &&
                    TextUtils.equals( oldItem.getMenuName(), newItem.getMenuName() ) &&
                    oldItem.getMenuTyp() == newItem.getMenuTyp() &&
                    oldItem.isOrdered() == newItem.isOrdered() &&
                    oldItem.isEditable() == newItem.isEditable();
        }
    }
}
