package com.wooeen.view.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import org.chromium.chrome.R;

public class WoeButtonTransparent extends AppCompatButton {
    public WoeButtonTransparent(@NonNull Context context) {
        super(context);

        setDefault(context);
    }

    public WoeButtonTransparent(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);

        setDefault(context);
    }

    public WoeButtonTransparent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setDefault(context);
    }

    private void setDefault(Context context){
        setBackgroundColor(ContextCompat.getColor(context, R.color.woe_transparent));
        setTypeface(ResourcesCompat.getFont(context, R.font.montserrat_bold));
    }
}