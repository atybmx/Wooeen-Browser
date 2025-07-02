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

public class CountryAPI {

    public List<CountryTOA> get(){
        return get(0,0);
    }

    public List<CountryTOA> get(int pg,int qtdPerPage){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("country")
                    .appendPath("get")
                    .appendQueryParameter("pg",""+pg)
                    .appendQueryParameter("qpp",""+qtdPerPage);

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString(),
                            new Header("app_id",WoeDAOUtils.APP_ID),
                            new Header("app_token",WoeDAOUtils.APP_TOKEN));

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<List<CountryTOA>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<List<CountryTOA>>>(){}.getType());
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

    public CountryTO get(String id){
        if(TextUtils.isEmpty(id))
           return null;

        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(WoeDAOUtils.SCHEMA)
                    .encodedAuthority(WoeDAOUtils.AUTHORITY)
                    .path(WoeDAOUtils.PATH)
                    .appendPath("country")
                    .appendPath("get")
                    .appendQueryParameter("id", id);

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString(),
                            new Header("app_id",WoeDAOUtils.APP_ID),
                            new Header("app_token",WoeDAOUtils.APP_TOKEN));

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                ApiCallReturn<List<CountryTOA>> result = gson.fromJson(json, new TypeToken<ApiCallReturn<List<CountryTOA>>>(){}.getType());
                if(result == null || !result.getResult() || result.getCallback() == null || result.getCallback().isEmpty()) {
                    return null;
                }

                CountryTOA cra = result.getCallback().get(0);

                CountryTO cr = new CountryTO();
                cr.setId(cra.getId());
                cr.setName(cra.getName());
                cr.setLanguage(cra.getLanguage());
                cr.setImageUrl(cra.getImageUrl());
                cr.setCurrency(new CurrencyTO(cra.getCurrency()));
                cr.setLoadPosts(cra.getLoadPosts());
                cr.setLoadOffers(cra.getLoadOffers());
                cr.setLoadCoupons(cra.getLoadCoupons());
                cr.setLoadTasks(cra.getLoadTasks());
                cr.setLoadGames(cra.getLoadGames());
                cr.setSearchHint(cra.getSearchHint());

                if(cra.getCategoryB2b() != null && cra.getCategoryB2b().getId() > 0){
                    cr.setCategoryB2b(cra.getCategoryB2b());
                }

                return cr;
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static class CountryTOA{
        private String id;
        private String name;
        private String language;
        private String imageUrl;
        private String currency;
        private boolean loadPosts;
        private boolean loadOffers;
        private boolean loadCoupons;
        private boolean loadTasks;
        private boolean loadGames;
        private String searchHint;
        private CategoryTO categoryB2b;

        public CountryTOA() {
        }
        public CountryTOA(String id) {
            this.id = id;
        }
        public CountryTOA(String id, String imageUrl) {
            this.id = id;this.imageUrl = imageUrl;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getImageUrl() {
            return imageUrl;
        }
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
        public String getLanguage() {
            return language;
        }
        public void setLanguage(String language) {
            this.language = language;
        }
        public boolean getLoadPosts() {
            return loadPosts;
        }
        public void setLoadPosts(boolean loadPosts) {
            this.loadPosts = loadPosts;
        }
        public boolean getLoadOffers() {
            return loadOffers;
        }
        public void setLoadOffers(boolean loadOffers) {
            this.loadOffers = loadOffers;
        }
        public boolean getLoadCoupons() {
            return loadCoupons;
        }
        public void setLoadCoupons(boolean loadCoupons) {
            this.loadCoupons = loadCoupons;
        }
        public boolean getLoadTasks() {
            return loadTasks;
        }
        public void setLoadTasks(boolean loadTasks) {
            this.loadTasks = loadTasks;
        }

        public String getCurrency() {
            return currency;
        }
        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public boolean getLoadGames() {
            return loadGames;
        }

        public void setLoadGames(boolean loadGames) {
            this.loadGames = loadGames;
        }

        public CategoryTO getCategoryB2b() {
            return categoryB2b;
        }

        public void setCategoryB2b(CategoryTO categoryB2b) {
            this.categoryB2b = categoryB2b;
        }

        public String getSearchHint() {
            return searchHint;
        }

        public void setSearchHint(String searchHint) {
            this.searchHint = searchHint;
        }
    }
}
