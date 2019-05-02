package de.wackernagel.essbar.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.wackernagel.essbar.R;

public class ToolbarSpinnerAdapter<T> extends BaseAdapter {
    private final List<T> items;
    private final LayoutInflater layoutInflater;

    ToolbarSpinnerAdapter( final Context themedContext ) {
        this.items = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from( themedContext );
    }

    void setItems( @Nullable final List<T> newItems ) {
        items.clear();
        if( newItems != null && !newItems.isEmpty() ) {
            this.items.addAll( newItems );
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem( int position ) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    CharSequence getTitle( int position, boolean forDropDown ) {
        return position >= 0 && position < items.size() ? String.valueOf( items.get( position ) ) : "";
    }

    @Override
    public View getDropDownView( final int position, @Nullable final View view, final ViewGroup parent) {
        return createOrUpdateView(position, view, parent, R.layout.toolbar_spinner_item_dropdown, "DROPDOWN");
    }

    @Override
    public View getView( final int position, @Nullable final View view, final ViewGroup parent) {
        return createOrUpdateView(position, view, parent, R.layout.toolbar_spinner_item, "NON_DROPDOWN");
    }

    @Nonnull
    private View createOrUpdateView( final int position, @Nullable final View view, final ViewGroup parent, @LayoutRes final int layoutId, @NonNull final String tag) {
        View item = view;
        if( item == null || !TextUtils.equals( String.valueOf( item.getTag() ), tag) ) {
            item = layoutInflater.inflate( layoutId, parent, false );
            item.setTag( tag );
        }

        final TextView textView = item.findViewById( android.R.id.text1 );
        textView.setText( getTitle( position, "DROPDOWN".equals( tag ) ) );
        return item;
    }
}