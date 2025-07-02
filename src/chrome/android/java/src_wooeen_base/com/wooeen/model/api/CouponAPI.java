package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.CouponTO;
import com.wooeen.utils.TextUtils;

import java.util.List;

public class CouponAPI {

    private String country;

    public CouponAPI(String country) {
        this.country = country;
    }

    public List<CouponTO> get(){
        return get(0,0);
    }

    public List<CouponTO> get(int pg,int qtdPerPage){
        return get(pg, qtdPerPage, null);
    }

    public List<CouponTO> get(int pg,int qtdPerPage,String q){
        return get(pg, qtdPerPage, 0, q);
    }

    public List<CouponTO> get(int pg,int qtdPerPage,int advertiser,String q){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("coupon")
                    .appendPath("get")
                    .appendQueryParameter("cr", country)
                    .appendQueryParameter("st","1")
                    .appendQueryParameter("pg",""+pg)
                    .appendQueryParameter("qpp",""+qtdPerPage);

            if(!TextUtils.isEmpty(advertiser))
                builder.appendQueryParameter("ad",""+advertiser);

            if(!TextUtils.isEmpty(q))
                builder.appendQueryParameter("q",q);

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString(),
                            new Header("app_id",WoeDAOUtils.APP_ID),
                            new Header("app_token",WoeDAOUtils.APP_TOKEN));

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<List<CouponTO>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<List<CouponTO>>>(){}.getType());
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

}
