package br.com.redu.redumobile.fragments.space;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.File;
import br.com.developer.redu.models.Folder;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.activities.HomeSpaceActivity.SupportMaterialFragmentListener;
import br.com.redu.redumobile.adapters.SupportMaterialsAdapter;
import br.com.redu.redumobile.fragments.EnvironmentFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SupportMaterialFragment extends Fragment {
	private int mCurrentPage;
	private boolean mUpdatingList;
	private Space mSpace;
	
	private List<Folder> folders;
	private List<File> files;
	private Folder mFolder;
	
	
	ListView lvFiles;
	public SupportMaterialFragmentListener mListener;
	
	public SupportMaterialFragment(){
		super();
	}
	
	public SupportMaterialFragment(Folder folder) {
		mFolder = folder;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View v = inflater.inflate(R.layout.fragment_support_material, container, false);
		TextView indice;
		ImageButton ibBack;
		if(mFolder != null){
			indice = (TextView)v.findViewById(R.id.tvIndice);
			indice.setText(mFolder.name);
			ibBack = (ImageButton)v.findViewById(R.id.ibBack);
			ibBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mListener.onBackToPreviousFragment(mFolder);
				}
			});
		}else{
			ibBack = (ImageButton)v.findViewById(R.id.ibBack);
			ibBack.setVisibility(View.GONE);
		}
		
		mSpace = (Space)getActivity().getIntent().getExtras().get(Space.class.getName());
		
		lvFiles = (ListView) v.findViewById(R.id.lvFiles);
		lvFiles.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Object obj = lvFiles.getItemAtPosition(position);
				if (obj instanceof Folder){
					Folder folder = (Folder)lvFiles.getItemAtPosition(position);
					/*transaction.replace(R.id.fragment_container, coursesAndServicesFragment);
					transaction.addToBackStack(null);*/
					/*FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.addToBackStack(null);*/
					/*Fragment newFragment = new FolderMaterialFragment();
					getChildFragmentManager().beginTransaction().replace(R.id.vp2, newFragment);*/
					mListener.onSwitchToNextFragment(folder);
				}else{
					File file = (File)lvFiles.getItemAtPosition(position);
					//TODO DOWNLOAD DE THE FILE
				}
			}
			
		});
		
		new LoadFoldersAndFilesTask().execute();
		
		return v;
	}
	
	class LoadFoldersAndFilesTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getReduClient();
			String folderRaizID;
			if(mFolder == null){
				folderRaizID = redu.getFolderID(mSpace.id);
			}else{
				folderRaizID = mFolder.id;
			}
			folders = redu.getFolders(folderRaizID);
			folders.removeAll(Collections.singleton(null));
			files = redu.getFilesByFolder(folderRaizID);
			files.removeAll(Collections.singleton(null));
			/*files = redu.getFilesByFolder(mSpace.)*/
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			/*List<String> array = new ArrayList<String>();
			for (int i = 0; i < folders.size()	; i++) {
				array.add(folders.get(i).name);
			}
			for (int i = 0; i < files.size(); i++) {
				array.add(files.get(i).name);
			}*/
			/*array.removeAll(Collections.singleton(null));*/
			lvFiles.setAdapter(new SupportMaterialsAdapter(getActivity(), folders, files));
		};
	}

	public void setListener(SupportMaterialFragmentListener listener){
		mListener = listener;
	}
	
	
/*	public static Fragment newInstance(
			SupportMaterialFragmentListener firstPageFragmentListener) {
			
		return null;
	}*/
}
