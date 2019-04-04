package de.wackernagel.essbar.ui;

import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import de.wackernagel.essbar.R;

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

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuViewHolder(LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_menu, parent, false ));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        final Menu item = getItem( position );
        holder.textView.setText( item.getMenuName() );
        holder.checkBox.setEnabled( item.isEditable() );
        holder.checkBox.setChecked( checkedItems.get( item.getId() ) );
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked ) -> {
            // Check if change was by button press or setter based.
            if( buttonView.isPressed() ) {
                final int clickedItemId = getItem( holder.getAdapterPosition() ).getId();
                checkedItems.put( clickedItemId, !checkedItems.get( clickedItemId ) );
            }
        });
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById( R.id.textView );
            checkBox = itemView.findViewById( R.id.checkbox );
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
