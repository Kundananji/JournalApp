package com.journalapp.michaelsinkolongo.journalapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.journalapp.michaelsinkolongo.journalapp.data.AppDatabase;
import com.journalapp.michaelsinkolongo.journalapp.data.DiaryEntry;
import com.journalapp.michaelsinkolongo.journalapp.utilities.AppPreferences;
import com.journalapp.michaelsinkolongo.journalapp.utilities.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainViewModel extends AndroidViewModel {
    final static String TAG =  MainViewModel.class.getSimpleName();
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private String sDate;
    private String eDate;
    private Date startDate = new Date();
    private Date endDate = new Date();
    private String userId ="0";
    SharedPreferences sharedPreferences;
    String date="0";
    AppDatabase database;

    LiveData<List<DiaryEntry>> entries;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(getApplication());






    }

    public LiveData<List<DiaryEntry>> getAllEntries(){

        sharedPreferences = getApplication().getSharedPreferences("SESSION", 0);
        userId = sharedPreferences.getString("userId","0");
        entries = database.entryDao().loadAllEntries(userId);
        Logger.printLog(TAG,"Getting all entries",Logger.ERROR);
        return entries;

    }

    public LiveData<List<DiaryEntry>> getEntriesByDate(){
        startDate = new Date();
        endDate = new Date();

        sharedPreferences = getApplication().getSharedPreferences("SESSION", 0);
        date = sharedPreferences.getString(AppPreferences.GO_TO_DATE,sDate);
        userId = sharedPreferences.getString("userId","0");

        sDate = date+" 00:00:000";;
        eDate = date+" 23:59:999";



        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).parse(sDate);
            Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).parse(eDate);

            final long startStamp = date1.getTime();
            final long endStamp = date2.getTime();

            entries = database.entryDao().loadEntriesByDate(startStamp,endStamp,userId);
            Logger.printLog(TAG,"Getting entries by date > "+sDate,Logger.ERROR);

        }
        catch (ParseException e) {
            e.printStackTrace();
            Logger.printLog(TAG,"Error occurred: "+e.getMessage(),Logger.ERROR);
        }

        return entries;

    }

    public LiveData<List<DiaryEntry>> getEntriesForToday(){
        startDate = new Date();
        endDate = new Date();

        sharedPreferences = getApplication().getSharedPreferences("SESSION", 0);
        userId = sharedPreferences.getString("userId","0");

        sDate = dateFormat.format(startDate);
        sDate+=" 00:00:000";
        eDate = dateFormat.format(endDate);
        eDate+=" 23:59:999";
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).parse(sDate);
            Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).parse(eDate);

            final long startStamp = date1.getTime();
            final long endStamp = date2.getTime();


            entries = database.entryDao().loadEntriesByDate(startStamp,endStamp,userId);

            Logger.printLog(TAG,"Getting today's entries> "+sDate+" EndDate: "+eDate,Logger.ERROR);

        }
        catch (ParseException e) {
            e.printStackTrace();
            Logger.printLog(TAG,"Error occurred: "+e.getMessage(),Logger.ERROR);
        }

        return entries;

    }


}
