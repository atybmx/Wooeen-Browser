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

public class WoeButtonPrimary extends AppCompatButton {
    public WoeButtonPrimary(@NonNull Context context) {
        super(context);

        setDefault(context);
    }

    public WoeButtonPrimary(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);

        setDefault(context);
    }

    public WoeButtonPrimary(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setDefault(context);
    }

    private void setDefault(Context context){
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_btn_primary));
        } else {
            setBackground(ContextCompat.getDrawable(context, R.drawable.woe_btn_primary));
        }

        setAllCaps(true);
        setTypeface(ResourcesCompat.getFont(context, R.font.montserrat_bold));
    }
}