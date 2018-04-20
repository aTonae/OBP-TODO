package raven.oblabs.com.raven.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Map;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.activity.MainActivity;
import mauth.oblabs.com.firebaseauthentication.activity.ShoppingActivity;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.SelectSingleItemCallback;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;

/**
 * Created by android on 18/4/17.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static int NOTIFICATION_ID = 1;


    ContentResolver contentResolver;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sendNotification(remoteMessage.getData());
    }

    private void sendNotification(Map<String, String> data) {

        contentResolver = getContentResolver();

        final int num = ++NOTIFICATION_ID;
        final Bundle msg = new Bundle();
        for (String key : data.keySet()) {
            Log.e(key, data.get(key));
            msg.putString(key, data.get(key));
        }
        Intent intent;
        if(new SharedPreference().getSession(this)){

            intent = new Intent(this, ShoppingActivity.class);
            if (msg.containsKey("action")) {
                intent.putExtra("action", msg.getString("action"));
            }
        }else{
            intent = new Intent(this, MainActivity.class);
            if (msg.containsKey("action")) {
                intent.putExtra("action", msg.getString("action"));
            }
            return;
        }



        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, num /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        getName(new SelectSingleItemCallback() {
            @Override
            public void selectedItem(String item) {

                String message = item.concat(" : ").concat(msg.getString("msg"));

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this , "ToDoS")
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle(msg.getString("title"))
//                        .setContentText(message)
//                        .setAutoCancel(true)
//                        .setStyle(new NotificationCompat.MessagingStyle())
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, "1")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(msg.getString("title"))
                        .setStyle(new NotificationCompat.MessagingStyle(item).setConversationTitle(msg.getString("title"))
                                .addMessage(msg.getString("msg"), 12, null) // Pass in null for user.


                        )                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setColor(Color.rgb(207,36,82))
                        .setSmallIcon(R.drawable.ic_insert_invitation_white_48dp)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(num, notificationBuilder.build());


            }
        } , msg.getString("by"));



    }

    public void getName(final SelectSingleItemCallback callback , final String partner) {




                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(partner));

                String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

                String contactName = "";
                Cursor cursor = contentResolver.query(uri, projection, null, null, null);

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        contactName = cursor.getString(0);
                    }
                    cursor.close();
                }


                callback.selectedItem(contactName);



    }
}
