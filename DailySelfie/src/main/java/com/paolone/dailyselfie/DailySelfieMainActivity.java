package com.paolone.dailyselfie;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.text.format.DateUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    // Times
    private static final long ONE_WEEK = 1000L * 60L * 60L * 24L * 7L;
    private static final long ONE_MONTH = 1000L * 60L * 60L * 24L * 30L;

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

    /*****************************************
     *          ATIVITY LIFECYCLE            *
     *****************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        Log.i(TAG, "DailySelfieMainActivity.onCreate entered");
        
        // Choose which layout use and load it
        loadLayout(mForceTwoPaneMode);
        
        // Check layout style
        mTwoPane = isInTwoPaneMode();

        createDummyData(SelfiesContent.mChildList);
        mapDummyData(SelfiesContent.mGroups, SelfiesContent.mChildList);

        // Manage different layouts based on device size
        fragmentsInit(mTwoPane, savedInstanceState);

        // TODO: If exposing deep links into your app, handle intents here.
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
    
    // Menu selected item callback
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
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
        }
        return super.onOptionsItemSelected(item);
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

    private void mapDummyData(SparseArray<SelfiesGroup> groups, ArrayList<SelfieItem> mChildList) {

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


    // *** END OF CLASS ***
}
