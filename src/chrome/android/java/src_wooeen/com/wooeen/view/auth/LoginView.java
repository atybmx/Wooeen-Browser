package com.wooeen.view.auth;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.wooeen.utils.TextUtils;
import com.wooeen.utils.UserUtils;

import org.chromium.chrome.browser.util.TabUtils;
import org.chromium.chrome.R;

public class LoginView extends AppCompatActivity implements LoginStepEmailView.OnItemSelectedListener,LoginStepPassView.OnItemSelectedListener,LoginStepVerifyView.OnItemSelectedListener {

    private Fragment mCurFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mEmail = null;

        if(getIntent() != null && getIntent().getExtras() != null){
            mEmail = getIntent().getExtras().getString("LoginView.email");
        }

        if(savedInstanceState != null) {
            mCurFragment = getSupportFragmentManager().getFragment(savedInstanceState, "LoginView.curFragment");
        }

        //hide toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        setContentView(R.layout.activity_login);

        //create the default fragment
        if(mCurFragment == null) {
            if(mEmail != null)
                mCurFragment = LoginStepEmailView.newInstance(mEmail);
            else
                mCurFragment = LoginStepEmailView.newInstance();

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
        int userId = UserUtils.getUserId(this);
        if(userId > 0)
            finish();
    }

    @Override
    public void onNextPass(String email) {
        //init the next fragment
        mCurFragment = LoginStepPassView.newInstance(email);

        //show the fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out);
        ft.replace(R.id.fragment_content, mCurFragment);
        ft.commit();
    }

    @Override
    public void onNextPin(String email,int id) {
        //init the next fragment
        mCurFragment = LoginStepVerifyView.newInstance(email, id);

        //show the fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out);
        ft.replace(R.id.fragment_content, mCurFragment);
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "LoginView.curFragment", mCurFragment);

        super.onSaveInstanceState(outState);        
    }

    @Override
    public void onRegister() {
        Intent i = new Intent(LoginView.this, RegisterView.class);
        startActivity(i);
    }

    @Override
    public void onEnter() {
        //notify browser current tab
        TabUtils.refreshCashbackData();

        finish();
    }

    @Override
    public void onValidated() {
        //notify browser current tab
        TabUtils.refreshCashbackData();

        finish();
    }
}
