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

import org.chromium.chrome.R;

public class UserAuthLoader implements LoaderManager.LoaderCallbacks<Integer>{

    private Context mContext;

    private String mEmail;
    private int count;

    public final static int LOADER_ID_AUTH = 1;

    private OnItemSelectedListener listener;

    public OnItemSelectedListener getListener() {
        return listener;
    }

    public void setListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnItemSelectedListener {
        public void onLoadFinished(Integer result);
    }

    public UserAuthLoader(Context mContext,String mEmail,int count,OnItemSelectedListener listener){
        this.mContext = mContext;
        this.mEmail = mEmail;
        this.count = count;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Loader<Integer> onCreateLoader(int id, @Nullable Bundle args) {
        if(id == LOADER_ID_AUTH) {
            return new TaskAction(mContext,mEmail,count);
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Integer> loader, Integer result) {
        if(loader.getId() == LOADER_ID_AUTH) {
            //check if the result
            if(result <= 0) {
                Toast.makeText(mContext,R.string.woe_internal_error,Toast.LENGTH_LONG).show();
            }else{
                if(listener != null) listener.onLoadFinished(result);
                Toast.makeText(mContext,R.string.woe_send_in_u_email,Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Integer> loader) {

    }

    public static class TaskAction extends AsyncTaskLoader<Integer> {

        private String mEmail;
        private int count;

        public TaskAction(Context context,String mEmail,int count) {
            super(context);

            this.mEmail = mEmail;
            this.count = count;
        }

        @Override
        public Integer loadInBackground() {
            UserAPI apiDAO = new UserAPI();
            UserAuthTO auth = apiDAO.auth(mEmail,count);
            if(auth != null)
                return auth.getId();

            return 0;
        }
    }
}
