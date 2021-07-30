package com.example.smartparksystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartparksystem.util.NotificationUtils;
import com.example.smartparksystem.util.SharedPrefsUtil;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.example.smartparksystem.util.AppConstants.PUSH_NOTIFICATION;
import static com.example.smartparksystem.util.AppConstants.REGISTRATION_COMPLETE;
import static com.example.smartparksystem.util.AppConstants.TOPIC_GLOBAL;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = PaymentsActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtMessage;
    private EditText txtRegId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
//        txtRegId = findViewById(R.id.txt_reg_id);
//        txtMessage = findViewById(R.id.txt_push_message);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPrefsUtil sharedPrefsUtil = new SharedPrefsUtil(this);
        String regId = sharedPrefsUtil.getFirebaseRegistrationID();

        if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver.
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));

        // register new push message receiver.
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));

        // clear the notification area when the app is opened.
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}