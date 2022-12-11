package com.wooeen.view.auth;

import android.content.Context;
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
import com.wooeen.model.api.WoeTrkAPI;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.UserAuthTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.model.to.WoeTrkClickTO;
import com.wooeen.utils.Decodificador;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.UserUtils;
import com.wooeen.utils.WoeTrkUtils;
import com.wooeen.view.ui.MaskEditUtil;

import org.chromium.chrome.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterStepPassView extends Fragment implements LoaderManager.LoaderCallbacks<Map<String,Object>>{

    private UserTO mUser;

    private EditText txtPass;
    private Button btnNext;
    private Button btnBack;

    private OnItemSelectedListener listener;

    private ProgressBar mProgressBar;
    private boolean processing;

    private LoaderManager loaderManager;

    private final static int LOADER_ID_NEXT = 1;

    public interface OnItemSelectedListener {
        public void onStepPassNext(UserTO user);
        public void onStepPassBack(UserTO user);
    }

    public RegisterStepPassView() {
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * @return A new instance of fragment FragmentLoginStepName.
     */
    public static RegisterStepPassView newInstance(UserTO mUser) {
        RegisterStepPassView fragment = new RegisterStepPassView();
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
            processing = savedInstanceState.getBoolean("RegisterStepPassView.processing");
            mUser = (UserTO) savedInstanceState.getSerializable(RegisterView.USER);
        }

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.register_step_pass, container, false);

        txtPass = view.findViewById(R.id.txt_data);
        txtPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    next();
                }
                return handled;
            }
        });

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
        outState.putBoolean("RegisterStepPassView.processing",processing);
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
        String pass = txtPass.getText().toString();
        if(TextUtils.isEmpty(pass) || pass.length() < 6){
            Toast.makeText(getContext(), R.string.woe_register_pass_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //set user name
        mUser.setPass(Decodificador.hash256(pass));

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

    public static class RegisterUser extends AsyncTaskLoader<Map<String,Object>> {

        private UserTO mUser;
        private WoeTrkClickTO click;

        public RegisterUser(Context context,UserTO mUser) {
            super(context);

            this.mUser = mUser;

            //get the click
            click = WoeTrkUtils.getClick(context);
        }

        @Override
        public Map<String,Object> loadInBackground() {
            //try to get the country from ip
            if(mUser.getCountry() == null || TextUtils.isEmpty(mUser.getCountry().getId())){
                UtilsAPI.IpInfo ipInfo = UtilsAPI.getIpInfo();
                if(ipInfo != null)
                    mUser.setCountry(new CountryTO(ipInfo.getCountrycode()));
            }

            Map<String,Object> result = new HashMap<String,Object>();

            UserAPI apiDAO = new UserAPI();
            UserTO user = apiDAO.newUser(mUser);
            result.put("user",user);

            if(user.getId() > 0){
                UserTokenTO token = apiDAO.login(mUser.getEmail(), mUser.getPass());
                result.put("token", token);

                //woe trk api
                if(click != null) {
                    //send user tracking
                    if (click.getSource() > 0 &&
                            click.getLink() > 0 &&
                            !TextUtils.isEmpty(click.getDateClick())) {
                        WoeTrkAPI.user(user.getId(), click.getSource(), click.getLink(),
                                click.getDateClick());
                    }

                    //send click tracking
                    WoeTrkAPI.event(2, user.getId(), click.getSource(), click.getLink(),
                            click.getDateClick());
                }
            }

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

            return new RegisterUser(getContext(),mUser);
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String,Object>> loader, Map<String,Object> result) {
        if(loader.getId() == LOADER_ID_NEXT) {
            UserTO user = null;
            UserTokenTO token = null;

            if(result != null && result.containsKey("user") || result.get("user") instanceof UserTO)
                user = (UserTO) result.get("user");

            if(result != null && result.containsKey("token") || result.get("token") instanceof UserTokenTO)
                token = (UserTokenTO) result.get("token");

            if(user == null || user.getId() <= 0 || token == null) {
                Toast.makeText(getContext(),getString(R.string.woe_internal_error),Toast.LENGTH_LONG).show();

                processing = false;
                mProgressBar.setVisibility(View.INVISIBLE);
                btnNext.setEnabled(true);
                btnBack.setEnabled(true);
            }else{
                mUser.setId(user.getId());

                //saving the user
                UserUtils.saveUserData(getContext(),mUser,token);

                if(listener != null) listener.onStepPassNext(mUser);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String,Object>> loader) {

    }

    private void back() {
        if(listener != null) listener.onStepPassBack(mUser);
    }

}