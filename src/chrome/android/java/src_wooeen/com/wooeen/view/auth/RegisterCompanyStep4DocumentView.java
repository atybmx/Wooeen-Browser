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
import com.wooeen.view.ui.MaskEditUtil;

import org.chromium.chrome.R;

public class RegisterCompanyStep4DocumentView extends Fragment{

    private UserTO mUser;

    private EditText txtDocument;
    private Button btnNext;
    private Button btnBack;

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        public void onStep4PhoneNext(UserTO user);
        public void onStep4PhoneBack(UserTO user);
    }

    public RegisterCompanyStep4DocumentView() {
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * @return A new instance of fragment FragmentLoginStepName.
     */
    public static RegisterCompanyStep4DocumentView newInstance(UserTO mUser) {
        RegisterCompanyStep4DocumentView fragment = new RegisterCompanyStep4DocumentView();
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
        View view =  inflater.inflate(R.layout.register_cp_step_4_document, container, false);

        txtDocument = view.findViewById(R.id.txt_data);

        if(mUser != null && mUser.getCountry() != null && "BR".equals(mUser.getCountry().getId())) {
            txtDocument.setHint(""+MaskEditUtil.FORMAT_CNPJ);
            txtDocument.addTextChangedListener(
                    MaskEditUtil.mask(txtDocument, MaskEditUtil.FORMAT_CNPJ));
            txtDocument.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        }

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
        String document = txtDocument.getText().toString();
        document = TextUtils.noSpecialChars(document);
        if(TextUtils.isEmpty(document) || document.length() < 14){
            Toast.makeText(getContext(), R.string.woe_cp_whats_your_document_error, Toast.LENGTH_SHORT).show();
            return;
        }

        //set user name
        mUser.setDocument(document);

        if(listener != null) listener.onStep4PhoneNext(mUser);
    }

    private void back() {
        if(listener != null) listener.onStep4PhoneBack(mUser);
    }

}