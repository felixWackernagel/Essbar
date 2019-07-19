package de.wackernagel.essbar.ui.lists;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import de.wackernagel.essbar.ui.pojos.CalendarWeek;
import de.wackernagel.essbar.ui.pojos.ChangedMenu;

public class ListableItemCallback<T extends Listable> extends DiffUtil.ItemCallback<T> {

    @Override
    public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
        return oldItem.getClass().equals( newItem.getClass() ) && oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
        if( newItem instanceof ChangedMenu && oldItem instanceof ChangedMenu ) {
            return changedMenusSame( (ChangedMenu) oldItem, (ChangedMenu) newItem );
        }
        if( newItem instanceof CalendarWeek && oldItem instanceof CalendarWeek ) {
            return calendarWeeksSame( (CalendarWeek) oldItem, (CalendarWeek) newItem );
        }
        return false;
    }

    private boolean changedMenusSame(ChangedMenu oldItem, ChangedMenu newItem) {
        return TextUtils.equals( oldItem.getDate(), newItem.getDate() ) &&
                TextUtils.equals( oldItem.getName(), newItem.getName() ) &&
                TextUtils.equals( oldItem.getNewQuantity(), newItem.getNewQuantity() ) &&
                TextUtils.equals( oldItem.getOldQuantity(), newItem.getOldQuantity() );
    }

    private boolean calendarWeeksSame(CalendarWeek oldItem, CalendarWeek newItem) {
        return TextUtils.equals( oldItem.getDateRange(), newItem.getDateRange() ) &&
                oldItem.isSelected() == newItem.isSelected();
    }

}
