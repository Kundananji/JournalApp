package com.journalapp.michaelsinkolongo.journalapp.data;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE id=:id")
    LiveData<User> getUser(String id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUser(User user);

    @Delete
    void deleteUser(User user);



}

