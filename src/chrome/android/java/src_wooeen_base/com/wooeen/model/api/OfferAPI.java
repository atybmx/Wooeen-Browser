package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.OfferTO;
import com.wooeen.utils.TextUtils;

import java.util.List;

public class OfferAPI {

    private String country;

    public OfferAPI(String country) {
        this.country = country;
    }

    public List<OfferTO> get(){
        return get(0,0);
    }

    public List<OfferTO> get(int pg,int qtdPerPage){
        return get(pg, qtdPerPage, null);
    }

    public List<OfferTO> get(int pg,int qtdPerPage,String q){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("offer")
                    .appendPath("get")
                    .appendQueryParameter("cr", country)
                    .appendQueryParameter("st","1")
                    .appendQueryParameter("pg",""+pg)
                    .appendQueryParameter("qpp",""+qtdPerPage);

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

                ApiCallReturn<List<OfferTO>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<List<OfferTO>>>(){}.getType());
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
