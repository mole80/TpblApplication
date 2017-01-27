package tpbl.tpblappl;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
     //   Log.d(TAG, "From: " + remoteMessage.getFrom());
     //   Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        // Pour afficher des notifications si le programme est en train de tourner
        // Si l'activité est en fonction, c'est un message qui est reçu
        // Si l'activité est en fond, c'est une notification.


        // Check if message contains a data payload.
        int nbr = remoteMessage.getData().size();
        if ( nbr > 0 ) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        RemoteMessage.Notification not =  remoteMessage.getNotification();
        // Check if message contains a notification payload.
        if ( not != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
         // AAFICHE une notification

           // Intent intent = new Intent(this, Login.class);
           // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
           //         PendingIntent.FLAG_ONE_SHOT);

       //     Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(not.getTitle())
                    .setContentText(not.getBody())
                    .setAutoCancel(true);
                    //.setSound(defaultSoundUri)
                    //.setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());
        }

//        super.onMessageReceived(remoteMessage);

        // AAFICHE une notification
        /*
            Intent intent = new Intent(this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

       //     Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Firebase Push Notification")
                    .setContentText("messageBody")
                    .setAutoCancel(true)
                    //.setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());*/
    }
}