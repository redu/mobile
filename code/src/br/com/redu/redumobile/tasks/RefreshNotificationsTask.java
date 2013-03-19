package br.com.redu.redumobile.tasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import br.com.developer.redu.DefaultReduClient;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.activities.HomeActivity;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.util.NotificationRefreshingHelper;

import com.buzzbox.mob.android.scheduler.NotificationMessage;
import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;

public class RefreshNotificationsTask implements Task {

	static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", new Locale("pt-BR"));
	
	@Override
	public String getTitle() {
		return "Reminder";
	}

	@Override
	public String getId() {
		return "reminder"; // give it an ID
	}

	@Override
	public TaskResult doWork(ContextWrapper ctx) {
		TaskResult res = new TaskResult();

		// TODO implement your business logic here
		// i.e. query the DB, connect to a web service using HttpUtils, etc..

		NotificationMessage notification = new NotificationMessage(
				"Redu Mobile", "You have new notifications in Redu");
		// notification.notificationIconResource =
		// R.drawable.icon_notification_cards_clubs;
		notification.setNotificationClickIntentClass(HomeActivity.class);

		res.addMessage(notification);

		return res;
	}

	class LoadStatusesTask extends
			AsyncTask<Void, Void, Void> {

		private Context mContext;

		public LoadStatusesTask(Context context) {
			mContext = context;
		}
		
		protected Void doInBackground(Void... params) {
			DbHelper dbHelper = new DbHelper(mContext);
			
			DefaultReduClient redu = ReduApplication.getReduClient();
			String userId = String.valueOf(ReduApplication.getUser().id);
			
			long lastSavedTimestamp = NotificationRefreshingHelper.getLastStatusTimestamp(mContext);
			long lastTimestamp = 0;
			
			boolean loadNextPage = true;
			
			for(int page = 1; loadNextPage; page++) {
				List<br.com.developer.redu.models.Status> statuses = redu.getStatusesTimelineByUser(userId, null, String.valueOf(page));
				
				if (statuses != null) {
					for (br.com.developer.redu.models.Status status : statuses) {
						long timestamp;
						
						try {
							timestamp = df.parse(status.created_at).getTime();
						} catch (ParseException e) {
							e.printStackTrace();
							timestamp = 0;
						}
						
						if(timestamp > lastSavedTimestamp) {
							if (status.type.equals("Log") && status.logeable_type.equals("CourseEnrollment")) {
								continue;
							}
							
							dbHelper.putStatus(status);
							
							if(timestamp > lastTimestamp) {
								lastTimestamp = timestamp;
							}
							
						} else {
							loadNextPage = false;
							break;
						}
					}
				}
			}
			
			if(lastTimestamp > 0) {
				NotificationRefreshingHelper.setLastStatusTimestamp(mContext, lastTimestamp);
			}
			
			return null;
		}
	}

}
