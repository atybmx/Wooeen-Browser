package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.AdvertiserTO;
import com.wooeen.model.to.VersionTO;
import com.wooeen.utils.NumberUtils;

import java.util.List;

public class VersionAPI {

    public VersionTO getVersionScript(){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("version")
                    .appendPath("script");

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString());

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<VersionTO> result = gson.fromJson(json, new TypeToken<ApiCallReturn<VersionTO>>(){}.getType());
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

    public int getVersionProduct(){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("version")
                    .appendPath("script/product");

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString());

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<String> result = gson.fromJson(json, new TypeToken<ApiCallReturn<String>>(){}.getType());
                if(result == null || !result.getResult()) {
                    return 0;
                }

                return NumberUtils.getInteger(result.getCallback());
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return 0;
    }

    public int getVersionQuery(){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("version")
                    .appendPath("script/query");

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString());

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<String> result = gson.fromJson(json, new TypeToken<ApiCallReturn<String>>(){}.getType());
                if(result == null || !result.getResult()) {
                    return 0;
                }

                return NumberUtils.getInteger(result.getCallback());
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return 0;
    }
}
