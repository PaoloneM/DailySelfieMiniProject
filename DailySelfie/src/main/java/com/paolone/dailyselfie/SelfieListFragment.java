package com.paolone.dailyselfie;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paolone.dailyselfie.dummy.DummyContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
    private static final String FILE_DIR = "DailySelfie";
    private static final String LIST_FILE = "DailiySelfiesList.txt";
    // Times
    private static final long ONE_WEEK = 1000L * 60L * 60L * 24L * 7L;
    private static final long ONE_MONTH = 1000L * 60L * 60L * 24L * 30L;

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

    /*****************************************
     *                INTERFACES             *
     *****************************************/

    // A callback interface that all activities containing this fragment must implement.
    // This mechanism allows activities to be notified of item selections.
    public interface Callbacks {
        // Callback for when an item has been selected.
        public void onItemSelected(int i, int i2);
    }

    // A dummy implementation of the {@link Callbacks} interface that does nothing.
    // Used only when this fragment is not attached to an activity.
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int i, int i2) {
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
                mCallbacks.onItemSelected(i, i2);
                return false;
            }
        });

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

    }

    @Override
    public void onResume(){
        super.onResume();

        Log.i(TAG, "**** SelfieListFragment.onResume entered");

        // Create an instance of storage manager to load stored selfies list data
        mStorageManager = new DailySelfieStorageManager(getString(R.string.storage_dir), getString(R.string.storage_file), DailySelfieStorageManager.EXTERNAL_MEMORY, mContext);

        // Load selfies' list in a local variable
        ArrayList<SelfieItem> list = mStorageManager.loadSelfieList();

        // Populate adapter content with loaded list
        if ((list != null) && (mSelfieListAdaper.getSelfiesListSize() == 0)) {
            for (SelfieItem item: list)
                mSelfieListAdaper.addSelfie(item);
        }


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

        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /*****************************************
     *           EXPOSED METHODS             *
     *****************************************/

    public boolean refreshList(){

        mSelfieListAdaper.notifyDataSetChanged();
        return true;

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

    // *** END OF CLASS ***

}

