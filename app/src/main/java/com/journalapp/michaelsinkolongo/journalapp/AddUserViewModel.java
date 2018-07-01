package com.journalapp.michaelsinkolongo.journalapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.journalapp.michaelsinkolongo.journalapp.data.AppDatabase;
import com.journalapp.michaelsinkolongo.journalapp.data.User;

public class AddUserViewModel extends ViewModel {

    private LiveData<User> user;

    public AddUserViewModel(AppDatabase database, String mUserId){
        user = database.userDao().getUser(mUserId);
    }


    public LiveData<User> getUser(){
        return user;
    }
}
