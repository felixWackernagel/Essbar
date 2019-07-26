package de.wackernagel.essbar.ui.pojos;

import android.content.Context;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.wackernagel.essbar.R;
import de.wackernagel.essbar.ui.lists.Listable;

public class Section implements Listable {

    private final long id;
    private final Weekday weekday;
    private final Date date;

    public Section( final Weekday weekday, final Type type, final Date date ) {
        this.weekday = weekday;
        this.id = type.getNumber() + ( 5 * weekday.getNumber() );
        this.date = date;
    }

    public String getTitle( @NonNull final Context context ) {
        if( DateUtils.isToday( date.getTime() ) ) {
            return context.getString( R.string.date_today );
        }
        if( de.wackernagel.essbar.utils.DateUtils.isYesterday( date ) ) {
            return context.getString( R.string.date_yesterday );
        }
        String pattern = context.getString( R.string.date_past_year );
        if( de.wackernagel.essbar.utils.DateUtils.isCurrentWeekOfYear( date ) ) {
            pattern = context.getString( R.string.date_day_of_week );
        } else if( de.wackernagel.essbar.utils.DateUtils.isCurrentYear( date ) ) {
            pattern = context.getString( R.string.date_past );
        }
        return new SimpleDateFormat( pattern, Locale.getDefault() ).format( date );
    }

    public boolean isEditable() {
        return (DateUtils.isToday( date.getTime() ) && Calendar.getInstance().get( Calendar.HOUR_OF_DAY ) < 8) ||
                date.after( new Date() );
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public long getId() {
        return id;
    }
}
