package com.journalapp.michaelsinkolongo.journalapp.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.journalapp.michaelsinkolongo.journalapp.utilities.Logger;
@Database(entities = {DiaryEntry.class,User.class},version = 1,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "mydiary";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context){

        if(sInstance == null){
            synchronized (LOCK){
                Logger.printLog(LOG_TAG,"Creating new database Instance",Logger.DEGUG);
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class,AppDatabase.DATABASE_NAME)
                        .build();
            }
        }


        Logger.printLog(LOG_TAG,"Getting the database instance",Logger.DEGUG);
        return sInstance;


    }

    public abstract EntryDao entryDao();
    public abstract UserDao userDao();

}
