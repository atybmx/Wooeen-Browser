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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.wooeen.model.to.UserTO;
import com.wooeen.utils.TextUtils;

import org.chromium.chrome.R;

public class RegisterCompanyStep2NameView extends Fragment{

    private UserTO mUser;

    private EditText txtName;
    private Button btnNext;
    private Button btnBack;

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        public void onStep2NameNext(UserTO user);
        public void onStep2NameBack();
    }

    public RegisterCompanyStep2NameView() {
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * @return A new instance of fragment FragmentLoginStepName.
     */
    public static RegisterCompanyStep2NameView newInstance(UserTO mUser) {
        RegisterCompanyStep2NameView fragment = new RegisterCompanyStep2NameView();
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
            mUser = (UserTO) savedInstanceState.getSerializable(RegisterView.USER);
        }

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.register_cp_step_2_name, container, false);

        txtName = view.findViewById(R.id.txt_data);
        txtName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
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
        String name = txtName.getText().toString();
        if(TextUtils.isEmpty(name) || !name.contains(" ")){
            Toast.makeText(getContext(), R.string.woe_type_valid_name, Toast.LENGTH_SHORT).show();
            return;
        }

        //set user name
        mUser.setName(name);

        if(listener != null) listener.onStep2NameNext(mUser);
    }

    private void back() {
        if(listener != null) listener.onStep2NameBack();
    }

}