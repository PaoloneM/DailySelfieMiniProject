package com.paolone.dailyselfie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

//import org.json.JSONArray;
import org.json.JSONException;
//import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * An activity representing a list of Selfies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SelfieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SelfieListFragment} and the item details
 * (if present) is a {@link SelfieDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link SelfieListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class DailySelfieMainActivity extends Activity
        implements SelfieListFragment.Callbacks {

	/*****************************************
	 *              CONSTANTS                *
	 *****************************************/
    // TAG for logging
	private static final String TAG = "Dailiy_Selfie";
	// Saved Instance key for the currently selected selfie item (UUID)
	private static final String SELECTED_SELFIE_KEY = "16f38740-6d99-11e4-9803-0800200c9a66";
	// Saved Instance key for the action bar home button back capability flag (UUID)
	private static final String ACTIONBAR_DISPLAY_OPTIONS = "4fdad5f0-6dd9-11e4-9803-0800200c9a66";
    // File paths
    private static final String DIR = MediaStore.Images.Media.DATA;
    private static final String FILE_RADIX = "Selfie_";
    private static final String FILE_DIR = "DailySelfie";
    // Times
    private static final long ONE_WEEK = 1000L * 60L * 60L * 24L * 7L;
    private static final long ONE_MONTH = 1000L * 60L * 60L * 24L * 30L;
    // Camera management
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String LIST_FILE = "DailiySelfiesList.txt" ;

    /*****************************************
	 *                FIELDS                 *
	 *****************************************/
	// layout type flag
    private boolean mTwoPane = false;
    // layout choice flag - for future uses
	private boolean mForceTwoPaneMode = false;
    // reference to Fragments
    private SelfieDetailFragment mSelfieDetailFragment = null;
    private SelfieListFragment mSelfieListFragment = null;
    // currently selected item pointer
    private String mLastSelectedPosition = null;
    // Picture related fields
    private String mCurrentPhotoPath;
    private String mImageFileName;
    private String mTimeStamp;
    private Date mSelfieTime;
    private Location mSelfieLocation = null;
    private File mImageFile;

    /*****************************************
     *          ACTIVITY LIFECYCLE           *
     *****************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        Log.i(TAG, "DailySelfieMainActivity.onCreate entered");
        
        // Choose which layout use and load it
        loadLayout(mForceTwoPaneMode);
        
        // Check layout style
        mTwoPane = isInTwoPaneMode();

        // Create dummy data for testing purposes
        //createDummyData(SelfiesContent.mChildList);

        SelfiesContent.mChildList = loadSelfieList();
        mapData(SelfiesContent.mGroups, SelfiesContent.mChildList);

        // Manage different layouts based on device size
        fragmentsInit(mTwoPane, savedInstanceState);

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "DailySelfieMainActivity.onStart entered");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "DailySelfieMainActivity.onResume entered");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "DailySelfieMainActivity.onPause entered");
        saveSelfieList(SelfiesContent.mChildList);
   }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "DailySelfieMainActivity.onStop entered");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.i(TAG, "DailySelfieMainActivity.onRestart entered");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "DailySelfieMainActivity.onDestroy entered");
    }

    // Callback method from  SelfieListFragment.Callbacks}
    @Override
    public void onItemSelected(int i, int i2) {

    	Log.i(TAG, "DailySelfieMainActivity.onItemSelected entered");

        showSelfieDetailsNew(mTwoPane, i, i2);
        
        if (!mTwoPane) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    // Menu creation callback
    // Create Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.i(TAG, "DailySelfieMainActivity.onCreateOptionsMenu entered");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dailyselfiemainactivity_menu, menu);
        return true;

    }

    // Menu selected item callback
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.i(TAG, "DailySelfieMainActivity.onOptionsItemSelected entered");

        int id = item.getItemId();

        switch (id){

            // Action's bar home button
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                getActionBar().setDisplayHomeAsUpEnabled(false);
                getFragmentManager().popBackStack();
                return true;

            // Action bar camera button
            case R.id.action_shoot:
                //Toast.makeText(getApplicationContext(), "Click!", Toast.LENGTH_LONG).show();
                saveSelfieList(SelfiesContent.mChildList);
                dispatchTakePictureIntent();
                return true;

            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), getString(R.string.action_settings_message), Toast.LENGTH_LONG).show();
                return true;

        }

        return super.onOptionsItemSelected(item);

    }

    // Camera activity result callback
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "DailySelfieMainActivity.onActivityResult entered");

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if (data != null) {

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                //mImageView.setImageBitmap(imageBitmap);
            }

            Toast.makeText(getApplicationContext(), "WoW! You look great!!!!", Toast.LENGTH_LONG).show();
            createNewSelfie(mSelfieTime, mSelfieLocation, mImageFile);
            updateDisplaiedData();
            saveSelfieList(SelfiesContent.mChildList);

        }
    }

    // Saved Instance Management
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Log.i(TAG, "DailySelfieMainActivity.onSaveInstanceState entered");

		// save the current foreground feed
        savedInstanceState.putString(SELECTED_SELFIE_KEY, mLastSelectedPosition);
        savedInstanceState.putInt(ACTIONBAR_DISPLAY_OPTIONS, getActionBar().getDisplayOptions());
        Log.i(TAG, "Saved selfie index = " + mLastSelectedPosition);
        // as recommended by android basics training, always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

    }
    
    /*****************************************
     *           SUPPORT METHODS             *
     *****************************************/

    // Simply load default main layout
    // TODO: improve managing user layout selection or override Android choice for mid-layer devices
    private void loadLayout(boolean forceTwoPaneMode) {
		// Load fragment container frame layout
        setContentView(R.layout.daily_selfie_main);
	}

	// Determines weather the layout must be composed of two panes:
    // if exists selfie_detail_container that means app is running on a large screen
	private boolean isInTwoPaneMode() {
		
        Log.i(TAG, "DailySelfieMainActivity.isInTwoPaneMode entered");

		return findViewById(R.id.selfie_list) != null;
	}

    // Manage different layouts based on device size
	private void fragmentsInit(boolean mIsInTwoPanesMode, Bundle savedInstanceBundle) {
		
        Log.i(TAG, "DailySelfieMainActivity.fragmentsInit entered");

		if (mIsInTwoPanesMode) {           

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((SelfieListFragment) getFragmentManager()
                    .findFragmentById(R.id.selfie_list))
                    .setActivateOnItemClick(true);
            
        } else {
        	
        	// Check if there is no saved instance before create a new list. If the state is 
        	// saved, the fragment is automatically re-created
        	if (savedInstanceBundle == null) {
        		
				// Create selfie list fragment
				mSelfieListFragment = new SelfieListFragment();
				// In single pane mode add the selfies' list Fragment to the container
				getFragmentManager()
						.beginTransaction()
						.add(R.id.selfie_fragment_container,
								mSelfieListFragment).commit();			
				
			} else {
				// Restore Action Bar View Options
				getActionBar().setDisplayOptions(savedInstanceBundle.getInt(ACTIONBAR_DISPLAY_OPTIONS));
			}
        	
        }
	}
	

	// depending on layout style, changes foreground fragment and updates its content 
	private void showSelfieDetailsNew(boolean mIsInTwoPanesMode, int groupId, int childId) {
		
		Log.i(TAG, "DailySelfieMainActivity.showSelfieDetailsNew entered");

		// Create FeedFragment instance
		mSelfieDetailFragment = new SelfieDetailFragment();

        // Show the detail view in this activity by
        // adding or replacing the detail fragment using a fragment transaction.
		
		// Set arguments for the new fragment
        Bundle arguments = new Bundle();
        arguments.putInt(SelfieDetailFragment.ARG_GROUP_ID, groupId);
        arguments.putInt(SelfieDetailFragment.ARG_CHILD_ID, childId);
        mSelfieDetailFragment.setArguments(arguments);
        
        // Begin fragment transaction
        FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction()
                .replace(R.id.selfie_fragment_container, mSelfieDetailFragment);
        
        // Is in single pane mode add transaction to backstack to allow back to list fragment
        if (!mIsInTwoPanesMode) {
        	
        	mFragmentTransaction.addToBackStack(null);
        	
        }
        
        // Commit changes to fragments
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        					.commit();

		// execute transaction now
	    getFragmentManager().executePendingTransactions();

        mLastSelectedPosition = (groupId + "-" + childId);
     
	}

    private void createDummyData(ArrayList<SelfieItem> childList) {

        // only add data if no data available, for testing purposes before persistance management
        if (!childList.isEmpty()) return;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Date mDate = null;

        try {
            mDate = format.parse("2014-01-01T01:02:03Z");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        File mFile = new File(DIR, (FILE_RADIX + mDate.toString()));

        childList.add(new SelfieItem(mDate, mFile));

        try {
            mDate = format.parse("2014-11-01T01:02:03Z");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mFile = new File(DIR, (FILE_RADIX + mDate.toString()));

        childList.add(new SelfieItem(mDate, mFile));

        try {
            mDate = format.parse("2014-11-21T01:02:03Z");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mFile = new File(DIR, (FILE_RADIX + mDate.toString()));

        childList.add(new SelfieItem(mDate, mFile));

        try {
            mDate = format.parse("2014-10-21T11:02:03Z");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mFile = new File(DIR, (FILE_RADIX + mDate.toString()));

        childList.add(new SelfieItem(mDate, mFile));

        try {
            mDate = format.parse("2013-11-21T01:22:00Z");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mFile = new File(DIR, (FILE_RADIX + mDate.toString()));

        childList.add(new SelfieItem(mDate, mFile));

        try {
            mDate = format.parse("2014-11-22T08:10:03Z");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mFile = new File(DIR, (FILE_RADIX + mDate.toString()));

        childList.add(new SelfieItem(mDate, mFile));

        try {
            mDate = format.parse("2014-11-23T09:39:03Z");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mFile = new File(DIR, (FILE_RADIX + mDate.toString()));

        childList.add(new SelfieItem(mDate, mFile));


    }

    private void mapData(SparseArray<SelfiesGroup> groups, ArrayList<SelfieItem> mChildList) {

        Log.i(TAG, "DailySelfieMainActivity.mapData entered");

        groups.clear();

    	// Create 3 groups
        SelfiesGroup mLatestSelfies = new SelfiesGroup(getString(R.string.recent_selfies_group));
        SelfiesGroup mMonthSelfies = new SelfiesGroup(getString(R.string.last_month_selfies_group));
        SelfiesGroup mOlderSelfies = new SelfiesGroup(getString(R.string.older_selfies_group));

        // Scan selfies to assign to the correct group
    	for (SelfieItem child: mChildList){

            long mSelfieAge = child.getSelfieAge();

            if (mSelfieAge > ONE_MONTH) {
    			mOlderSelfies.children.add(mChildList.indexOf(child));
    		} else if (mSelfieAge > ONE_WEEK) {
                mMonthSelfies.children.add(mChildList.indexOf(child));
            } else {
                mLatestSelfies.children.add(mChildList.indexOf(child));
            }
    	}

        if (mLatestSelfies != null) {

            groups.append(0, mLatestSelfies);

        }

        if (mMonthSelfies != null) {

            groups.append(1, mMonthSelfies);

        }

        if (mOlderSelfies != null) {

            groups.append(2, mOlderSelfies);

        }

    }

    // Dispatch intent for taking pictures
    private void dispatchTakePictureIntent() {

        Log.i(TAG, "DailySelfieMainActivity.dispatchTakePictureIntent entered");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "DailySelfieMainActivity.dispatchTakePictureIntent: unable to create file");
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                Log.i(TAG, "DailySelfieMainActivity.dispatchTakePictureIntent: launch intent!");
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            } else {

                Log.i(TAG, "DailySelfieMainActivity.dispatchTakePictureIntent: file not found");

            }

        }
    }

    private File createImageFile() throws IOException {

        Log.i(TAG, "DailySelfieMainActivity.createImageFile entered");
        // Create an image file name
        mSelfieTime = new Date();
        mTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(mSelfieTime);
        mImageFileName = FILE_RADIX + "_" + mTimeStamp;
        Log.i(TAG, "DailySelfieMainActivity.createImageFile filename is " + mImageFileName);

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/" + FILE_DIR );
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdir();
        }

        Log.i(TAG, "DailySelfieMainActivity.createImageFile storage dir is " + storageDir);

        mImageFile = File.createTempFile(
                mImageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + mImageFile.getAbsolutePath();
        return mImageFile;
    }

    // Create new data item
    private boolean createNewSelfie(Date selfieTime, Location selfieLocation, File imageFile){

        Log.i(TAG, "DailySelfieMainActivity.createNewSelfie entered");

        SelfiesContent.addSelfie(selfieTime, selfieLocation, imageFile);

        return true;
    }

    // Order selfies data and update display
    private void updateDisplaiedData() {

        Log.i(TAG, "DailySelfieMainActivity.updateDisplayedData entered");

        mapData(SelfiesContent.mGroups, SelfiesContent.mChildList);
        mSelfieListFragment.refreshList();

    }

    // Persistance management
    private void saveSelfieList (ArrayList<SelfieItem> list){

        Log.i(TAG, "********** DailySelfieMainActivity.saveSelfieList entered **********");

        Gson gson = new GsonBuilder().create();

        JsonArray jsArray = gson.toJsonTree(list).getAsJsonArray();

        JSONObject obj = new JSONObject();

        try {
            obj.put("Name", "paolone");
            obj.put("Author", "paolone");
            obj.put("SelfieList", jsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            writeFile(obj);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<SelfieItem> loadSelfieList () {

        Log.i(TAG, "********** DailySelfieMainActivity.loadSelfieList entered *************");

        ArrayList<SelfieItem> list = null;

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject = readFile();
        } catch (IOException e) {
            Log.i(TAG, "DailySelfieMainActivity.loadSelfieList: readFile trows file not found error");
        }

        String jsonArray = null;
        try {
            String name = (String) jsonObject.get("Name");
            String author = (String) jsonObject.get("Author");
            Log.i(TAG, "DailySelfieMainActivity.loadSelfieList decodes object: Author = " + author + ", name = " + name);
            jsonArray = (String) jsonObject.get("SelfieList") ;
            Log.i(TAG, "DailySelfieMainActivity.loadSelfieList decodes object: SelfieList = " + jsonArray.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonArray != null) {

            list = new Gson().fromJson(jsonArray, new TypeToken<List<SelfieItem>>(){}.getType());

        }

        return list;

    }

    private void writeFile(JSONObject listToSave) throws FileNotFoundException {

        Log.i(TAG, "DailySelfieMainActivity.writeFile entered");

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/" + FILE_DIR );
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdir();
        }

        File storageFile = new File(storageDir, LIST_FILE);

        FileOutputStream fos = new FileOutputStream(storageFile);

        PrintWriter pw = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(fos)));

        pw.write(listToSave.toString());

        pw.close();

    }

    private JSONObject readFile() throws IOException {

        Log.i(TAG, "DailySelfieMainActivity.readFile entered");

        JSONObject listToFill = new JSONObject();

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/" + FILE_DIR );

        File storageFile = new File(storageDir, LIST_FILE);

        FileInputStream fis = new FileInputStream(storageFile);

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        StringBuilder responseStrBuilder = new StringBuilder();

        String mInputStr = br.readLine();

        while (mInputStr != null){
            responseStrBuilder.append(mInputStr.toString());
            mInputStr = br.readLine();
        }

        try {
            listToFill = new JSONObject(responseStrBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        br.close();

        return listToFill;

    }


    // *** END OF CLASS ***
}
