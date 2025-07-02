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
import com.wooeen.model.to.UserAuthTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.utils.TextUtils;

import org.w3c.dom.Text;

import org.chromium.chrome.R;

public class LoginStepEmailView extends Fragment implements LoaderManager.LoaderCallbacks<Integer> {

    private String mEmail;
    private EditText txtEmail;
    private Button btnNext;
    private TextView btnRegister;

    private OnItemSelectedListener listener;

    private ProgressBar mProgressBar;
    private boolean processing;

    private LoaderManager loaderManager;

    private final static int LOADER_ID_NEXT = 1;

    public interface OnItemSelectedListener {
        public void onNextPass(String email);

        public void onRegister(String email);
    }

    public LoginStepEmailView() {
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * @return A new instance of fragment FragmentLoginStepEmail.
     */
    public static LoginStepEmailView newInstance() {
        LoginStepEmailView fragment = new LoginStepEmailView();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static LoginStepEmailView newInstance(String email) {
        LoginStepEmailView fragment = new LoginStepEmailView();
        Bundle args = new Bundle();
        args.putString("LoginView.email",email);
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
        if(getArguments() != null)
            mEmail = getArguments().getString("LoginView.email");

        if(savedInstanceState != null) {
            processing = savedInstanceState.getBoolean("LoginStepEmailView.processing");
            mEmail = savedInstanceState.getString("LoginStepEmailView.email");
        }

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.login_step_email, container, false);

        txtEmail = view.findViewById(R.id.txt_email);
        txtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_DONE) {
                    next();
                }
                return handled;
            }
        });
        if(mEmail != null)
            txtEmail.setText(mEmail);

        btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        btnRegister = view.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) listener.onRegister(null);
            }
        });

        mProgressBar = view.findViewById(R.id.progress_bar);

        if(!processing) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }else{
            btnNext.setEnabled(false);
            btnRegister.setEnabled(false);
        }

        this.loaderManager = LoaderManager.getInstance(this);
        Loader<Integer> loader = loaderManager.getLoader(LOADER_ID_NEXT);
        if(loader != null) {
            loaderManager.initLoader(LOADER_ID_NEXT, null, this);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("LoginStepEmailView.processing",processing);
        outState.putString("LoginStepEmailView.email",mEmail);

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

    public void next(){
        String email = txtEmail.getText().toString();
        if(!TextUtils.isEmailValid(email)){
            Toast.makeText(getContext(), R.string.woe_type_valid_email, Toast.LENGTH_SHORT).show();
            return;
        }

        mEmail = email;
        mEmail = mEmail.replaceAll(" ","").trim();

        LoaderManager.LoaderCallbacks<Integer> loaderCallbacks = this;

        // Arguments:
        Bundle args = new Bundle();

        // You can pass a null args to a Loader
        Loader<Integer> loader = loaderManager.getLoader(LOADER_ID_NEXT);
        if(loader != null)
            loaderManager.destroyLoader(LOADER_ID_NEXT);
        loader = loaderManager.initLoader(LOADER_ID_NEXT, args, loaderCallbacks);
        loader.forceLoad();
    }

    public static class ValidateEmail extends AsyncTaskLoader<Integer> {

        private String mEmail;

        public ValidateEmail(Context context,String mEmail) {
            super(context);

            this.mEmail = mEmail;
        }

        @Override
        public Integer loadInBackground() {
            UserAPI apiDAO = new UserAPI();
            return apiDAO.validEmail(mEmail);
//            int result = apiDAO.validEmail(mEmail);
//            if(result > 0) {
//                UserAuthTO auth = apiDAO.auth(mEmail, 0);
//                if(auth != null)
//                    return auth.getId();
//            }
//
//            return 0;
        }
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

    @NonNull
    @Override
    public Loader<Integer> onCreateLoader(int id, @Nullable Bundle args) {
        if(id == LOADER_ID_NEXT) {
            processing = true;
            mProgressBar.setVisibility(View.VISIBLE);
            btnNext.setEnabled(false);
            btnRegister.setEnabled(false);

            return new ValidateEmail(getContext(),mEmail);
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Integer> loader, Integer result) {
        if(loader.getId() == LOADER_ID_NEXT) {
            //check if the result
            if (result <= 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(getString(R.string.woe_email_dont_registered));
                builder.setMessage(getString(R.string.woe_email_would_register_now));
                builder.setPositiveButton(getString(R.string.woe_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (listener != null) listener.onRegister(mEmail);
                            }
                        });
                builder.setNegativeButton(getString(R.string.woe_no), null);
                builder.show();

                processing = false;
                mProgressBar.setVisibility(View.INVISIBLE);
                btnNext.setEnabled(true);
                btnRegister.setEnabled(true);
            } else {
                if (listener != null) listener.onNextPass(mEmail);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Integer> loader) {

    }
}