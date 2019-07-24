package de.wackernagel.essbar.ui.lists;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import de.wackernagel.essbar.room.Customer;
import de.wackernagel.essbar.ui.pojos.CalendarWeek;
import de.wackernagel.essbar.ui.pojos.ChangedMenu;
import de.wackernagel.essbar.ui.pojos.Menu;
import de.wackernagel.essbar.ui.pojos.Section;

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
        if( newItem instanceof Customer && oldItem instanceof Customer ) {
            return customersSame( (Customer) oldItem, (Customer) newItem );
        }
        if( newItem instanceof Menu && oldItem instanceof Menu ) {
            return menusSame( (Menu) oldItem, (Menu) newItem );
        }
        if( newItem instanceof Section && oldItem instanceof Section ) {
            return sectionsSame( (Section) oldItem, (Section) newItem );
        }
        throw new IllegalStateException( "Incomplete implementation to compare old with new item of class '" + oldItem + "', '" + newItem + "'!" );
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

    private boolean customersSame(Customer oldItem, Customer newItem) {
        return TextUtils.equals( oldItem.getNumber(), newItem.getNumber() ) &&
                TextUtils.equals( oldItem.getEncryptedPassword(), newItem.getEncryptedPassword() ) &&
                TextUtils.equals( oldItem.getEncryptionIv(), newItem.getEncryptionIv() ) &&
                TextUtils.equals( oldItem.getName(), newItem.getName() );
    }

    private boolean menusSame( Menu oldItem, Menu newItem ) {
        return oldItem.getWeekday().equals( newItem.getWeekday() ) &&
                TextUtils.equals( oldItem.getMenuName(), newItem.getMenuName() ) &&
                oldItem.getTyp().equals( newItem.getTyp() ) &&
                oldItem.isOrdered() == newItem.isOrdered() &&
                oldItem.isActualOrdered() == newItem.isActualOrdered() &&
                oldItem.isEditable() == newItem.isEditable();
    }

    private boolean sectionsSame( Section oldItem, Section newItem ) {
        return oldItem.getWeekday().equals( newItem.getWeekday() ) &&
                oldItem.getDate().equals( newItem.getDate() );
    }

}
