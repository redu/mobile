package br.com.redu.redumobile.data;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.activities.StatusDetailActivity;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.util.DateUtil;
import br.com.redu.redumobile.util.SettingsHelper;

import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;

public class LoadStatusesFromWebTask implements Task {

	private static final int DELAY_TO_CHECK_NOTIFICATIONS_IN_MINUTES = 30;

	@Override
	public String getTitle() {
		return "Redu";
	}

	@Override
	public String getId() {
		return "redu"; // give it an ID
	}

	synchronized public static void run(Context context) {
		LoadStatusesFromWebManager.runNow(context);
	}

	@Override
	public TaskResult doWork(ContextWrapper ctx) {
		Log.i("Redu Sync", "doWork() started");
		
		if(isWorking()) {
			return null;
		}
		
		loadStatuses(ctx);
//
//		if (notifiableStatues != null) {
//			for (Status status : notifiableStatues) {
//				NotificationMessage notification = new NotificationMessage(getTitle(), status.text);
//				notification.setNotificationId(Integer.valueOf(status.id));
//				// notification.setNotificationIconResource(R.drawable.ic_status_notification);
//
//				Bundle extras = new Bundle();
//
//				if (status.isActivityType() || status.isHelpType()) {
//					notification.setNotificationClickIntentClass(StatusDetailActivity.class);
//					extras.putSerializable(StatusDetailActivity.EXTRAS_STATUS, status);
//					extras.putSerializable(StatusDetailActivity.EXTRAS_ENABLE_GO_TO_WALL_ACTION, false);
//					extras.putSerializable(StatusDetailActivity.EXTRAS_IS_FROM_NOTIFICATION, true);
//					notification.setNotificationClickIntentBundle(extras);
//					notification.setFlagResource(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//
//				} else if (status.isLogType()) {
//					// TODO abrir notificacao de novas aulas
//				}
//
//				taskResult.addMessage(notification);
//			}
//		}

		return null;
	}

	private List<Status> loadStatuses(Context context) {
		Log.i("Redu Sync", "loadStatus() started");
		
		LoadStatusesFromWebManager.notifyOnStart();

		List<Status> notifiableStatuses = new ArrayList<Status>();

		DbHelper dbHelper = DbHelper.getInstance(context);

		try {
			DefaultReduClient redu = ReduApplication.getReduClient(context);
			String userId = String.valueOf(ReduApplication.getUser(context).id);

			long timeOfMostRecentStatus = dbHelper.getTimeOFMostRecentStatus();
			long timeToStopSync = (dbHelper.isAllAncientStatusesWereDownloaded(userId)) ? timeOfMostRecentStatus : 0;

			boolean loadNextPage = true;

			List<Status> temp;
			List<Status> statuses = new ArrayList<Status>();

			for (int page = 1; loadNextPage; page++) {
				String pageStr = String.valueOf(page);

				temp = redu.getStatusesTimelineByUser(userId, Status.TYPE_ACTIVITY, pageStr);
				if (temp != null) {
					statuses.addAll(temp);
				}

				temp = redu.getStatusesTimelineByUser(userId, Status.TYPE_HELP, pageStr);
				if (temp != null) {
					statuses.addAll(temp);
				}

				temp = redu.getStatusesTimelineLogByUser(userId, Status.LOGEABLE_TYPE_COURSE, pageStr);
				if (temp != null) {
					statuses.addAll(temp);
				}

				temp = redu.getStatusesTimelineLogByUser(userId, Status.LOGEABLE_TYPE_SUBJECT, pageStr);
				if (temp != null) {
					statuses.addAll(temp);
				}

				temp = redu.getStatusesTimelineLogByUser(userId, Status.LOGEABLE_TYPE_LECTURE, pageStr);
				if (temp != null) {
					statuses.addAll(temp);
				}

				if (statuses.size() == 0) {
					dbHelper.setAllAncientStatusesWereDownloaded(userId);
					loadNextPage = false;
				} else {
					for (Status status : statuses) {
						try {
							status.createdAtInMillis = DateUtil.dfIn.parse(status.created_at).getTime();
						} catch (ParseException e) {
							e.printStackTrace();
							status.createdAtInMillis = 0;
						}

						if (status.createdAtInMillis < timeToStopSync) {
							loadNextPage = false;
						} else {
							if (checkNotifiable(context, status, timeOfMostRecentStatus)) {
//								notifiableStatuses.add(status);
								showNotification(context, status);
							}
						}
					}

					dbHelper.putAllStatuses(statuses, userId);

					statuses.clear();
				}
				
				Log.i("Redu Sync", "Page " + pageStr + " synchronized.");
			}

			LoadStatusesFromWebManager.notifyOnComplete(context);

		} catch (Exception e) {
			e.printStackTrace();
			LoadStatusesFromWebManager.notifyOnError(context, e);
		}

		return notifiableStatuses;
	}

	private boolean checkNotifiable(Context ctx, Status status, long timeOfMostRecentStatus) {
		if (timeOfMostRecentStatus == 0 || status.createdAtInMillis <= timeOfMostRecentStatus) {
			return false;
		}
		
		if (SettingsHelper.get(ctx, SettingsHelper.KEY_ACTIVATED_NOTIFICATIONS)) {
			if (status.isLogType()) {
				if (status.isLectureLogeableType() && SettingsHelper.get(ctx, SettingsHelper.KEY_NEW_LECTURES)) {
					return true;

				} else if (status.isCourseLogeableType() && SettingsHelper.get(ctx, SettingsHelper.KEY_NEW_COURSES)) {
					return true;

				} else if (status.isSubjectLogeableType() && SettingsHelper.get(ctx, SettingsHelper.KEY_NEW_SUBJECTS)) {
					return true;
				}

			} else if (status.isActivityType() && SettingsHelper.get(ctx, SettingsHelper.KEY_WHEN_ANSWER_ME)) {
				return true;
			}
		}

		return false;
	}

