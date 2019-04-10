package de.wackernagel.essbar.ui;

import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import de.wackernagel.essbar.R;

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
        return R.layout.item_menu;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from( parent.getContext() );
        final ViewDataBinding binding = DataBindingUtil.inflate( layoutInflater, viewType, parent, false );
        return new MenuViewHolder( binding );
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        final Menu item = getItem( position );
        holder.binding.setVariable( obj, item );

        // DONE in Databinding
        //        holder.binding.textView.setText( item.getMenuName() );

        // TODO
        // https://medium.com/@alzahm/thank-you-for-your-great-post-i-learned-a-lot-e24de2371166
//        holder.binding.imageView.setVisibility( item.isEditable() ? View.GONE : View.VISIBLE );

//        holder.binding.checkbox.setVisibility( item.isEditable() ? View.VISIBLE : View.GONE );
//        holder.binding.checkbox.setEnabled( item.isEditable() );
//        holder.binding.checkbox.setChecked( checkedItems.get( item.getId() ) );
//        holder.binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked ) -> {
//            // Check if change was by button press or setter based.
//            if( buttonView.isPressed() ) {
//                final int clickedItemId = getItem( holder.getAdapterPosition() ).getId();
//                checkedItems.put( clickedItemId, !checkedItems.get( clickedItemId ) );
//            }
//        });

        holder.binding.executePendingBindings(); // run binding immediately
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        final ViewDataBinding binding;

        MenuViewHolder(@NonNull ViewDataBinding binding) {
            super( binding.getRoot() );
            this.binding = binding;
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
