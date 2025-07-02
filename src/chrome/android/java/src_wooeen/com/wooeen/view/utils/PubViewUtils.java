package com.wooeen.view.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wooeen.model.api.PubAPI;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.NavigationTO;
import com.wooeen.model.to.PubTO;
import com.wooeen.model.to.TrackingTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.utils.NumberUtils;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.TrackingUtils;
import com.wooeen.utils.UserUtils;
import com.wooeen.view.ui.RoundedCornersTransformation;

import org.json.JSONException;
import org.json.JSONObject;

import org.chromium.base.task.AsyncTask;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.ntp.BraveNtpAdapter;
import org.chromium.chrome.browser.util.TabUtils;

public class PubViewUtils {

    private static final String WOE_WIDGET_TAG_START = "<woe_widget>";
    private static final String WOE_WIDGET_TAG_END = "</woe_widget>";
    private static final int WOE_WIDGET_START_CHARS = 12;
    private static final int WOE_WIDGET_END_CHARS = 13;

    public static void toFront(Context context, CountryTO country, UserTO user, UserTokenTO token, PubTO pub, BraveNtpAdapter.PubViewHolder holder) {
        if(TextUtils.isEmpty(pub.getContent()))
            return;

        holder.text.setVisibility(View.GONE);
        holder.link.setVisibility(View.GONE);
        holder.offer.setVisibility(View.GONE);

        String contentTemp = pub.getContent();
        if(!contentTemp.contains(WOE_WIDGET_TAG_START) || !contentTemp.contains(WOE_WIDGET_TAG_END)){
            if(!TextUtils.isEmpty(contentTemp)) {
                holder.text.setText(contentTemp);
                holder.text.setVisibility(View.VISIBLE);
            }
        }

        while(contentTemp.contains(WOE_WIDGET_TAG_START) && contentTemp.contains(WOE_WIDGET_TAG_END)) {
            String text = contentTemp.substring(0, contentTemp.indexOf(WOE_WIDGET_TAG_START));
            if(!TextUtils.isEmpty(text)) {
                holder.text.setText(text);
                holder.text.setVisibility(View.VISIBLE);
            }

            //get next json
            String json = contentTemp.substring(contentTemp.indexOf(WOE_WIDGET_TAG_START) + WOE_WIDGET_START_CHARS, contentTemp.indexOf(WOE_WIDGET_TAG_END));

            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(json);

                if(jsonObj != null) {
                    if(jsonObj.has("type") && jsonObj.get("type") instanceof String && !TextUtils.isEmpty(jsonObj.getString("type"))) {
                        String type = jsonObj.getString("type");
                        if("link".equalsIgnoreCase(type)){
                            if(jsonObj.has("ad") && jsonObj.get("ad") instanceof Integer && jsonObj.getInt("ad") > 0 &&
                                    jsonObj.has("link") && jsonObj.get("link") instanceof String && !TextUtils.isEmpty(jsonObj.getString("link"))){
                                int ad = jsonObj.getInt("ad");
                                String link = jsonObj.getString("link");

                                if (jsonObj.has("title") && jsonObj.get(
                                        "title") instanceof String && !TextUtils.isEmpty(
                                        jsonObj.getString("title"))) {
                                    holder.linkTitle.setText(jsonObj.getString("title"));
                                    holder.linkTitle.setVisibility(View.VISIBLE);
                                } else if (jsonObj.has("stitle") && jsonObj.get("stitle") instanceof String
                                        && !TextUtils.isEmpty(jsonObj.getString("stitle"))) {
                                    holder.linkTitle.setText(jsonObj.getString("stitle"));
                                    holder.linkTitle.setVisibility(View.VISIBLE);
                                } else {
                                    holder.linkTitle.setVisibility(View.GONE);
                                }

                                if (jsonObj.has("desc") && jsonObj.get("desc") instanceof String
                                        && !TextUtils.isEmpty(jsonObj.getString("desc"))) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        holder.linkDesc.setText(
                                                Html.fromHtml(jsonObj.getString("desc"), Html.FROM_HTML_MODE_COMPACT).toString());
                                    } else {
                                        holder.linkDesc.setText(Html.fromHtml(jsonObj.getString("desc")).toString());
                                    }

                                    holder.linkDesc.setVisibility(View.VISIBLE);
                                } else if (jsonObj.has("sdesc") && jsonObj.get(
                                        "sdesc") instanceof String && !TextUtils.isEmpty(
                                        jsonObj.getString("sdesc"))) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        holder.linkDesc.setText(
                                                Html.fromHtml(jsonObj.getString("sdesc"), Html.FROM_HTML_MODE_COMPACT).toString());
                                    } else {
                                        holder.linkDesc.setText(Html.fromHtml(jsonObj.getString("sdesc")).toString());
                                    }

                                    holder.linkDesc.setVisibility(View.VISIBLE);
                                } else {
                                    holder.linkDesc.setVisibility(View.GONE);
                                }

                                if (jsonObj.has("image") && jsonObj.get("image") instanceof String
                                        && !TextUtils.isEmpty(jsonObj.getString("image"))) {
                                    String image = jsonObj.getString("image");
                                    if (image.startsWith("//"))
                                        image = "https:" + image;
                                    if(TextUtils.isUrlValid(image)) {
                                        Picasso.with(context).load(image).fit().centerCrop().transform(new RoundedCornersTransformation(60, 0)).into(
                                                holder.linkImageMedia);
                                        holder.linkImage.setVisibility(View.VISIBLE);
                                    }else{
                                        holder.linkImage.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    holder.linkImage.setVisibility(View.INVISIBLE);
                                }

                                holder.link.setVisibility(View.VISIBLE);
                                holder.link.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (ad > 0 && pub.getAdvertisers() != null) {
                                            for (PubTO.PubAdvertiserTOA advertiser : pub.getAdvertisers()) {
                                                if (advertiser.getId() == ad) {
                                                    UserUtils.saveUserTrkSocial(context);

                                                    String linkTracked =
                                                            TrackingUtils.parseTrackingLink(
                                                                    advertiser.getDeeplink(),
                                                                    advertiser.getParams(),
                                                                    link,
                                                                    user.getId(),
                                                                    TrackingUtils.SOURCE_SOCIAL,
                                                                    pub.getUser().getId());
                                                    TabUtils.openUrlInSameTab(linkTracked);

                                                    //send to dao
                                                    new PubClickLoader(context, token, pub).executeOnExecutor(
                                                            AsyncTask.THREAD_POOL_EXECUTOR);

                                                    break;
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }else if("offer".equalsIgnoreCase(type)){
                            /*
                             * WIDGET TYPE OFFER
                             */
                            if(jsonObj.has("ad") && jsonObj.get("ad") instanceof Integer && jsonObj.getInt("ad") > 0 &&
                                    jsonObj.has("link") && jsonObj.get("link") instanceof String && !TextUtils.isEmpty(jsonObj.getString("link"))){
                                int ad = jsonObj.getInt("ad");
                                String link = jsonObj.getString("link");

                                if (jsonObj.has("title") && jsonObj.get("title") instanceof String
                                        && !TextUtils.isEmpty(jsonObj.getString("title"))) {
                                    holder.offerTitle.setText(jsonObj.getString("title"));
                                    holder.offerTitle.setVisibility(View.VISIBLE);
                                } else if (jsonObj.has("stitle") && jsonObj.get(
                                        "stitle") instanceof String && !TextUtils.isEmpty(
                                        jsonObj.getString("stitle"))) {
                                    holder.offerTitle.setText(jsonObj.getString("stitle"));
                                    holder.offerTitle.setVisibility(View.VISIBLE);
                                } else {
                                    holder.offerTitle.setVisibility(View.GONE);
                                }

                                if (jsonObj.has("desc") && jsonObj.get("desc") instanceof String
                                        && !TextUtils.isEmpty(jsonObj.getString("desc"))) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        holder.offerDesc.setText(
                                                Html.fromHtml(jsonObj.getString("desc"), Html.FROM_HTML_MODE_COMPACT).toString());
                                    } else {
                                        holder.offerDesc.setText(Html.fromHtml(jsonObj.getString("desc")).toString());
                                    }

                                    holder.offerDesc.setVisibility(View.VISIBLE);
                                } else if (jsonObj.has("sdesc") && jsonObj.get(
                                        "sdesc") instanceof String && !TextUtils.isEmpty(
                                        jsonObj.getString("sdesc"))) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        holder.offerDesc.setText(
                                                Html.fromHtml(jsonObj.getString("sdesc"), Html.FROM_HTML_MODE_COMPACT).toString());
                                    } else {
                                        holder.offerDesc.setText(Html.fromHtml(jsonObj.getString("sdesc")).toString());
                                    }

                                    holder.offerDesc.setVisibility(View.VISIBLE);
                                } else {
                                    holder.offerDesc.setVisibility(View.GONE);
                                }

                                if (jsonObj.has("image") && jsonObj.get("image") instanceof String
                                        && !TextUtils.isEmpty(jsonObj.getString("image"))) {
                                    String image = jsonObj.getString("image");
                                    if (image.startsWith("//"))
                                        image = "https:" + image;
                                    if(TextUtils.isUrlValid(image)) {
                                        Picasso.with(context).load(image).fit().centerCrop().transform(new RoundedCornersTransformation(60, 0)).into(
                                                holder.offerImageMedia);
                                        holder.offerImageMedia.setVisibility(View.VISIBLE);
                                    }else{
                                        holder.offerImageMedia.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    holder.offerImageMedia.setVisibility(View.INVISIBLE);
                                }

                                double amount = 0;
                                if (jsonObj.has("amount")) {
                                    if(jsonObj.get("amount") instanceof String && !TextUtils.isEmpty(jsonObj.getString("amount"))) {
                                        amount = NumberUtils.getDouble(
                                                jsonObj.getString("amount"));
                                    }else if(jsonObj.get("amount") instanceof Double && jsonObj.getDouble("amount") > 0) {
                                        amount = jsonObj.getDouble("amount");
                                    }

                                    if (amount > 0) {
                                        holder.offerPrice.setText(NumberUtils.realToString(
                                                country.getCurrency().getId(), country.getId(),
                                                user.getLanguage(), amount));
                                        holder.offerPrice.setVisibility(View.VISIBLE);

                                        /*
                                        * Verify price old and discount
                                         */
                                        if (jsonObj.has("amountOld")) {
                                            double amountOld = 0;
                                            if(jsonObj.get("amountOld") instanceof String && !TextUtils.isEmpty(jsonObj.getString("amountOld"))) {
                                                amountOld = NumberUtils.getDouble(
                                                        jsonObj.getString("amountOld"));
                                            }else if(jsonObj.get("amountOld") instanceof Double && jsonObj.getDouble("amountOld") > 0) {
                                                amountOld = jsonObj.getDouble("amountOld");
                                            }

                                            if (amountOld > amount) {
                                                holder.offerPriceOld.setText(NumberUtils.realToString(
                                                        country.getCurrency().getId(), country.getId(),
                                                        user.getLanguage(), amountOld));
                                                holder.offerPriceOld.setVisibility(View.VISIBLE);

                                                double discount = (amount * 100 / amountOld) - 100;
                                                holder.offerPriceDiscount.setText(
                                                        NumberUtils.percentIntToString(discount));
                                                holder.offerPriceDiscount.setVisibility(View.VISIBLE);
                                            }else {
                                                holder.offerPriceOld.setVisibility(View.GONE);
                                                holder.offerPriceDiscount.setVisibility(View.GONE);
                                            }

                                        } else {
                                            holder.offerPriceOld.setVisibility(View.GONE);
                                            holder.offerPriceDiscount.setVisibility(View.GONE);
                                        }
                                    }else {
                                        holder.offerPrice.setVisibility(View.GONE);
                                        holder.offerPriceOld.setVisibility(View.GONE);
                                        holder.offerPriceDiscount.setVisibility(View.GONE);
                                    }

                                } else {
                                    holder.offerPrice.setVisibility(View.GONE);
                                    holder.offerPriceOld.setVisibility(View.GONE);
                                    holder.offerPriceDiscount.setVisibility(View.GONE);
                                }

                                if (jsonObj.has("rating")) {
                                    double rating = 0;
                                    if (jsonObj.get("rating") instanceof String
                                            && !TextUtils.isEmpty(jsonObj.getString("rating"))) {
                                        rating = NumberUtils.getDouble(
                                                jsonObj.getString("rating"));
                                    } else if (jsonObj.get("rating") instanceof Double
                                            && jsonObj.getDouble("rating") > 0) {
                                        rating = jsonObj.getDouble("rating");
                                    }

                                    if(rating > 0){
                                        holder.offerPlus.setVisibility(View.VISIBLE);
                                        holder.offerStars.setRating((float) rating);
                                    }else{
                                        holder.offerPlus.setVisibility(View.GONE);
                                    }
                                }else{
                                    holder.offerPlus.setVisibility(View.GONE);
                                }

                                if(pub.getType() == 1 &&
                                        pub.getAdvertisers() != null && !pub.getAdvertisers().isEmpty() && pub.getAdvertisers().get(0).getTracking() != null){
                                    TrackingTO trk = pub.getAdvertisers().get(0).getTracking();
                                    String commission = TrackingUtils.printCashbackValueCPA(country, trk, amount);
                                    if(commission != null){
                                        if(TrackingUtils.getToCashbackValueCPA(trk)){
                                            holder.offerPriceCashbackTo.setVisibility(View.VISIBLE);
                                        }else{
                                            holder.offerPriceCashbackTo.setVisibility(View.GONE);
                                        }

                                        holder.offerPriceCashback.setVisibility(View.VISIBLE);
                                        holder.offerPriceCashbackValue.setText(commission);
                                    }else{
                                        holder.offerPriceCashback.setVisibility(View.GONE);
                                        holder.offerPriceCashbackTo.setVisibility(View.GONE);
                                    }
                                }else{
                                    holder.offerPriceCashback.setVisibility(View.GONE);
                                    holder.offerPriceCashbackTo.setVisibility(View.GONE);
                                }

                                if(pub.getType() == 2){
                                    holder.offerSocial.setOnClickListener(new View.OnClickListener(){
                                        @Override
                                        public void onClick(View v) {
                                            TabUtils.openUrlInNewTab(false, "https://www.wooeen.com/br/wooeen-social-a-forma-mais-facil-de-colaborar-com-a-vida/");
                                        }
                                    });
                                    holder.offerSocial.setVisibility(View.VISIBLE);
                                }else{
                                    holder.offerSocial.setVisibility(View.GONE);
                                }

                                holder.offer.setVisibility(View.VISIBLE);
                                holder.offer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (ad > 0 && pub.getAdvertisers() != null) {
                                            for (PubTO.PubAdvertiserTOA advertiser : pub.getAdvertisers()) {
                                                if (advertiser.getId() == ad) {
                                                    UserUtils.saveUserTrkSocial(context);

                                                    String linkTracked =
                                                            TrackingUtils.parseTrackingLink(
                                                                    advertiser.getDeeplink(),
                                                                    advertiser.getParams(),
                                                                    link,
                                                                    user.getId(),
                                                                    TrackingUtils.SOURCE_SOCIAL,
                                                                    pub.getUser().getId());
                                                    TabUtils.openUrlInSameTab(linkTracked);

                                                    //send to dao
                                                    new PubClickLoader(context, token, pub).executeOnExecutor(
                                                            AsyncTask.THREAD_POOL_EXECUTOR);

                                                    break;
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //continue verifing
            contentTemp = contentTemp.substring(contentTemp.indexOf(WOE_WIDGET_TAG_END) + WOE_WIDGET_END_CHARS);

            //generate tags if end content
            if(contentTemp != null) {
                if(!contentTemp.contains(WOE_WIDGET_TAG_START) || !contentTemp.contains(WOE_WIDGET_TAG_END)) {

                }
            }
        }
    }

    private static class PubClickLoader extends AsyncTask<Boolean> {

        private Context context;
        private UserTokenTO token;
        private PubTO pub;

        public PubClickLoader(Context context, UserTokenTO token, PubTO pub){
            this.context = context;
            this.pub = pub;
        }
        @Override
        protected Boolean doInBackground() {
            PubAPI apiDAO = new PubAPI(token);
            return apiDAO.click(pub);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
        }

    }

    public static void toShare(Context context, CountryTO country, UserTO user, PubTO pub, Intent sendIntent) {
        if(TextUtils.isEmpty(pub.getContent()))
            return;

        String metaTitle = "";
        String metaDescription = "";
        String metaImage = "";

        String contentTemp = pub.getContent();
        while(contentTemp.contains(WOE_WIDGET_TAG_START) && contentTemp.contains(WOE_WIDGET_TAG_END)) {
            String text = contentTemp.substring(0, contentTemp.indexOf(WOE_WIDGET_TAG_START));
            if(text != null && !"".equals(text))
                metaTitle += text;

            //get next json
            String json = contentTemp.substring(contentTemp.indexOf(WOE_WIDGET_TAG_START) + WOE_WIDGET_START_CHARS, contentTemp.indexOf(WOE_WIDGET_TAG_END));

            try{
                JSONObject jsonObj = new JSONObject(json);
                if(jsonObj != null) {
                    if(jsonObj.has("type") && jsonObj.get("type") instanceof String) {
                        String type = jsonObj.getString("type");
                        if("link".equalsIgnoreCase(type)){
                            if(jsonObj.has("ad") && jsonObj.get("ad") instanceof Integer && jsonObj.getInt("ad") > 0 &&
                                    jsonObj.has("link") && jsonObj.get("link") instanceof String && !TextUtils.isEmpty(jsonObj.getString("link"))){

                                if (jsonObj.has("image") && jsonObj.get("image") instanceof String
                                        && !TextUtils.isEmpty(jsonObj.getString("image"))) {
                                    String image = jsonObj.getString("image");
                                    if (image.startsWith("//"))
                                        image = "https:" + image;
                                    if(TextUtils.isUrlValid(image)) {
                                        metaImage = image;
                                    }
                                }

                                if (jsonObj.has("title") && jsonObj.get(
                                        "title") instanceof String && !TextUtils.isEmpty(
                                        jsonObj.getString("title"))) {
                                    metaTitle = jsonObj.getString("title");
                                }else if (jsonObj.has("stitle") && jsonObj.get("stitle") instanceof String
                                        && !TextUtils.isEmpty(jsonObj.getString("stitle"))) {
                                    metaTitle = jsonObj.getString("stitle");
                                }

                                if (jsonObj.has("desc") && jsonObj.get(
                                        "desc") instanceof String && !TextUtils.isEmpty(
                                        jsonObj.getString("desc"))) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        metaDescription = Html.fromHtml(jsonObj.getString("desc"), Html.FROM_HTML_MODE_COMPACT).toString();
                                    } else {
                                        metaDescription = Html.fromHtml(jsonObj.getString("desc")).toString();
                                    }
                                }else if (jsonObj.has("sdesc") && jsonObj.get("sdesc") instanceof String
                                        && !TextUtils.isEmpty(jsonObj.getString("sdesc"))) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        metaDescription = Html.fromHtml(jsonObj.getString("sdesc"), Html.FROM_HTML_MODE_COMPACT).toString();
                                    } else {
                                        metaDescription = Html.fromHtml(jsonObj.getString("sdesc")).toString();
                                    }
                                }
                            }
                        }else if("offer".equalsIgnoreCase(type)){
                            if(jsonObj.has("ad") && jsonObj.get("ad") instanceof Integer && jsonObj.getInt("ad") > 0 &&
                                    jsonObj.has("link") && jsonObj.get("link") instanceof String && !TextUtils.isEmpty(jsonObj.getString("link"))){


                                if (jsonObj.has("image") && jsonObj.get("image") instanceof String
                                        && !TextUtils.isEmpty(jsonObj.getString("image"))) {
                                    String image = jsonObj.getString("image");
                                    if (image.startsWith("//"))
                                        image = "https:" + image;
                                    if(TextUtils.isUrlValid(image)) {
                                        metaImage = image;
                                    }
                                }

                                if (jsonObj.has("title") && jsonObj.get(
                                        "title") instanceof String && !TextUtils.isEmpty(
                                        jsonObj.getString("title"))) {
                                    metaTitle = jsonObj.getString("title");
                                }else if (jsonObj.has("stitle") && jsonObj.get("stitle") instanceof String
                                        && !TextUtils.isEmpty(jsonObj.getString("stitle"))) {
                                    metaTitle = jsonObj.getString("stitle");
                                }

                                if (jsonObj.has("desc") && jsonObj.get(
                                        "desc") instanceof String && !TextUtils.isEmpty(
                                        jsonObj.getString("desc"))) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        metaDescription = Html.fromHtml(jsonObj.getString("desc"), Html.FROM_HTML_MODE_COMPACT).toString();
                                    } else {
                                        metaDescription = Html.fromHtml(jsonObj.getString("desc")).toString();
                                    }
                                }else if (jsonObj.has("sdesc") && jsonObj.get("sdesc") instanceof String
                                        && !TextUtils.isEmpty(jsonObj.getString("sdesc"))) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        metaDescription = Html.fromHtml(jsonObj.getString("sdesc"), Html.FROM_HTML_MODE_COMPACT).toString();
                                    } else {
                                        metaDescription = Html.fromHtml(jsonObj.getString("sdesc")).toString();
                                    }
                                }

                                if (jsonObj.has("amount")) {
                                    double amount = 0;
                                    if(jsonObj.get("amount") instanceof String && !TextUtils.isEmpty(jsonObj.getString("amount"))) {
                                        amount = NumberUtils.getDouble(
                                                jsonObj.getString("amount"));
                                    }else if(jsonObj.get("amount") instanceof Double && jsonObj.getDouble("amount") > 0) {
                                        amount = jsonObj.getDouble("amount");
                                    }

                                    if (amount > 0) {
                                        metaTitle +=
                                                "\n "+NumberUtils.realToString(
                                                        country.getCurrency().getId(), country.getId(),
                                                        user.getLanguage(), amount);
                                    }
                                }
                            }
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //continue verifing
            contentTemp = contentTemp.substring(contentTemp.indexOf(WOE_WIDGET_TAG_END) + WOE_WIDGET_END_CHARS);
        }

        sendIntent.putExtra(Intent.EXTRA_TITLE, context.getString(R.string.app_name)+" - "+context.getString(R.string.woe_slogan));
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, metaTitle);
        sendIntent.putExtra(Intent.EXTRA_TEXT, (!TextUtils.isEmpty(metaTitle) ? metaTitle+"\n\n":"")+(!TextUtils.isEmpty(metaDescription) ? TextUtils.getLimit(metaDescription, 300)+"\n\n":"")+"https://app.wooeen.com/u/pub/"+pub.getId());
    }

