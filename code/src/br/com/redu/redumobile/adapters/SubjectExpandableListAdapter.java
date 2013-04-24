package br.com.redu.redumobile.adapters;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.developer.redu.models.Lecture;
import br.com.developer.redu.models.Subject;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.activities.LectureActivity;
import br.com.redu.redumobile.activities.lecture.UploadStep1Activity;

public class SubjectExpandableListAdapter extends BaseExpandableListAdapter {

	Context mContext;
	List<Subject> mSubjects;
	List<List<Lecture>> mLectures;
	Subject mCurrentSubject;
	Dialog mDialogInfo;
	
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.environment_lecture_row, null);
		}
		
		final Lecture lecture = (Lecture) getChild(groupPosition, childPosition);
		final Subject subject = (Subject) getGroup(groupPosition); 
		
		TextView tvOrder = (TextView) convertView.findViewById(R.id.tvOrdering);
		tvOrder.setText(Integer.toString(lecture.position));
		
		TextView tv = (TextView) convertView.findViewById(R.id.tvLecturesName);
		tv.setText(lecture.name);
		
		//TODO PROGRESSO DA AULA IMPLEMENTAR
		//ImageView iv = (ImageView) convertView.findViewById(R.id.ivIsFinalized);
		
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, LectureActivity.class);
				i.putExtra(Lecture.class.getName(), lecture);
				i.putExtra(Subject.class.getName(), subject);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.environment_module_row, null);
		}
		mCurrentSubject = mSubjects.get(groupPosition);
		
		ImageView ibAdd = (ImageView) convertView.findViewById(R.id.iv_add);
		ibAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*Builder builder = new AlertDialog.Builder(mContext);
				builder.setView(LayoutInflater.from(mContext).inflate(R.layout.popup_listview_row, null));
				mDialogNewLecture = builder.create();
		    	mDialogNewLecture.show();*/
				Intent it = new Intent(mContext, UploadStep1Activity.class);
				mContext.startActivity(it);
			}
		});
		
		ImageView ivHelp = (ImageView) convertView.findViewById(R.id.iv_info);
		ivHelp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Builder builder = new AlertDialog.Builder(mContext);
				View v2 = LayoutInflater.from(mContext).inflate(R.layout.popup_listview_row, null);
				TextView tvInfo = (TextView)v2.findViewById(R.id.tv_insert_file_folder);
				tvInfo.setText(mCurrentSubject.description);
				builder.setView(v2);
				mDialogInfo = builder.create();
		    	mDialogInfo.show();
			}
		});
		
		TextView tv = (TextView) convertView.findViewById(R.id.tvSuject);
		tv.setText(Html.fromHtml(mCurrentSubject.name+"<br/>"+"<font color=\"#CCCCCC\"><smal>"+getChildrenCount(groupPosition)+" Aulas</small></font>"));
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
