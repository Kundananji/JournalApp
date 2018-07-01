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
public interface EntryDao {

    @Query("SELECT * FROM entry WHERE date>=:startDate AND date<=:endDate AND userId=:userId  ORDER BY date DESC")
    LiveData<List<DiaryEntry>> loadEntriesByDate(long startDate,long endDate,String userId);

    @Query("SELECT * FROM entry WHERE userId=:userId ORDER BY date DESC")
    LiveData<List<DiaryEntry>> loadAllEntries(String userId);

    @Insert
    void insertEntry(DiaryEntry diaryEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEntry(DiaryEntry diaryEntry);

    @Delete
    void deleteEntry(DiaryEntry diaryEntry);

    @Query("SELECT * FROM entry WHERE id = :id")
    LiveData<DiaryEntry> loadEntryById(long id);


}
