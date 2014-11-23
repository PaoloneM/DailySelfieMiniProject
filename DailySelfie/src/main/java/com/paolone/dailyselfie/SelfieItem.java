package com.paolone.dailyselfie;

import android.graphics.Bitmap;
import android.location.Location;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * The class for Selfie Item
 */
public class SelfieItem {

    /*****************************************
     *              CONSTANTS                *
     *****************************************/
    // TAG for logging
    private static final String TAG = "Dailiy_Selfie";


    /*****************************************
     *                FIELDS                 *
     *****************************************/

    private Date mDate;
    private Location mLocation;
    private File mFile;

    /*****************************************
     *              CONSTRUCTOR              *
     *****************************************/

    public SelfieItem(Date date, File file){

        this(date, null, file);

    }

    public SelfieItem(Date date, Location location, File file){

        mDate = date;

        if (mLocation != null) {
            mLocation = location;
        }
        mFile = file;

    }

    /*****************************************
     *          EXPOSED  METHODS             *
     *****************************************/

    public Date getDate(){
        return mDate;
    }

    public Location getLocation(){
        return mLocation;
    }

    public File getFile(){
        return mFile;
    }

    public long getSelfieAge() {
        long mSelfieDate = this.mDate.getTime();
        long mNow = Calendar.getInstance().getTimeInMillis();
        return mNow - mSelfieDate;
    }
    public void setDate(Date date){
        mDate = date;
    }

    public void setLocation(Location location){
        mLocation = location;
    }

    public void setFile(File file){
        mFile = file;
    }


    /*****************************************
     *           SUPPORT METHODS             *
     *****************************************/


}
