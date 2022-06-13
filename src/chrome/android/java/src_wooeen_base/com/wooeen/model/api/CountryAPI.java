package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.CountryTO;
import com.wooeen.utils.TextUtils;

import java.util.List;

public class CountryAPI {

    public List<CountryTO> get(){
        return get(0,0);
    }

    public List<CountryTO> get(int pg,int qtdPerPage){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("country")
                    .appendPath("get")
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

                ApiCallReturn<List<CountryTO>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<List<CountryTO>>>(){}.getType());
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

    public CountryTO get(String id){
        if(TextUtils.isEmpty(id))
           return null;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("country")
                    .appendPath("get")
                    .appendQueryParameter("id", id);

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString(),
                            new Header("app_id",WoeDAOUtils.APP_ID),
                            new Header("app_token",WoeDAOUtils.APP_TOKEN));

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<List<CountryTO>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<List<CountryTO>>>(){}.getType());
                if(result == null || !result.getResult() || result.getCallback() == null || result.getCallback().isEmpty()) {
                    return null;
                }

                return result.getCallback().get(0);
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }
}
