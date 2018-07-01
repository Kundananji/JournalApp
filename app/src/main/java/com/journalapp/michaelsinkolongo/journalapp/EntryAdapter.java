/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.journalapp.michaelsinkolongo.journalapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.journalapp.michaelsinkolongo.journalapp.data.DiaryEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * This TaskAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    // Constant for date format
    private static final String DATE_FORMAT = "EEEE, MMMM dd";
    private static final String TIME_FORMAT = "HH:mm";
    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds entry data and the Context
    private List<DiaryEntry> mDiaryEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public EntryAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the entry_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.entry_layout, parent, false);

        return new EntryViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(EntryViewHolder holder, int position) {
        // Determine the values of the wanted data
        DiaryEntry diaryEntry = mDiaryEntries.get(position);
        String body = diaryEntry.getBody();
        String title = diaryEntry.getTitle();
        String date = dateFormat.format(diaryEntry.getDate());
        String time = timeFormat.format(diaryEntry.getDate());
        String modifiedDate = dateFormat.format(diaryEntry.getDateUpdated());
        String modifiedTime = timeFormat.format(diaryEntry.getDateUpdated());
        boolean showDate = true;
        if(body.length()>100) {
            body = body.substring(0, 100)+"...";
        }






        if(position>0){
            //get previous date
            DiaryEntry previousEntry = mDiaryEntries.get(position-1);
            if(previousEntry!=null) {
                String previousDate = dateFormat.format(previousEntry.getDate());
                if(previousDate.equals(date)){
                    showDate=false;
                }
            }
        }

        if(showDate){
            holder.entryDate.setVisibility(View.VISIBLE);
        }
        else{
            holder.entryDate.setVisibility(View.GONE);
        }

        if(modifiedDate.equals(date) && modifiedTime.equals(time)){
            //so not show modified date
            holder.lastModified.setVisibility(View.GONE);
        }
        else{
            holder.lastModified.setVisibility(View.VISIBLE);
        }
        String lastModified = "Last Modified: "+modifiedDate+" at "+modifiedTime;

        //Set values
        holder.entryTitle.setText(title);
        holder.entryDescription.setText(body);
        holder.entryDate.setText(date);
        holder.entryTime.setText(time);
        holder.lastModified.setText(lastModified);




    }



    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mDiaryEntries == null) {
            return 0;
        }
        return mDiaryEntries.size();
    }

    public List<DiaryEntry> getEntries() {

        return mDiaryEntries;
    }

    /**
     * When data changes, this method updates the list of diaryEntries
     * and notifies the adapter to use the new values on it
     */
    public void setEntries(List<DiaryEntry> diaryEntries) {
        mDiaryEntries = diaryEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(long itemId);
    }

    // Inner class for creating ViewHolders
    class EntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the entry title, body and brief description
        TextView entryTitle;
        TextView entryDate;
        TextView entryDescription;
        TextView entryTime;
        TextView lastModified;

        /**
         * Constructor for the EntryViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public EntryViewHolder(View itemView) {
            super(itemView);

            entryTitle = itemView.findViewById(R.id.textViewEntryTitle);
            entryDate = itemView.findViewById(R.id.textViewEntryDate);
            entryDescription = itemView.findViewById(R.id.textViewEntryDescription);
            entryTime = itemView.findViewById(R.id.textViewEntryTime);
            lastModified = itemView.findViewById(R.id.textViewLastModified);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            long elementId = mDiaryEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}