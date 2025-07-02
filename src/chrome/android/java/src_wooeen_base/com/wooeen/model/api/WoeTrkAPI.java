package com.wooeen.model.api;

import android.net.Uri;

import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WoeDAOUtils;

public class WoeTrkAPI {

    private final static String SCHEMA = "https";
    private final static String AUTHORITY = "tracking.wooeen.com";

    public static boolean eventOLD(int event,int user,int source,int link,String dateClick) {
        if(event == 0)
            return false;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(SCHEMA)
                    .encodedAuthority(AUTHORITY)
                    .path("conversion/woetconv")
                    .appendQueryParameter("e", ""+event);

            if(user > 0)
                builder.appendQueryParameter("u",""+user);

            if(source > 0)
                builder.appendQueryParameter("so",""+source);

            if(link > 0)
                builder.appendQueryParameter("lk",""+link);

            if(dateClick != null)
                builder.appendQueryParameter("dr",dateClick);

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

    public static boolean user(int user,int source,int link,String dateClick) {
        if(user <= 0 || source <= 0 || link <= 0 || dateClick == null)
            return false;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(SCHEMA)
                    .encodedAuthority(AUTHORITY)
                    .path("user/woetuser")
                    .appendQueryParameter("u",""+user);

            if(source > 0)
                builder.appendQueryParameter("so",""+source);

            if(link > 0)
                builder.appendQueryParameter("lk",""+link);

            if(dateClick != null)
                builder.appendQueryParameter("dr",dateClick);

            String urlLink = builder.build().toString();

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString());

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
