package com.paolone.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    /*****************************************
     *              CONSTANTS                *
     *****************************************/
    // TAG for logging
    private static final String TAG = "Dailiy_Selfie";

    /*****************************************
     *                FIELDS                 *
     *****************************************/

    private final LayoutInflater inf;
    private SparseArray<SelfiesGroup> groups;

    private class ViewHolder {
        TextView date;
        TextView place;
        ImageView thumb;
    }

    private Context mFragmentContext;

    /*****************************************
     *              CONSTRUCTOR              *
     *****************************************/

    public ExpandableListAdapter(SparseArray<SelfiesGroup> groups, Context context) {
        this.groups = groups;
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
        SelfieItem mChildObj = SelfiesContent.mChildList.get(mChildIndex);
        String mText = mChildObj.getDate().toString();
        holder.date.setText(mText);
        holder.thumb.setImageBitmap(BitmapFactory.decodeResource(convertView.getResources(), R.drawable.selfie_place_holder));

        File selfieFile = mChildObj.getFile();
//        new LoadSelfieTask(mFragmentContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, selfieFile, holder.thumb);
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

}