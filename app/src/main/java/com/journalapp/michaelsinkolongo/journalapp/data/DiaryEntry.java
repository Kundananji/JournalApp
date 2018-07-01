package com.journalapp.michaelsinkolongo.journalapp.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "entry")
public class DiaryEntry {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String userId;
    private Date date;
    private String title;
    private String body;
    private Date dateUpdated;

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @Ignore
    public DiaryEntry(String userId, Date date, String title, String body,Date dateUpdated){
        this.date = date;
        this.title = title;
        this.body = body;
        this.dateUpdated = dateUpdated;
        this.userId = userId;
    }

    public DiaryEntry(long id,String userId,Date date,String title, String body){
        this.id = id;
        this.date = date;
        this.title = title;
        this.body = body;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
