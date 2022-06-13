package com.wooeen.view.auth;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;

import org.chromium.chrome.R;

public class RegisterStep2View extends Fragment{

    private OnItemSelectedListener listener;

    private LoaderManager loaderManager;

    private final static int LOADER_ID_NEXT = 1;

    private Button btnNext;

    public interface OnItemSelectedListener {
        public void onStep2Next();

        public void onStep2Back();
    }

    public RegisterStep2View() {
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * @return A new instance of fragment FragmentLoginStepEmail.
     */
    public static RegisterStep2View newInstance() {
        RegisterStep2View fragment = new RegisterStep2View();
        Bundle args = new Bundle();
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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.register_step_2, container, false);

        CheckBox woeLegalAge = view.findViewById(R.id.woe_legal_age);
        woeLegalAge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked)
                    btnNext.setEnabled(true);
                else
                    btnNext.setEnabled(false);
            }
        });

        btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        Button btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        return view;
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
        if(listener != null) listener.onStep2Next();
    }

    private void back() {
        if(listener != null) listener.onStep2Back();
    }
}