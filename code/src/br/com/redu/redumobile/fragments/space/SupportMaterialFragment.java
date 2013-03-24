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
import android.widget.ListAdapter;
import android.widget.ListView;

public class SupportMaterialFragment extends Fragment {
	private int mCurrentPage;
	private boolean mUpdatingList;
	private Space mSpace;
	
	private List<Folder> folders;
	private List<File> files;
	
	
	ListView lvFiles;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View v = inflater.inflate(R.layout.fragment_support_material, container, false);
		
		mSpace = (Space)getActivity().getIntent().getExtras().get(Space.class.getName());
		
		lvFiles = (ListView) v.findViewById(R.id.lvFiles);
		lvFiles.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Object obj = lvFiles.getItemAtPosition(position);
				if (obj instanceof Folder){
					Folder folder = (Folder)lvFiles.getItemAtPosition(position);
				}else{
					File file = (File)lvFiles.getItemAtPosition(position);
				}
				/*transaction.replace(R.id.fragment_container, coursesAndServicesFragment);
				transaction.addToBackStack(null);*/
				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
				transaction.addToBackStack(null);
				Fragment fragment = new SupportMaterialFragment();
			}
			
		});
		
		new LoadFoldersAndFilesTask().execute();
		
		return v;
	}
	
	class LoadFoldersAndFilesTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getReduClient();
			
			String folderRaizID = redu.getFolderID(mSpace.id);
			folders = redu.getFolders(folderRaizID);
			folders.removeAll(Collections.singleton(null));
			files = redu.getFilesByFolder(folderRaizID);
			files.removeAll(Collections.singleton(null));
			/*files = redu.getFilesByFolder(mSpace.)*/
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
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
			Log.i("THIAGO", "MSG1");
		};
	}
}
