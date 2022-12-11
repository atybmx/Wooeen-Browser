package com.wooeen.model.api;

import android.net.Uri;

import com.wooeen.model.api.utils.WebServiceClient;

public class WoePushAPI {

    private final static String SCHEMA = "https";
    private final static String AUTHORITY = "tracking.wooeen.com";

    public static boolean event(int type,int notification,int push,int user,int advertiser) {
        if(type == 0 || user == 0 || notification == 0 || push == 0)
            return false;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(SCHEMA)
                    .encodedAuthority(AUTHORITY)
                    .path("push/woepconv")
                    .appendQueryParameter("tp", ""+type)
                    .appendQueryParameter("u",""+user)
                    .appendQueryParameter("n",""+notification)
                    .appendQueryParameter("p",""+push)
                    .appendQueryParameter("ad",(advertiser > 0 ? ""+advertiser : ""));

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
