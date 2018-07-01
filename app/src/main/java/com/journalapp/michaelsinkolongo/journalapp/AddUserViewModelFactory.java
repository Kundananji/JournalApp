package com.journalapp.michaelsinkolongo.journalapp;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.journalapp.michaelsinkolongo.journalapp.data.AppDatabase;

public class AddUserViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase mDb;
    private final String mUserId;


    public AddUserViewModelFactory(AppDatabase mDb, String mUserId) {
        this.mDb = mDb;
        this.mUserId = mUserId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddUserViewModel(mDb,mUserId);
    }
}
