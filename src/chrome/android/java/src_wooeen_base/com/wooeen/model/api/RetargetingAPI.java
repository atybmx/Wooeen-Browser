package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.NavigationTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.utils.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetargetingAPI {

    private UserTokenTO token;

    public RetargetingAPI(UserTokenTO token) {
        this.token = token;
    }

    public List<NavigationTO> get(){
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
                    .appendPath("retargeting")
                    .appendPath("get");

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString(),
                            new Header("uti",token.getIdToken()),
                            new Header("uta",token.getAccessToken()));

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<List<NavigationTO>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<List<NavigationTO>>>(){}.getType());
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
