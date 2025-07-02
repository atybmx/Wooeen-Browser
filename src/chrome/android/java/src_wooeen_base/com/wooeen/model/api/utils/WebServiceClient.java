package com.wooeen.model.api.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebServiceClient {

	public final String[] get(String link,Header...headers) {
		return get(link,15000,headers);
	}

	public final String[] get(String link,int connectTimeout,Header...headers) {
		String[] result = new String[2];

		URL url;
	  HttpURLConnection conn = null;
		InputStream in = null;
		try {
			url = new URL(link);

	    conn = (HttpURLConnection) url.openConnection();

			conn.setReadTimeout(10000);
			conn.setConnectTimeout(connectTimeout);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type","application/json");
			conn.setRequestProperty("Accept", "application/json");

			if(headers != null && headers.length > 0) {
				for(Header header:headers) {
					conn.setRequestProperty(header.getKey(),header.getValue());
				}
			}

	    in = conn.getInputStream();
	    result[0] = String.valueOf(conn.getResponseCode());
	    result[1] = toString(in);

//		result[0] = ""+response.code();
//		result[1] = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


		return result;
	}

	public final String[] post(String link, String body,Header...headers) {
		String[] result = new String[2];

		URL url;
	  HttpURLConnection conn = null;
		OutputStreamWriter wr = null;
		try {
			url = new URL(link);

      conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000);
      conn.setConnectTimeout(15000);
      conn.setRequestMethod("POST");
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setRequestProperty("Content-Type","application/json");
      conn.setRequestProperty("Accept", "application/json");

			if(headers != null && headers.length > 0) {
				for(Header header:headers) {
					conn.setRequestProperty(header.getKey(),header.getValue());
				}
			}

			conn.connect();

      wr = new OutputStreamWriter(conn.getOutputStream());
      wr.write(body);
      wr.flush ();

      result[0] = String.valueOf(conn.getResponseCode());
      result[1] = toString(conn.getInputStream());
//		result[0] = ""+response.code();
//		result[1] = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(wr != null) {
				try {
					wr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public static class Header{

		private String key;
		private String value;

		public Header(String key,String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}

    private String toString(InputStream is) throws IOException {

    	byte[] bytes = new byte[1024];
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	int lidos;
    	while ((lidos = is.read(bytes)) > 0) {
    		baos.write(bytes, 0, lidos);
    	}

    	return new String(baos.toByteArray());
    }
}
