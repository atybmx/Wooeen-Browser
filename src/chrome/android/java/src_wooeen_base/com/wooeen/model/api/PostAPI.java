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

    public List<PostTO> get(String cr){
        return get(cr,1,50);
    }

    public List<PostTO> get(String cr,int pg,int qtdPerPage){
        return get(cr,pg, qtdPerPage, null);
    }

    public List<PostTO> get(String cr,int pg,int qtdPerPage, String q){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(SCHEMA)
                    .encodedAuthority(AUTHORITY)
                    .path(PATH)
                    .appendPath("posts")
                    .appendQueryParameter("_fields","id,featured_media,fimg_url,title,link,date,excerpt,author")
                    .appendQueryParameter("page",""+pg)
                    .appendQueryParameter("per_page",""+qtdPerPage);

            if(!TextUtils.isEmpty(cr))
                builder.appendQueryParameter("language",getCountryId(cr));

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
                    if(holder.getTitle() != null)
                        item.setTitle(holder.getTitle().getRendered());
                    if(holder.getExcerpt() != null)
                        item.setExcerpt(holder.getExcerpt().getRendered());
                    item.setImage(holder.getFimg_url());
                    item.setLink(holder.getLink());
                    item.setDate(DatetimeUtils.stringToDate(holder.getDate(),"yyyy-MM-dd'T'HH:mm:ss"));
                    if(holder.getAuthor() != null) {
                        item.setAuthorId(holder.getAuthor().getId());
                        item.setAuthorName(holder.getAuthor().getName());
                        item.setAuthorPhoto(holder.getAuthor().getPhoto());
                    }

                    items.add(item);
                }
                return items;
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    private String getCountryId(String cr) {
        if("BR".equalsIgnoreCase(cr)) return "205";
        else if("ES".equalsIgnoreCase(cr)) return "212";
        else if("DE".equalsIgnoreCase(cr)) return "216";
        else if("UK".equalsIgnoreCase(cr)) return "208";
        else if("FR".equalsIgnoreCase(cr)) return "508";
        else return "205";
    }

    public static class PostAPIHolder{
        private int id;
        private String date;
        private String link;
        private String fimg_url;
        private PostApiTitleHolder title;
        private PostApiExcerptHolder excerpt;
        private PostApiAuthorHolder author;

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

        public PostApiExcerptHolder getExcerpt() {
            return excerpt;
        }

        public void setExcerpt(PostApiExcerptHolder excerpt) {
            this.excerpt = excerpt;
        }

        public PostApiAuthorHolder getAuthor() {
            return author;
        }

        public void setAuthor(PostApiAuthorHolder author) {
            this.author = author;
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

    public static class PostApiExcerptHolder{
        private String rendered;

        public String getRendered() {
            return rendered;
        }

        public void setRendered(String rendered) {
            this.rendered = rendered;
        }
    }

    public static class PostApiAuthorHolder{
        private String id;
        private String name;
        private String photo;
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
        public String getPhoto() {
            return photo;
        }
        public void setPhoto(String photo) {
            this.photo = photo;
        }

    }

}
