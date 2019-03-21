package de.wackernagel.essbar.ui;

import android.os.Bundle;
import android.widget.TextView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import dagger.android.AndroidInjection;
import de.wackernagel.essbar.R;
import de.wackernagel.essbar.web.Resource;
import de.wackernagel.essbar.web.WebService;

public class MainActivity extends AppCompatActivity {

    @Inject
    WebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[] weekdays = getResources().getStringArray( R.array.weekdays );
        final String todayWord = getString( R.string.today );
        final TextView textView = findViewById( R.id.textView );

        webService.fetchMenus().observe(this, new Observer<Resource<Document>>() {
            @Override
            public void onChanged( Resource<Document> resource ) {
            if( resource.isSuccess() ) {
                if( resource.getResource() != null ) {
                    // starts by 1 and 1 is sunday
                    // minus 1 to get monday and additional minus 1 to match element index which starts by 0
                    final int todayIndex = Math.max( GregorianCalendar.getInstance().get( Calendar.DAY_OF_WEEK ) - 2, 0 );

                    final Elements lunchMenus = resource.getResource().select("tr.menue-line-Mittag td.menue-Mittag");
                    for( int index = 0; index < lunchMenus.size(); index++ ) {
                        final Element element = lunchMenus.get( index );
                        // remove child tags
                        element.select("div").remove();
                        String isToday = todayIndex == index ? todayWord : "";
                        textView.append( weekdays[ index ] + isToday + ":\n" + element.text().replaceAll("\\(.*?\\) ?", "") + "\n\n" );
                    }
                }
            }
            }
        });
    }
}
