package br.com.redu.redumobile.widgets;

import java.util.HashMap;

import br.com.redu.redumobile.R;
import br.com.redu.redumobile.util.ImageLoader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

/**
 * Free for anyone to use, just say thanks and share :-)
 * 
 * @author Blundell
 * 
 */
public class LazyLoadingImageView extends FrameLayout {

	static private HashMap<String, Bitmap> memoryCache = new HashMap<String, Bitmap>();
	
	private static final ScaleType[] mScaleTypeArray = { 
			ScaleType.MATRIX,
			ScaleType.FIT_XY, 
			ScaleType.FIT_START, 
			ScaleType.FIT_CENTER,
			ScaleType.FIT_END, 
			ScaleType.CENTER, 
			ScaleType.CENTER_CROP,
			ScaleType.CENTER_INSIDE };
	
	private Context mContext;
	
	private ProgressBar mSpinner;
	private ImageView mImageView;
	
	private int mScaleTypeIndex;
	private Drawable mDefaultSrc;
	
	private boolean existDefaultImage;

	/**
	 * This is used when creating the view in XML To have an image load in XML
	 * use the tag
	 * 'image="http://developer.android.com/images/dialog_buttons.png"'
	 * Replacing the url with your desired image Once you have instantiated the
	 * XML view you can call setImageDrawable(url) to change the image
	 * 
	 * @param context
	 * @param attrSet
	 */
	public LazyLoadingImageView(final Context context, final AttributeSet attrSet) {
		super(context, attrSet);

		TypedArray styledAttrs = context.obtainStyledAttributes(attrSet, R.styleable.LazyLoadingImageView);
		mScaleTypeIndex = styledAttrs.getInt(R.styleable.LazyLoadingImageView_scaleType, -1);
		mDefaultSrc = styledAttrs.getDrawable(R.styleable.LazyLoadingImageView_src);
		
		existDefaultImage = (mDefaultSrc != null);
		
		styledAttrs.recycle();
		
		instantiate(context, null);
	}

	/**
	 * This is used when creating the view programatically Once you have
	 * instantiated the view you can call setImageDrawable(url) to change the
	 * image
	 * 
	 * @param context
	 *            the Activity context
	 * @param imageUrl
	 *            the Image URL you wish to load
	 */
	public LazyLoadingImageView(final Context context, final String imageUrl) {
		super(context);
		instantiate(context, imageUrl);
	}

	/**
	 * First time loading of the LoaderImageView Sets up the LayoutParams of the
	 * view, you can change these to get the required effects you want
	 */
	private void instantiate(final Context context, final String imageUrl) {
		mContext = context;

		mImageView = new ImageView(mContext);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
		mImageView.setAdjustViewBounds(true);
		
		if(mScaleTypeIndex != -1) {
			mImageView.setScaleType(mScaleTypeArray[mScaleTypeIndex]);
		}
		
		if(existDefaultImage) {
			mImageView.setImageDrawable(mDefaultSrc);
		} else {
			mSpinner = new ProgressBar(mContext);
			mSpinner.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
			mSpinner.setIndeterminate(true);
			addView(mSpinner);
		}
		
		addView(mImageView);

		if (imageUrl != null) {
			this.setImageUrl(imageUrl);
		}
	}

	public void setImageUrl(String imageUrl) {
		(new ImageViewLoader()).execute(imageUrl);
	}

	public void setImageResource(int drawableResource) {
        setImage(drawableResource);
	}
	
	class ImageViewLoader extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected void onPreExecute() {
	        setImageDefault();
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bmp;
			String urlImagem = params[0];
			try {
				if(memoryCache.containsKey(urlImagem)) {
					bmp = memoryCache.get(urlImagem);
				} else {
					bmp = ImageLoader.loadBitmap(mContext, urlImagem, false);
					if(bmp != null) {
						memoryCache.put(urlImagem, bmp);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				bmp = null;
			}
			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if(result != null) {
				setImage(result);
			} else {
				setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private void setImageDefault() {
		if(existDefaultImage) {
			mImageView.setImageDrawable(mDefaultSrc);
			mImageView.setVisibility(View.VISIBLE);
		} else {
			mSpinner.setVisibility(View.VISIBLE);
			mImageView.setVisibility(View.GONE);
		}		
		
		LazyLoadingImageView.this.invalidate();
	}
	
	private void setImage(Bitmap bmp) {
		mImageView.setImageBitmap(bmp);
		mImageView.setVisibility(View.VISIBLE);
		
		if(!existDefaultImage) {
			mSpinner.setVisibility(View.GONE);
		}
		
		LazyLoadingImageView.this.invalidate();
	}
	
	private void setImage(int resId) {
		mImageView.setImageResource(resId);
		mImageView.setVisibility(View.VISIBLE);
		
		if(!existDefaultImage) {
			mSpinner.setVisibility(View.GONE);
		}
		
		LazyLoadingImageView.this.invalidate();
	}

}
