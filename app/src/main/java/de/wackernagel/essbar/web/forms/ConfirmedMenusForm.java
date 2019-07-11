package de.wackernagel.essbar.web.forms;

import androidx.annotation.NonNull;

import java.util.Map;

public class ConfirmedMenusForm {
    private final Map<String, String> fields;

    public ConfirmedMenusForm( @NonNull final Map<String, String> fields ) {
        this.fields = fields;
    }

    public Map<String, String> getFields() {
        return fields;
    }
}
