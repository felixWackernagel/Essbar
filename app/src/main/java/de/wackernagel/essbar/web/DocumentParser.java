package de.wackernagel.essbar.web;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import de.wackernagel.essbar.ui.pojos.CalendarWeek;
import de.wackernagel.essbar.ui.pojos.ChangedMenu;
import de.wackernagel.essbar.ui.pojos.Menu;

public class DocumentParser {

    private DocumentParser() {
        // no instance required
    }

    public static boolean isLoginSuccessful( @Nullable final Document document ) {
        if( document != null ) {
            final Element headline = document.selectFirst("h1");
            return headline != null && "speiseplan".equalsIgnoreCase( headline.text() );
        }
        return false;
    }

    @Nullable
    public static String getLoginUsername( @Nullable final Document document ) {
        if( document != null ) {
            final Element userNameElement = document.selectFirst( "site > header > session > user > a" );
            if( userNameElement != null ) {
                return userNameElement.text();
            }
        }
        return null;
    }

    public static List<Menu> getMenuList( @Nullable final Document document ) {
        final List<Menu> menus = new ArrayList<>();
        if( document != null ) {
            final String[] menuTypeSelectors = new String[] {
                    "tr.menue-line-Fruehstueck > td.menue-Fruehstueck",
                    "tr.menue-line-Obstfruehstueck > td.menue-Obstfruehstueck",
                    "tr.menue-line-Mittag > td.menue-Mittag",
                    "tr.menue-line-Vesper > td.menue-Vesper"
            };

            for( int menuTypeIndex = 0; menuTypeIndex < menuTypeSelectors.length; menuTypeIndex++ ) {
                final Elements menuTypeElements = document.select( menuTypeSelectors[ menuTypeIndex ] );
                for( int menuIndex = 0; menuIndex < menuTypeElements.size(); menuIndex++ ) {
                    final int menuItemIndex = menuTypeIndex + ( ( menuTypeIndex + 1 ) * menuIndex );
                    menus.add( menuItemIndex, new Menu( menuTypeElements.get( menuIndex ) ) );
                }
            }
        }
        return menus;
    }

    public static List<CalendarWeek> getCalendarWeekList( @Nullable final Document document ) {
        final List<CalendarWeek> calendarWeeks = new ArrayList<>();
        if( document != null ) {
            for( Element option : document.select( "site > content > form > label > select > option" ) ) {
                calendarWeeks.add( new CalendarWeek( option ) );
            }
        }
        return calendarWeeks;
    }

    public static List<ChangedMenu> getChangedMenuList( @Nullable final Document document) {
        final List<ChangedMenu> changedMenus = new ArrayList<>();
        if( document != null ) {
            for( Element tableRow : document.select( "form .best_table_top tr[class^=auflistung]" ) ) {
                changedMenus.add( new ChangedMenu( tableRow ) );
            }
        }
        return changedMenus;
    }

    public static Boolean isOrderSuccessful( @Nullable final Document document ) {
        if( document != null && document.select( ".bestellfortschritt.bestellfortschritt2" ).size() > 0 ) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Nullable
    public static String getCSRFToken( @Nullable final Document document ) {
        if( document != null ) {
            final Elements csrfElements = document.select( "form > input[name=csrfmiddlewaretoken]" );
            if( csrfElements.size() == 1 ) {
                return csrfElements.get( 0 ).attr( "value" );
            }
            return null;
        }
        return null;
    }
}
