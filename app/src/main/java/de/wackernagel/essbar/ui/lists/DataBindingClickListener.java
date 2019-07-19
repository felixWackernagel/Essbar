package de.wackernagel.essbar.ui.lists;

public interface DataBindingClickListener<T extends Listable> {

    void onBindingClicked( T item );

}
