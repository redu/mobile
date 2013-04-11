package br.com.redu.redumobile.adapters;

import java.util.List;

import br.com.developer.redu.models.Environment;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.widgets.LazyLoadingImageView;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EnviromentListAdapter extends BaseAdapter {

	final private LayoutInflater mInflater;
	final private Context mContext;
	
	private List<Environment> mEnviroment;
	
	public EnviromentListAdapter(Context context, List<Environment> enviroments) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mEnviroment = enviroments; 
	}
	
	@Override
	public int getCount() {
		if(mEnviroment == null) {
			return 0;
		}
				
		return mEnviroment.size();
	}

	@Override
	public Object getItem(int position) {
		return mEnviroment.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
//	public void add(TextView status) {
//		listMaterials.add(status);
//	}
//
//	public void add(List<TextView> statuses) {
//		listMaterials.addAll(statuses);
//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView environment = (TextView) mInflater.inflate(R.layout.enviroment_list_row, null);
		environment.setText(Html.fromHtml(mEnviroment.get(position).name+"<br/>"+"<font color=\"#CCCCCC\"><small>"+mEnviroment.get(position).courses_count+" Cursos</small></font>"));
		return environment;
	}
}
