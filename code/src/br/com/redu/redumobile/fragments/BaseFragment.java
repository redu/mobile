package br.com.redu.redumobile.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.util.ImageUtils;
import br.com.redu.redumobile.widgets.VerticalAnimation;

public class BaseFragment extends Fragment {

	private View mNoConnectionView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ViewGroup vg = (ViewGroup) getActivity().findViewById(R.id.no_connection_container);
		if(vg != null) {
			mNoConnectionView = LayoutInflater.from(getActivity()).inflate(R.layout.no_connection, null);
			mNoConnectionView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					hideNoConnectionAlert();
				}
			});

			//vg.addView(mNoConnectionView);
		}
	}
	
	private void animateNoConnectionAlert(boolean isToShow) {
		if(getActivity() != null && mNoConnectionView != null) {
			
			int expandedHeightDip = 50;
			int collapsedHeightDip = 0;
			int duration = 500;
			Resources res = getResources();
			
			int expandedHeight = (int) ImageUtils.dipToPx(expandedHeightDip, res);
			int collapsedHeight = (int) ImageUtils.dipToPx(collapsedHeightDip, res);
			
			if (mNoConnectionView.getHeight() < collapsedHeight) {			
				return;
			}
			
			if (expandedHeight < collapsedHeight) {
				expandedHeight = collapsedHeight;
			}
	
			final int startHeight = mNoConnectionView.getHeight();
			int finishHeight;
			
			if (isToShow) {
				finishHeight = expandedHeight;
			} else {
				finishHeight = collapsedHeight;
			}
	
			mNoConnectionView.startAnimation(new VerticalAnimation(mNoConnectionView, startHeight, finishHeight, duration));
		}	
	}
	
	public void showNoConnectionAlert() {
		animateNoConnectionAlert(true);
	}
	
	public void hideNoConnectionAlert() {
		animateNoConnectionAlert(false);
	}
}
