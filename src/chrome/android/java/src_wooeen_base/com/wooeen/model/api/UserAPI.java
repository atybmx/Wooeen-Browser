package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.TrackingTO;
import com.wooeen.model.to.UserAuthTO;
import com.wooeen.model.to.UserQuickAccessTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.utils.NumberUtils;
import com.wooeen.utils.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAPI {

    private UserTokenTO token;

    public UserAPI(){

    }

    public UserAPI(UserTokenTO token){
        this.token = token;
    }

    public UserTO newUser(UserTO user){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("user")
                    .appendPath("new");

            Gson gsonBuilder = WoeDAOUtils.getGson();

            String[] response = new WebServiceClient()
                    .post(
                            builder.build().toString(),
                            gsonBuilder.toJson(user));

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<UserTO> result = gson.fromJson(json, new TypeToken<ApiCallReturn<UserTO>>(){}.getType());
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

    public int validEmail(String email){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("user")
                    .appendPath("valid")
                    .appendPath("email");

            builder.appendQueryParameter("email",email);

            String[] response = new WebServiceClient()
                    .get(builder.build().toString());

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<Map<String,Object>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<Map<String,Object>>>(){}.getType());
                if(result == null || !result.getResult()) {
                    return -1;
                }

                return NumberUtils.getInteger(""+(Double) result.getCallback().get("id"));
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return -1;
    }

    public UserTokenTO login(String email,String pass){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("user")
                    .appendPath("login");

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("email", email);
            pars.put("pass", pass);

            Gson gsonBuilder = WoeDAOUtils.getGson();

            String[] response = new WebServiceClient()
                    .post(
                            builder.build().toString(),
                            gsonBuilder.toJson(pars));

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<UserTokenTO> result = gson.fromJson(json, new TypeToken<ApiCallReturn<UserTokenTO>>(){}.getType());
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

    public boolean recoverPass(String email){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("user")
                    .appendPath("recover-password");

            builder.appendQueryParameter("email",email);

            String[] response = new WebServiceClient()
                    .get(builder.build().toString());

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<Map<String,Object>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<Map<String,Object>>>(){}.getType());
                if(result == null) {
                    return false;
                }

                return result.getResult();
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    public UserAuthTO auth(String email,int count){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("user")
                    .appendPath("auth");

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("email", email);
            pars.put("count", count);

            Gson gsonBuilder = WoeDAOUtils.getGson();

            String[] response = new WebServiceClient()
                    .post(
                            builder.build().toString(),
                            gsonBuilder.toJson(pars));

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<UserAuthTO> result = gson.fromJson(json, new TypeToken<ApiCallReturn<UserAuthTO>>(){}.getType());
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

    public UserTokenTO authValid(String validator,List<Integer> ids){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("user")
                    .appendPath("auth")
                    .appendPath("valid");

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("validator", validator);
            pars.put("ids", ids);

            Gson gsonBuilder = WoeDAOUtils.getGson();

            String[] response = new WebServiceClient()
                    .post(
                            builder.build().toString(),
                            gsonBuilder.toJson(pars));

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<UserTokenTO> result = gson.fromJson(json, new TypeToken<ApiCallReturn<UserTokenTO>>(){}.getType());
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

    public UserQuickAccessTO quickAccessNew(){
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
                    .appendPath("user")
                    .appendPath("quick-access")
                    .appendPath("new");

            Map<String,Object> pars = new HashMap<String,Object>();

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

                ApiCallReturn<UserQuickAccessTO> result = gson.fromJson(json, new TypeToken<ApiCallReturn<UserQuickAccessTO>>(){}.getType());
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

    public boolean setFcmToken(String fcmToken){
        if(token == null ||
                TextUtils.isEmpty(token.getIdToken()) ||
                TextUtils.isEmpty(token.getAccessToken()))
            return false;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("user")
                    .appendPath("set")
                    .appendPath("fcm-token");

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("fcmToken", fcmToken);

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

                return true;
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    public boolean setRecTerms(UserTO user){
        if(token == null ||
                TextUtils.isEmpty(token.getIdToken()) ||
                TextUtils.isEmpty(token.getAccessToken()))
            return false;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("user")
                    .appendPath("set")
                    .appendPath("rec-terms");

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("recTermsIp",user.getRecTermsIp());

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

                return true;
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    public UserTO get(){
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
                    .appendPath("user")
                    .appendPath("get");

            Gson gsonBuilder = WoeDAOUtils.getGson();

            String[] response = new WebServiceClient()
                    .get(
                            builder.build().toString(),
                            new Header("uti",token.getIdToken()),
                            new Header("uta",token.getAccessToken()));

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<UserTO> result = gson.fromJson(json, new TypeToken<ApiCallReturn<UserTO>>(){}.getType());
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
