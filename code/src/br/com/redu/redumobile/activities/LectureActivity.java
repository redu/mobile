package br.com.redu.redumobile.activities;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.scribe.exceptions.OAuthConnectionException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Lecture;
import br.com.developer.redu.models.Space;
import br.com.developer.redu.models.Subject;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.util.DownloadHelper;

public class LectureActivity extends BaseActivity {

	public static final String EXTRAS_SUBJECT = "EXTRAS_SUBJECT";
	public static final String EXTRAS_LECTURE = "EXTRAS_LECTURE";

	public static final String EXTRAS_SUBJECT_ID = "EXTRAS_SUBJECT_ID";
	public static final String EXTRAS_LECTURE_ID = "EXTRAS_LECTURE_ID";
	public static final String EXTRAS_SPACE_ID = "EXTRAS_SPACE_ID";
	public static final String EXTRAS_ENVIRONMENT_PATH = "EXTRAS_ENVIRONMENT_PATH";

	private TextView mTvSubject;
	private TextView mTvLecture;
	private Button mBtEdit;
	private Button mBtRemove;

	private Button mBtIsDone;
	private Button mBtWall;

	private Lecture mLecture;
	private Subject mSubject;
	private Space mSpace;

	private ProgressDialog mProgressDialog;

	private AlertDialog alertDialog;

