package com.wooeen.view.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.wooeen.model.api.UtilsAPI;
import com.wooeen.model.sync.WoeSyncAdapter;
import com.wooeen.model.to.CategoryTO;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.model.to.WoeTrkClickTO;
import com.wooeen.utils.NumberUtils;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.UserUtils;
import com.wooeen.utils.WoeTrkUtils;

import org.chromium.chrome.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterCompanyStep5CategoryView extends Fragment implements LoaderManager.LoaderCallbacks<Map<String,Object>>{

    private UserTO mUser;

    private LinearLayout txtCategory;
    private TextView catText;
    private Button btnNext;
    private TextView btnBack;

    private OnItemSelectedListener listener;

    private ProgressBar mProgressBar;
    private boolean processing;

    private LoaderManager loaderManager;

    private final static int LOADER_ID_NEXT = 1;

    public interface OnItemSelectedListener {
        public void onStep5CategoryNext(UserTO user);
        public void onStep5CategoryBack(UserTO user);
    }

    public RegisterCompanyStep5CategoryView() {
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * @return A new instance of fragment FragmentLoginStepEmail.
     */
    public static RegisterCompanyStep5CategoryView newInstance(UserTO mUser) {
        RegisterCompanyStep5CategoryView fragment = new RegisterCompanyStep5CategoryView();
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
            processing = savedInstanceState.getBoolean("RegisterCompanyStep5CategoryView.processing");
            mUser = (UserTO) savedInstanceState.getSerializable(RegisterView.USER);
        }

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.register_cp_step_5_category, container, false);

        // hide keyboard
        View viewFocus = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
        }

        txtCategory = view.findViewById(R.id.txt_data);
        txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModal();
            }
        });

        catText = view.findViewById(R.id.cat_text);

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
    public void onResume() {
        super.onResume();

        showModal();
    }

    private void showModal(){
        CategoryListAdapter adapter = new CategoryListAdapter(getContext());

        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        b.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CategoryTO category = adapter.getItem(which);
                if(category != null && !TextUtils.isEmpty(category.getId())){
                    mUser.setCategory(category);
                    loadData();
                }
            }
        });
        b.setTitle(getString(R.string.woe_cp_whats_your_category));

        b.show();
    }

    private void loadData(){
        if(mUser != null && mUser.getCategory() != null && !TextUtils.isEmpty(mUser.getCategory().getId())) {
            catText.setText(mUser.getCategory().getName());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("RegisterCompanyStep5CategoryView.processing",processing);
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
        if(mUser.getCategory() == null || TextUtils.isEmpty(mUser.getCategory().getId())){
            Toast.makeText(getContext(), R.string.woe_cp_whats_your_category_error, Toast.LENGTH_SHORT).show();
            return;
        }

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

    public static class RegisterCompany extends AsyncTaskLoader<Map<String,Object>> {

        private UserTO mUser;
        private Context context;

        public RegisterCompany(Context context,UserTO mUser) {
            super(context);

            this.context = context;

            this.mUser = mUser;
        }

        @Override
        public Map<String,Object> loadInBackground() {
            //Getting the user
            UserTO loggedUser = UserUtils.getUser(context);
            UserTokenTO loggedUserToken = UserUtils.getUserToken(context);
            if(loggedUser == null){
                return null;
            }

            UserAPI apiDAO = new UserAPI(loggedUserToken);
            Map<String,Object> result = apiDAO.setCompany(mUser);
            if(result == null || !result.containsKey("user") || !result.containsKey("token"))
                return null;

            //load initial data wallet user
            WoeSyncAdapter.syncUser(context, loggedUserToken);

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

            return new RegisterCompany(getContext(),mUser);
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String,Object>> loader, Map<String,Object> result) {
        if(loader.getId() == LOADER_ID_NEXT) {
            Map<String,Object> user = null;
            UserTokenTO token = null;

            if(result != null && result.containsKey("user"))
                user = (Map<String,Object>) result.get("user");

            if(result != null && result.containsKey("token")) {
                Map<String,String> tokenP = (Map<String, String>) result.get("token");

                token = new UserTokenTO();
                token.setIdToken(tokenP.get("idToken"));
                token.setAccessToken(tokenP.get("accessToken"));
            }

            if(user == null || !user.containsKey("id") || token == null) {
                Toast.makeText(getContext(),getString(R.string.woe_internal_error),Toast.LENGTH_LONG).show();

                processing = false;
                mProgressBar.setVisibility(View.INVISIBLE);
                btnNext.setEnabled(true);
                btnBack.setEnabled(true);
            }else{
                mUser.setId(NumberUtils.getInteger((String) user.get("id")));

                //save company token
                UserUtils.saveCompanyData(getContext(), mUser, token);

                if(listener != null) listener.onStep5CategoryNext(mUser);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String,Object>> loader) {

    }

    private void back() {
        if(listener != null) listener.onStep5CategoryBack(mUser);
    }
}