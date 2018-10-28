package com.supportaeon.supportaeonapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences prefs = getSharedPreferences("SupportAeonAppKeys", MODE_PRIVATE);
        String restoredWallet = prefs.getString("WalletAddress", "");
        Boolean notificationBlock = prefs.getBoolean("NotificationBlock", false);
        Boolean PaymentReceived = prefs.getBoolean("PaymentReceived", false);
        Boolean BlockUnlocked = prefs.getBoolean("BlockUnlocked", false);
        Boolean statusNotification = prefs.getBoolean("statusNotification", false);
        int updateInterval = prefs.getInt("updateInterval", 0);


        EditText editTextAddress = findViewById(R.id.editTextAddress);
        Switch switchNewBlockFound = findViewById(R.id.switchNewBlockFound);
        Switch switchPayment = findViewById(R.id.switchPayment);
        Switch switchBlockUnlocked = findViewById(R.id.switchBlockUnlocked);
        Switch switchStatus = findViewById(R.id.switchStatus);

        editTextAddress.setText(restoredWallet);
        switchNewBlockFound.setChecked(notificationBlock);
        switchPayment.setChecked(PaymentReceived);
        switchBlockUnlocked.setChecked(BlockUnlocked);
        switchStatus.setChecked(statusNotification);


        Spinner spinner = findViewById(R.id.spinnerInterval);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.interval_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setSelection(updateInterval);


        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText editTextAddress = findViewById(R.id.editTextAddress);
                Switch switchNewBlockFound = findViewById(R.id.switchNewBlockFound);
                Switch switchPayment = findViewById(R.id.switchPayment);
                Switch switchBlockUnlocked = findViewById(R.id.switchBlockUnlocked);
                Switch switchStatus = findViewById(R.id.switchStatus);
                Spinner spinner = findViewById(R.id.spinnerInterval);

                SharedPreferences.Editor editor = getSharedPreferences("SupportAeonAppKeys", MODE_PRIVATE).edit();
                editor.putString("WalletAddress", editTextAddress.getText().toString());
                editor.putBoolean("NotificationBlock", switchNewBlockFound.isChecked());
                editor.putBoolean("PaymentReceived", switchPayment.isChecked());
                editor.putBoolean("BlockUnlocked", switchBlockUnlocked.isChecked());
                editor.putBoolean("statusNotification", switchStatus.isChecked());
                editor.putInt("updateInterval", spinner.getSelectedItemPosition());
                editor.apply();
                Context context = getApplicationContext();
                //Toast.makeText(context, "Settings Saved", Toast.LENGTH_LONG).show();

                if(!switchStatus.isChecked())
                {
                    try
                    {
                        NotificationManager mNotificationManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        Objects.requireNonNull(mNotificationManager).cancel(4);

                    }
                    catch (Exception ignored)
                    {


                    }

                }


                iniciarNotifications(context, spinner.getSelectedItemPosition());


            }
        });

    }


    private void iniciarNotifications(Context context, int minutes)
    {

        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Intent resultIntent = new Intent(context, CheckDataReceiver.class);
        boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                resultIntent,
                PendingIntent.FLAG_NO_CREATE) != null);

        int interval = 60000;
        String txt = "1m";
        switch (minutes)
        {
            case 0:  interval = 60 * 1000;
                txt = "1m";
                break;
            case 1:  interval = 60 * 1000 * 5;
                txt = "5m";
                break;
            case 2:  interval = 60 * 1000 * 15;
                txt = "15m";
                break;
            case 3:  interval = 60 * 1000 * 30;
                txt = "30m";
                break;
            case 4:  interval = 60 * 1000 * 60;
                txt = "1h";
                break;
            case 5:  interval = 60 * 1000 * 60 * 6;
                txt = "6h";
                break;
            case 6:  interval = 60 * 1000 * 60 * 12;
                txt = "12h";
                break;
        }


        if (alarmUp)
        {

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, resultIntent, 0);

            Objects.requireNonNull(manager).cancel(pendingIntent);

            pendingIntent.cancel();

            Intent resultIntentNew = new Intent(context, CheckDataReceiver.class);

            PendingIntent pendingIntentNew = PendingIntent.getBroadcast(context, 0, resultIntentNew, 0);

            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntentNew);

            Toast.makeText(context, "Settings Saved. Server check updated to every " + txt, Toast.LENGTH_LONG).show();
        }
        else
        {

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, resultIntent, 0);

            Objects.requireNonNull(manager).setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

            Toast.makeText(context, "Settings Saved. Server check started every " +  txt, Toast.LENGTH_LONG).show();
        }




    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }




}
