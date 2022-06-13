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
import com.wooeen.model.to.UserAuthTO;
import com.wooeen.model.to.UserQuickAccessTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.UserUtils;

import org.chromium.chrome.R;

public class UserQuickAccessNewLoader implements LoaderManager.LoaderCallbacks<UserQuickAccessTO>{

    private Context mContext;

    private String mEmail;

    public final static int LOADER_UQA_NEW = 1;

    private OnItemSelectedListener listener;

    public OnItemSelectedListener getListener() {
        return listener;
    }

    public void setListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnItemSelectedListener {
        public void onCreateLoader(@Nullable Bundle args);

        public void onLoadFinished(UserQuickAccessTO result);
    }

    public UserQuickAccessNewLoader(Context mContext,OnItemSelectedListener listener){
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Loader<UserQuickAccessTO> onCreateLoader(int id, @Nullable Bundle args) {
        if(id == LOADER_UQA_NEW) {
            if(listener != null) listener.onCreateLoader(args);
            return new TaskAction(mContext);
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<UserQuickAccessTO> loader, UserQuickAccessTO result) {
        if(loader.getId() == LOADER_UQA_NEW) {
            //check if the result
            if(result == null || TextUtils.isEmpty(result.getId())) {
                if(listener != null) listener.onLoadFinished(result);
                Toast.makeText(mContext,R.string.woe_internal_error,Toast.LENGTH_LONG).show();
            }else{
                if(listener != null) listener.onLoadFinished(result);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<UserQuickAccessTO> loader) {

    }

    public static class TaskAction extends AsyncTaskLoader<UserQuickAccessTO> {

        public TaskAction(Context context) {
            super(context);
        }

        @Override
        public UserQuickAccessTO loadInBackground() {
            UserAPI apiDAO = new UserAPI(UserUtils.getToken(getContext()));
            return apiDAO.quickAccessNew();
        }
    }
}