    public static void RetargetingToFront(NavigationTO nav, UserTO user, CountryTO country, Context context, View item1, View item1Image, ImageView item1ImageMedia, TextView item1Title, TextView item1Amount, TextView item1AmountOld,
            RelativeLayout faviconBox, GradientDrawable faviconDrawable, ImageView faviconImage, TextView name){
        NavigationTO.NavAdvertiserTOA ad = nav.getAdvertiser();

        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ad != null && ad.getId() > 0) {
                    UserUtils.saveUserTrkRtg(context);

                    String linkTracked =
                            TrackingUtils.parseTrackingLink(
                                    ad.getDeeplink(),
                                    ad.getParams(),
                                    nav.getUrl(),
                                    user.getId(),
                                    TrackingUtils.SOURCE_RTG_ANDROID,
                                    0);
                    TabUtils.openUrlInSameTab(linkTracked);
                }
            }
        });

        if (!TextUtils.isEmpty(nav.getImage())) {
            String image = nav.getImage();
            if (image.startsWith("//"))
                image = "https:" + image;
            if(TextUtils.isUrlValid(image)) {
                Picasso.with(context).load(image).fit().centerCrop().into(
                        item1ImageMedia);
                item1Image.setVisibility(View.VISIBLE);
            }else{
                item1Image.setVisibility(View.INVISIBLE);
            }
        } else {
            item1Image.setVisibility(View.INVISIBLE);
        }

        if(!TextUtils.isEmpty(nav.getTitle())) {
            item1Title.setText(nav.getTitle());
            item1Title.setVisibility(View.VISIBLE);
        }else{
            item1Title.setText("");
            item1Title.setVisibility(View.GONE);
        }

        if(nav.getAmount() > 0){
            item1Amount.setText(NumberUtils.realToString(
                    country.getCurrency().getId(), country.getId(),
                    user.getLanguage(), nav.getAmount()));
            item1Amount.setVisibility(View.VISIBLE);

            if (nav.getAmountOld() > nav.getAmount()) {
                item1AmountOld.setText(NumberUtils.realToString(
                        country.getCurrency().getId(), country.getId(),
                        user.getLanguage(), nav.getAmountOld()));
                item1AmountOld.setVisibility(View.VISIBLE);
            }else{
                item1AmountOld.setText("");
                item1AmountOld.setVisibility(View.GONE);
            }
        }else{
            item1Amount.setText("");
            item1Amount.setVisibility(View.GONE);
            item1AmountOld.setText("");
            item1AmountOld.setVisibility(View.GONE);
        }

        if(ad != null && ad.getId() > 0){
            if(!TextUtils.isEmpty(ad.getLogo())) {
                Picasso.with(context).load(ad.getLogo()).fit().into(
                        faviconImage);
                faviconBox.setVisibility(View.VISIBLE);
            }else{
                faviconBox.setVisibility(View.INVISIBLE);
            }
            if(TextUtils.isEmpty(ad.getColor())) {
                faviconDrawable.setColor(Color.parseColor("#FFFFFF"));
                faviconBox.setClipToOutline(true);
            }else{
//                            pubViewHolder.faviconBox.setPadding(10, 10, 10, 10);
                faviconDrawable.setColor(Color.parseColor(ad.getColor()));
            }

            name.setText(ad.getName());
        }else{
            faviconBox.setVisibility(View.INVISIBLE);
            name.setVisibility(View.INVISIBLE);
        }
    }

}
