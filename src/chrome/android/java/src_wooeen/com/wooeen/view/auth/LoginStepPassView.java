package com.wooeen.view.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.wooeen.model.api.UserAPI;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.UserAuthTO;
import com.wooeen.model.to.UserQuickAccessTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.utils.Decodificador;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.UserUtils;
import com.wooeen.view.auth.loader.UserAuthLoader;
import com.wooeen.view.auth.loader.UserAuthValidLoader;
import com.wooeen.view.auth.loader.UserLoginLoader;

import org.chromium.base.task.AsyncTask;
import org.chromium.chrome.R;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginStepPassView extends Fragment implements LoaderManager.LoaderCallbacks<Integer> {

    private EditText txtPass;

    private String mEmail;
    private String mPass;

    private OnItemSelectedListener listener;

    private Button btnEnter;
    private Button btnEnterPin;
    private ProgressBar mProgressBar;
    private boolean processing;
    private TextView btnRecover;

    private LoaderManager loaderManager;

    private final static int LOADER_ID_NEXT_PIN = 2;

    public interface OnItemSelectedListener {
        public void onEnter();

        public void onNextPin(String email, int id);
    }

    public LoginStepPassView() {
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * @return A new instance of fragment FragmentLoginStepEmail.
     */
    public static LoginStepPassView newInstance(String email) {
        LoginStepPassView fragment = new LoginStepPassView();
        Bundle args = new Bundle();
        args.putString("LoginStepPassView.email",email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if(getArguments() != null) {
            mEmail = getArguments().getString("LoginStepPassView.email");
            mPass = getArguments().getString("LoginStepPassView.pass");
        }

        if(savedInstanceState != null) {
            processing = savedInstanceState.getBoolean("LoginStepPassView.processing");
            mEmail = savedInstanceState.getString("LoginStepPassView.email");
            mPass = savedInstanceState.getString("LoginStepPassView.pass");
        }

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.login_step_pass, container, false);

        txtPass = view.findViewById(R.id.txt_pass);
        txtPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_DONE) {
                    enter();
                }
                return handled;
            }
        });

        btnEnter = view.findViewById(R.id.btn_enter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enter();
            }
        });

        btnRecover = view.findViewById(R.id.btn_recover);
        btnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UserRecoverPasswordTask(mEmail).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        btnEnterPin = view.findViewById(R.id.btn_enter_pin);
        btnEnterPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPin();
            }
        });

        mProgressBar = view.findViewById(R.id.progress_bar);

        if(!processing) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }else{
            btnEnter.setEnabled(false);
        }

        //reset the loaders
        this.loaderManager = LoaderManager.getInstance(this);

        Loader<UserTokenTO> loaderUserAuthValid = loaderManager.getLoader(UserLoginLoader.LOADER_ID_LOGIN);
        if(loaderUserAuthValid != null)
            loaderManager.initLoader(UserLoginLoader.LOADER_ID_LOGIN, null,
                    new UserLoginLoader(getContext(), mEmail, mPass,
                            new UserLoginLoader.OnItemSelectedListener() {
                                @Override
                                public void onCreateLoader(@Nullable Bundle args) {
                                    processing = true;
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    btnEnter.setEnabled(false);
                                }

                                @Override
                                public void onLoadFinished(UserTokenTO result) {
                                    enter(result);
                                }
                            }));

        Loader<Integer> loaderPin = loaderManager.getLoader(LOADER_ID_NEXT_PIN);
        if(loaderPin != null) {
            loaderManager.initLoader(LOADER_ID_NEXT_PIN, null, this);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("LoginStepPassView.processing",processing);
        outState.putString("LoginStepPassView.email", mEmail);
        outState.putString("LoginStepPassView.pass", mPass);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException();
        }
    }

    public void nextPin(){
        LoaderManager.LoaderCallbacks<Integer> loaderCallbacks = this;

        // Arguments:
        Bundle args = new Bundle();

        // You can pass a null args to a Loader
        Loader<Integer> loader = loaderManager.getLoader(LOADER_ID_NEXT_PIN);
        if(loader != null)
            loaderManager.destroyLoader(LOADER_ID_NEXT_PIN);
        loader = loaderManager.initLoader(LOADER_ID_NEXT_PIN, args, loaderCallbacks);
        loader.forceLoad();
    }

    public static class ValidateEmailPin extends AsyncTaskLoader<Integer> {

        private String mEmail;

        public ValidateEmailPin(Context context,String mEmail) {
            super(context);

            this.mEmail = mEmail;
        }

        @Override
        public Integer loadInBackground() {
            UserAPI apiDAO = new UserAPI();
            int result = apiDAO.validEmail(mEmail);
            if(result > 0) {
                UserAuthTO auth = apiDAO.auth(mEmail, 0);
                if(auth != null)
                    return auth.getId();
            }

            return 0;
        }
    }

    public void enter(){
        //get the data
        mPass = txtPass.getText().toString();
        if(TextUtils.isEmpty(mPass)){
            Toast.makeText(getContext(), R.string.woe_login_denied,Toast.LENGTH_LONG).show();
            return;
        }

        //process the data
        mPass = Decodificador.hash256(mPass);

        // You can pass a null args to a Loader
        Loader<UserTokenTO> loader = loaderManager.getLoader(UserAuthValidLoader.LOADER_ID_AUTH_VALID);
        if(loader != null)
            loaderManager.destroyLoader(UserAuthValidLoader.LOADER_ID_AUTH_VALID);
        loader = loaderManager.initLoader(UserAuthValidLoader.LOADER_ID_AUTH_VALID, null,
                new UserLoginLoader(getContext(), mEmail, mPass,
                        new UserLoginLoader.OnItemSelectedListener() {
                            @Override
                            public void onCreateLoader(@Nullable Bundle args) {
                                processing = true;
                                mProgressBar.setVisibility(View.VISIBLE);
                                btnEnter.setEnabled(false);
                            }

                            @Override
                            public void onLoadFinished(UserTokenTO result) {
                                enter(result);
                            }
                        }));
        loader.forceLoad();
    }

    public void enter(UserTokenTO token){
        processing = false;
        mProgressBar.setVisibility(View.INVISIBLE);
        btnEnter.setEnabled(true);

        if(token == null || TextUtils.isEmpty(token.getIdToken()) || TextUtils.isEmpty(token.getAccessToken())) {
            Toast.makeText(getContext(), R.string.woe_login_denied,Toast.LENGTH_LONG).show();
        }else{
            UserTO mUser = UserUtils.newInstance(getContext(), token.getUser());
            UserUtils.saveUserData(getContext(),mUser,token);

            if(listener != null)
                listener.onEnter();
        }
    }

    private class UserRecoverPasswordTask extends AsyncTask<Boolean> {

        private String mEmail;

        public UserRecoverPasswordTask(String mEmail){
            this.mEmail = mEmail;
        }

        @Override
        protected Boolean doInBackground() {
            UserAPI apiDAO = new UserAPI();
            return apiDAO.recoverPass(mEmail);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(btnRecover != null ) btnRecover.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(btnRecover != null ) btnRecover.setEnabled(true);

            if (isCancelled()) return;

            //check if the result
            if(result == null || !result) {
                Toast.makeText(getContext(), R.string.woe_internal_error,Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getContext(), R.string.woe_recover_pass_info,Toast.LENGTH_LONG).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Integer> onCreateLoader(int id, @Nullable Bundle args) {
        if(id == LOADER_ID_NEXT_PIN) {
            processing = true;
            mProgressBar.setVisibility(View.VISIBLE);
            btnEnter.setEnabled(false);
            btnEnterPin.setEnabled(false);

            return new LoginStepEmailView.ValidateEmailPin(getContext(),mEmail);
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Integer> loader, Integer result) {
        if (loader.getId() == LOADER_ID_NEXT_PIN) {
            //check if the result
            if (result <= 0) {
                processing = false;
                mProgressBar.setVisibility(View.INVISIBLE);
                btnEnter.setEnabled(true);
                btnEnterPin.setEnabled(true);
            } else {
                if (listener != null) listener.onNextPin(mEmail, result);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Integer> loader) {

    }
}