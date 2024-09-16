package com.example.lym;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_BATTERY_LOW)) {
            // Xử lý khi pin yếu
            Toast.makeText(context, "Pin yếu!", Toast.LENGTH_SHORT).show();
        }
    }
}
