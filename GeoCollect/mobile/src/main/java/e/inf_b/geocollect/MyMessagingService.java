/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package e.inf_b.geocollect;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

public class MyMessagingService extends IntentService {
    private static final String TAG = MyMessagingService.class.getSimpleName();

    public static final String SEND_MESSAGE_ACTION =
            "e.inf_b.geocollect.ACTION_SEND_MESSAGE";
    public static final String READ_ACTION =
            "e.inf_b.geocollect.ACTION_MESSAGE_READ";
    public static final String REPLY_ACTION =
            "e.inf_b.geocollect.ACTION_MESSAGE_REPLY";
    public static final String CONVERSATION_ID = "conversation_id";
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    String canal = "my_channel_01";

    public MyMessagingService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Handle intent to send a new notification.
        if (intent != null && SEND_MESSAGE_ACTION.equals(intent.getAction())) {

            sendNotificationForConversation(
                    Conversations.CONVERSATION_ID,
                    Conversations.SENDER_NAME,
                    Conversations.getUnreadMessage(),
                    System.currentTimeMillis());
        }
    }

    private void sendNotificationForConversation(int conversationId,
                                                 String sender, String message, long timestamp) {
        // A pending Intent for reads
        PendingIntent readPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                conversationId,
                getMessageReadIntent(conversationId),
                PendingIntent.FLAG_UPDATE_CURRENT);

        /// Add the code to create the UnreadConversation
        // Build a RemoteInput for receiving voice input in a Car Notification
        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY).build();

        // Building a Pending Intent for the reply action to trigger
        PendingIntent replyIntent = PendingIntent.getBroadcast(getApplicationContext(),
                conversationId,
                getMessageReplyIntent(conversationId),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the UnreadConversation and populate it with the participant name,
        // read and reply intents.
        NotificationCompat.CarExtender.UnreadConversation.Builder unreadConversationBuilder =
                new NotificationCompat.CarExtender.UnreadConversation.Builder(sender)
                        .setLatestTimestamp(timestamp)
                        .setReadPendingIntent(readPendingIntent)
                        .setReplyAction(replyIntent, remoteInput);

        // Note: Add messages from oldest to newest to the UnreadConversation.Builder
        // Since we are sending a single message here we simply add the message.
        // In a real world application there could be multiple messages which should be ordered
        // and added from oldest to newest.
        unreadConversationBuilder.addMessage("¿Desea ir a la ruta?, diga si o no");

        /// End create UnreadConversation

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), canal)
                .setSmallIcon(R.mipmap.icono)
                //.setLargeIcon(BitmapFactory.decodeResource(
                //        getApplicationContext().getResources(), R.drawable.ic_launcher_background))
                .setContentText(message)
                .setWhen(timestamp)
                .setContentTitle(sender)
                .setContentIntent(readPendingIntent)
                /// Extend the notification with CarExtender.
                .extend(new NotificationCompat.CarExtender()
                        .setUnreadConversation(unreadConversationBuilder.build()));
                /// End

        Log.d(TAG, "Sending notification "
                + conversationId + " conversation: " + message);

        //NotificationManagerCompat.from(this)
        //        .notify(conversationId, builder.build());
        mostrarNotificacion(conversationId, builder.build());
    }


    // Creates an intent that will be triggered when a message is read.
    private Intent getMessageReadIntent(int id) {
        return new Intent().setAction(READ_ACTION)
                .putExtra(CONVERSATION_ID, id);
    }

    // Creates an Intent that will be triggered when a voice reply is received.
    private Intent getMessageReplyIntent(int conversationId) {
        return new Intent().setAction(REPLY_ACTION)
                .putExtra(CONVERSATION_ID, conversationId);
    }

    public void mostrarNotificacion(int id, Notification notificacion) {
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "my channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(canal, name, importance);
            channel.setDescription(description);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            manager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(id, notificacion);

    }

}
