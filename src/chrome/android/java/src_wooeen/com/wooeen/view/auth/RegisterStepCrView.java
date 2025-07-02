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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.squareup.picasso.Picasso;
import com.wooeen.model.api.CountryAPI;
import com.wooeen.model.api.UserAPI;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.utils.TextUtils;

import org.chromium.chrome.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterStepCrView extends Fragment{

    private UserTO mUser;

    private LinearLayout txtCountry;
    private ImageView crImg;
    private TextView crText;
    private Button btnNext;
    private TextView btnBack;

    private OnItemSelectedListener listener;

    private final static int LOADER_ID_NEXT = 1;

    public interface OnItemSelectedListener {
        public void onStepCrNext(UserTO user);
        public void onStepCrBack(UserTO user);
    }

    public RegisterStepCrView() {
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * @return A new instance of fragment FragmentLoginStepEmail.
     */
    public static RegisterStepCrView newInstance(UserTO mUser) {
        RegisterStepCrView fragment = new RegisterStepCrView();
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
        View view =  inflater.inflate(R.layout.register_step_cr, container, false);

        txtCountry = view.findViewById(R.id.txt_data);
        txtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryListAdapter adapter = new CountryListAdapter(getContext());

                AlertDialog.Builder b = new AlertDialog.Builder(getContext());
                b.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CountryAPI.CountryTOA cr = adapter.getItem(which);
                        if(cr != null && !TextUtils.isEmpty(cr.getId())){
                            CountryTO country = new CountryTO();
                            country.setId(cr.getId());
                            country.setName(cr.getName());
                            country.setLanguage(cr.getLanguage());

                            mUser.setCountry(country);
                            loadData();
                        }
                    }
                });
                b.setTitle(getString(R.string.woe_whats_your_country));

                b.show();
            }
        });

        crImg = view.findViewById(R.id.cr_img);
        crText = view.findViewById(R.id.cr_text);

        //init cr field
        loadData();

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

    private void loadData(){
        if(mUser != null && mUser.getCountry() != null && !TextUtils.isEmpty(mUser.getCountry().getId())) {
            Picasso.with(getContext()).load(getCountryFlag(mUser.getCountry().getId(),40)).fit().centerCrop().into(crImg);
            crText.setText(mUser.getCountry().getId());
        }
    }

    public static String getCountryFlag(String country, int size) {
        if(TextUtils.isEmpty(country))
            return "";

//		if(size <= 0)
//			size = 24;

        if(size < 25)
            size = 20;
        else if(size < 80)
            size = 40;
        else if(size < 160)
            size = 80;
        else
            size = 160;

        if("uk".equalsIgnoreCase(country))
            country = "gb";

        country = country.toLowerCase();

        return "https://flagcdn.com/w"+size+"/"+country+".png";
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
        if(listener != null) listener.onStepCrNext(mUser);
    }

    private void back() {
        if(listener != null) listener.onStepCrBack(mUser);
    }
}