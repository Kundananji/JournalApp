package com.journalapp.michaelsinkolongo.journalapp;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.journalapp.michaelsinkolongo.journalapp.data.AppDatabase;
import com.journalapp.michaelsinkolongo.journalapp.data.DiaryEntry;
import com.journalapp.michaelsinkolongo.journalapp.utilities.Logger;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewEntry extends AppCompatActivity {
    public static final String EXTRA_ENTRY_ID="extraEntryId";
    public static final String INSTANCE_ENTRY_ID="instanceEntryId";
    public static final String TAG = ViewEntry.class.getSimpleName();

    TextView entryTitle;
    TextView entryDate;
    TextView entryDescription;
    TextView entryTime;
    TextView lastModified;
    LinearLayout linearLayoutEditPanel;
    ImageButton imageButtonEdit;
    ImageButton imageButtonDelete;

    // Constants for date format
    private static final String DATE_FORMAT = "EEEE, MMMM dd";
    private static final String TIME_FORMAT = "HH:mm";

    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());


    private AppDatabase mDb;
    public static final int DEFAULT_ENTRY_ID=-1;

    private long mEntryId = DEFAULT_ENTRY_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        entryTitle = findViewById(R.id.textViewEntryTitle);
        entryDate = findViewById(R.id.textViewEntryDate);
        entryDescription = findViewById(R.id.textViewEntryDescription);
        entryTime = findViewById(R.id.textViewEntryTime);
        lastModified = findViewById(R.id.textViewLastModified);
        linearLayoutEditPanel = findViewById(R.id.linearLayoutEditPanel);
         imageButtonEdit = findViewById(R.id.ImageButtonEdit);
         imageButtonDelete = findViewById(R.id.ImageButtonDelete);

        mDb = AppDatabase.getInstance(getApplicationContext());
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ENTRY_ID)) {
            mEntryId = savedInstanceState.getInt(INSTANCE_ENTRY_ID, DEFAULT_ENTRY_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ENTRY_ID)) {

            if (mEntryId == DEFAULT_ENTRY_ID) {
                //populate the UI
                Logger.printLog(TAG, "Entry ID: " + mEntryId, Logger.DEGUG);
                mEntryId = intent.getLongExtra(EXTRA_ENTRY_ID, DEFAULT_ENTRY_ID);

                final AddEntryViewModelFactory factory = new AddEntryViewModelFactory(mDb, mEntryId);
                final AddEntryViewModel viewModel = ViewModelProviders.of(this, factory).get(AddEntryViewModel.class);


                viewModel.getDiaryEntry().observe(this, new Observer<DiaryEntry>() {
                    @Override
                    public void onChanged(@Nullable DiaryEntry diaryEntry) {
                        Logger.printLog(TAG, "Receiving database Update: " + mEntryId, Logger.DEGUG);

                        populateUI(diaryEntry);
                    }
                });

            }
            else{
                Toast.makeText(ViewEntry.this,"This entry doesn't exist or has been deleted",Toast.LENGTH_LONG).show();
                finish();
            }

        }
        else{
            Toast.makeText(ViewEntry.this,"This entry doesn't exist or has been deleted",Toast.LENGTH_LONG).show();
            finish();
        }


    }

    private void populateUI(final DiaryEntry diaryEntry){

         if(diaryEntry == null){
             Toast.makeText(ViewEntry.this,"This entry doesn't exist or has been deleted",Toast.LENGTH_LONG).show();
             finish();
             return;
         }
        String body = diaryEntry.getBody();
        String title = diaryEntry.getTitle();
        String date = dateFormat.format(diaryEntry.getDate());
        String time = timeFormat.format(diaryEntry.getDate());
        String modifiedDate = dateFormat.format(diaryEntry.getDateUpdated());
        String modifiedTime = timeFormat.format(diaryEntry.getDateUpdated());
        boolean showDate = true;

        linearLayoutEditPanel.setVisibility(View.VISIBLE);


        if(modifiedDate.equals(date) && modifiedTime.equals(time)){
            //so not show modified date
            lastModified.setVisibility(View.GONE);
        }
        else{
            lastModified.setVisibility(View.VISIBLE);
        }
        String lastModifiedText = "Last Modified: "+modifiedDate+" at "+modifiedTime;

        //Set values
        entryTitle.setText(title);
        entryDescription.setText(body);
        entryDate.setText(date);
        entryTime.setText(time);
        lastModified.setText(lastModifiedText);


        imageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete item
                String message = "Are you sure you want to delete this entry. This action cannot be undone?";
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewEntry.this);
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //yes... you is deleting now

                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        mDb.entryDao().deleteEntry(diaryEntry);
                                        finish();

                                    }
                                });


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                onBackPressed(); //restart the activity

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ViewEntry.this,AddDiaryEntry.class);
                intent.putExtra(AddDiaryEntry.EXTRA_ENTRY_ID,diaryEntry.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
