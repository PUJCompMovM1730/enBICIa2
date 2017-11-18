package com.example.oscar.enbicia2;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.clases.ChatMessage;
import com.example.clases.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MessageIntentService extends IntentService {

    private String TAG = "MessageIntentService";

    private DatabaseReference messageReference;
    private ChildEventListener mMessageListener;

    public MessageIntentService() {
        super("MessageIntentService");
        messageReference = FirebaseDatabase.getInstance().getReference(Constants.PATH_MESSAGES).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ChildEventListener messageListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    dataSnapshot.getRef().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot message : dataSnapshot.getChildren()) {
                                Log.d(TAG, "Recibi Notificación");
                                ChatMessage chatMessage = message.getValue(ChatMessage.class);
                                if (!chatMessage.getUidSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    NotificationCompat.Builder mBuilder =
                                            new NotificationCompat.Builder(MessageIntentService.this)
                                                    .setSmallIcon(R.drawable.logo)
                                                    .setContentTitle("enBICIa2 Mensaje")
                                                    .setContentText(chatMessage.getMessageText());
                                    Intent resultIntent = new Intent(getBaseContext(), ChatDetailActivity.class);
                                    resultIntent.putExtra("friendUid", chatMessage.getUidSender());
                                    PendingIntent resultPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(resultPendingIntent);
                                    mBuilder.setAutoCancel(true);
                                    NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    mNotifyMgr.notify(Constants.mNotificationId, mBuilder.build());
                                    Constants.mNotificationId++;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            messageReference.addChildEventListener(messageListener);
            mMessageListener = messageListener;
        }
    }
}
