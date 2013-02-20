package br.com.redu.redumobile.adapters;

import java.util.List;

import br.com.developer.redu.models.Course;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.activities.SpaceActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class CoursesExpandableListAdapter extends BaseExpandableListAdapter {

	Context mContext;
	List<Course> mCourses;
	List<List<Space>> mSpaces;
	
	public CoursesExpandableListAdapter(Context context, List<Course> courses, List<List<Space>> spaces) {
		mContext = context;
		mCourses = courses;
		mSpaces = spaces;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mSpaces.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_expandable_list_item_1, null);
		}
		
		final Space space = (Space) getChild(groupPosition, childPosition);
		
		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		tv.setText(space.name);
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, SpaceActivity.class);
				i.putExtra(Space.class.getName(), space);
				mContext.startActivity(i);	
			}
		});
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mSpaces.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mCourses.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mCourses.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_expandable_list_item_1, null);
		}
		
		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		tv.setText(mCourses.get(groupPosition).name);
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
