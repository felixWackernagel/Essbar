package de.wackernagel.essbar.utils;

import androidx.annotation.Nullable;

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
public class Event<T> {
    private final T content;
    private boolean hasBeenHandled = false;

    public Event( final T content ) {
        this.content = content;
    }

    /**
     * Returns the content and prevents its use again.
     */
    @Nullable
    public T getContentIfNotHandled() {
        if( hasBeenHandled ) {
            return  null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    public T peekContent() {
        return content;
    }
}
