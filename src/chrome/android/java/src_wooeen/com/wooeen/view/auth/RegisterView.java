package com.wooeen.view.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.UserUtils;

import org.chromium.chrome.browser.app.BraveActivity;
import org.chromium.chrome.browser.util.TabUtils;
import org.chromium.chrome.R;

import java.util.TimeZone;

public class RegisterView extends AppCompatActivity
        implements
        RegisterStep1View.OnItemSelectedListener,
        RegisterStep2View.OnItemSelectedListener,
        RegisterStep3View.OnItemSelectedListener,
        RegisterStep4View.OnItemSelectedListener,
        RegisterStepPassView.OnItemSelectedListener,
        RegisterStepCrView.OnItemSelectedListener,
        RegisterStep8View.OnItemSelectedListener{

    private Fragment mCurFragment;

    public static final String USER = "RegisterView.user";

    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            mCurFragment = getSupportFragmentManager().getFragment(savedInstanceState, "RegisterView.curFragment");
        }

        if(getIntent() != null && getIntent().getExtras() != null){
            mEmail = getIntent().getStringExtra("email");
        }

        //hide toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        setContentView(R.layout.activity_login);

        //create the default fragment
        if(mCurFragment == null) {
            mCurFragment = RegisterStep1View.newInstance();

            //show the fragment
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment_content, mCurFragment);
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //close the activity if the user is logged
//        int userId = UserUtils.getUserId(this);
//        if(userId > 0)
//            finish();
    }

    @Override
    protected void onDestroy() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_content);
        if(f != null && f.isResumed()){
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().remove(f).commit();
        }

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        if (mCurFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "RegisterView.curFragment",
                    mCurFragment);
        }
    }

    private void nextFragment(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out);
        ft.replace(R.id.fragment_content, mCurFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void backFragment(){
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.setCustomAnimations(R.anim.slide_out,R.anim.fade_out,R.anim.fade_in,R.anim.slide_in);
//        ft.replace(R.id.fragment_content, mCurFragment);
//        ft.addToBackStack(null);
//        ft.commit();
    }

    @Override
    public void onStep1Next() {
        mCurFragment = RegisterStep2View.newInstance();
        nextFragment();
    }

    @Override
    public void onStep2Next() {
        mCurFragment = RegisterStep3View.newInstance(UserUtils.newInstance(this));
        nextFragment();
    }

    @Override
    public void onStep2Back() {
//        mCurFragment = RegisterStep1View.newInstance();
        backFragment();
    }

    @Override
    public void onStep3Next(UserTO user) {
        mCurFragment = RegisterStepCrView.newInstance(user);
        nextFragment();
    }

    @Override
    public void onStep3Back() {
//        mCurFragment = RegisterStep2View.newInstance();
        backFragment();
    }

    @Override
    public void onStepCrNext(UserTO user) {
        if(user != null && TextUtils.isEmpty(user.getEmail()) && !TextUtils.isEmpty(mEmail))
            user.setEmail(mEmail);

        mCurFragment = RegisterStep4View.newInstance(user);
        nextFragment();
    }

    @Override
    public void onStepCrBack(UserTO user) {
//        mCurFragment = RegisterStep3View.newInstance(user);
        backFragment();
    }

    @Override
    public void onStep4Next(UserTO user,int auth) {
        mCurFragment = RegisterStepPassView.newInstance(user);
        nextFragment();

        //VAI PARA NOVA VIEW COM A SENHA
        //COPIAR RegisterStep6View
    }

    @Override
    public void onStep4Login(String email) {
        Intent i = new Intent(RegisterView.this, LoginView.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("LoginView.email", email);
        i.putExtras(mBundle);
        startActivity(i);
    }

    @Override
    public void onStep4Back(UserTO user) {
//        mCurFragment = RegisterStepCrView.newInstance(user);
        backFragment();
    }

    @Override
    public void onStepPassNext(UserTO user) {
        mCurFragment = RegisterStep8View.newInstance();
        nextFragment();
    }

    @Override
    public void onStepPassBack(UserTO user) {
//        mCurFragment = RegisterStep4View.newInstance(user);
        backFragment();
    }

    @Override
    public void onStep8Next() {
        BraveActivity.addEvent(this, "register_success");

        //notify browser current tab
        TabUtils.refreshCashbackData(getBaseContext());

//        Intent resultIntent = new Intent();
//        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }
}
