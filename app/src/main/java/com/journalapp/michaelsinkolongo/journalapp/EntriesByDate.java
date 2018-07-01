package com.journalapp.michaelsinkolongo.journalapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.journalapp.michaelsinkolongo.journalapp.data.DiaryEntry;
import com.journalapp.michaelsinkolongo.journalapp.utilities.Logger;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class EntriesByDate extends AppCompatActivity implements EntryAdapter.ItemClickListener {

    //Constant for Logging
    private static final String TAG = EntriesByDate.class.getSimpleName();

    //Member variables for the adapter and RecyclerView

    private RecyclerView mRecyclerView;
    private EntryAdapter mAdapter;

    private MainViewModel mViewModel;
    private LinearLayout mLinearLayoutNoContent;

    LiveData<List<DiaryEntry>> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries_by_date);
        getSupportActionBar().setTitle(R.string.activity_title_entries_by_date);
        initViews();
        setUpViewModel();
    }

    private void setUpViewModel() {

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        entries = mViewModel.getEntriesByDate();

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



    }

    @Override
    public void onItemClickListener(long itemId) {
        //launch AddEntry activity, adding the itemId as an extra in the intent
        Logger.printLog(TAG,"Item Id Selected: "+itemId,Logger.DEGUG);
        Intent intent = new Intent(EntriesByDate.this,ViewEntry.class);
        intent.putExtra(ViewEntry.EXTRA_ENTRY_ID,itemId);
        startActivity(intent);

    }
}
