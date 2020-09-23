package de.wackernagel.essbar.ui.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import de.wackernagel.essbar.repository.EssbarRepository;
import de.wackernagel.essbar.ui.pojos.MealListItem;
import de.wackernagel.essbar.ui.pojos.Type;
import de.wackernagel.essbar.utils.DateUtils;

public class LunchListViewModel extends ViewModel {

    private final EssbarRepository repository;

    @Inject
    LunchListViewModel(final EssbarRepository repository ) {
        this.repository = repository;
    }

    public LiveData<List<MealListItem>> getLunchesFromCurrentWeekOfYear() {
        return repository.getMealsOfTypeFromWeekOfYear( Type.LUNCH, DateUtils.calculateCalendarWeek( null ), DateUtils.calculateCurrentYear() );
    }

}
