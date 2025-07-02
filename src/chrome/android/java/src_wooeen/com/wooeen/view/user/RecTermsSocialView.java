package com.wooeen.view.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.wooeen.utils.UserUtils;

import org.chromium.chrome.R;

public class RecTermsSocialView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        setContentView(R.layout.activity_rec_terms_social);

        Button btnYes = findViewById(R.id.btn_rec_terms_yes);
        btnYes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int userId = UserUtils.getUserId(getBaseContext());
                if(userId > 0){
                    UserUtils.saveUserRecTermsSocial(getBaseContext());
                }

                finish();
            }
        });

        Button btnNo = findViewById(R.id.btn_back);
        btnNo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}