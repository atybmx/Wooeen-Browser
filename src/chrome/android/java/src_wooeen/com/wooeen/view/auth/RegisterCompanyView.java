package com.wooeen.view.auth;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wooeen.model.to.UserTO;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.UserUtils;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.app.BraveActivity;
import org.chromium.chrome.browser.util.TabUtils;

public class RegisterCompanyView extends AppCompatActivity
        implements
        RegisterCompanyStep1View.OnItemSelectedListener,
        RegisterCompanyStep2NameView.OnItemSelectedListener,
        RegisterCompanyStep3EmailView.OnItemSelectedListener,
        RegisterCompanyStep4DocumentView.OnItemSelectedListener,
        RegisterCompanyStep5CategoryView.OnItemSelectedListener,
        RegisterCompanyStep6FinishView.OnItemSelectedListener{

    private Fragment mCurFragment;

    public static final String USER = "RegisterCompanyView.user";

    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        UserTO user = new UserTO();
//        user.setId(1);
//        user.setName("Ederson Ladeira da Silva");
//        user.setEmail("atybmx@gmail.com");
//
//        CountryTO cr = new CountryTO();
//        cr.setId("BR");
//
//        CategoryTO cat = new CategoryTO();
//        cat.setId(1);
//        cat.setName("Bebidas");
//        cr.setCategoryB2b(cat);
//        user.setCountry(cr);
//
//        MediaTO photo = new MediaTO();
//        photo.setUrl("https://media-gru1-1.cdn.whatsapp.net/v/t61.24694-24/225732854_1222738701958359_8302513817818056741_n.jpg?stp=dst-jpg_s96x96&ccb=11-4&oh=01_Q5AaIFre5tMIBcb3sWFz3aak3acKxfBm6PfEvbpkZKRnQi_-&oe=6673EC67&_nc_sid=e6ed6c&_nc_cat=106");
//        user.setPhotoProfile(photo);
//
//        UserTokenTO token = new UserTokenTO();
//        token.setIdToken("17bd1df49b2951fb434fd30fc01659d2");
//        token.setAccessToken("de293d9f1e4928fa9952d2a7c3394311");
//
//        UserUtils.saveUserData(this, user, token);
//        UserUtils.saveCountryData(this, cr);

        if(savedInstanceState != null) {
            mCurFragment = getSupportFragmentManager().getFragment(savedInstanceState, "RegisterCompanyView.curFragment");
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
            mCurFragment = RegisterCompanyStep1View.newInstance();

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
            getSupportFragmentManager().putFragment(outState, "RegisterCompanyView.curFragment",
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
        mCurFragment = RegisterCompanyStep2NameView.newInstance(UserUtils.newCompany(this));
        nextFragment();
    }

    @Override
    public void onStep2NameNext(UserTO user) {
        if(user != null && TextUtils.isEmpty(user.getEmail()) && !TextUtils.isEmpty(mEmail))
            user.setEmail(mEmail);

        mCurFragment = RegisterCompanyStep3EmailView.newInstance(user);
        nextFragment();
    }

    @Override
    public void onStep2NameBack() {
//        mCurFragment = RegisterStep1View.newInstance();
        backFragment();
    }

    @Override
    public void onStep3EmailNext(UserTO user) {
        mCurFragment = RegisterCompanyStep4DocumentView.newInstance(user);
        nextFragment();
    }

    @Override
    public void onStep3EmailBack(UserTO user) {
//        mCurFragment = RegisterStep2View.newInstance();
        backFragment();
    }

    @Override
    public void onStep4PhoneNext(UserTO user) {
        mCurFragment = RegisterCompanyStep5CategoryView.newInstance(user);
        nextFragment();
    }

    @Override
    public void onStep4PhoneBack(UserTO user) {
//        mCurFragment = RegisterStep3View.newInstance(user);
        backFragment();
    }

    @Override
    public void onStep5CategoryNext(UserTO user) {
        mCurFragment = RegisterCompanyStep6FinishView.newInstance();
        nextFragment();
    }

    @Override
    public void onStep5CategoryBack(UserTO user) {
//        mCurFragment = RegisterStepCrView.newInstance(user);
        backFragment();
    }

    @Override
    public void onStep6FinishNext() {
        BraveActivity.addEvent(this, "register_cp_success");

        //login to company
        TabUtils.loginToCompany(getBaseContext());

//        Intent resultIntent = new Intent();
//        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }
}
