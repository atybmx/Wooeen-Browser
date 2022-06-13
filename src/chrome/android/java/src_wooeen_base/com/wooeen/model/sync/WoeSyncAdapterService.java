package com.wooeen.model.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class WoeSyncAdapterService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static WoeSyncAdapter sWoeSyncAdapter;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock){
            if(sWoeSyncAdapter == null){
                sWoeSyncAdapter = new WoeSyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sWoeSyncAdapter.getSyncAdapterBinder();
    }
}
