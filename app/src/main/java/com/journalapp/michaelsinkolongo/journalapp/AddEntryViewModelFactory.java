package com.journalapp.michaelsinkolongo.journalapp;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.journalapp.michaelsinkolongo.journalapp.data.AppDatabase;

public class AddEntryViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase mDb;
    private final long mEntryId;


    public AddEntryViewModelFactory(AppDatabase mDb, long mEntryId) {
        this.mDb = mDb;
        this.mEntryId = mEntryId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddEntryViewModel(mDb,mEntryId);
    }
}
