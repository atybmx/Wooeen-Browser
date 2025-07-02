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
import com.wooeen.model.api.UtilsAPI;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.UserAuthTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.utils.TextUtils;

import org.chromium.chrome.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterStep4View extends Fragment implements LoaderManager.LoaderCallbacks<Map<String,Object>> {

    private UserTO mUser;

    private EditText txtEmail;
    private Button btnNext;
    private TextView btnBack;

    private OnItemSelectedListener listener;

    private ProgressBar mProgressBar;
    private boolean processing;

    private LoaderManager loaderManager;

    private final static int LOADER_ID_NEXT = 1;

    public interface OnItemSelectedListener {
        public void onStep4Next(UserTO user,int auth);
        public void onStep4Login(String email);
        public void onStep4Back(UserTO user);
    }

    public RegisterStep4View() {
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * @return A new instance of fragment FragmentLoginStepEmail.
     */
    public static RegisterStep4View newInstance(UserTO mUser) {
        RegisterStep4View fragment = new RegisterStep4View();
        Bundle args = new Bundle();
        args.putSerializable(RegisterView.USER,mUser);
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
            mUser = (UserTO) getArguments().getSerializable(RegisterView.USER);

        if(savedInstanceState != null) {
            processing = savedInstanceState.getBoolean("LoginStepEmailView.processing");
            mUser = (UserTO) savedInstanceState.getSerializable(RegisterView.USER);
        }

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.register_step_4, container, false);

        txtEmail = view.findViewById(R.id.txt_data);
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

        //init email field
        if(TextUtils.isEmpty(txtEmail.getText().toString()) && mUser != null && !TextUtils.isEmpty(mUser.getEmail()))
            txtEmail.setText(mUser.getEmail());

        btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        mProgressBar = view.findViewById(R.id.progress_bar);

        if(!processing) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }else{
            btnNext.setEnabled(false);
            btnBack.setEnabled(false);
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
        outState.putSerializable(RegisterView.USER,mUser);

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

        //configure and set the email
        email = email.replaceAll(" ","").trim();
        mUser.setEmail(email);

        LoaderManager.LoaderCallbacks<Map<String,Object>> loaderCallbacks = this;

        // Arguments:
        Bundle args = new Bundle();

        // You can pass a null args to a Loader
        Loader<Map<String,Object>> loader = loaderManager.getLoader(LOADER_ID_NEXT);
        if(loader != null)
            loaderManager.destroyLoader(LOADER_ID_NEXT);
        loader = loaderManager.initLoader(LOADER_ID_NEXT, args, loaderCallbacks);
        loader.forceLoad();
    }

    public static class ValidateEmail extends AsyncTaskLoader<Map<String,Object>> {

        private UserTO mUser;

        public ValidateEmail(Context context,UserTO mUser) {
            super(context);

            this.mUser = mUser;
        }

        @Override
        public Map<String,Object> loadInBackground() {
            Map<String,Object> result = new HashMap<String,Object>();

            UserAPI apiDAO = new UserAPI();
            int resultEmail = apiDAO.validEmail(mUser.getEmail());
            result.put("resultEmail",resultEmail);

//            if(resultEmail == 0){
//                //try to get the country from ip
//                if(mUser.getCountry() == null || TextUtils.isEmpty(mUser.getCountry().getId())){
//                    UtilsAPI.IpInfo ipInfo = UtilsAPI.getIpInfo();
//                    if(ipInfo != null)
//                        mUser.setCountry(new CountryTO(ipInfo.getCountrycode()));
//                }
//
//                UserTO user = apiDAO.newUser(mUser);
//                result.put("user",user);
//                result.put("auth", 0);
//
//                if(user != null && user.getId() > 0){
//                    UserAuthTO auth = apiDAO.auth(user.getEmail(),0);
//                    if(auth != null)
//                        result.put("auth", auth.getId());
//                }
//            }

            return result;
        }
    }

    @NonNull
    @Override
    public Loader<Map<String,Object>> onCreateLoader(int id, @Nullable Bundle args) {
        if(id == LOADER_ID_NEXT) {
            processing = true;
            mProgressBar.setVisibility(View.VISIBLE);
            btnNext.setEnabled(false);
            btnBack.setEnabled(false);

            return new ValidateEmail(getContext(),mUser);
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String,Object>> loader, Map<String,Object> result) {
        if(loader.getId() == LOADER_ID_NEXT) {
            processing = false;
            mProgressBar.setVisibility(View.INVISIBLE);
            btnNext.setEnabled(true);
            btnBack.setEnabled(true);

            //check if the result email
            int resultEmail = (int) result.get("resultEmail");
            if(resultEmail > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(getString(R.string.woe_email_already_registered));
                builder.setMessage(getString(R.string.woe_email_would_like_login));
                builder.setPositiveButton(getString(R.string.woe_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(listener != null) listener.onStep4Login(mUser.getEmail());
                            }
                        });
                builder.setNegativeButton(getString(R.string.woe_no), null);
                builder.show();
            }else if(resultEmail == -2){
                Toast.makeText(getContext(), R.string.woe_email_not_allowed, Toast.LENGTH_LONG).show();
                return;
            }else if(resultEmail == -1){
                Toast.makeText(getContext(), R.string.woe_internal_error, Toast.LENGTH_LONG).show();
                return;
            }else{
//                UserTO user = null;
//                if(result != null && result.containsKey("user") || result.get("user") instanceof UserTO)
//                    user = (UserTO) result.get("user");
//
//                if(user == null || user.getId() <= 0) {
//                    Toast.makeText(getContext(),getString(R.string.woe_internal_error),Toast.LENGTH_LONG).show();
//                }else{
//                    mUser.setId(user.getId());
//
//                    if(listener != null) listener.onStep4Next(mUser, (Integer)result.get("auth"));
//                }
                if(listener != null) listener.onStep4Next(mUser, -1);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String,Object>> loader) {

    }

    private void back() {
        if(listener != null) listener.onStep4Back(mUser);
    }
}