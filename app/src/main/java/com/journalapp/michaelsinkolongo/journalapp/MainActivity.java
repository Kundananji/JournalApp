package com.journalapp.michaelsinkolongo.journalapp;

import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.journalapp.michaelsinkolongo.journalapp.data.DiaryEntry;
import com.journalapp.michaelsinkolongo.journalapp.utilities.AppPreferences;
import com.journalapp.michaelsinkolongo.journalapp.utilities.Logger;


import java.util.Calendar;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity implements EntryAdapter.ItemClickListener {
    Calendar myCalendar = Calendar.getInstance();
    //Constant for Logging
    private static final String TAG = MainActivity.class.getSimpleName();

    //Member variables for the adapter and RecyclerView

    private RecyclerView mRecyclerView;
    private EntryAdapter mAdapter;

    private MainViewModel mViewModel;
    private LinearLayout mLinearLayoutNoContent;
    private Button mButtonAddEntry;



    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences sharedPreferences;

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }

    };

    LiveData<List<DiaryEntry>> entries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END build_client]

        initViews();


        setUpViewModel();
    }


    @Override
    public void onStart() {
        super.onStart();

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null){
            //exit
            Intent intent = new Intent(MainActivity.this,SignIn.class);
            startActivity(intent);
        }
        // [END on_start_sign_in]
    }

    @Override
    protected  void onResume(){
        super.onResume();

    }

    private void setUpViewModel() {

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        entries = mViewModel.getEntriesForToday();
        //entries = mViewModel.getAllEntries();
        if(entries!=null) {
            entries.observe(this, new Observer<List<DiaryEntry>>() {
                @Override
                public void onChanged(@Nullable List<DiaryEntry> diaryEntries) {
                    Logger.printLog(TAG, "Entries Retrieved: "+diaryEntries.size(), Logger.DEGUG);
                    mAdapter.setEntries(diaryEntries);
                    if (diaryEntries!=null && diaryEntries.size() == 0) {
                        mLinearLayoutNoContent.setVisibility(View.VISIBLE);
                    } else {
                        mLinearLayoutNoContent.setVisibility(View.GONE);
                    }
                }
            });
        }
        else{
            mLinearLayoutNoContent.setVisibility(View.VISIBLE);
            Logger.printLog(TAG,"Live date object is null",Logger.ERROR);
        }







    }


    private void initViews(){
        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewEntries);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new EntryAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        mLinearLayoutNoContent = findViewById(R.id.linearLayoutNoContent);
        mButtonAddEntry = findViewById(R.id.buttonAddNewEntry);

        mButtonAddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddDiaryEntry.class);
                startActivity(intent);
            }
        });




        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddDiaryEntry.class);
                startActivity(addTaskIntent);
            }
        });

    }

    private void updateDate(){
        int dayOfMonth = myCalendar.get(Calendar.DAY_OF_MONTH);
        int monthOfYear = myCalendar.get(Calendar.MONTH);
        int year = myCalendar.get(Calendar.YEAR);
        String str_date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;



            SharedPreferences sessionData = getSharedPreferences("SESSION", 0);
            SharedPreferences.Editor editor = sessionData.edit();
            editor.putString(AppPreferences.GO_TO_DATE,str_date);
            editor.apply();

            Class destinationClass = EntriesByDate.class;
            Intent intent = new Intent(MainActivity.this,destinationClass);
            startActivity(intent);


    }

    public void showDatePicker() {
        new DatePickerDialog(MainActivity.this, datePickerListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main,menu);


        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        if(id == R.id.action_add_entry){
            Class destinationClass = AddDiaryEntry.class;
            Intent intent = new Intent(MainActivity.this,destinationClass);
            startActivity(intent);

        }
        else if(id == R.id.action_view_all_entries){

            Class destinationClass = ViewAllEntries.class;
            Intent intent = new Intent(MainActivity.this,destinationClass);
            startActivity(intent);




        }
        else if(id == R.id.action_go_to_date){
            showDatePicker();

        } else if(id==R.id.action_sign_out){
            signOut();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onItemClickListener(long itemId) {
        //launch AddEntry activity, adding the itemId as an extra in the intent
        Logger.printLog(TAG,"Item Id Selected: "+itemId,Logger.DEGUG);
        Intent intent = new Intent(MainActivity.this,ViewEntry.class);
        intent.putExtra(ViewEntry.EXTRA_ENTRY_ID,itemId);
        startActivity(intent);

    }


    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(MainActivity.this,SignIn.class);
                        startActivity(intent);
                    }
                });
    }
}
