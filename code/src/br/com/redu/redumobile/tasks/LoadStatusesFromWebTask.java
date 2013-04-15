package br.com.redu.redumobile.tasks;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.scribe.exceptions.OAuthConnectionException;

import android.content.Context;
import android.content.ContextWrapper;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Status;
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

	private static boolean mIsWorking;
	
	@Override
	public TaskResult doWork(ContextWrapper ctx) {

		mIsWorking = true;
		
		TaskResult res = new TaskResult();

		List<Status> notifiableStatues = loadStatuses(ctx);

		for (Status status : notifiableStatues) {
			NotificationMessage notification = new NotificationMessage("Redu Mobile", status.text);
			notification.setNotificationId(Integer.valueOf(status.id));
			// notification.notificationIconResource = R.drawable.icon_notification_cards_clubs;
			notification.setNotificationClickIntentClass(HomeActivity.class);
			res.addMessage(notification);
		}

		mIsWorking = false;
		
		return res;
	}

	private List<Status> loadStatuses(Context context) {
		List<Status> notifiableStatuses = new ArrayList<Status>();

		DbHelper dbHelper = DbHelper.getInstance(context);

		try {
			DefaultReduClient redu = ReduApplication.getReduClient();
			String userId = String.valueOf(ReduApplication.getUser().id);
	
			long dbTimestamp = dbHelper.getTimestamp();
	
			boolean loadNextPage = true;
	
			for (int page = 1; loadNextPage; page++) {
				List<Status> statuses = redu.getStatusesTimelineByUser(userId, null, String.valueOf(page));
	
				if (statuses != null) {
	
					for (Status status : statuses) {
						try {
							status.createdAtInMillis = DateUtil.dfIn.parse(status.created_at).getTime();
						} catch (ParseException e) {
							e.printStackTrace();
							status.createdAtInMillis = 0;
						}
	
						if (status.createdAtInMillis <= dbTimestamp) {
							loadNextPage = false;
							break;
	
						} else {
							// ignoring unused Status on mobile app
							if (status.isLogType()) {
								if (!status.isLectureLogeableType() 
										&& !status.isCourseLogeableType() 
										&& !status.isSubjectLogeableType()) {
									continue;
								}
							}
	
							if(checkNotifiable(context, status)) {
								notifiableStatuses.add(status);
							}
							
							dbHelper.putStatus(status);
						}
					}
				}
			}
		} catch(OAuthConnectionException e) {
			e.printStackTrace();
			mIsWorking = false;
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

	public static boolean isWorking() {
		return mIsWorking;
	}
}