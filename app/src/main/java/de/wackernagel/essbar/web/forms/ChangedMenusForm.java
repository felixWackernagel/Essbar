package de.wackernagel.essbar.web.forms;

import androidx.annotation.NonNull;

import java.util.Map;

import de.wackernagel.essbar.EssbarConstants;

public class ChangedMenusForm {
    private final String referer;
    private final String startDate;
    private final String endDate;
    private final Map<String, String> fields;

    public ChangedMenusForm( @NonNull final String startDate, @NonNull final String endDate, @NonNull final Map<String, String> fields ) {
        this.referer = String.format( EssbarConstants.Urls.MENUS, startDate, endDate );
        this.startDate = startDate;
        this.endDate = endDate;
        this.fields = fields;
    }

    public String getReferer() {
        return referer;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Map<String, String> getFields() {
        return fields;
    }
}
