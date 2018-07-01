package com.journalapp.michaelsinkolongo.journalapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.journalapp.michaelsinkolongo.journalapp.data.AppDatabase;
import com.journalapp.michaelsinkolongo.journalapp.data.DiaryEntry;
import com.journalapp.michaelsinkolongo.journalapp.utilities.Logger;

import java.util.Date;

public class AddDiaryEntry extends AppCompatActivity {
    public static final String EXTRA_ENTRY_ID="extraEntryId";
    public static final String INSTANCE_ENTRY_ID="instanceEntryId";
    public static final int DEFAULT_ENTRY_ID=-1;
    public static final String TAG = AddDiaryEntry.class.getSimpleName();

    private EditText mEditTextTitle;
    private EditText mEditTextBody;
    private Button mButtonSave;

    private AppDatabase mDb;
    private DiaryEntry entry;
    private long mEntryId = DEFAULT_ENTRY_ID;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary_entry);

        sharedPreferences = getSharedPreferences("SESSION",0);

        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());
        if(savedInstanceState!=null && savedInstanceState.containsKey(INSTANCE_ENTRY_ID)){
            mEntryId = savedInstanceState.getInt(INSTANCE_ENTRY_ID,DEFAULT_ENTRY_ID);
        }

        Intent intent = getIntent();
        if(intent!=null && intent.hasExtra(EXTRA_ENTRY_ID)){
            mButtonSave.setText(R.string.button_text_update);
            if(mEntryId == DEFAULT_ENTRY_ID){
                //populate the UI
                Logger.printLog(TAG,"Entry ID: "+mEntryId,Logger.DEGUG);
                mEntryId = intent.getLongExtra(EXTRA_ENTRY_ID,DEFAULT_ENTRY_ID);

                final AddEntryViewModelFactory factory = new AddEntryViewModelFactory(mDb,mEntryId);
                final AddEntryViewModel viewModel = ViewModelProviders.of(this,factory).get(AddEntryViewModel.class);



                viewModel.getDiaryEntry().observe(this, new Observer<DiaryEntry>() {
                    @Override
                    public void onChanged(@Nullable DiaryEntry diaryEntry) {
                        Logger.printLog(TAG,"Receiving database Update: "+mEntryId,Logger.DEGUG);
                        viewModel.getDiaryEntry().removeObserver(this);
                        populateUI(diaryEntry);
                    }
                });

            }

        }




    }

    private void populateUI(DiaryEntry diaryEntry){
        Logger.printLog(TAG,"Diary Entry Entered: "+diaryEntry.getTitle(),Logger.DEGUG);

        if(diaryEntry == null){
           return;
       }

       entry = diaryEntry;

       mEditTextTitle.setText(diaryEntry.getTitle());
       mEditTextBody.setText(diaryEntry.getBody());
    }

    public void onSaveButtonClicked(){
        String title = mEditTextTitle.getText().toString();
        String body = mEditTextBody.getText().toString();
        Date date = new Date();
        Date upDatedDate = new Date();
        String userId = sharedPreferences.getString("userId",null);

        if(TextUtils.isEmpty(title) || title.trim().length()==0){
            Toast.makeText(AddDiaryEntry.this,"Sorry, Title cannot be Empty",Toast.LENGTH_LONG).show();

            return;
        }

        if(TextUtils.isEmpty(body)|| body.trim().length()==0){
            Toast.makeText(AddDiaryEntry.this,"Sorry, Body cannot be Empty",Toast.LENGTH_LONG).show();

            return;
        }

        if(mEntryId!=DEFAULT_ENTRY_ID){
            if(entry!=null){
                date = entry.getDate();
            }
        }

        final DiaryEntry diaryEntry = new DiaryEntry(userId,date,title,body,date);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mEntryId == DEFAULT_ENTRY_ID) {
                    //insert a new entry
                    mDb.entryDao().insertEntry(diaryEntry);

                    Logger.printLog(TAG,"Inserting Brand new Entry",Logger.DEGUG);
                }
                else{
                    //update existing entry
                    if(entry!=null) {
                        diaryEntry.setId(mEntryId);
                        mDb.entryDao().updateEntry(diaryEntry);
                        Logger.printLog(TAG,"Updating existing Entry",Logger.DEGUG);
                    }

                }

            }
        });






       finish();

    }

    private void initViews(){
        mEditTextTitle = findViewById(R.id.editTextTitle);
        mEditTextBody = findViewById(R.id.editTextBody);
        mButtonSave = findViewById(R.id.buttonSave);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClicked();
            }
        });

    }
}
