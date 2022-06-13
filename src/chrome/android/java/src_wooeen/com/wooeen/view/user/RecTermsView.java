package com.wooeen.view.user;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.wooeen.model.api.UserAPI;
import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.utils.DeviceUtils;
import com.wooeen.utils.UserUtils;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.ntp.BraveNewTabPageLayout;

public class RecTermsView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        setContentView(R.layout.activity_rec_terms);

        Button btnYes = findViewById(R.id.btn_rec_terms_yes);
        btnYes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int userId = UserUtils.getUserId(getBaseContext());
                if(userId > 0){
                    UserUtils.saveUserRecTerms(getBaseContext());
                }

                finish();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name)+" - "+getString(R.string.woe_slogan));
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.woe_rec_share_title));
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.woe_rec_share_text)+
                                "\n\n"+
                                String.format(BraveNewTabPageLayout.WOOEEN_RECOMMENDATION_URL,""+userId));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
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