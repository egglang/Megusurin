package ma10.megusurin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class EventNotify {

    private static final int BATTLE_EVENT_NOTIFY_ID = 0x01;

    public static void sendBattleEventNotify(final Context context) {

        String title = "MAGICAL MEGSURIN";

        String message = "敵を発見！";

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setVibrate(new long[] {250, 250, 100, 100, 250, 250, 100, 100, 250, 250, 100, 100})
                        .setContentText(message);

        Intent intent = new Intent();
        intent.setAction(MegusurinActivity.INTENT_BATTLE_START);

//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        notificationBuilder.addAction(R.drawable.ic_launcher, "Battle Start!", pendingIntent);

        sendNotify(context, notificationBuilder.build());
    }

    public static void cancelBattleEventNotify(final Context context) {
        cancelNotify(context, BATTLE_EVENT_NOTIFY_ID);
    }

    private static void sendNotify(final Context context, final Notification notify) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(BATTLE_EVENT_NOTIFY_ID, notify);
    }

    private static void cancelNotify(final Context context, final int id) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(id);
    }

}
