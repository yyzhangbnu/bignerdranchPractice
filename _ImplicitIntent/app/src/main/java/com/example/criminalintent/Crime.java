package com.example.criminalintent;
import android.provider.ContactsContract;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTile;
    private Date mDate;
    private boolean mSolved;
    public String mSuspect;

    public int getmRequiresPolice() {
        return mRequiresPolice;
    }

    public void setmRequiresPolice(int mRequiresPolice) {
        this.mRequiresPolice = mRequiresPolice;
    }

    private int mRequiresPolice;

    public String getmTile() {
        return mTile;
    }

    public void setmTile(String mTile) {
        this.mTile = mTile;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean ismSolved() {
        return mSolved;
    }

    public void setmSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }

    public UUID getmId() {
        return mId;
    }

    public Crime(){
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public String getmSuspect(){
        return mSuspect;
    }

    public Void setmSuspect(String suspect){
        mSuspect = suspect;
    }
}

