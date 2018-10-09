package i.am.shiro.chesto.notification;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.framework.Notification;

public final class ErrorNotification implements Notification {

    @NonNull
    @Override
    public android.app.Notification onCreateNotification(Context context) {
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Failed to save image")
                .setContentText("Tap to retry")
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setLocalOnly(true)
                .setAutoCancel(true)
                .build();
    }
}
