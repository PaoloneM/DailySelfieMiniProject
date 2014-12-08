package com.paolone.dailyselfie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A list fragment representing a list of Selfies. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link SelfieDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class SelfieListFragment extends Fragment {

	/*****************************************
	 *              CONSTANTS                *
	 *****************************************/
    // TAG for logging
	private static final String TAG = "Dailiy_Selfie";

    // The serialization (saved instance state) Bundle key representing the activated item position.
    // Only used on tablets.
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static final String NEXT_SELFIE_KEY = "NextSelfie";
    // Camera management
    static final int REQUEST_IMAGE_CAPTURE = 1;


    /*****************************************
     *                FIELDS                 *
     *****************************************/

    // The fragment's current callback object, which is notified of list item clicks.
    private Callbacks mCallbacks = sDummyCallbacks;

    // The current activated item position. Only used on tablets.
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private ExpandableListView selfiesExpandableList;
    private View rootView;

    // List adapter
    private ExpandableListAdapter mSelfieListAdaper;

    // fragment context
    private Context mContext;
    private DailySelfieStorageManager mStorageManager;

    // Selfie data
    private Date mSelfieTime;
    private String mTimeStamp;
    private String mImageFileName;
    private File mImageFile;
    private Location mSelfieLocation = null;
    private SelfieItem mNextSelfie;


    /*****************************************
     *                INTERFACES             *
     *****************************************/

    // A callback interface that all activities containing this fragment must implement.
    // This mechanism allows activities to be notified of item selections.
    public interface Callbacks {
        // Callback for when an item has been selected.
        public void onItemSelected(String filePath);
    }

    // A dummy implementation of the {@link Callbacks} interface that does nothing.
    // Used only when this fragment is not attached to an activity.
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String filePath) {
        }
    };

    /*****************************************
     *          FRAGMENT LIFECYCLE           *
     *****************************************/

    // Mandatory empty constructor for the fragment manager to instantiate the* fragment
    // (e.g. upon screen orientation changes).
    public SelfieListFragment() {
    	Log.i(TAG, "**** SelfieListFragment constructor entered");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.i(TAG, "**** SelfieListFragment.onAttach entered");

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        Log.i(TAG, "**** SelfieListFragment.onCreate entered");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflates layout for this fragment (linear layout containing an ExpandableListView)
        rootView = inflater.inflate(R.layout.fragment_selfies_list, container, false);

        return rootView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, "**** SelfieListFragment.onViewCreated entered");

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(NEXT_SELFIE_KEY)) {
            mNextSelfie = savedInstanceState.getParcelable(NEXT_SELFIE_KEY);
        }

        // Ensures that action bar's home button doesn't show back capabilities (used in detail view)
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);

        // Get a reference to the ExpandableListView of previously inflated layout and set the
        // adapter and onClick callbacks
        selfiesExpandableList = (ExpandableListView) view.findViewById(R.id.SelfiesExpandableView);

        mContext = view.getContext();

        mSelfieListAdaper = new ExpandableListAdapter(mContext);

        selfiesExpandableList.setAdapter(mSelfieListAdaper);

        selfiesExpandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {

                Log.i(TAG, "SelfieListFragment.selfiesExpandableList.onChildClick entered");
                Log.i(TAG, "SelfieListFragment.selfiesExpandableList.onChildClick ExpandableView = " + expandableListView.toString());
                Log.i(TAG, "SelfieListFragment.selfiesExpandableList.onChildClick View = " + view.toString());
                Log.i(TAG, "SelfieListFragment.selfiesExpandableList.onChildClick i = " + i + " i2 = " + i2);
                Log.i(TAG, "SelfieListFragment.selfiesExpandableList.onChildClick l = " + l);
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                //mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
                String filePath = mSelfieListAdaper.getSelfie((Integer) mSelfieListAdaper.getChild(i, i2)).getFile().getAbsolutePath();
                mCallbacks.onItemSelected(filePath);
                return false;
            }
        });

        Log.i(TAG, "**** SelfieListFragment.onViewCreated: mSelfieListAdaper = " + mSelfieListAdaper.toString());

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "**** SelfieListFragment.onActivityCreated entered");

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i(TAG, "**** SelfieListFragment.onStart entered");

        // Create an instance of storage manager to load stored selfies list data
        mStorageManager = new DailySelfieStorageManager(getString(R.string.storage_dir), getString(R.string.storage_file), DailySelfieStorageManager.EXTERNAL_MEMORY, mContext);
        Log.i(TAG, "**** SelfieListFragment.onStart: mStorageManager = " + mStorageManager.toString());

        // Load selfies' list in a local variable
        ArrayList<SelfieItem> list = mStorageManager.loadSelfieList();
        Log.i(TAG, "**** SelfieListFragment.onStart: list size = " + list.size());

        // Populate adapter content with loaded list
        if (list != null) {
            Log.i(TAG, "**** SelfieListFragment.onStart: populate adapter's list");
            mSelfieListAdaper.clearSelfielist();
            for (SelfieItem item: list)
                mSelfieListAdaper.addSelfie(item);
        }

        Log.i(TAG, "**** SelfieListFragment.onStart: mSelfieListAdaper = " + mSelfieListAdaper.toString());

    }

    @Override
    public void onResume(){
        super.onResume();

        Log.i(TAG, "**** SelfieListFragment.onResume entered");

    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG, "**** SelfieListFragment.onPause entered");

        // Save list data to persistent storage
        mStorageManager.saveSelfieList(mSelfieListAdaper.getSelfiesList());


    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG, "**** SelfieListFragment.onStop entered");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "**** SelfieListFragment.onDestroyView entered");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "**** SelfieListFragment.onDestroy entered");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.i(TAG, "**** SelfieListFragment.onDetach entered");

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.i(TAG, "**** SelfieListFragment.onSaveInstanceState entered");

        // TODO: save selfie item to be created
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }

        if (mNextSelfie != null){
            outState.putParcelable(NEXT_SELFIE_KEY, mNextSelfie);
        }

    }

    // Camera activity result callback
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "****++++ SelfieListFragment.onActivityResult entered");
        Log.i(TAG, "**** SelfieListFragment.onActivityResult: mSelfieListAdaper = " + mSelfieListAdaper.toString());


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            if (data != null) {

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
            }

            createNewSelfie(mNextSelfie.getDate(), mNextSelfie.getLocation(), mNextSelfie.getFile());

            mStorageManager.saveSelfieList(mSelfieListAdaper.getSelfiesList());

        }
    }

    /*****************************************
     *           EXPOSED METHODS             *
     *****************************************/

    public void shootSelfie(){

        mStorageManager.saveSelfieList(mSelfieListAdaper.getSelfiesList());

        dispatchTakePictureIntent();

    }


    /*****************************************
     *           SUPPORT METHODS             *
     *****************************************/

    // Turns on activate-on-click mode. When this mode is on, list items will be given the
    // 'activated' state when touched.
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        selfiesExpandableList.setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            selfiesExpandableList.setItemChecked(mActivatedPosition, false);
        } else {
            selfiesExpandableList.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    // Dispatch intent for taking pictures
    private void dispatchTakePictureIntent() {

        Log.i(TAG, "SelfieListFragment.dispatchTakePictureIntent entered");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "** SelfieListFragment.dispatchTakePictureIntent: unable to create file");
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                Log.i(TAG, "****++++ SelfieListFragment.dispatchTakePictureIntent: launch intent!");
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            } else {

                Log.i(TAG, "**SelfieListFragment.dispatchTakePictureIntent: file not found");

            }

        }

    }

    private File createImageFile() throws IOException {

        Log.i(TAG, "SelfieListFragment.createImageFile entered");
        // Create an image file name
        mSelfieTime = new Date();
        mTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(mSelfieTime);
        mImageFileName = mContext.getString(R.string.selfie_file_radix) + "_" + mTimeStamp;
        Log.i(TAG, "SelfieListFragment.createImageFile: filename is " + mImageFileName);

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdir();
        }

        storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/" + mContext.getString(R.string.storage_dir) );
        success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdir();
        }

        Log.i(TAG, "SelfieListFragment.createImageFile: storage dir is " + storageDir);

        mImageFile = File.createTempFile(
                mImageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String mCurrentPhotoPath = "file:" + mImageFile.getAbsolutePath();

        Log.i(TAG, "SelfieListFragment.createImageFile: storage file is " + mCurrentPhotoPath);

        mNextSelfie = new SelfieItem(mSelfieTime,mImageFile);

        return mImageFile;

    }

    // Create new data item
    private boolean createNewSelfie(Date selfieTime, Location selfieLocation, File imageFile){

        Log.i(TAG, "SelfieListFragment.createNewSelfie entered");

        SelfieItem newSelfie = new SelfieItem(selfieTime, selfieLocation, imageFile);

        Log.i(TAG, "SelfieListFragment.createNewSelfie: mSelfieListAdaper = " + mSelfieListAdaper.toString());

        Log.i(TAG, "SelfieListFragment.createNewSelfie: newSelfie = " + newSelfie.getDate().toString());

        mSelfieListAdaper.addSelfie(newSelfie);

        return true;
    }
    // *** END OF CLASS ***

}

