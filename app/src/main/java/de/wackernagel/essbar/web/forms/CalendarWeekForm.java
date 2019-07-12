package de.wackernagel.essbar.web.forms;

import androidx.annotation.NonNull;

public class CalendarWeekForm {
    private final String startDate;
    private final String endDate;

    public CalendarWeekForm( @NonNull final String startDate, @NonNull final String endDate ) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
