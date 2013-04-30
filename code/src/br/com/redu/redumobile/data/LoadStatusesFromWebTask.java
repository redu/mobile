package br.com.redu.redumobile.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.scribe.exceptions.OAuthConnectionException;

import android.content.Context;
import android.content.ContextWrapper;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.activities.HomeActivity;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.util.DateUtil;
import br.com.redu.redumobile.util.SettingsHelper;

import com.buzzbox.mob.android.scheduler.NotificationMessage;
import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;

public class LoadStatusesFromWebTask implements Task {

	@Override
	public String getTitle() {
		return "Redu Mobile";
	}

	@Override
	public String getId() {
		return "redumobile"; // give it an ID
	}

	@Override
	public TaskResult doWork(ContextWrapper ctx) {

		TaskResult taskResult = new TaskResult();
		
		List<Status> notifiableStatues = loadStatuses(ctx);

		for (Status status : notifiableStatues) {
			NotificationMessage notification = new NotificationMessage(getTitle(), status.text);
			notification.setNotificationId(Integer.valueOf(status.id));
			notification.setNotificationIconResource(R.drawable.ic_status_notification);
			notification.setNotificationClickIntentClass(HomeActivity.class);
			taskResult.addMessage(notification);
		}
		
		return taskResult;
	}

	private List<Status> loadStatuses(Context context) {
		boolean firstRunning = false;
		
		LoadingStatusesManager.notifyOnStart();

		List<Status> notifiableStatuses = new ArrayList<Status>();

		DbHelper dbHelper = DbHelper.getInstance(context);

		try {
			DefaultReduClient redu = ReduApplication.getReduClient(context);
			String userId = String.valueOf(ReduApplication.getUser(context).id);
	
			long dbTimestamp;
			if(dbHelper.getOldestStatusesWereDownloaded(userId)) {
				dbTimestamp = dbHelper.getTimestamp();
			} else {
				dbTimestamp = 0;
			}
			firstRunning = (dbTimestamp == 0) ? true : false;
	
			boolean loadNextPage = true;
	
			for (int page = 1; loadNextPage; page++) {
				String pageStr = String.valueOf(page);
				
				List<Status> statuses = new ArrayList<Status>();
				statuses.addAll(redu.getStatusesTimelineByUser(userId, Status.TYPE_ACTIVITY, pageStr));
				statuses.addAll(redu.getStatusesTimelineByUser(userId, Status.TYPE_HELP, pageStr));
				statuses.addAll(redu.getStatusesTimelineLogByUser(userId, Status.LOGEABLE_TYPE_COURSE, pageStr));
				statuses.addAll(redu.getStatusesTimelineLogByUser(userId, Status.LOGEABLE_TYPE_LECTURE, pageStr));
				statuses.addAll(redu.getStatusesTimelineLogByUser(userId, Status.LOGEABLE_TYPE_SUBJECT, pageStr));
	
				if (statuses.size() == 0) {
					dbHelper.setOldestStatusesWereDownloaded(userId);
					loadNextPage = false;
					
				} else {
					for (Status status : statuses) {
						try {
							status.createdAtInMillis = DateUtil.dfIn.parse(status.created_at).getTime();
						} catch (ParseException e) {
							e.printStackTrace();
							status.createdAtInMillis = 0;
						}
	
						if (status.createdAtInMillis <= dbTimestamp) {
							loadNextPage = false;
						} else {
							long id = dbHelper.putStatus(status, userId);
							if(checkNotifiable(context, status) && firstRunning == false && id != -1) {
								notifiableStatuses.add(status);
							}
						}
					}
				}
			}

			LoadingStatusesManager.notifyOnComplete();

		} catch(OAuthConnectionException e) {
			e.printStackTrace();
			LoadingStatusesManager.notifyOnError(e);
		}

		return notifiableStatuses;
	}

	private boolean checkNotifiable(Context ctx, Status status) {
		if(SettingsHelper.get(ctx, SettingsHelper.KEY_ACTIVATED_NOTIFICATIONS)) {
			if (status.isLogType()) {
				if(status.isLectureLogeableType() 
						&& SettingsHelper.get(ctx, SettingsHelper.KEY_NEW_LECTURES)) {
					return true;
					
				} else if (status.isCourseLogeableType() 
						&& SettingsHelper.get(ctx, SettingsHelper.KEY_NEW_COURSES)) {
					return true;
					
				} else if (status.isSubjectLogeableType()
						&& SettingsHelper.get(ctx, SettingsHelper.KEY_NEW_SUBJECTS)) {
					return true;
				}
				
			} else if (status.isActivityType() 
					&& SettingsHelper.get(ctx, SettingsHelper.KEY_WHEN_ANSWER_ME)) {
				return true;
			}
		}
		
		return false;
	}
}