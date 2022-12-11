package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WebServiceClient.Header;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.PostTO;
import com.wooeen.utils.DatetimeUtils;
import com.wooeen.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class PostAPI {

    private static final String SCHEMA = "https";
    private static final String AUTHORITY = "www.wooeen.com";
    private static final String PATH = "blog/wp-json/wp/v2/";

    public List<PostTO> get(){
        return get(1,50);
    }

    public List<PostTO> get(int pg,int qtdPerPage){
        return get(pg, qtdPerPage, null);
    }

    public List<PostTO> get(int pg,int qtdPerPage, String q){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(SCHEMA)
                    .encodedAuthority(AUTHORITY)
                    .path(PATH)
                    .appendPath("posts")
                    .appendQueryParameter("_fields","id,featured_media,fimg_url,title,link,date")
                    .appendQueryParameter("page",""+pg)
                    .appendQueryParameter("per_page",""+qtdPerPage);

            if(!TextUtils.isEmpty(q))
                builder.appendQueryParameter("search",q);

            String[] resposta = new WebServiceClient()
                    .get(builder.build().toString());

            //trata o retorno
            if ("200".equals(resposta[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = resposta[1];

                List<PostAPIHolder> result = gson.fromJson(json, new TypeToken<List<PostAPIHolder>>(){}.getType());
                if(result == null || result.isEmpty()) {
                    return null;
                }

                List<PostTO> items = new ArrayList<PostTO>();
                for(PostAPIHolder holder:result){
                    PostTO item = new PostTO();
                    item.setId(holder.getId());
                    item.setTitle(holder.getTitle().getRendered());
                    item.setImage(holder.getFimg_url());
                    item.setLink(holder.getLink());
                    item.setDate(DatetimeUtils.stringToDate(holder.getDate(),"yyyy-MM-dd'T'HH:mm:ss"));
                    items.add(item);
                }
                return items;
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static class PostAPIHolder{
        private int id;
        private String date;
        private String link;
        private String fimg_url;
        private PostApiTitleHolder title;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getFimg_url() {
            return fimg_url;
        }

        public void setFimg_url(String fimg_url) {
            this.fimg_url = fimg_url;
        }

        public PostApiTitleHolder getTitle() {
            return title;
        }

        public void setTitle(PostApiTitleHolder title) {
            this.title = title;
        }
    }

    public static class PostApiTitleHolder{
        private String rendered;

        public String getRendered() {
            return rendered;
        }

        public void setRendered(String rendered) {
            this.rendered = rendered;
        }
    }

}
