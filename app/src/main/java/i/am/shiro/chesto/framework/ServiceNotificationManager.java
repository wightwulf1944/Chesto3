package i.am.shiro.chesto.framework;

import android.app.Service;
import android.support.annotation.NonNull;

public class ServiceNotificationManager extends NotificationManager {

    public ServiceNotificationManager(@NonNull Service service, int notificationId) {
        super(service, notificationId);
    }

    public void startForeground(@NonNull Notification notification) {
        Service service = (Service) context;
        service.startForeground(notificationId, notification.onCreateNotification(context));
    }
}
