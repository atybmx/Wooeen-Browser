package com.wooeen.view.advertiser;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.wooeen.model.to.AdvertiserTO;
import com.wooeen.utils.TextUtils;

import org.chromium.chrome.R;

import java.util.List;

public class AdvertiserAdapter extends
        RecyclerView.Adapter<AdvertiserAdapter.MyViewHolder> {

    private List<AdvertiserTO> advertiserList;

    /**
     * View holder class
     * */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout advertiserImageBox;
        public ImageView advertiserImage;

        public MyViewHolder(View view) {
            super(view);
            advertiserImageBox = (RelativeLayout) view.findViewById(R.id.advertiser_image_box);
            advertiserImage = (ImageView) view.findViewById(R.id.advertiser_image);
        }
    }

    public AdvertiserAdapter() {
    }

    public AdvertiserAdapter(List<AdvertiserTO> advertiserList) {
        this.advertiserList = advertiserList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AdvertiserTO c = advertiserList.get(position);
        GradientDrawable imageDrawable = (GradientDrawable)holder.advertiserImageBox.getBackground();
        if(!TextUtils.isEmpty(c.getColor()))
            imageDrawable.setColor(Color.parseColor(c.getColor()));
        if(!TextUtils.isEmpty(c.getLogo()))
            Picasso.with(holder.itemView.getContext()).load(c.getLogo()).into(holder.advertiserImage);
    }

    @Override
    public int getItemCount() {
        if(advertiserList == null)
            return 0;

        return advertiserList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.advertiser_item,parent, false);
        return new MyViewHolder(v);
    }

    public void setAdvertiserList(List<AdvertiserTO> advertiserList) {
        this.advertiserList = advertiserList;
    }

    public List<AdvertiserTO> getAdvertiserList() {
        return advertiserList;
    }
}