package de.wackernagel.essbar.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.ui.pojos.CalendarWeek;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;
import static de.wackernagel.essbar.BR.obj;

public class CalendarWeekListAdapter extends ListAdapter<CalendarWeek, CalendarWeekListAdapter.CalendarWeekViewHolder> {

    interface OnCalendarWeekClickListener {
        void onCalendarWeekClick( CalendarWeek calendarWeek );
    }

    @NonNull
    private OnCalendarWeekClickListener calendarWeekClickListener;

    CalendarWeekListAdapter( @NonNull final OnCalendarWeekClickListener calendarWeekClickListener  ) {
        super( new CalendarWeekItemCallback() );
        this.calendarWeekClickListener = calendarWeekClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_calendar_week;
    }

    @NonNull
    @Override
    public CalendarWeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from( parent.getContext() );
        final ViewDataBinding binding = DataBindingUtil.inflate( layoutInflater, viewType, parent, false );
        return new CalendarWeekViewHolder( binding );
    }

    @Override
    public void onBindViewHolder(@NonNull final CalendarWeekViewHolder holder, final int position) {
        holder.bindCalendarWeek( getItem( position ) );
        holder.itemView.setOnClickListener( v -> {
            final int adapterPosition = holder.getAdapterPosition();
            if( adapterPosition != NO_POSITION ) {
                calendarWeekClickListener.onCalendarWeekClick( getItem( adapterPosition ) );
            }
        } );
    }

    static class CalendarWeekItemCallback extends DiffUtil.ItemCallback<CalendarWeek> {

        @Override
        public boolean areItemsTheSame(@NonNull CalendarWeek oldItem, @NonNull CalendarWeek newItem) {
            return oldItem.getValue().equals( newItem.getValue() );
        }

        @Override
        public boolean areContentsTheSame(@NonNull CalendarWeek oldItem, @NonNull CalendarWeek newItem) {
            return TextUtils.equals( oldItem.getDateRange(), newItem.getDateRange() ) &&
                    oldItem.isSelected() == newItem.isSelected();
        }
    }

    public static class CalendarWeekViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        final ViewDataBinding binding;

        CalendarWeekViewHolder( @NonNull final ViewDataBinding binding ) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindCalendarWeek( final CalendarWeek calendarWeek ) {
            binding.setVariable( obj, calendarWeek );
            binding.executePendingBindings(); // run binding immediately
        }
    }
}
