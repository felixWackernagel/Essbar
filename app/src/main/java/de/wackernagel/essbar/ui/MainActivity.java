package de.wackernagel.essbar.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.AndroidInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.web.Resource;
import de.wackernagel.essbar.web.WebService;

public class MainActivity extends AppCompatActivity {

    @Inject
    WebService webService;

    private static String[] menuTypeSelectors = new String[] {
      "tr.menue-line-Fruehstueck > td.menue-Fruehstueck",
      "tr.menue-line-Obstfruehstueck > td.menue-Obstfruehstueck",
      "tr.menue-line-Mittag > td.menue-Mittag",
      "tr.menue-line-Vesper > td.menue-Vesper"
    };

    private static String REMOVE_BRACES = "\\(.*?\\) ?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ElementItemListAdapter adapter = new ElementItemListAdapter( new ElementItemCallback() );
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setHasFixedSize( true );
        recyclerView.setAdapter( adapter );

        webService.fetchMenus().observe(this, new Observer<Resource<Document>>() {
            @Override
            public void onChanged( Resource<Document> resource ) {
                if( resource.isSuccess() ) {
                    if( resource.getResource() != null ) {
                        adapter.submitList( convertMenuTableToList( resource.getResource() ) );
                    }
                }
            }
        });
    }

    private List<ElementItem> convertMenuTableToList( final Document menuPage) {
        final List<ElementItem> allMenuItems = new ArrayList<>();
        for(int menuTypeIndex = 0; menuTypeIndex < menuTypeSelectors.length; menuTypeIndex++ ) {
            final String menuTypeSelector = menuTypeSelectors[ menuTypeIndex ];
            final Elements menuTypeElements = menuPage.select( menuTypeSelector );
            for( int menuIndex = 0; menuIndex < menuTypeElements.size(); menuIndex++ ) {
                final int menuItemIndex = menuTypeIndex + ( ( menuTypeIndex + 1 ) * menuIndex );
                allMenuItems.add( menuItemIndex, new ElementItem( menuTypeElements.get( menuIndex ) ) );
            }
        }
        return allMenuItems;
    }

    public static class ElementItem {
        private final Element element;

        ElementItem(Element element) {
            this.element = element;
        }

        public String getMenu() {
            // remove all meta data
            element.select("div").remove();
            return element.text().replaceAll(REMOVE_BRACES, "");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ElementItem that = (ElementItem) o;
            return Objects.equals(element, that.element);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element);
        }
    }

    public static class ElementItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        ElementItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById( R.id.textView );
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
        ElementItemListAdapter(@NonNull DiffUtil.ItemCallback<ElementItem> diffCallback) {
            super(diffCallback);
        }

        @NonNull
        @Override
        public ElementItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ElementItemViewHolder(LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_menu, parent, false ));
        }

        @Override
        public void onBindViewHolder(@NonNull ElementItemViewHolder holder, int position) {
            holder.textView.setText( getItem( position ).getMenu() );
        }
    }
}
