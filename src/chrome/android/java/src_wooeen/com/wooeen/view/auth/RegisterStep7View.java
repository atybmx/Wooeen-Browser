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

public class RegisterStep7View extends Fragment {

    private EditText txtCode;

    private UserTO mUser;
    private String mCode;
    private ArrayList<Integer> mIds = new ArrayList<Integer>();

    private int count = 1;

    private OnItemSelectedListener listener;

    private Button btnNext;
    private Button btnBack;

    private ProgressBar mProgressBar;
    private boolean processing;

    private LoaderManager loaderManager;

    public interface OnItemSelectedListener {
        public void onStep7Next(UserTO user);
        public void onStep7Back(UserTO user);
    }

    public RegisterStep7View() {
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * @return A new instance of fragment FragmentLoginStepEmail.
     */
    public static RegisterStep7View newInstance(UserTO mUser) {
        RegisterStep7View fragment = new RegisterStep7View();
        Bundle args = new Bundle();
        args.putSerializable(RegisterView.USER,mUser);
        fragment.setArguments(args);
        return fragment;
    }

    public static RegisterStep7View newInstance(UserTO mUser,int id) {
        RegisterStep7View fragment = new RegisterStep7View();
        Bundle args = new Bundle();
        args.putSerializable(RegisterView.USER,mUser);
        args.putIntegerArrayList("RegisterStep7View.ids",new ArrayList<Integer>(Arrays.asList(id)));
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
            mUser = (UserTO) getArguments().getSerializable(RegisterView.USER);
            mIds = getArguments().getIntegerArrayList("RegisterStep7View.ids");
            count = getArguments().getInt("RegisterStep7View.count");
        }

        if(savedInstanceState != null) {
            processing = savedInstanceState.getBoolean("RegisterStep7View.processing");
            mUser = (UserTO) savedInstanceState.getSerializable(RegisterView.USER);
            mIds = savedInstanceState.getIntegerArrayList("RegisterStep7View.ids");
            count = savedInstanceState.getInt("RegisterStep7View.count");
        }

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.register_step_7, container, false);

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

        btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
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
                        new UserAuthLoader(getContext(), mUser.getEmail(), count,
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
            btnNext.setEnabled(false);
            btnBack.setEnabled(false);
        }

        //reset the loaders
        this.loaderManager = LoaderManager.getInstance(this);

        Loader<Integer> loaderUserAuth = loaderManager.getLoader(UserAuthLoader.LOADER_ID_AUTH);
        if(loaderUserAuth != null)
            loaderManager.initLoader(UserAuthLoader.LOADER_ID_AUTH, null,
                    new UserAuthLoader(getContext(), mUser.getEmail(), count,
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
                                    btnNext.setEnabled(false);
                                    btnBack.setEnabled(false);
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
        outState.putBoolean("RegisterStep7View.processing",processing);
        outState.putSerializable(RegisterView.USER,mUser);
        outState.putIntegerArrayList("RegisterStep7View.ids",mIds);
        outState.putInt("RegisterStep7View.count",count);

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
                                btnNext.setEnabled(false);
                                btnBack.setEnabled(false);
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
        btnNext.setEnabled(true);
        btnBack.setEnabled(true);

        if(token == null || TextUtils.isEmpty(token.getIdToken()) || TextUtils.isEmpty(token.getAccessToken())) {
            Toast.makeText(getContext(), R.string.woe_code_invalid,Toast.LENGTH_LONG).show();
        }else{
            //saving the user
            UserUtils.saveUserData(getContext(),mUser,token);

            if(listener != null)
                listener.onStep7Next(mUser);
        }
    }

    private void back() {
        if(listener != null) listener.onStep7Back(mUser);
    }
}