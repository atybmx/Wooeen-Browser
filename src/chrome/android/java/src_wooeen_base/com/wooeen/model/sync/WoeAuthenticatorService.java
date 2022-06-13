package com.wooeen.model.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class WoeAuthenticatorService extends Service {

    private WoeAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new WoeAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
