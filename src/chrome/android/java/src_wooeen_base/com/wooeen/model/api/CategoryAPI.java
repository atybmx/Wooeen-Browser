package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.CategoryTO;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.CurrencyTO;
import com.wooeen.utils.TextUtils;

import java.util.List;

public class CategoryAPI {

    private String country;

    public CategoryAPI(String country) {
        this.country = country;
    }

    public List<CategoryTO> get(){
        return get(0);
    }

    public List<CategoryTO> get(int parent){
        return get(0,0, parent);
    }

    public List<CategoryTO> get(int pg,int qtdPerPage,int parent){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("category")
                    .appendPath("get")
                    .appendQueryParameter("cr", country)
                    .appendQueryParameter("pg",""+pg)
                    .appendQueryParameter("qpp",""+qtdPerPage);

            if(parent > 0)
                builder.appendQueryParameter("parent", ""+parent);

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString(),
                            new Header("app_id",WoeDAOUtils.APP_ID),
                            new Header("app_token",WoeDAOUtils.APP_TOKEN));

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<List<CategoryTO>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<List<CategoryTO>>>(){}.getType());
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
