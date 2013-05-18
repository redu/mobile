package br.com.redu.redumobile.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.scribe.exceptions.OAuthConnectionException;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.activities.StatusDetailActivity;
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
			
			Bundle extras = new Bundle();

			if(status.isActivityType() || status.isHelpType()) {
				notification.setNotificationClickIntentClass(StatusDetailActivity.class);
				extras.putSerializable(StatusDetailActivity.EXTRAS_STATUS, status);
				extras.putSerializable(StatusDetailActivity.EXTRAS_ENABLE_GO_TO_WALL_ACTION, false);
				extras.putSerializable(StatusDetailActivity.EXTRAS_IS_FROM_NOTIFICATION, true);
				notification.setNotificationClickIntentBundle(extras);
				
			} else if(status.isLogType()) {
				// TODO abrir notificacao de novas aulas 
			}
			
			taskResult.addMessage(notification);
		}
		
		return taskResult;
	}

	private List<Status> loadStatuses(Context context) {
		boolean firstRunning = false;
		
		LoadStatusesFromWebManager.notifyOnStart();

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
				List<Status> temp;
				temp = redu.getStatusesTimelineByUser(userId, Status.TYPE_ACTIVITY, pageStr);
				if(temp != null) {
					statuses.addAll(temp);
				}
				
				temp = redu.getStatusesTimelineByUser(userId, Status.TYPE_HELP, pageStr);
				if(temp != null) {
					statuses.addAll(temp);
				}
				
				temp = redu.getStatusesTimelineLogByUser(userId, Status.LOGEABLE_TYPE_COURSE, pageStr);
				if(temp != null) {
					statuses.addAll(temp);
				}
				
				temp = redu.getStatusesTimelineLogByUser(userId, Status.LOGEABLE_TYPE_SUBJECT, pageStr);
				if(temp != null) {
					statuses.addAll(temp);
				}
				
				temp = redu.getStatusesTimelineLogByUser(userId, Status.LOGEABLE_TYPE_LECTURE, pageStr);
				if(temp != null) {
					statuses.addAll(temp);
				}
	
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
							if(checkNotifiable(context, status) && firstRunning == false) {
								notifiableStatuses.add(status);
							}
						}
					}
					
					dbHelper.putAllStatuses(statuses, userId);
				}
			}

			LoadStatusesFromWebManager.notifyOnComplete();

		} catch(OAuthConnectionException e) {
			e.printStackTrace();
			LoadStatusesFromWebManager.notifyOnError(e);
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
	
	public static void addOnLoadStatusesFromWebListener(OnLoadStatusesFromWebListener listener) {
		LoadStatusesFromWebManager.add(listener);
	}
	
	public static boolean isWorking() {
		return LoadStatusesFromWebManager.isWorking();
	}
	
	private static class LoadStatusesFromWebManager {

		private static boolean mIsWorking;
		private static final List<OnLoadStatusesFromWebListener> mListeners = new ArrayList<OnLoadStatusesFromWebListener>();

		public static void add(OnLoadStatusesFromWebListener listener) {
			mListeners.add(listener);
		}
		
		public static void clear() {
			mListeners.clear();
		}

		public static void notifyOnStart() {
			mIsWorking = true;
			for (OnLoadStatusesFromWebListener listener : mListeners) {
				listener.onStart();
			}
		}

		public static void notifyOnComplete() {
			mIsWorking = false;
			for (OnLoadStatusesFromWebListener listener : mListeners) {
				listener.onComplete();
			}
		}

		public static void notifyOnError(Exception e) {
			mIsWorking = false;
			for (OnLoadStatusesFromWebListener listener : mListeners) {
				listener.onError(e);
			}
		}
		
		public static boolean isWorking() {
			return mIsWorking;
		}
	}
}