package com.wooeen.view.auth.loader;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.wooeen.model.api.UserAPI;
import com.wooeen.model.sync.WoeSyncAdapter;
import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.UserUtils;

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
        private Context context;

        public TaskAction(Context context,String mEmail, String mPass) {
            super(context);

            this.context = context;
            this.mEmail = mEmail;
            this.mPass = mPass;
        }

        @Override
        public UserTokenTO loadInBackground() {
            UserAPI apiDAO = new UserAPI();
            UserAPI.LoginHolderAPI callback = apiDAO.login(mEmail,mPass);
            if(callback != null &&
                    callback.getToken() != null &&
                    callback.getUser() != null &&
                    !TextUtils.isEmpty(callback.getUser().getId()) &&
                    !TextUtils.isEmpty(callback.getToken().getIdToken()) &&
                    !TextUtils.isEmpty(callback.getToken().getAccessToken())) {
                //init data
                UserUtils.saveUserData(context, callback.getUser(), callback.getToken(), callback.getTokenCp());

                if(callback.getUser().getCompany() != null && callback.getUser().getCompany().getId() > 0){
                    UserUtils.saveCompanyData(context, callback.getUser().getCompany());
                }

                WoeSyncAdapter.syncCountry(context);

                return callback.getToken();
            }

            return null;
        }
    }
}
