package com.wooeen.view.auth.loader;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.wooeen.model.api.UserAPI;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.utils.TextUtils;

import java.util.List;

public class UserLoginLoader implements LoaderManager.LoaderCallbacks<UserTokenTO>{

    private Context mContext;

    private String mEmail;
    private String mPass;

    public final static int LOADER_ID_LOGIN = 2;

    private OnItemSelectedListener listener;

    public OnItemSelectedListener getListener() {
        return listener;
    }

    public void setListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnItemSelectedListener {
        public void onCreateLoader(@Nullable Bundle args);

        public void onLoadFinished(UserTokenTO result);
    }

    public UserLoginLoader(Context mContext,String mEmail, String mPass,OnItemSelectedListener listener){
        this.mContext = mContext;
        this.mEmail = mEmail;
        this.mPass = mPass;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Loader<UserTokenTO> onCreateLoader(int id, @Nullable Bundle args) {
        if(id == LOADER_ID_LOGIN) {
            if(listener != null) listener.onCreateLoader(args);
            return new UserLoginLoader.TaskAction(mContext,mEmail, mPass);
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<UserTokenTO> loader, UserTokenTO result) {
        if(loader.getId() == LOADER_ID_LOGIN) {
            if(listener != null) listener.onLoadFinished(result);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<UserTokenTO> loader) {

    }

    public static class TaskAction extends AsyncTaskLoader<UserTokenTO> {

        private String mEmail;
        private String mPass;

        public TaskAction(Context context,String mEmail, String mPass) {
            super(context);

            this.mEmail = mEmail;
            this.mPass = mPass;
        }

        @Override
        public UserTokenTO loadInBackground() {
            UserAPI apiDAO = new UserAPI();
            UserTokenTO token = apiDAO.login(mEmail,mPass);
            if(token != null &&
                    token.getUser() != null &&
                    !TextUtils.isEmpty(token.getUser().getId()) &&
                    !TextUtils.isEmpty(token.getIdToken()) &&
                    !TextUtils.isEmpty(token.getAccessToken())) {
                return token;
            }

            return null;
        }
    }
}
