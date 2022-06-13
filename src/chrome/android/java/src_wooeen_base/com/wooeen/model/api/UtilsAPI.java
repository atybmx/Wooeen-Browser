package com.wooeen.model.api;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wooeen.model.api.utils.ApiCallReturn;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.UserTO;

public class UtilsAPI {

    public static IpInfo getIpInfo(){
        try {
            //configura a url e os parametros
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .encodedAuthority("iplist.cc")
                    .path("api");

            String[] response = new WebServiceClient()
                    .get(builder.build().toString(),
                            4000);

            //trata o retorno
            if ("200".equals(response[0])) {
                Gson gson = WoeDAOUtils.getGson();

                String json = response[1];

                return gson.fromJson(json, new TypeToken<IpInfo>(){}.getType());
            }

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return new IpInfo("BR");
    }

    public static class IpInfo{
        private String ip;
        private String countrycode;
        private String countryname;
        private String city;

        public IpInfo() {
        }
        public IpInfo(String countrycode) {
            this.countrycode = countrycode;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountrycode() {
            return countrycode;
        }

        public void setCountrycode(String countrycode) {
            this.countrycode = countrycode;
        }

        public String getCountryname() {
            return countryname;
        }

        public void setCountryname(String countryname) {
            this.countryname = countryname;
        }
    }
}
