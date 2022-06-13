package com.wooeen.view.auth;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.wooeen.model.to.UserTO;
import com.wooeen.utils.DatetimeUtils;

import org.chromium.chrome.R;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

public class RegisterStep5View extends Fragment{

    private UserTO mUser;
    private Calendar myCalendar = Calendar.getInstance();

    private EditText txtBirthDate;
    private Button btnNext;
    private Button btnBack;

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        public void onStep5Next(UserTO user);
        public void onStep5Back(UserTO user);
    }

    public RegisterStep5View() {
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * @return A new instance of fragment FragmentLoginStepName.
     */
    public static RegisterStep5View newInstance(UserTO mUser) {
        RegisterStep5View fragment = new RegisterStep5View();
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
        View view =  inflater.inflate(R.layout.register_step_5, container, false);

        txtBirthDate = view.findViewById(R.id.txt_data);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                txtBirthDate.setText(DatetimeUtils.dateToString(myCalendar.getTime(),"dd/MM/yyyy"));
            }

        };
        txtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        txtBirthDate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        Date birthDate = DatetimeUtils.stringToDate(txtBirthDate.getText().toString(),"dd/MM/yyyy");
        if(birthDate == null){
            Toast.makeText(getContext(), R.string.woe_type_valid_birth, Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDate);

        Calendar legalAge = Calendar.getInstance();
        legalAge.add(Calendar.YEAR, -18);
        if(!legalAge.after(birth)) {
            Toast.makeText(getContext(), R.string.woe_birth_date_legal_age, Toast.LENGTH_SHORT).show();
            return;
        }

        //set user name
        mUser.setBirthDate(birthDate);

        if(listener != null) listener.onStep5Next(mUser);
    }

    private void back() {
        if(listener != null) listener.onStep5Back(mUser);
    }

}