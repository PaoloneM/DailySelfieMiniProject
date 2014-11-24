package com.paolone.dailyselfie;

import android.location.Location;
import android.util.SparseArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Static content
 */
public class SelfiesContent {

    /*****************************************
     *                FIELDS                 *
     *****************************************/
    // Data
    public static SparseArray<SelfiesGroup> mGroups = new SparseArray<SelfiesGroup>();
    public static ArrayList<SelfieItem> mChildList = new ArrayList<SelfieItem>();

    /*****************************************
     *          EXPOSED  METHODS             *
     *****************************************/

    static boolean addSelfie(Date selfieDate, Location selfieLocation, File selfieFile){

        SelfieItem mCandidateSelfieItem = new SelfieItem(selfieDate, selfieLocation, selfieFile);
        mChildList.add(mCandidateSelfieItem);

        return true;

    }
}
