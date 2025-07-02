package com.wooeen.view.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import org.chromium.chrome.R;

public class WoeButtonMain extends AppCompatButton {
    private Drawable mBackground;
    private Bitmap mIcon;
    private Paint mPaint;
    private Rect mSrcRect;
    private int mIconPaddingTop;
    private int mIconPaddingLeft;
    private int mIconSize;

    public WoeButtonMain(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public WoeButtonMain(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WoeButtonMain(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int shift = (mIconSize + mIconPaddingLeft) / 2;

        canvas.save();
        canvas.translate(shift, 0);

        super.onDraw(canvas);

        if (mIcon != null) {
            float textWidth = getPaint().measureText((String)getText());
//            int left = (int)((getWidth() / 2f) - (textWidth / 2f) - mIconSize - mIconPaddingLeft);
            int top = getHeight()/2 - mIconSize/2;

            int left = mIconPaddingLeft;
//            int top = mIconPaddingTop;

            Rect destRect = new Rect(left, top, left + mIconSize, top + mIconSize);
            canvas.drawBitmap(mIcon, mSrcRect, destRect, mPaint);

            setPadding(left + mIconSize + mIconPaddingLeft, mIconPaddingTop, mIconPaddingLeft, mIconPaddingTop);
        }

        canvas.restore();
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WoeButtonMain);

        for (int i = 0; i < array.getIndexCount(); ++i) {
            int attr = array.getIndex(i);
            if(attr == R.styleable.WoeButtonMain_iconSrc)
                mIcon = drawableToBitmap(array.getDrawable(attr));
            else if(attr == R.styleable.WoeButtonMain_iconPaddingTop)
                mIconPaddingTop = array.getDimensionPixelSize(attr, 0);
            else if(attr == R.styleable.WoeButtonMain_iconPaddingLeft)
                mIconPaddingLeft = array.getDimensionPixelSize(attr, 0);
            else if(attr == R.styleable.WoeButtonMain_iconSize)
                mIconSize = array.getDimensionPixelSize(attr, 0);
            else if(attr == R.styleable.WoeButtonMain_iconBackground)
                mBackground = array.getDrawable(attr);
        }

        array.recycle();

        //If we didn't supply an icon in the XML
        if(mIcon != null){
            mPaint = new Paint();
            mSrcRect = new Rect(0, 0, mIcon.getWidth(), mIcon.getHeight());
        }

        if(mBackground != null){
            final int sdk = Build.VERSION.SDK_INT;
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                setBackgroundDrawable(mBackground);
            } else {
                setBackground(mBackground);
            }
        }

        setAllCaps(true);
        setTypeface(ResourcesCompat.getFont(context, R.font.poppins_bold));
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}