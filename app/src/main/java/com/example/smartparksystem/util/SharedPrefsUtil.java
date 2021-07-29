package com.example.smartparksystem.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.core.app.NotificationCompat;

import com.example.smartparksystem.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//import static com.example.smartparksystem.util.Utils.NOTIFICATION_ID_BIG_IMAGE;

public class SharedPrefsUtil {
    private static final String SHARED_PREFER_FILE_NAME = "keys";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    /**
     * Retrieve data from preference:
     */

    public SharedPrefsUtil(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(SHARED_PREFER_FILE_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public void saveFirebaseRegistrationID(String firebaseRegId) {
        editor.putString("regId", firebaseRegId);
        editor.commit();
    }

    public String getFirebaseRegistrationID() {
        return pref.getString("regId", null);
    }

    public void saveIsFirstTime(boolean isFirstTime) {
        editor.putBoolean("firstTime", isFirstTime);
        editor.commit();
    }

    public boolean getIsFirstTime() {
        return pref.getBoolean("firstTime", false);
    }
}
