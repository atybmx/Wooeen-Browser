package com.wooeen.model.api;

import android.net.Uri;

import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WoeDAOUtils;

public class WoeTrkAPI {

    private final static String SCHEMA = "https";
    private final static String AUTHORITY = "tracking.wooeen.com";

    public static boolean purchase(int event,int user,int source,int link,String dateClick) {
        if(event == 0)
            return false;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(SCHEMA)
                    .encodedAuthority(AUTHORITY)
                    .path("conversion/woetconv")
                    .appendQueryParameter("e", ""+event)
                    .appendQueryParameter("u",""+user)
                    .appendQueryParameter("so",""+source)
                    .appendQueryParameter("lk",""+link)
                    .appendQueryParameter("dr",dateClick);

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
