package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.PubTO;
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
    private static final int SOURCE_DEFAULT = 25;
    private static final int LINK_DEFAULT = 130;

    public UserAPI(){

    }

    public UserAPI(UserTokenTO token){
        this.token = token;
    }

    public UserTO newUser(UserTO user,int source,int link,String dateClick){
        //source App Android
        if(source <= 0)
            source = SOURCE_DEFAULT;
        //link new user app
        if(link <= 0)
            link = LINK_DEFAULT;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("user")
                    .appendPath("new");

            Gson gsonBuilder = WoeDAOUtils.getGson();

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("name",user.getName());
            pars.put("email",user.getEmail());
            pars.put("pass",user.getPass());
            pars.put("language",user.getLanguage());
            pars.put("timezone",user.getTimezone());

            Map<String,Object> country = new HashMap<String,Object>();
            country.put("id", user.getCountry().getId());
            pars.put("country", country);

            if(source > 0)
                pars.put("source",""+source);

            if(link > 0)
                pars.put("link",""+link);

            if(dateClick != null)
                pars.put("dateClick",dateClick);

            String[] response = new WebServiceClient()
                    .post(
                            builder.build().toString(),
                            gsonBuilder.toJson(pars));

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

    public RegisterHolderAPI newUserV2(UserTO user,int source,int link,String dateClick){
        //source App Android
        if(source <= 0)
            source = SOURCE_DEFAULT;
        //link new user app
        if(link <= 0)
            link = LINK_DEFAULT;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("user")
                    .appendPath("new");

            Gson gsonBuilder = WoeDAOUtils.getGson();

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("v2","true");
            pars.put("name",user.getName());
            pars.put("email",user.getEmail());
            pars.put("pass",user.getPass());
            pars.put("language",user.getLanguage());
            pars.put("timezone",user.getTimezone());

            Map<String,Object> country = new HashMap<String,Object>();
            country.put("id", user.getCountry().getId());
            pars.put("country", country);

            if(source > 0)
                pars.put("source",""+source);

            if(link > 0)
                pars.put("link",""+link);

            if(dateClick != null)
                pars.put("dateClick",dateClick);

            String[] response = new WebServiceClient()
                    .post(
                            builder.build().toString(),
                            gsonBuilder.toJson(pars));

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<RegisterHolderAPI> result = gson.fromJson(json, new TypeToken<ApiCallReturn<RegisterHolderAPI>>(){}.getType());
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

    public static class RegisterHolderAPI{
        private UserTokenTO token;
        private UserTokenTO tokenCp;
        private UserTO user;

        public UserTokenTO getToken() {
            return token;
        }

        public void setToken(UserTokenTO token) {
            this.token = token;
        }

        public UserTO getUser() {
            return user;
        }

        public void setUser(UserTO user) {
            this.user = user;
        }

        public UserTokenTO getTokenCp() {
            return tokenCp;
        }

        public void setTokenCp(UserTokenTO tokenCp) {
            this.tokenCp = tokenCp;
        }
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

    public LoginHolderAPI login(String email,String pass){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("user")
                    .appendPath("login");

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("v2", "true");
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

                ApiCallReturn<LoginHolderAPI> result = gson.fromJson(json, new TypeToken<ApiCallReturn<LoginHolderAPI>>(){}.getType());
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

    public static class LoginHolderAPI{
        private UserTokenTO token;
        private UserTokenTO tokenCp;
        private UserTO user;

        public UserTokenTO getToken() {
            return token;
        }

        public void setToken(UserTokenTO token) {
            this.token = token;
        }

        public UserTO getUser() {
            return user;
        }

        public void setUser(UserTO user) {
            this.user = user;
        }

        public UserTokenTO getTokenCp() {
            return tokenCp;
        }

        public void setTokenCp(UserTokenTO tokenCp) {
            this.tokenCp = tokenCp;
        }
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

    public LoginHolderAPI authValid(String validator,List<Integer> ids){
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
            pars.put("v2", "true");
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

                ApiCallReturn<LoginHolderAPI> result = gson.fromJson(json, new TypeToken<ApiCallReturn<LoginHolderAPI>>(){}.getType());
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
        return quickAccessNew(null);
    }

    public UserQuickAccessTO quickAccessNew(String redirect){
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
            if(!TextUtils.isEmpty(redirect))
                pars.put("redirect",redirect);

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

    public boolean setFcmToken(String fcmToken, String fcmTokenOld){
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
            if(!TextUtils.isEmpty(fcmTokenOld))
                pars.put("fcmTokenOld", fcmTokenOld);

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

    public boolean setTrk(int media){
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
                    .appendPath("trk");

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("source", ""+SOURCE_DEFAULT);
            pars.put("link", ""+LINK_DEFAULT);
            if(!TextUtils.isEmpty(media))
                pars.put("media", ""+media);

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

    public Map<String,Object> setCompany(UserTO user){
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
                    .appendPath("set")
                    .appendPath("company");

            Gson gsonBuilder = WoeDAOUtils.getGson();

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("name",user.getName());
            pars.put("email",user.getEmail());
            pars.put("document",user.getDocument());
            pars.put("language",user.getLanguage());
            pars.put("timezone",user.getTimezone());

            if(user.getCategory() != null && user.getCategory().getId() > 0)
                pars.put("category",user.getCategory().getId());

            Map<String,Object> country = new HashMap<String,Object>();
            country.put("id", user.getCountry().getId());
            pars.put("country", country);

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

                ApiCallReturn<Map<String,Object>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<Map<String,Object>>>(){}.getType());
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

    public boolean setRecTermsSocial(UserTO user){
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
                    .appendPath("rec-terms-social");

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

    public boolean addExcludePub(PubTO pub){
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
                    .appendPath("add")
                    .appendPath("exclude-pub");

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("pub",pub.getId());

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

    public boolean addExcludeAdvertiser(int advertiser){
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
                    .appendPath("add")
                    .appendPath("exclude-advertiser");

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("advertiser",advertiser);

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

    public boolean addExcludeTag(String tag){
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
                    .appendPath("add")
                    .appendPath("exclude-tag");

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("tag",tag);

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

    public UserTO getWallet(){
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
                    .appendPath("wallet")
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
