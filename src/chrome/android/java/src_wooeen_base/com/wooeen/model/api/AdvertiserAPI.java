package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.AdvertiserDetailTO;
import com.wooeen.model.to.AdvertiserTO;
import com.wooeen.model.to.CouponTO;

import java.util.ArrayList;
import java.util.List;

public class AdvertiserAPI {

    private String country;

    public AdvertiserAPI(String country) {
        this.country = country;
    }

    public List<AdvertiserTO> get(){
        return get(0,0);
    }

    public List<AdvertiserTO> get(int pg,int qtdPerPage){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("advertiser")
                    .appendPath("get")
                    .appendQueryParameter("cr", country)
                    .appendQueryParameter("st","1")
                    .appendQueryParameter("pg",""+pg)
                    .appendQueryParameter("qpp",""+qtdPerPage);

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString(),
                            new Header("app_id",WoeDAOUtils.APP_ID),
                            new Header("app_token",WoeDAOUtils.APP_TOKEN));

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<List<AdvertiserTO>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<List<AdvertiserTO>>>(){}.getType());
                if(result == null || !result.getResult()) {
                    return null;
                }

                return result.getCallback();
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public AdvertiserDetailResponse detail(int advertiser){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("advertiser")
                    .appendPath("detail")
                    .appendPath(""+advertiser);

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString(),
                            new Header("app_id",WoeDAOUtils.APP_ID),
                            new Header("app_token",WoeDAOUtils.APP_TOKEN));
            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<AdvertiserDetailResponse> result = gson.fromJson(json, new TypeToken<ApiCallReturn<AdvertiserDetailResponse>>(){}.getType());
                if(result == null || !result.getResult()) {
                    return null;
                }

                return result.getCallback();
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static final class AdvertiserDetailResponse{
        private AdvertiserDetailTO advertiser;
        private List<CouponTO> coupons = new ArrayList<CouponTO>();

        public AdvertiserDetailTO getAdvertiser() {
            return advertiser;
        }

        public void setAdvertiser(AdvertiserDetailTO advertiser) {
            this.advertiser = advertiser;
        }

        public List<CouponTO> getCoupons() {
            return coupons;
        }

        public void setCoupons(List<CouponTO> coupons) {
            this.coupons = coupons;
        }
    }
}
