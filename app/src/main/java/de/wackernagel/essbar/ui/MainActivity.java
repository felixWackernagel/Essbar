package de.wackernagel.essbar.ui;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.jsoup.nodes.Element;

import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.AndroidInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.ui.viewModels.MainViewModel;
import de.wackernagel.essbar.utils.GridGutterDecoration;

public class MainActivity extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ElementItemListAdapter adapter = new ElementItemListAdapter( new ElementItemCallback());
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setHasFixedSize( true );
        recyclerView.setAdapter( adapter );
        recyclerView.addItemDecoration( new GridGutterDecoration( getResources().getDimensionPixelSize( R.dimen.view_space ), 1, true, true ) );

        final MainViewModel viewModel = new ViewModelProvider( this, viewModelFactory ).get( MainViewModel.class );
        viewModel.getMenuItems().observe(this, itemList -> {
            for( ElementItem item : itemList ) {
                adapter.setItemChecked( item.getId(), item.isChecked() );
            }
            adapter.submitList( itemList );
        } );
    }

    public static class ElementItem {
        private int id;
        private String menuName;
        private boolean enabled = false;
        private boolean checked = false;

        public ElementItem( final Element element) {
            id = getMenuTypIndex( element ) * getWeekdayIndex( element );
            for( Element checkbox : element.select( "div.controllElements > input" ) ) {
                if( checkbox.attr( "type" ).equalsIgnoreCase( "checkbox" ) ) {
                    enabled = true;
                    checked = checkbox.attr("checked").equalsIgnoreCase("checked");
                }
            }

            // remove all meta data
            element.select("div").remove();

            // remove braces
            menuName = element.text().replaceAll("\\(.*?\\) ?", "");
        }

        String getMenuName() {
            return menuName;
        }

        int getId() {
            return id;
        }

        private int getMenuTypIndex( final Element element ) {
            if( element.classNames().contains("menue-Fruehstueck") ) {
                return 1;
            }
            if( element.classNames().contains("menue-Obstfruehstueck") ) {
                return 2;
            }
            if( element.classNames().contains("menue-Mittag") ) {
                return 3;
            }
            if( element.classNames().contains("menue-Vesper") ) {
                return 4;
            }
            return 0;
        }

        private int getWeekdayIndex( final Element element ) {
            if( element.classNames().contains("weekday-1") ) {
                return 1;
            }
            if( element.classNames().contains("weekday-2") ) {
                return 2;
            }
            if( element.classNames().contains("weekday-3") ) {
                return 3;
            }
            if( element.classNames().contains("weekday-4") ) {
                return 4;
            }
            if( element.classNames().contains("weekday-5") ) {
                return 5;
            }
            return 0;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public boolean isChecked() {
            return checked;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ElementItem item = (ElementItem) o;
            return id == item.id &&
                    enabled == item.enabled &&
                    checked == item.checked &&
                    Objects.equals(menuName, item.menuName);
        }

        @Override
        public int hashCode() {

            return Objects.hash(id, menuName, enabled, checked);
        }
    }

    static class ElementItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;

        ElementItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById( R.id.textView );
            checkBox = itemView.findViewById( R.id.checkbox );
        }
    }

    public static class ElementItemCallback extends DiffUtil.ItemCallback<ElementItem> {
        @Override
        public boolean areItemsTheSame(@NonNull ElementItem oldItem, @NonNull ElementItem newItem) {
            return oldItem.equals( newItem );
        }

        @Override
        public boolean areContentsTheSame(@NonNull ElementItem oldItem, @NonNull ElementItem newItem) {
            return oldItem.equals( newItem );
        }
    }

    public static class ElementItemListAdapter extends ListAdapter<ElementItem, ElementItemViewHolder> {
        private final SparseBooleanArray checkedItems;

        ElementItemListAdapter(@NonNull DiffUtil.ItemCallback<ElementItem> diffCallback) {
            super(diffCallback);
            this.checkedItems = new SparseBooleanArray();
            setHasStableIds( true );
        }

        @Override
        public long getItemId(int position) {
            return Integer.valueOf( getItem( position ).getId() ).longValue();
        }

        void setItemChecked( final int key, boolean isChecked ) {
            checkedItems.put( key, isChecked );
        }

        @NonNull
        @Override
        public ElementItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ElementItemViewHolder(LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_menu, parent, false ));
        }

        @Override
        public void onBindViewHolder(@NonNull ElementItemViewHolder holder, int position) {
            final ElementItem item = getItem( position );
            holder.textView.setText( item.getMenuName() );
            holder.checkBox.setEnabled( item.isEnabled() );
            holder.checkBox.setChecked( checkedItems.get( item.getId() ) );
        }
    }
}
