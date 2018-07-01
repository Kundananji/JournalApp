package com.journalapp.michaelsinkolongo.journalapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.journalapp.michaelsinkolongo.journalapp.data.AppDatabase;
import com.journalapp.michaelsinkolongo.journalapp.data.DiaryEntry;

public class AddEntryViewModel extends ViewModel {

    private LiveData<DiaryEntry> diaryEntry;

    public AddEntryViewModel(AppDatabase database,long mEntryId){
        diaryEntry = database.entryDao().loadEntryById(mEntryId);
    }


    public LiveData<DiaryEntry> getDiaryEntry(){
        return diaryEntry;
    }
}
