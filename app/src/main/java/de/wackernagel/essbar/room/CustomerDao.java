package de.wackernagel.essbar.room;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CustomerDao {

    @Insert
    long insertCustomer( Customer customer );

    @Query( "SELECT * FROM customers ORDER BY id" )
    LiveData<List<Customer>> queryAllCustomers();

}
