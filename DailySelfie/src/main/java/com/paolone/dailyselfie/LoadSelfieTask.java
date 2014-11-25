package com.paolone.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by pmorgano on 25/11/14.
 */
public class LoadSelfieTask extends AsyncTask<Object, Integer, Bitmap> {

    /*****************************************
     *              CONSTANTS                *
     *****************************************/
    // TAG for logging
    private static final String TAG = "Dailiy_Selfie";

    /*****************************************
     *                FIELDS                 *
     *****************************************/
    // The context of calling activity
    private Context context;
    // The view to be updated
    private ImageView mImageViewToBeUpdated;
    private int mImageWidth;
    private int mImageHeight;

    /*****************************************
     *              CONSTRUCTOR              *
     *****************************************/

    public LoadSelfieTask(Context context) {
        this.context = context;
    }

    /*****************************************
     *             TASK LIFECYCLE            *
     *****************************************/

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "LoadSelfieTask.onPreExecute entered");
    }

    @Override
    protected Bitmap doInBackground(Object... params) {

        File selfieFile = (File)params[0];
        Bitmap mySrcBitmap = null;
        Bitmap myDestBitmap = null;

        if (selfieFile.exists()) {

            mySrcBitmap = BitmapFactory.decodeFile(selfieFile.getAbsolutePath());

        }

        if (mySrcBitmap != null) {

            // Get info about view to be updated
            mImageViewToBeUpdated = (ImageView) params[1];
            mImageHeight = mImageViewToBeUpdated.getHeight();
            mImageWidth = mImageViewToBeUpdated.getWidth();

            if (mySrcBitmap.getWidth() >= mySrcBitmap.getHeight()){

                myDestBitmap = Bitmap.createBitmap(
                        mySrcBitmap,
                        mySrcBitmap.getWidth()/2 - mySrcBitmap.getHeight()/2,
                        0,
                        mySrcBitmap.getHeight(),
                        mySrcBitmap.getHeight()
                );

            }else{

                myDestBitmap = Bitmap.createBitmap(
                        mySrcBitmap,
                        0,
                        mySrcBitmap.getHeight()/2 - mySrcBitmap.getWidth()/2,
                        mySrcBitmap.getWidth(),
                        mySrcBitmap.getWidth()
                );
            }

            mySrcBitmap = Bitmap.createScaledBitmap(myDestBitmap, mImageWidth, mImageHeight, true);

        }

        return mySrcBitmap;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        Log.i(TAG, "LoadSelfieTask.onProgressUpdate entered");

    }

    @Override
    protected void onPostExecute(Bitmap result) {

        Log.i(TAG, "LoadSelfieTask.onProgressUpdate entered");

        if (result != null) {

            Toast.makeText(context, "LoadSelfieTask: Image Loaded", Toast.LENGTH_LONG).show();
            mImageViewToBeUpdated.setImageBitmap(result);

        }

    }

    /*****************************************
     *           SUPPORT METHODS             *
     *****************************************/

    // *** END OF CLASS ***

}