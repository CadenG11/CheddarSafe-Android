package com.cheddarsecurity.cheddarsafe;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ClearDBService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearDBService", "END");
        AccountDatabaseHelper db = new AccountDatabaseHelper(this);
        db.dropAccTable();
        db.dropInfoTable();
        stopSelf();
    }
}
