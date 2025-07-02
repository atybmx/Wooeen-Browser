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
import androidx.loader.content.Loader;

import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.UserUtils;
import com.wooeen.view.auth.loader.UserAuthLoader;
import com.wooeen.view.auth.loader.UserAuthValidLoader;

import org.chromium.chrome.R;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginStepVerifyView extends Fragment {

    private EditText txtCode;

    private String mEmail;
    private String mCode;
    private ArrayList<Integer> mIds = new ArrayList<Integer>();

    private int count = 1;

    private OnItemSelectedListener listener;

    private Button btnValidate;
    private ProgressBar mProgressBar;
    private boolean processing;

    private LoaderManager loaderManager;

    public interface OnItemSelectedListener {
        public void onValidated();
    }

    public LoginStepVerifyView() {
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * @return A new instance of fragment FragmentLoginStepEmail.
     */
    public static LoginStepVerifyView newInstance(String email) {
        LoginStepVerifyView fragment = new LoginStepVerifyView();
        Bundle args = new Bundle();
        args.putString("LoginStepVerifyView.email",email);
        fragment.setArguments(args);
        return fragment;
    }

    public static LoginStepVerifyView newInstance(String email,int id) {
        LoginStepVerifyView fragment = new LoginStepVerifyView();
        Bundle args = new Bundle();
        args.putString("LoginStepVerifyView.email",email);
        args.putIntegerArrayList("LoginStepVerifyView.ids",new ArrayList<Integer>(Arrays.asList(id)));
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
            mEmail = getArguments().getString("LoginStepVerifyView.email");
            mIds = getArguments().getIntegerArrayList("LoginStepVerifyView.ids");
            count = getArguments().getInt("LoginStepVerifyView.count");
        }

        if(savedInstanceState != null) {
            processing = savedInstanceState.getBoolean("LoginStepVerifyView.processing");
            mEmail = savedInstanceState.getString("LoginStepVerifyView.email");
            mIds = savedInstanceState.getIntegerArrayList("LoginStepVerifyView.ids");
            count = savedInstanceState.getInt("LoginStepVerifyView.count");
        }

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.login_step_verify, container, false);

        txtCode = view.findViewById(R.id.txt_code);
        txtCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_DONE) {
                    validate();
                }
                return handled;
            }
        });

        btnValidate = view.findViewById(R.id.btn_validate);
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        TextView btnResend = view.findViewById(R.id.btn_resend);
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count += 1;
                // You can pass a null args to a Loader
                Loader<Integer> loader = loaderManager.getLoader(UserAuthLoader.LOADER_ID_AUTH);
                if(loader != null)
                    loaderManager.destroyLoader(UserAuthLoader.LOADER_ID_AUTH);
                loader = loaderManager.initLoader(UserAuthLoader.LOADER_ID_AUTH, null,
                        new UserAuthLoader(getContext(), mEmail,count,
                                new UserAuthLoader.OnItemSelectedListener() {
                                    @Override
                                    public void onLoadFinished(Integer result) {
                                        resend(result);
                                    }
                                }));
                loader.forceLoad();
            }
        });

        mProgressBar = view.findViewById(R.id.progress_bar);

        if(!processing) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }else{
            btnValidate.setEnabled(false);
        }

        //reset the loaders
        this.loaderManager = LoaderManager.getInstance(this);

        Loader<Integer> loaderUserAuth = loaderManager.getLoader(UserAuthLoader.LOADER_ID_AUTH);
        if(loaderUserAuth != null)
            loaderManager.initLoader(UserAuthLoader.LOADER_ID_AUTH, null,
                    new UserAuthLoader(getContext(), mEmail,count,
                            new UserAuthLoader.OnItemSelectedListener() {
                                @Override
                                public void onLoadFinished(Integer result) {
                                    resend(result);
                                }
                            }));

        Loader<UserTokenTO> loaderUserAuthValid = loaderManager.getLoader(UserAuthValidLoader.LOADER_ID_AUTH_VALID);
        if(loaderUserAuthValid != null)
            loaderManager.initLoader(UserAuthValidLoader.LOADER_ID_AUTH_VALID, null,
                    new UserAuthValidLoader(getContext(), mCode, mIds,
                            new UserAuthValidLoader.OnItemSelectedListener() {
                                @Override
                                public void onCreateLoader(@Nullable Bundle args) {
                                    processing = true;
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    btnValidate.setEnabled(false);
                                }

                                @Override
                                public void onLoadFinished(UserTokenTO result) {
                                    validated(result);
                                }
                            }));

        return view;
    }

    private void resend(Integer result) {
        if(result != null && result > 0)
            mIds.add(result);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("LoginStepVerifyView.processing",processing);
        outState.putString("LoginStepVerifyView.email", mEmail);
        outState.putIntegerArrayList("LoginStepVerifyView.ids",mIds);
        outState.putInt("LoginStepVerifyView.count", count);

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

    public void validate(){
        mCode = txtCode.getText().toString();

        // You can pass a null args to a Loader
        Loader<UserTokenTO> loader = loaderManager.getLoader(UserAuthValidLoader.LOADER_ID_AUTH_VALID);
        if(loader != null)
            loaderManager.destroyLoader(UserAuthValidLoader.LOADER_ID_AUTH_VALID);
        loader = loaderManager.initLoader(UserAuthValidLoader.LOADER_ID_AUTH_VALID, null,
                new UserAuthValidLoader(getContext(), mCode, mIds,
                        new UserAuthValidLoader.OnItemSelectedListener() {
                            @Override
                            public void onCreateLoader(@Nullable Bundle args) {
                                processing = true;
                                mProgressBar.setVisibility(View.VISIBLE);
                                btnValidate.setEnabled(false);
                            }

                            @Override
                            public void onLoadFinished(UserTokenTO result) {
                                validated(result);
                            }
                        }));
        loader.forceLoad();
    }

    public void validated(UserTokenTO token){
        processing = false;
        mProgressBar.setVisibility(View.INVISIBLE);
        btnValidate.setEnabled(true);

        if(token == null || TextUtils.isEmpty(token.getIdToken()) || TextUtils.isEmpty(token.getAccessToken())) {
            Toast.makeText(getContext(), R.string.woe_code_invalid,Toast.LENGTH_LONG).show();
        }else{
            if(listener != null)
                listener.onValidated();
        }
    }
}