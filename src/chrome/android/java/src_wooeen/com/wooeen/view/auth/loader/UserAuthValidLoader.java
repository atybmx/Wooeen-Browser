package com.wooeen.view.auth.loader;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.wooeen.model.api.UserAPI;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.utils.TextUtils;

import org.chromium.chrome.R;

import java.util.List;

public class UserAuthValidLoader implements LoaderManager.LoaderCallbacks<UserTokenTO>{

    private Context mContext;

    private String mValidator;
    private List<Integer> mIds;

    public final static int LOADER_ID_AUTH_VALID = 2;

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

    public UserAuthValidLoader(Context mContext,String mValidator, List<Integer> mIds,OnItemSelectedListener listener){
        this.mContext = mContext;
        this.mValidator = mValidator;
        this.mIds = mIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Loader<UserTokenTO> onCreateLoader(int id, @Nullable Bundle args) {
        if(id == LOADER_ID_AUTH_VALID) {
            if(listener != null) listener.onCreateLoader(args);
            return new UserAuthValidLoader.TaskAction(mContext,mValidator, mIds);
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<UserTokenTO> loader, UserTokenTO result) {
        if(loader.getId() == LOADER_ID_AUTH_VALID) {
            if(listener != null) listener.onLoadFinished(result);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<UserTokenTO> loader) {

    }

    public static class TaskAction extends AsyncTaskLoader<UserTokenTO> {

        private String mValidator;
        private List<Integer> mIds;

        public TaskAction(Context context,String mValidator, List<Integer> mIds) {
            super(context);

            this.mValidator = mValidator;
            this.mIds = mIds;
        }

        @Override
        public UserTokenTO loadInBackground() {
            UserAPI apiDAO = new UserAPI();
            UserTokenTO token = apiDAO.authValid(mValidator,mIds);
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
