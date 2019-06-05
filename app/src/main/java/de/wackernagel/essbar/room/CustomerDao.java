package de.wackernagel.essbar.room;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface CustomerDao {

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    void insertCustomer( Customer customer );

    @Query( "SELECT * FROM customers ORDER BY name" )
    LiveData<List<Customer>> queryAllCustomers();

    @Query( "SELECT COUNT(id) FROM customers" )
    LiveData<Integer> customersCount();

}
