package com.example.smartparksystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartparksystem.api.ApiClient;
import com.example.smartparksystem.api.model.AccessToken;
import com.example.smartparksystem.api.model.STKPush;
import com.example.smartparksystem.util.NotificationUtils;
import com.example.smartparksystem.util.SharedPrefsUtil;
import com.example.smartparksystem.util.Utils;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.example.smartparksystem.util.AppConstants.BUSINESS_SHORT_CODE;
import static com.example.smartparksystem.util.AppConstants.CALLBACKURL;
import static com.example.smartparksystem.util.AppConstants.PARTYB;
import static com.example.smartparksystem.util.AppConstants.PASSKEY;
import static com.example.smartparksystem.util.AppConstants.PUSH_NOTIFICATION;
import static com.example.smartparksystem.util.AppConstants.REGISTRATION_COMPLETE;
import static com.example.smartparksystem.util.AppConstants.TOPIC_GLOBAL;
import static com.example.smartparksystem.util.AppConstants.TRANSACTION_TYPE;

public class PaymentsActivity extends AppCompatActivity {
    private EditText phone;
    private EditText amount;
    @BindView(R.id.btn_pay)
    Button mButtonCheckout;
    String total_amount,user_phone;
    private String mFireBaseRegId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    private SharedPrefsUtil mSharedPrefsUtil;
    private ApiClient mApiClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        ButterKnife.bind(this);
        phone=findViewById(R.id.reg_number);
        amount=findViewById(R.id.amount);

        mProgressDialog = new ProgressDialog(this);
        mSharedPrefsUtil = new SharedPrefsUtil(this);
        mApiClient = new ApiClient();
        mApiClient.setIsDebug(true); //Set True to enable logging, false to disable.

        getAccessToken();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);
                    getFirebaseRegId();

                } else if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    NotificationUtils.createNotification(getApplicationContext(), message);
                    showResultDialog(message);
                }
            }

        };
        getFirebaseRegId();

    }
    @OnClick({R.id.btn_pay})
    public void onClickViews(View view) {
        showCheckoutDialog();

    }
    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {

                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {

            }
        });
    }

    public void showCheckoutDialog() {
        total_amount=amount.getText().toString().trim();
        user_phone=phone.getText().toString().trim();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Processing payment.."+" "+ total_amount);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setText(user_phone);
//        input.setHint(getString(R.string.hint_phone_number));
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            String phone_number = phone.getText().toString();
            performSTKPush(phone_number);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
//            mPriceArrayList.clear();
//            mButtonCheckout.setText(getString(R.string.checkout));
            dialog.cancel();
        });

        builder.show();
    }

    public void performSTKPush(String phone_number) {
        total_amount=amount.getText().toString().trim();

        mProgressDialog.setMessage("Processing payment");
        mProgressDialog.setTitle("Please wait..");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(total_amount),
                Utils.sanitizePhoneNumber(phone_number),
                PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                CALLBACKURL + mFireBaseRegId,
                "test", //The account reference
                "test"  //The transaction description
        );

        mApiClient.setGetAccessToken(false);

        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        Timber.d("post submitted to API. %s", response.body());
                    } else {
                        Timber.e("Response %s", response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(PaymentsActivity.this, "Failed"+t.getMessage(), Toast.LENGTH_SHORT).show();
                Timber.e(t);
            }
        });
    }

    private void getFirebaseRegId() {
        mFireBaseRegId = mSharedPrefsUtil.getFirebaseRegistrationID();

        if (!TextUtils.isEmpty(mFireBaseRegId)) {
            mSharedPrefsUtil.saveFirebaseRegistrationID(mFireBaseRegId);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }
    public void showResultDialog(String result) {
        Timber.d(result);
        if (!mSharedPrefsUtil.getIsFirstTime()) {
            // run your one time code
            mSharedPrefsUtil.saveIsFirstTime(true);

            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Payment Notification")
                    .setContentText("Payment was made successfully")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        mSharedPrefsUtil.saveIsFirstTime(false);
                    })
                    .show();
        }
    }
}
