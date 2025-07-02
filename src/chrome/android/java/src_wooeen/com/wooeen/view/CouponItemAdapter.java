package com.wooeen.view;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wooeen.model.to.CouponTO;
import com.wooeen.utils.TextUtils;

import org.chromium.chrome.R;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CouponItemAdapter extends RecyclerView.Adapter<CouponItemAdapter.ChildViewHolder> {

    private List<CouponTO> coupons;
    private Context context;
    private CountDownTimer timer;

    private final String daySingle;
    private final String hourSingle;
    private final String minuteSingle;
    private final String secondSingle;

    private boolean horizontal;

    // Constructor
    public CouponItemAdapter(Context context, List<CouponTO> coupons, boolean horizontal){
        this.context = context;
        this.coupons = coupons;
        this.horizontal = horizontal;

        this.daySingle = context.getString(R.string.woe_day_single);
        this.hourSingle = context.getString(R.string.woe_hour_single);
        this.minuteSingle = context.getString(R.string.woe_minute_single);
        this.secondSingle = context.getString(R.string.woe_second_single);
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pub_coupon_item, viewGroup, false);

        return new ChildViewHolder(view);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ChildViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder childViewHolder, int position){
        CouponTO coupon = coupons.get(position);

        if (childViewHolder.couponTitle != null)
            childViewHolder.couponTitle.setText(coupon.getTitle());

        if (childViewHolder.couponVoucher != null) {
            if(!TextUtils.isEmpty(coupon.getVoucher()))
                childViewHolder.couponVoucher.setText(coupon.getVoucher());
            else if(!TextUtils.isEmpty(coupon.getUrl()))
                childViewHolder.couponVoucher.setText(context.getString(R.string.woe_coupon_active));
        }

        if(childViewHolder.couponAction != null){
            if(!TextUtils.isEmpty(coupon.getVoucher())){
                childViewHolder.couponAction.setImageResource(R.drawable.woe_copy_white);
            }
        }
        childViewHolder.couponAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(coupon.getUrl())){

                }

                if(!TextUtils.isEmpty(coupon.getVoucher())){
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Wooeen Coupon Code", coupon.getVoucher());
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(context, R.string.woe_coupon_copied ,Toast.LENGTH_LONG).show();
                }
            }
        });

        //verify if show the expiration
        boolean showExpiration = false;
        if(coupon.getDateExpiration() != null) {
            Date eventDate = coupon.getDateExpiration();
            Date currentDate = new Date();
            if (!currentDate.after(eventDate)) {
                long diff = eventDate.getTime() - currentDate.getTime();
                long days = diff / (24 * 60 * 60 * 1000);
                if(days <= 0)
                    showExpiration = true;
            }
        }

        if(!showExpiration){
            childViewHolder.couponStopwatchContainer.setVisibility(View.GONE);
        }else{
            if(timer != null) {
                timer.cancel();
                timer = null;
            }

            timer = new CountDownTimer(coupon.getDateExpiration().getTime(), 1000) {

                public void onTick(long millisUntilFinished) {
                    Date eventDate = coupon.getDateExpiration();
                    Date currentDate = new Date();
                    if (!currentDate.after(eventDate)) {
                        long diff = eventDate.getTime() - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000) % 24;
                        long minutes = diff / (60 * 1000) % 60;
                        long seconds = diff / 1000 % 60;

                        childViewHolder.couponStopwatcher.setText(
                                (days > 0 ? days + daySingle +" : " : "")+
                                        (hours > 0 ? String.format(
                                                Locale.US, "%02d", hours) + hourSingle + " : " : "")+
                                        (minutes > 0 ? String.format(Locale.ROOT, "%02d", minutes) + minuteSingle +" : " : "")+
                                        (seconds > 0 ? String.format(Locale.US, "%02d", seconds) + secondSingle : "")
                        );
                    } else {
                        childViewHolder.couponStopwatcher.setText(context.getString(R.string.woe_coupon_expired));
                    }
                }

                public void onFinish() {
                    childViewHolder.couponStopwatcher.setText(context.getString(R.string.woe_coupon_expired));
                }

            }.start();
        }

        if(TextUtils.isEmpty(coupon.getDescription())){
            childViewHolder.couponRules.setVisibility(View.GONE);
        }else{
            childViewHolder.couponRules.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.dialog_layout, null);
                    alertDialogBuilder.setView(dialogView);

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView dialogTitle = dialogView.findViewById(R.id.woe_dialog_panel_title);
                    dialogTitle.setText(context.getString(R.string.woe_coupon_see_rules));

                    TextView dialogText = dialogView.findViewById(R.id.woe_dialog_panel_text);
                    dialogText.setText(coupon.getDescription());

                    Button dialogButton = dialogView.findViewById(R.id.woe_dialog_panel_btn);
                    dialogButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return coupons.size();
    }

    // This class is to initialize
    // the Views present
    // in the child RecyclerView
    class ChildViewHolder extends RecyclerView.ViewHolder {

        LinearLayout couponContent;
        TextView couponTitle;
        TextView couponVoucher;
        ImageButton couponAction;
        LinearLayout couponStopwatchContainer;
        TextView couponStopwatcher;
        Button couponRules;

        ChildViewHolder(View itemView){
            super(itemView);

            couponContent = itemView.findViewById(R.id.coupon_content);
            if(!horizontal){
                couponContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            couponTitle = itemView.findViewById(R.id.coupon_title);
            couponVoucher = itemView.findViewById(R.id.coupon_voucher);
            couponAction = itemView.findViewById(R.id.coupon_action);
            couponStopwatchContainer = itemView.findViewById(R.id.coupon_stopwatch_container);
            couponStopwatcher = itemView.findViewById(R.id.coupon_stopwatch);
            couponRules = itemView.findViewById(R.id.coupon_rules);
        }
    }

    public void setCoupons(List<CouponTO> coupons) {
        this.coupons = coupons;
    }
}
