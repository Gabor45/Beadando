package com.example.butorshop;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private static final String channelId = "my_channel_id";
    private static final int NOTIFICATION_ID=0;

    CharSequence channelName = "My Channel";
    private Context mContext;
    private NotificationManager mManager;

    public NotificationHandler(Context context) {
        this.mContext = context;
        this.mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        } else {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications from Shop application.");
            this.mManager.createNotificationChannel(channel);
        }
    }

    void send(String mess) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId)
                .setContentTitle("Shop Application")
                .setContentText(mess)
                .setSmallIcon(R.drawable.baseline_shopping_basket_24);
        this.mManager.notify(NOTIFICATION_ID, builder.build());
    }
}
