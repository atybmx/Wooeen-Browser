package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.PubTO;
import com.wooeen.model.to.UserQuickAccessTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.model.top.PubTOP;
import com.wooeen.utils.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PubAPI {

    private UserTokenTO token;

    public PubAPI(UserTokenTO token) {
        this.token = token;
    }

    public List<PubTO> get(PubTOP search){
        if(token == null ||
                TextUtils.isEmpty(token.getIdToken()) ||
                TextUtils.isEmpty(token.getAccessToken()))
            return null;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("pub")
                    .appendPath("get")
                    .appendQueryParameter("pg",""+search.getPg())
                    .appendQueryParameter("qpp",""+search.getQtdPerPage());

            if(!TextUtils.isEmpty(search.getQ()))
                builder.appendQueryParameter("q", search.getQ());

            if(search.getB2b() > 0)
                builder.appendQueryParameter("b2b", ""+search.getB2b());

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString(),
                            new Header("uti",token.getIdToken()),
                            new Header("uta",token.getAccessToken()));

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<List<PubTO>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<List<PubTO>>>(){}.getType());
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

    public boolean like(PubTO pub){
        if(token == null ||
                TextUtils.isEmpty(token.getIdToken()) ||
                TextUtils.isEmpty(token.getAccessToken()) ||
                pub == null ||
                pub.getId() <= 0)
            return false;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("pub")
                    .appendPath("like")
                    .appendPath("add");

            Map<String,Object> pars = new HashMap<String,Object>();

            pars.put("id",pub.getId());

            Gson gsonBuilder = WoeDAOUtils.getGson();

            String[] response = new WebServiceClient()
                    .post(
                            builder.build().toString(),
                            gsonBuilder.toJson(pars),
                            new Header("uti",token.getIdToken()),
                            new Header("uta",token.getAccessToken()));

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<Object> result = gson.fromJson(json, new TypeToken<ApiCallReturn<Object>>(){}.getType());
                if(result == null || !result.getResult()) {
                    return false;
                }

                return result.getResult();
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    public boolean share(PubTO pub){
        if(token == null ||
                TextUtils.isEmpty(token.getIdToken()) ||
                TextUtils.isEmpty(token.getAccessToken()) ||
                pub == null ||
                pub.getId() <= 0)
            return false;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("pub")
                    .appendPath("share")
                    .appendPath("add");

            Map<String,Object> pars = new HashMap<String,Object>();

            pars.put("id",pub.getId());

            Gson gsonBuilder = WoeDAOUtils.getGson();

            String[] response = new WebServiceClient()
                    .post(
                            builder.build().toString(),
                            gsonBuilder.toJson(pars),
                            new Header("uti",token.getIdToken()),
                            new Header("uta",token.getAccessToken()));

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<Object> result = gson.fromJson(json, new TypeToken<ApiCallReturn<Object>>(){}.getType());
                if(result == null || !result.getResult()) {
                    return false;
                }

                return result.getResult();
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    public boolean click(PubTO pub){
        if(token == null ||
                TextUtils.isEmpty(token.getIdToken()) ||
                TextUtils.isEmpty(token.getAccessToken()) ||
                pub == null ||
                pub.getId() <= 0)
            return false;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("pub")
                    .appendPath("click")
                    .appendPath("add");

            Map<String,Object> pars = new HashMap<String,Object>();

            pars.put("id",pub.getId());

            Gson gsonBuilder = WoeDAOUtils.getGson();

            String[] response = new WebServiceClient()
                    .post(
                            builder.build().toString(),
                            gsonBuilder.toJson(pars),
                            new Header("uti",token.getIdToken()),
                            new Header("uta",token.getAccessToken()));

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<Object> result = gson.fromJson(json, new TypeToken<ApiCallReturn<Object>>(){}.getType());
                if(result == null || !result.getResult()) {
                    return false;
                }

                return result.getResult();
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    public boolean remove(PubTO pub){
        if(token == null ||
                TextUtils.isEmpty(token.getIdToken()) ||
                TextUtils.isEmpty(token.getAccessToken()) ||
                pub == null ||
                pub.getId() <= 0)
            return false;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("pub")
                    .appendPath("remove");

            Map<String,Object> pars = new HashMap<String,Object>();

            pars.put("id",pub.getId());

            Gson gsonBuilder = WoeDAOUtils.getGson();

            String[] response = new WebServiceClient()
                    .post(
                            builder.build().toString(),
                            gsonBuilder.toJson(pars),
                            new Header("uti",token.getIdToken()),
                            new Header("uta",token.getAccessToken()));

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<Object> result = gson.fromJson(json, new TypeToken<ApiCallReturn<Object>>(){}.getType());
                if(result == null || !result.getResult()) {
                    return false;
                }

                return result.getResult();
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

}
