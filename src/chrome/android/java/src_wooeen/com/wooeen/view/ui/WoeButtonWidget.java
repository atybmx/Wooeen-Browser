package com.wooeen.view.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import org.chromium.chrome.R;

public class WoeButtonWidget extends AppCompatButton {
    public WoeButtonWidget(@NonNull Context context) {
        super(context);

        setDefault(context);
    }

    public WoeButtonWidget(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);

        setDefault(context);
    }

    public WoeButtonWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setDefault(context);
    }

    private void setDefault(Context context){
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_btn_widget));
        } else {
            setBackground(ContextCompat.getDrawable(context, R.drawable.woe_btn_widget));
        }

        setAllCaps(true);
        setTypeface(ResourcesCompat.getFont(context, R.font.poppins_bold));
    }
}