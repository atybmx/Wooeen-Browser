package com.wooeen.view.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.BraveDialogFragment;

public class WoeUpdateDialog extends BraveDialogFragment implements View.OnClickListener {
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.woe_dialog_update, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button mEnableButton = view.findViewById(R.id.btn_update);
        mEnableButton.setOnClickListener(this);

        ImageView btnClose = view.findViewById(R.id.modal_close);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_update) {
            final String appPackageName = getContext().getPackageName();
            try {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
        dismiss();
    }
}