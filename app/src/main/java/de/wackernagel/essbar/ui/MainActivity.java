package de.wackernagel.essbar.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.jsoup.nodes.Element;

import java.util.Calendar;
import java.util.GregorianCalendar;
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
import de.wackernagel.essbar.utils.SectionItemDecoration;

public class MainActivity extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[] localizedWeekdays = getResources().getStringArray( R.array.weekdays );
        final String todayAsWord = getString( R.string.today );
        // Calendar starts with sunday with index 1
        final int currentDayOfWeek = GregorianCalendar.getInstance().get( Calendar.DAY_OF_WEEK ) - 2;
        final MenuListAdapter adapter = new MenuListAdapter( new MenuItemCallback());
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setHasFixedSize( false );
        recyclerView.setAdapter( adapter );
        recyclerView.addItemDecoration( new GridGutterDecoration( getResources().getDimensionPixelSize( R.dimen.view_space ), 1, true, true ) );
        recyclerView.addItemDecoration( new SectionItemDecoration( this, false, new SectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection( int position ) {
                // first item or when current and previous position have different weekdays
                return position <= 0 || adapter.getListItem( position - 1 ).getWeekday() != adapter.getListItem( position ).getWeekday();
            }

            @Override
            public CharSequence getSectionHeader( int position ) {
                final MenuItem item = adapter.getListItem( position );
                final String weekdayAsWord = localizedWeekdays[ item.getWeekday() ];
                if( item.getWeekday() == currentDayOfWeek ) {
                    return weekdayAsWord.concat( todayAsWord );
                }
                return weekdayAsWord;
            }
        }) );

        final MainViewModel viewModel = new ViewModelProvider( this, viewModelFactory ).get( MainViewModel.class );
        viewModel.getMenuItems().observe(this, itemList -> {
            for( MenuItem item : itemList ) {
                if( item.isChecked() ) {
                    adapter.setItemChecked( item.getId() );
                }
            }
            adapter.submitList( itemList );
        } );
    }

    public static class MenuItem {
        private int id;
        private String menuName;
        private boolean enabled;
        private boolean checked;
        private int weekday;
        private int menuTyp;

        public MenuItem(final Element element ) {
            weekday = getWeekdayIndex( element );
            menuTyp = getMenuTypIndex( element );
            id = menuTyp + ( weekday * 4 );
            enabled = element.classNames().contains( "pointer" );
            checked = element.classNames().contains( "gruen" );

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

        boolean isEnabled() {
            return enabled;
        }

        boolean isChecked() {
            return checked;
        }

        int getWeekday() {
            return weekday;
        }

        int getMenuTyp() {
            return menuTyp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MenuItem item = (MenuItem) o;
            return id == item.id &&
                    enabled == item.enabled &&
                    checked == item.checked &&
                    weekday == item.weekday &&
                    menuTyp == item.menuTyp &&
                    Objects.equals(menuName, item.menuName);
        }

        @Override
        public int hashCode() {

            return Objects.hash(id, menuName, enabled, checked, weekday, menuTyp);
        }

        private static int getMenuTypIndex(final Element element ) {
            if( element.classNames().contains("menue-Fruehstueck") ) {
                return 1;
            } else if( element.classNames().contains("menue-Obstfruehstueck") ) {
                return 2;
            } else if( element.classNames().contains("menue-Mittag") ) {
                return 3;
            } else if( element.classNames().contains("menue-Vesper") ) {
                return 4;
            } else {
                throw new IllegalStateException("No known menu typ found.");
            }
        }

        private static int getWeekdayIndex( final Element element ) {
            if( element.classNames().contains("weekday-1") ) {
                return 0;
            } else if( element.classNames().contains("weekday-2") ) {
                return 1;
            } else if( element.classNames().contains("weekday-3") ) {
                return 2;
            } else if( element.classNames().contains("weekday-4") ) {
                return 3;
            } else if( element.classNames().contains("weekday-5") ) {
                return 4;
            } else {
                throw new IllegalStateException("No known weekday found.");
            }
        }
    }

    static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;

        MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById( R.id.textView );
            checkBox = itemView.findViewById( R.id.checkbox );
        }
    }

    public static class MenuItemCallback extends DiffUtil.ItemCallback<MenuItem> {
        @Override
        public boolean areItemsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
            return oldItem.getWeekday() == newItem.getWeekday() &&
                    TextUtils.equals( oldItem.getMenuName(), newItem.getMenuName() ) &&
                    oldItem.getMenuTyp() == newItem.getMenuTyp() &&
                    oldItem.isChecked() == newItem.isChecked() &&
                    oldItem.isEnabled() == newItem.isEnabled();
        }
    }

    public static class MenuListAdapter extends ListAdapter<MenuItem, MenuItemViewHolder> {
        private final SparseBooleanArray checkedItems;

        MenuListAdapter(@NonNull DiffUtil.ItemCallback<MenuItem> diffCallback) {
            super(diffCallback);
            this.checkedItems = new SparseBooleanArray();
            setHasStableIds( true );
        }

        @Override
        public long getItemId(int position) {
            return Integer.valueOf( getItem( position ).getId() ).longValue();
        }

        MenuItem getListItem(int position ) {
            return getItem( position );
        }

        void setItemChecked( final int key ) {
            checkedItems.put( key, true );
        }

        @NonNull
        @Override
        public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MenuItemViewHolder(LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_menu, parent, false ));
        }

        @Override
        public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
            final MenuItem item = getItem( position );
            holder.textView.setText( item.getMenuName() );
            holder.checkBox.setEnabled( item.isEnabled() );
            holder.checkBox.setChecked( checkedItems.get( item.getId() ) );
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked ) -> {
                // Check if change was button based or setter based.
                if( buttonView.isPressed() ) {
                    final int clickedItemId = getItem( holder.getAdapterPosition() ).getId();
                    if( checkedItems.get( clickedItemId ) ) {
                        checkedItems.delete( clickedItemId );
                    } else {
                        checkedItems.put( clickedItemId, true );
                    }
                }
            });
        }
    }
}
