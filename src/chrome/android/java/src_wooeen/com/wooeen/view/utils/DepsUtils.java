package com.wooeen.view.utils;

import android.webkit.URLUtil;

import com.google.common.net.InternetDomainName;
import com.wooeen.utils.TextUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class DepsUtils {

  public static String getDomain(String url) {
      try {
          if (TextUtils.isEmpty(url) || !url.contains(".") || !URLUtil.isValidUrl(url))
              return null;

          try {
              URI uri = new URI(url);
              String domain = uri.getHost();
              if (domain == null)
                  return "";

              if (!InternetDomainName.isValid(domain))
                  return null;

//            domain = domain.startsWith("www.") ? domain.substring(4) : domain;
              String topDomain = null;
              try {
                  topDomain = InternetDomainName.from(domain).topPrivateDomain().toString();
              } catch (java.lang.IllegalStateException ex) {
                  topDomain = null;
              }
              return !TextUtils.isEmpty(topDomain) ? topDomain : domain;
          } catch (URISyntaxException e) {
              return null;
          }
      }catch(Exception e){
          return null;
      }
  }

}
