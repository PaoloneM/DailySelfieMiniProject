package com.paolone.dailyselfie;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    /*****************************************
     *              CONSTANTS                *
     *****************************************/
    // TAG for logging
    private static final String TAG = "Dailiy_Selfie";
    // Times
    private static final long ONE_WEEK = 1000L * 60L * 60L * 24L * 7L;
    private static final long ONE_MONTH = 1000L * 60L * 60L * 24L * 30L;

    /*****************************************
     *                FIELDS                 *
     *****************************************/

    private final LayoutInflater inf;
    private SparseArray<SelfiesGroup> groups;
    private ArrayList<SelfieItem> selfies;

    private class ViewHolder {
        TextView date;
        TextView place;
        ImageView thumb;
    }

    private Context mFragmentContext;

    /*****************************************
     *              CONSTRUCTOR              *
     *****************************************/


    public ExpandableListAdapter(Context context) {

        Log.i(TAG, "****** ExpandableListAdapter constructor entered");

        this.selfies = new ArrayList<SelfieItem>();
        this.groups = mapData(context, selfies);
        inf = LayoutInflater.from(context);
        mFragmentContext = context;

    }


    /*****************************************
     *          ADAPTER LIFECYCLE            *
     *****************************************/

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = inf.inflate(R.layout.selfie_list_item_layout, parent, false);
            holder = new ViewHolder();

            holder.date = (TextView) convertView.findViewById(R.id.selfieListItemDateView);
            holder.place = (TextView) convertView.findViewById(R.id.selfieListItemPlaceView);
            holder.thumb = (ImageView) convertView.findViewById(R.id.selfieListItemThumbView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Integer mChildIndex = (Integer) getChild(groupPosition, childPosition);
        SelfieItem mChildObj = selfies.get(mChildIndex);
        String mText = mChildObj.getDate().toString();
        holder.date.setText(mText);
        holder.thumb.setImageBitmap(BitmapFactory.decodeResource(convertView.getResources(), R.drawable.selfie_place_holder));

        File selfieFile = mChildObj.getFile();
        new LoadSelfieTask(mFragmentContext).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, selfieFile, holder.thumb);
        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inf.inflate(R.layout.group_item, parent, false);

            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.group_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SelfiesGroup mGroup = (SelfiesGroup) getGroup(groupPosition);
        String mText = mGroup.toString();
        holder.date.setText(mText);

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    /*****************************************
     *            EXPOSED METHODS            *
     *****************************************/

    public void addSelfie(SelfieItem newSelfie){

        selfies.add(0, newSelfie);
        groups = mapData(mFragmentContext, selfies);

        notifyDataSetChanged();

    }

    public int getSelfiesListSize(){

        return selfies.size();

    }

    public ArrayList<SelfieItem> getSelfiesList(){

        return selfies;

    }

    public void clearSelfielist(){

        selfies.clear();

    }

    /*****************************************
     *            SUPPORT METHODS            *
     *****************************************/


    private SparseArray<SelfiesGroup> mapData(Context context, ArrayList<SelfieItem> mChildList) {

        Log.i(TAG, "ExpandableListAdapter.mapData entered");

        SparseArray<SelfiesGroup> mappedGroups = new SparseArray<SelfiesGroup>();

        // Create 3 groups
        SelfiesGroup mLatestSelfies = new SelfiesGroup(context.getString(R.string.recent_selfies_group));
        SelfiesGroup mMonthSelfies = new SelfiesGroup(context.getString(R.string.last_month_selfies_group));
        SelfiesGroup mOlderSelfies = new SelfiesGroup(context.getString(R.string.older_selfies_group));

        //if (mChildList == null) return;

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

            mappedGroups.append(0, mLatestSelfies);

        }

        if (mMonthSelfies != null) {

            mappedGroups.append(1, mMonthSelfies);

        }

        if (mOlderSelfies != null) {

            mappedGroups.append(2, mOlderSelfies);

        }

        return mappedGroups;

    }
}