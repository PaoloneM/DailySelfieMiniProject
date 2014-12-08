package com.paolone.dailyselfie;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * The class for Selfie Item
 */
public class SelfieItem implements Parcelable {

    /*****************************************
     *              CONSTANTS                *
     *****************************************/
    // TAG for logging
    private static final String TAG = "Dailiy_Selfie";
    // Date format for pacel
    private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd\'T\'HH:mm:ss.SSSZ";

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

    private SelfieItem(Parcel in) {

        Date parcelDate;
        String parcelDateString;
        Double parcelPlaceLat;
        Double parcelPlaceLon;
        Double parcelPlaceAlt;
        Location parcelPlace;
        File parcelFile;

        // Parceling date
        parcelDateString = in.readString();
        SimpleDateFormat sdf = new SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.US);
        parcelDate = new Date();
        try {
            parcelDate = sdf.parse(parcelDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Parceling location
        parcelPlaceLat = in.readDouble();
        parcelPlaceLon = in.readDouble();
        parcelPlaceAlt = in.readDouble();
        parcelPlace = new Location("");
        parcelPlace.setLatitude(parcelPlaceLat);
        parcelPlace.setLongitude(parcelPlaceLon);
        parcelPlace.setAltitude(parcelPlaceAlt);

        // Parceling file
        parcelFile = new File(in.readString());

        mDate = parcelDate;
        mLocation = parcelPlace;
        mFile = parcelFile;

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
        long mSelfieDate = mDate.getTime();
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
     *           PARCELABLE METHODS             *
     *****************************************/

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {

        String parcelDate;
        Double parcelPlaceLat;
        Double parcelPlaceLon;
        Double parcelPlaceAlt;
        String parcelFile;

        // Parceling date
        parcelDate = "";
        if (mDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.US);
            parcelDate = sdf.format(mDate);
        }
        out.writeString(parcelDate);

        // Parceling location
        parcelPlaceLat = 0d;
        parcelPlaceLon = 0d;
        parcelPlaceAlt = 0d;
        if (mLocation != null){
            parcelPlaceLat = mLocation.getLatitude();
            parcelPlaceLon = mLocation.getLongitude();
            parcelPlaceAlt = mLocation.getAltitude();
        }
        out.writeDouble(parcelPlaceLat);
        out.writeDouble(parcelPlaceLon);
        out.writeDouble(parcelPlaceAlt);

        // Parceling file
        parcelFile = "";
        if (mFile != null) {
            parcelFile = mFile.getAbsolutePath();
        }
        out.writeString(parcelFile);


    }

    public static final Parcelable.Creator<SelfieItem> CREATOR
            = new Parcelable.Creator<SelfieItem>() {
        public SelfieItem createFromParcel(Parcel in) {
            return new SelfieItem(in);
        }

        public SelfieItem[] newArray(int size) {
            return new SelfieItem[size];
        }
    };

}
