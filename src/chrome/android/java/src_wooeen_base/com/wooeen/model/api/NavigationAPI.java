package com.wooeen.model.api;

import android.net.Uri;

import com.wooeen.model.api.utils.WebServiceClient;

public class NavigationAPI {

    private final static String SCHEMA = "https";
    private final static String AUTHORITY = "webhook.wooeen.com";

    public static boolean woenav(int type,int user,int advertiser) {
        if(type == 0)
            return false;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(SCHEMA)
                    .encodedAuthority(AUTHORITY)
                    .path("navigation/woenav")
                    .appendQueryParameter("tp", ""+type);

            if(user > 0)
                builder.appendQueryParameter("u",""+user);

            if(advertiser > 0)
                builder.appendQueryParameter("ad",""+advertiser);

            String urlLink = builder.build().toString();

            String[] resposta = new WebServiceClient()
                    .get(urlLink);

            //trata o retorno
            if ("200".equals(resposta[0]) || "201".equals(resposta[0])) {
                return true;
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }


}
