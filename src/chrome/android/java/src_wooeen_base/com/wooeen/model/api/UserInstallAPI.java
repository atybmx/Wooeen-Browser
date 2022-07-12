package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.UserAuthTO;
import com.wooeen.model.to.UserQuickAccessTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.model.to.WoeTrkClickTO;
import com.wooeen.utils.NumberUtils;
import com.wooeen.utils.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInstallAPI {

    public UserInstallAPI(){

    }

    public int newUserInstall(String fcmToken, WoeTrkClickTO click){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("user")
                    .appendPath("install")
                    .appendPath("new");

            Map<String,Object> pars = new HashMap<String,Object>();
            pars.put("fcmToken", fcmToken);
            pars.put("user", ""+click.getUser());
            pars.put("source", ""+click.getSource());
            pars.put("link", ""+click.getLink());
            pars.put("dateClick", ""+click.getDateClick());

            Gson gsonBuilder = WoeDAOUtils.getGson();

            String[] response = new WebServiceClient()
                    .post(
                            builder.build().toString(),
                            gsonBuilder.toJson(pars));

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                ApiCallReturn<Object> result = gson.fromJson(json, new TypeToken<ApiCallReturn<Object>>(){}.getType());
                if(result == null || !result.getResult()) {
                    return NumberUtils.getInteger(result.getMessage());
                }

                return -1;
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return -1;
    }

}
