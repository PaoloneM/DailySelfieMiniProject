package com.paolone.dailyselfie;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paolone.dailyselfie.dummy.DummyContent;

/**
 * A fragment representing a single Selfie detail screen.
 * This fragment is either contained in a {@link DailySelfieMainActivity}
 * in two-pane mode (on tablets) or a {@link SelfieDetailActivity}
 * on handsets.
 */
public class SelfieDetailFragment extends Fragment {

	/*****************************************
	 *              CONSTANTS                *
	 *****************************************/
    // TAG for logging
	private static final String TAG = "Dailiy_Selfie";

	// The fragment argument representing the item ID that this fragment
    public static final String ARG_GROUP_ID = "group_id";
    public static final String ARG_CHILD_ID = "child_id";

    // The dummy content this fragment is presenting.
    private DummyContent.DummyItem mItem;
    private String DummyString;
    // Mandatory empty constructor for the fragment manager to instantiate the fragment
    // (e.g. upon screen orientation changes).
    public SelfieDetailFragment() {
    	
    	Log.i(TAG, "SelfieDetailFragment constructor entered");
    	
    }
    
    /*****************************************
     *          FRAGMENT LIFECYCLE           *
     *****************************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "SelfieDetailFragment.onCreate entered");
        
        if (getArguments().containsKey(ARG_GROUP_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            int argumentGroup = getArguments().getInt(ARG_GROUP_ID);
            int argumentChild = getArguments().getInt(ARG_CHILD_ID);
            DummyString = DummyContent.childData[argumentGroup][argumentChild];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        Log.i(TAG, "SelfieDetailFragment.onCreateView entered");

        View rootView = inflater.inflate(R.layout.fragment_selfie_detail, container, false);

        // Show the dummy content as date in a TextView.
        if (DummyString != null) {
            ((TextView) rootView.findViewById(R.id.selfie_detail)).setText(DummyString);
        }

        return rootView;
    }
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i(TAG, "SelfieDetailFragment.onAttach entered");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.i(TAG, "SelfieDetailFragment.onActivityCreated entered");
		
		if (savedInstanceState != null){
			Log.i(TAG, "Entered FeedFragment SelfieDetailFragment.onActivityCreated restore function");

			// TODO - check if need to do something
		}
		
	}
	
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Log.i(TAG, "Entered FeedFragment onSaveInstanceState");

        // TODO - check if need to do something

        // as recommended by android basics training, always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

    }

	// *** END OF CLASS ***
	
}
