package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.TaskTO;

import java.util.List;

public class TaskAPI {

    private String country;

    public TaskAPI(String country) {
        this.country = country;
    }

    public List<TaskTO> get(){
        return get(0,0,0);
    }

    public List<TaskTO> get(int user){
        return get(0,0,user);
    }

    public List<TaskTO> get(int pg,int qtdPerPage,int user){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("task")
                    .appendPath("get")
                    .appendQueryParameter("cr", country)
                    .appendQueryParameter("st","1")
                    .appendQueryParameter("pg",""+pg)
                    .appendQueryParameter("qpp",""+qtdPerPage);

            if(user > 0)
                builder.appendQueryParameter("us", ""+user);

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString(),
                            new Header("app_id",WoeDAOUtils.APP_ID),
                            new Header("app_token",WoeDAOUtils.APP_TOKEN));

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<List<TaskTO>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<List<TaskTO>>>(){}.getType());
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