	DownloadFile df;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_lecture);

		mTvSubject = (TextView) findViewById(R.id.tv_title_action_bar);
		mTvLecture = (TextView) findViewById(R.id.tvLecture);
		mBtEdit = (Button) findViewById(R.id.btEdit);
		mBtRemove = (Button) findViewById(R.id.btRemove);
		// mIvImage = (ImageView) findViewById(R.id.ivImage);
		// mTvFileName = (TextView) findViewById(R.id.tvFileName);

		mBtIsDone = (Button) findViewById(R.id.btIsDone);
		mBtWall = (Button) findViewById(R.id.btWall);

		Bundle extras = getIntent().getExtras();
		mLecture = (Lecture) extras.get(EXTRAS_LECTURE);
		mSubject = (Subject) extras.get(EXTRAS_SUBJECT);

		final String lectureId = extras.getString(EXTRAS_LECTURE_ID);
		final String subjectId = extras.getString(EXTRAS_SUBJECT_ID);
		final String spaceId = extras.getString(EXTRAS_SPACE_ID);
		final String environmentPath = extras
				.getString(EXTRAS_ENVIRONMENT_PATH);

		if (mLecture != null && mSubject != null) {
			init();
		} else {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					try {
						DefaultReduClient redu = ReduApplication
								.getReduClient(LectureActivity.this);
						mSubject = redu.getSubject(subjectId);
						mLecture = redu.getLecture(lectureId);
						mSpace = redu.getSpace(spaceId);
						return null;
					} catch (OAuthConnectionException e) {
						e.printStackTrace();
						return null;
					}
				}

				protected void onPostExecute(Void param) {
					if (mSubject != null && mLecture != null) {
						Bundle extrasToUp = new Bundle();
						extrasToUp.putSerializable(SpaceActivity.EXTRAS_SPACE,
								mSpace);
						extrasToUp.putSerializable(
								SpaceActivity.EXTRAS_ENVIRONMENT_PATH,
								environmentPath);
						setUpClass(SpaceActivity.class, extrasToUp);

						init();
					}
				};
			}.execute();
		}
	}

	private void initDialogs() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Aguarde…");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						df.cancel(true);
						df.running = false;
						dialog.dismiss();
					}
				});

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Visualização no Navegador");
		alertDialogBuilder
				.setMessage(
						"A visualização desta Aula será feita através do navegador web do seu dispositivo")
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(mLecture.getSelfLink()));
						startActivity(i);
					}
				})
				.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});
		alertDialog = alertDialogBuilder.create();
	}

	private void init() {
		initDialogs();

		LinearLayout layoutLecture;
		if (mLecture.type.equals(Lecture.TYPE_CANVAS)) {
			layoutLecture = (LinearLayout) findViewById(R.id.llCanvas);
			Button ibCanvas = (Button) layoutLecture
					.findViewById(R.id.ibCanvas);
			ibCanvas.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDialog.show();
				}
			});
			layoutLecture.setVisibility(View.VISIBLE);
		} else if (mLecture.type.equals(Lecture.TYPE_EXERCISE)) {
			layoutLecture = (LinearLayout) findViewById(R.id.llExercice);
			Button ibAccess = (Button) layoutLecture
					.findViewById(R.id.btExercice);
			ibAccess.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDialog.show();
				}
			});
			layoutLecture.setVisibility(View.VISIBLE);
		} else if (mLecture.type.equals(Lecture.TYPE_DOCUMENT)) {
			layoutLecture = (LinearLayout) findViewById(R.id.llDocument);
			ImageView ivDocument = (ImageView) layoutLecture
					.findViewById(R.id.ivDocument);
			ivDocument.setImageResource(R.drawable.ic_doc_big);
			TextView tvDocument = (TextView) layoutLecture
					.findViewById(R.id.tvDocument);
			tvDocument.setText(mLecture.name);
			Button ibDocument = (Button) layoutLecture
					.findViewById(R.id.btAcessarDocument);
			ibDocument.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Lecture[] lecture = { mLecture };

					java.io.File f = new java.io.File(DownloadHelper
							.getLecturePath(), mLecture.getFileName());
					if (f.exists()) {
						Intent it;
						try {
							it = DownloadHelper.loadDocInReader(f);
							startActivity(it);
						} catch (ActivityNotFoundException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						df = new DownloadFile();
						df.execute(lecture);
					}
				}
			});
			layoutLecture.setVisibility(View.VISIBLE);
		} else if (mLecture.type.equals(Lecture.TYPE_MEDIA)) {
			layoutLecture = (LinearLayout) findViewById(R.id.llMedia);
			ImageView ivMedia = (ImageView) layoutLecture
					.findViewById(R.id.ivMedia);
			ivMedia.setImageResource(R.drawable.ic_midia_big);
			TextView tvMedia = (TextView) layoutLecture
					.findViewById(R.id.tvMedia);
			tvMedia.setText(mLecture.name);
			Button ibMedia = (Button) layoutLecture
					.findViewById(R.id.ibAcessarMedia);
			ibMedia.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(mLecture.getFilePath())));
				}
			});
			layoutLecture.setVisibility(View.VISIBLE);
		} else if (mLecture.type.equals(Lecture.TYPE_PAGE)) {
			layoutLecture = (LinearLayout) findViewById(R.id.llPage);
			TextView tvPage = (TextView) layoutLecture
					.findViewById(R.id.tvPage);
			tvPage.setText(Html.fromHtml(mLecture.content));
			layoutLecture.setVisibility(View.VISIBLE);
		}

		mTvLecture.setText(mLecture.name);
		mTvSubject.setText(mSubject.name);

		Log.i("Aula", Integer.toString(mLecture.id));

		mBtEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});

		mBtRemove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		// String extension = mLecture.mimetype;

		// mTvFileName.setText(mLecture.name+" Tipo:"+mLecture.type);

		mBtIsDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		mBtWall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LectureActivity.this,
						LectureWallActivity.class);
				i.putExtra(LectureWallActivity.EXTRAS_LECTURE, mLecture);
				startActivity(i);
			}
		});

	}

	private class DownloadFile extends
			AsyncTask<Lecture, Integer, java.io.File> {
		private volatile boolean running = true;

		@Override
		protected java.io.File doInBackground(Lecture... lecture) {
			try {

				String path = lecture[0].getFilePath();
				URL url = new URL(path);

				String[] temp = path.split("\\?")[0].split("/");
				String fileName = temp[temp.length - 1];

				URLConnection connection = url.openConnection();
				connection.connect();
				// this will be useful so that you can show a typical 0-100%
				// progress bar
				int fileLength = connection.getContentLength();

				String newFolder = DownloadHelper.getLecturePath();
				java.io.File myNewFolder = new java.io.File(newFolder);
				if (!myNewFolder.exists())
					myNewFolder.mkdirs();

				// download the file
				InputStream input = new BufferedInputStream(url.openStream());
				// java.io.File sdCard =
				// Environment.getExternalStorageDirectory();
				/*
				 * java.io.File dir = new File (sdCard.getAbsolutePath() +
				 * "/dir1/dir2"); dir.mkdirs(); File file = new File(dir,
				 * "filename");
				 */
				java.io.File filling = new java.io.File(newFolder, fileName);
				OutputStream output = new FileOutputStream(filling);

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					// publishing the progress....
					if (!running) {
						output.flush();
						output.close();
						input.close();
						filling.delete();
						return null;
					}
					publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
				return filling;
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			running = false;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			mProgressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(java.io.File file) {
			super.onPostExecute(file);
			mProgressDialog.setProgress(0);
			mProgressDialog.dismiss();
			if (this != null && file != null) {
				try {
					Intent it = DownloadHelper.loadDocInReader(file);
					startActivity(it);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	class LoadProgress extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Void doInBackground(String... text) {
			DefaultReduClient redu = ReduApplication.getReduClient(LectureActivity.this);
			// redu.getProgress(mLecture.id);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		};
	}

}
