package br.com.redu.redumobile.tasks;

import android.content.ContextWrapper;
import br.com.redu.redumobile.activities.HomeActivity;

import com.buzzbox.mob.android.scheduler.NotificationMessage;
import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;

public class RefreshNotificationsTask implements Task {

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
                "Redu Mobile",
                "You have new notifications in Redu");
        //notification.notificationIconResource = R.drawable.icon_notification_cards_clubs;
        notification.setNotificationClickIntentClass(HomeActivity.class);
        
        res.addMessage( notification );    
        
        return res;
    }
}
