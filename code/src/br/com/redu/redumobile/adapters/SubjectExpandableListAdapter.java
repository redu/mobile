package br.com.redu.redumobile.adapters;

import java.util.List;

import br.com.developer.redu.models.Lecture;
import br.com.developer.redu.models.Subject;
import br.com.redu.redumobile.activities.LectureActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class SubjectExpandableListAdapter extends BaseExpandableListAdapter {

	Context mContext;
	List<Subject> mSubjects;
	List<List<Lecture>> mLectures;
	
	public SubjectExpandableListAdapter(Context context, List<Subject> subjects, List<List<Lecture>> lectures) {
		mContext = context;
		mSubjects = subjects;
		mLectures = lectures;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mLectures.get(groupPosition).get(childPosition);
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
		
		final Lecture lecture = (Lecture) getChild(groupPosition, childPosition);
		
		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		tv.setText(lecture.name);
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, LectureActivity.class);
				i.putExtra(Lecture.class.getName(), lecture);
				mContext.startActivity(i);	
			}
		});
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mLectures.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mSubjects.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mSubjects.size();
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
		tv.setText(mSubjects.get(groupPosition).name);
		
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