	synchronized public static void addOnLoadStatusesFromWebListener(OnLoadStatusesFromWebListener listener) {
		LoadStatusesFromWebManager.add(listener);
	}

	synchronized public static boolean isWorking() {
		return LoadStatusesFromWebManager.isWorking();
	}

	private static class LoadStatusesFromWebManager {

		private static boolean mIsWorking;
		private static final List<WeakReference<OnLoadStatusesFromWebListener>> mListeners = new ArrayList<WeakReference<OnLoadStatusesFromWebListener>>();

		private static void runNow(Context context) {
			if (!isWorking()) {
				Log.i("Redu Sync", "runNow()");
				SchedulerManager.getInstance().saveTask(context, "*/" + DELAY_TO_CHECK_NOTIFICATIONS_IN_MINUTES + " * * * *", LoadStatusesFromWebTask.class);
				SchedulerManager.getInstance().runNow(context, LoadStatusesFromWebTask.class, 0);
			}
		}
		
		private static void runDelayed(Context context) {
			Log.i("Redu Sync", "Run delayed registered to " + DELAY_TO_CHECK_NOTIFICATIONS_IN_MINUTES + " minutes.");
			SchedulerManager.getInstance().saveTask(context, "*/" + DELAY_TO_CHECK_NOTIFICATIONS_IN_MINUTES + " * * * *", LoadStatusesFromWebTask.class);
			SchedulerManager.getInstance().restart(context, LoadStatusesFromWebTask.class);
		}

		private static void add(OnLoadStatusesFromWebListener listener) {
			mListeners.add(new WeakReference<OnLoadStatusesFromWebListener>(listener));
		}

		private static void notifyOnStart() {
			Log.i("Redu Sync", "Sync started");
			mIsWorking = true;
			for (WeakReference<OnLoadStatusesFromWebListener> reference : mListeners) {
				if (!reference.isEnqueued() && reference.get() != null) {
					reference.get().onStart();
				}
			}
		}

		private static void notifyOnComplete(Context context) {
			Log.i("Redu Sync", "Sync completed");
			mIsWorking = false;
			for (WeakReference<OnLoadStatusesFromWebListener> reference : mListeners) {
				if (!reference.isEnqueued() && reference.get() != null) {
					reference.get().onComplete();
				}
			}
			runDelayed(context);
		}

		private static void notifyOnError(Context context, Exception e) {
			Log.i("Redu Sync", "Sync stopped by error: " + e.getCause() + ". Message: " + e.getMessage());
			mIsWorking = false;
			for (WeakReference<OnLoadStatusesFromWebListener> reference : mListeners) {
				if (!reference.isEnqueued() && reference.get() != null) {
					reference.get().onError(e);
				}
			}
			runDelayed(context);
		}

		private static boolean isWorking() {
			return mIsWorking;
		}
	}

	private void showNotification(Context context, Status status) {

//		NotificationMessage notification = new NotificationMessage(getTitle(), status.text);
//		notification.setNotificationId(Integer.valueOf(status.id));
//		// notification.setNotificationIconResource(R.drawable.ic_status_notification);
//
//		Bundle extras = new Bundle();
//
//		if (status.isActivityType() || status.isHelpType()) {
//			notification.setNotificationClickIntentClass(StatusDetailActivity.class);
//			extras.putSerializable(StatusDetailActivity.EXTRAS_STATUS, status);
//			extras.putSerializable(StatusDetailActivity.EXTRAS_ENABLE_GO_TO_WALL_ACTION, false);
//			extras.putSerializable(StatusDetailActivity.EXTRAS_IS_FROM_NOTIFICATION, true);
//			notification.setNotificationClickIntentBundle(extras);
//			notification.setFlagResource(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//
//		} else if (status.isLogType()) {
//			// TODO abrir notificacao de novas aulas
//		}
		
		long when = Calendar.getInstance().getTimeInMillis();

		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		int smalIcon = R.drawable.ic_status_notification;

		Intent intent = new Intent(context.getApplicationContext(), StatusDetailActivity.class);
		Bundle extras = new Bundle();
		extras.putSerializable(StatusDetailActivity.EXTRAS_STATUS, status);
		extras.putSerializable(StatusDetailActivity.EXTRAS_ENABLE_GO_TO_WALL_ACTION, false);
		extras.putSerializable(StatusDetailActivity.EXTRAS_IS_FROM_NOTIFICATION, true);
		intent.putExtras(extras);
		intent.setData(Uri.parse("content://" + when));
		PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);

		NotificationManager notificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context.getApplicationContext()).setWhen(when)
				.setContentText(status.text).setContentTitle(getTitle()).setSmallIcon(smalIcon)
				.setVibrate(new long[] { 500L, 200L, 200L, 500L }).setAutoCancel(true).setTicker(getTitle()).setLargeIcon(largeIcon)
				.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
				.setContentIntent(pendingIntent);

		Notification notification = notificationBuilder.build();

		notificationManager.notify(Integer.parseInt(status.id), notification);
	}

}